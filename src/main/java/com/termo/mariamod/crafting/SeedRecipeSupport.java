package com.termo.mariamod.crafting;

import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.item.GeneticSeedItem;
import com.termo.mariamod.registry.ModDataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// Piezas comunes de las recetas de semillas: grilla de 3x3 con un ítem en el centro y 8 semillas alrededor.
final class SeedRecipeSupport {
    private SeedRecipeSupport() {}

    static final int GRID_SIZE = 3;
    static final int CENTER_SLOT = 4;

    static boolean isFullGrid(CraftingInput input) {
        return input.width() == GRID_SIZE && input.height() == GRID_SIZE;
    }

    // Las 8 semillas alrededor del centro, o null si alguna casilla no tiene una semilla del mod.
    @Nullable
    static List<ItemStack> seedsAround(CraftingInput input) {
        List<ItemStack> seeds = new ArrayList<>();
        for (int slot = 0; slot < input.size(); slot++) {
            if (slot == CENTER_SLOT) {
                continue;
            }
            ItemStack stack = input.getItem(slot);
            if (!(stack.getItem() instanceof GeneticSeedItem)) {
                return null;
            }
            seeds.add(stack);
        }
        return seeds;
    }

    static PlantVariant variantOf(ItemStack seed) {
        return ((GeneticSeedItem) seed.getItem()).variant();
    }

    // La variante de todas las semillas, o null si no son todas iguales.
    @Nullable
    static PlantVariant commonVariant(List<ItemStack> seeds) {
        PlantVariant found = null;
        for (ItemStack seed : seeds) {
            PlantVariant variant = variantOf(seed);
            if (found == null) {
                found = variant;
            } else if (found != variant) {
                return null;
            }
        }
        return found;
    }

    // La genética de cada semilla, en una lista que se puede modificar.
    static List<Genetics> parentsOf(List<ItemStack> seeds) {
        List<Genetics> parents = new ArrayList<>();
        for (ItemStack seed : seeds) {
            Genetics genetics = seed.get(ModDataComponents.GENETICS.get());
            parents.add(genetics != null ? genetics : Genetics.WILD);
        }
        return parents;
    }

    // En Máxima y Primordial el riesgo está congelado.
    static boolean freezesRisk(PlantVariant variant) {
        return variant == PlantVariant.HYBRID_MAX || variant == PlantVariant.HYBRID_PRIMORDIAL;
    }
}
