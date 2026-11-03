package com.termo.mariamod.registry;

import com.termo.mariamod.MariaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    private ModTags() {}

    public static final TagKey<Item> LIGHTERS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MariaMod.MODID, "lighters"));
}