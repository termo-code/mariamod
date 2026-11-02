package com.termo.mariamod.item;

import com.termo.mariamod.genetics.GeneticsTooltip;
import com.termo.mariamod.genetics.PlantVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

// Semilla de una variante: se planta como su cultivo y muestra su genética en el tooltip.
public class GeneticSeedItem extends BlockItem {
    private final PlantVariant variant;

    public GeneticSeedItem(Block block, Item.Properties properties, PlantVariant variant) {
        // Antes ItemNameBlockItem: el nombre viene del item, no del bloque.
        super(block, properties.useItemDescriptionPrefix());
        this.variant = variant;
    }

    public PlantVariant variant() {
        return variant;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        GeneticsTooltip.append(stack, tooltip);
    }
}