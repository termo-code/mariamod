package com.termo.mariamod.registry;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.worldgen.WildPatchFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, MariaMod.MODID);

    public static final DeferredHolder<Feature<?>, WildPatchFeature> WILD_PATCH =
            FEATURES.register("wild_patch", WildPatchFeature::new);

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}