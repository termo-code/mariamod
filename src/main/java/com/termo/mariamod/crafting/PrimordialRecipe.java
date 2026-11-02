package com.termo.mariamod.crafting;

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

// Primordial: Sustrato Primordial en el centro y 8 semillas de Híbrida Máxima = 1 Híbrida Primordial con riesgo 0%.
// Junta los efectos de las 8 semillas. Si pasan de 5, no hay resultado.
public class PrimordialRecipe extends CustomRecipe {
    public PrimordialRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return craft(input, RandomSource.create()).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return craft(input, RandomSource.create()).orElse(ItemStack.EMPTY);
    }

    private static Optional<ItemStack> craft(CraftingInput input, RandomSource random) {
        if (!isFullGrid(input)) {
            return Optional.empty();
        }
        if (!input.getItem(CENTER_SLOT).is(ModItems.PRIMORDIAL_SUBSTRATE.get())) {
            return Optional.empty();
        }
        List<ItemStack> seeds = seedsAround(input);
        if (seeds == null || commonVariant(seeds) != PlantVariant.HYBRID_MAX) {
            return Optional.empty();
        }
        Optional<Genetics> child = GeneticsBreeder.breed(parentsOf(seeds), true, random);
        if (child.isEmpty()) {
            return Optional.empty();
        }
        Genetics mixed = child.get();
        Genetics primordial = new Genetics(mixed.potency(), mixed.durationLevel(), 0.0f, mixed.effects());

        ItemStack result = new ItemStack(ModItems.SEEDS.get(PlantVariant.HYBRID_PRIMORDIAL).get());
        result.set(ModDataComponents.GENETICS.get(), primordial);
        return Optional.of(result);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= GRID_SIZE && height >= GRID_SIZE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.PRIMORDIAL.get();
    }
}