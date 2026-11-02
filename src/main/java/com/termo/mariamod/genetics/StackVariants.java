package com.termo.mariamod.genetics;

import com.termo.mariamod.item.GeneticBudItem;
import com.termo.mariamod.item.GeneticSeedItem;
import com.termo.mariamod.registry.ModDataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

// Averigua de qué variante es un ítem: semillas y cogollos la conocen por su clase, el puro por un dato.
public final class StackVariants {
    private StackVariants() {}

    public static Optional<PlantVariant> of(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof GeneticSeedItem seed) {
            return Optional.of(seed.variant());
        }
        if (item instanceof GeneticBudItem bud) {
            return Optional.of(bud.variant());
        }
        String id = stack.get(ModDataComponents.VARIANT.get());
        return id != null ? PlantVariant.byId(id) : Optional.empty();
    }
}