package com.termo.mariamod.item;

import com.termo.mariamod.registry.ModDataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// Sustrato Volátil con un efecto de poción guardado como dato. Su nombre incluye el efecto.
public class EffectSubstrateItem extends Item {
    public EffectSubstrateItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        ResourceLocation effectId = stack.get(ModDataComponents.SUBSTRATE_EFFECT.get());
        if (effectId == null) {
            return super.getName(stack);
        }
        Component effectName = BuiltInRegistries.MOB_EFFECT.getOptional(effectId)
                .map(effect -> Component.translatable(effect.getDescriptionId()))
                .orElse(Component.literal(effectId.toString()));
        return Component.translatable("item.mariamod.effect_substrate.named", effectName);
    }
}