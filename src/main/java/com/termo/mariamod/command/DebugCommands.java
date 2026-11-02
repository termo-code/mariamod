package com.termo.mariamod.command;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.GeneticsBreeder;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.genetics.WildGenetics;
import com.termo.mariamod.registry.ModDataComponents;
import com.termo.mariamod.registry.ModItems;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Comandos de desarrollo para probar el mod. Requieren permiso de operador.
 *
 * /mariamod breed [cantidad_A]  : semilla A en la mano principal y B en la secundaria (8 padres: cantidad_A de A y el resto de B).
 * /mariamod simulate N          : repite la selección N generaciones con la semilla de la mano principal.
 * /mariamod wild                : te da una semilla de cada silvestre con genética al azar.
 * /mariamod wildstats N         : sortea N silvestres y muestra la distribución (para comprobar los pesos de la config).
 */
@EventBusSubscriber(modid = MariaMod.MODID)
public class DebugCommands {

    private static final int PARENT_COUNT = 8;
    private static final int DEFAULT_COUNT_A = 4;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(MariaMod.MODID)
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("breed")
                        .executes(ctx -> breed(ctx, DEFAULT_COUNT_A))
                        .then(Commands.argument("count_a", IntegerArgumentType.integer(1, PARENT_COUNT - 1))
                                .executes(ctx -> breed(ctx, IntegerArgumentType.getInteger(ctx, "count_a")))))
                .then(Commands.literal("simulate")
                        .then(Commands.argument("generations", IntegerArgumentType.integer(1, 500))
                                .executes(ctx -> simulate(ctx, IntegerArgumentType.getInteger(ctx, "generations")))))
                .then(Commands.literal("wild")
                        .executes(DebugCommands::wild))
                .then(Commands.literal("wildstats")
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 100000))
                                .executes(ctx -> wildStats(ctx, IntegerArgumentType.getInteger(ctx, "count"))))));
    }

    private static int breed(CommandContext<CommandSourceStack> ctx, int countA) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        ItemStack mainHand = player.getMainHandItem();

        Genetics parentA = mainHand.get(ModDataComponents.GENETICS.get());
        Genetics parentB = player.getOffhandItem().get(ModDataComponents.GENETICS.get());
        if (parentA == null || parentB == null) {
            ctx.getSource().sendFailure(Component.literal(
                    "Poné una semilla con genética en cada mano (principal = A, secundaria = B)."));
            return 0;
        }

        List<Genetics> parents = new ArrayList<>();
        for (int i = 0; i < countA; i++) {
            parents.add(parentA);
        }
        for (int i = countA; i < PARENT_COUNT; i++) {
            parents.add(parentB);
        }

        Optional<Genetics> child = GeneticsBreeder.breed(parents, false, player.getRandom());
        if (child.isEmpty()) {
            ctx.getSource().sendFailure(Component.literal("No hay resultado: los padres juntan más de 5 efectos."));
            return 0;
        }

        ItemStack seed = new ItemStack(mainHand.getItem());
        seed.set(ModDataComponents.GENETICS.get(), child.get());
        give(player, seed);

        ctx.getSource().sendSuccess(() -> Component.literal(
                "Selección lista: " + countA + " de A y " + (PARENT_COUNT - countA) + " de B dan 1 semilla."), false);
        return 1;
    }

    private static int simulate(CommandContext<CommandSourceStack> ctx, int generations) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Genetics current = player.getMainHandItem().get(ModDataComponents.GENETICS.get());
        if (current == null) {
            ctx.getSource().sendFailure(Component.literal("Poné una semilla con genética en la mano principal."));
            return 0;
        }

        int every = Math.max(1, generations / 10);
        for (int generation = 1; generation <= generations; generation++) {
            List<Genetics> parents = Collections.nCopies(PARENT_COUNT, current);
            current = GeneticsBreeder.breed(parents, false, player.getRandom()).orElse(current);

            if (generation % every == 0 || generation == generations) {
                String line = String.format("Generación %d: potencia %d, duración %d, riesgo %.1f%%",
                        generation, current.potency(), current.durationLevel(), current.badTripRisk() * 100);
                ctx.getSource().sendSuccess(() -> Component.literal(line), false);
            }
        }
        return generations;
    }

    private static int wild(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        for (PlantVariant variant : List.of(PlantVariant.SATIVA_WILD, PlantVariant.INDICA_WILD, PlantVariant.RUDERALIS_WILD)) {
            ItemStack seed = new ItemStack(ModItems.SEEDS.get(variant).get());
            seed.set(ModDataComponents.GENETICS.get(), WildGenetics.roll(player.getRandom()));
            give(player, seed);
        }
        ctx.getSource().sendSuccess(() -> Component.literal("3 semillas silvestres con genética al azar."), false);
        return 3;
    }

    private static int wildStats(CommandContext<CommandSourceStack> ctx, int count) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int[] potency = new int[Genetics.MAX_POTENCY + 1];
        int[] duration = new int[Genetics.MAX_DURATION_LEVEL + 1];
        double riskSum = 0;
        float riskMin = 1.0f;
        float riskMax = 0.0f;

        for (int i = 0; i < count; i++) {
            Genetics genetics = WildGenetics.roll(player.getRandom());
            potency[genetics.potency()]++;
            duration[genetics.durationLevel()]++;
            riskSum += genetics.badTripRisk();
            riskMin = Math.min(riskMin, genetics.badTripRisk());
            riskMax = Math.max(riskMax, genetics.badTripRisk());
        }

        String potencyLine = String.format("Potencia:  I %.1f%%  |  II %.1f%%  |  III %.1f%%",
                100.0 * potency[1] / count, 100.0 * potency[2] / count, 100.0 * potency[3] / count);
        String durationLine = String.format("Duración:  I %.1f%%  |  II %.1f%%  |  III %.1f%%",
                100.0 * duration[1] / count, 100.0 * duration[2] / count, 100.0 * duration[3] / count);
        String riskLine = String.format("Riesgo: mín %.1f%%  |  promedio %.1f%%  |  máx %.1f%%",
                riskMin * 100, riskSum / count * 100, riskMax * 100);
        ctx.getSource().sendSuccess(() -> Component.literal(potencyLine), false);
        ctx.getSource().sendSuccess(() -> Component.literal(durationLine), false);
        ctx.getSource().sendSuccess(() -> Component.literal(riskLine), false);
        return count;
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}