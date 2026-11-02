package com.termo.mariamod.genetics;

import com.termo.mariamod.config.BalanceConfig;
import com.termo.mariamod.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/** Toda la lógica visual del tooltip genético. Los umbrales de color están en BalanceConfig. */
public final class GeneticsTooltip {
    private GeneticsTooltip() {}

    public static void append(ItemStack stack, List<Component> tooltip) {
        Genetics genetics = stack.get(ModDataComponents.GENETICS.get());
        if (genetics == null) return;

        // Efectos naturales de la especie, más los efectos de poción del genoma.
        Species species = StackVariants.of(stack).map(PlantVariant::species).orElse(null);
        for (ResourceLocation id : NaturalEffects.allFor(species, genetics)) {
            tooltip.add(effectLine(id, genetics));
        }

        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.mariamod.genetics_header").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.mariamod.potency", roman(genetics.potency()))
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.mariamod.duration",
                        roman(genetics.durationLevel()) + " (" + formatDuration(genetics.durationLevel()) + ")")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.mariamod.bad_trip_risk",
                        String.format("%.1f%%", genetics.badTripRisk() * 100))
                .withStyle(riskColor(genetics.badTripRisk())));
    }

    private static Component effectLine(ResourceLocation id, Genetics genetics) {
        return BuiltInRegistries.MOB_EFFECT.getOptional(id)
                .map(effect -> effectName(effect).append(" " + roman(genetics.potency())
                                + " (" + formatDuration(genetics.durationLevel()) + ")")
                        .withStyle(ChatFormatting.BLUE))
                .orElseGet(() -> Component.literal("• " + id).withStyle(ChatFormatting.RED));
    }

    private static MutableComponent effectName(MobEffect effect) {
        return Component.literal("• ").append(Component.translatable(effect.getDescriptionId()));
    }

    private static ChatFormatting riskColor(float risk) {
        if (risk <= BalanceConfig.RISK_LOW.get().floatValue()) return ChatFormatting.GREEN;
        if (risk <= BalanceConfig.RISK_MEDIUM.get().floatValue()) return ChatFormatting.YELLOW;
        return ChatFormatting.RED;
    }

    /** Nivel de duración a "mm:ss", con los segundos de la configuración. */
    public static String formatDuration(int durationLevel) {
        int totalSeconds = BalanceConfig.durationSeconds(durationLevel);
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    private static String roman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> String.valueOf(n);
        };
    }
}