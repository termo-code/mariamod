package com.termo.mariamod.crafting;

import com.termo.mariamod.config.BalanceConfig;
import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.GeneticsBreeder;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.registry.ModDataComponents;
import com.termo.mariamod.registry.ModItems;
import com.termo.mariamod.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

import static com.termo.mariamod.crafting.SeedRecipeSupport.*;

// Selección: Sustrato Volátil en el centro y 8 semillas de la MISMA variante alrededor = 1 semilla mejorada.
// Si la unión de efectos pasa de 5, no hay resultado.
// Híbrida Plus asciende a Híbrida Máxima si el resultado tiene potencia y duración al tope y el riesgo en el piso.
public class SeedSelectionRecipe extends CustomRecipe {
    public SeedSelectionRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return selection(input, RandomSource.create()).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return selection(input, RandomSource.create()).orElse(ItemStack.EMPTY);
    }

    private static Optional<ItemStack> selection(CraftingInput input, RandomSource random) {
        if (!isFullGrid(input)) {
            return Optional.empty();
        }
        if (!input.getItem(CENTER_SLOT).is(ModItems.VOLATILE_SUBSTRATE.get())) {
            return Optional.empty();
        }
        List<ItemStack> seeds = seedsAround(input);
        if (seeds == null) {
            return Optional.empty();
        }
        PlantVariant variant = commonVariant(seeds);
        if (variant == null) {
            return Optional.empty();
        }
        Optional<Genetics> child = GeneticsBreeder.breed(parentsOf(seeds), freezesRisk(variant), random);
        if (child.isEmpty()) {
            return Optional.empty();
        }
        PlantVariant resultVariant = promote(variant, child.get());
        ItemStack result = new ItemStack(ModItems.SEEDS.get(resultVariant).get());
        result.set(ModDataComponents.GENETICS.get(), child.get());
        return Optional.of(result);
    }

    // Ascenso de Plus a Máxima: potencia y duración al tope y riesgo en el piso.
    private static PlantVariant promote(PlantVariant variant, Genetics child) {
        if (variant != PlantVariant.HYBRID_PLUS) {
            return variant;
        }
        boolean maxLevels = child.potency() == Genetics.MAX_POTENCY
                && child.durationLevel() == Genetics.MAX_DURATION_LEVEL;
        boolean atFloor = child.badTripRisk() <= BalanceConfig.RISK_FLOOR.get().floatValue() + 0.0005f;
        return (maxLevels && atFloor) ? PlantVariant.HYBRID_MAX : variant;
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipeSerializers.SEED_SELECTION.get();
    }

}