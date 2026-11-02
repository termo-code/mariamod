package com.termo.mariamod.item;

import com.termo.mariamod.genetics.Drying;
import com.termo.mariamod.genetics.GeneticsTooltip;
import com.termo.mariamod.genetics.PlantVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

// Cogollo de una variante: muestra su estado de secado y su genética en el tooltip.
public class GeneticBudItem extends Item {
    private final PlantVariant variant;

    public GeneticBudItem(Properties properties, PlantVariant variant) {
        super(properties);
        this.variant = variant;
    }

    public PlantVariant variant() {
        return variant;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        Level level = context.level();
        if (level != null) {
            tooltip.add(Drying.statusLine(stack, level.getGameTime()));
        }
        GeneticsTooltip.append(stack, tooltip);
    }
}