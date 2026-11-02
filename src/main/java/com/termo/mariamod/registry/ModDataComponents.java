package com.termo.mariamod.registry;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.genetics.Genetics;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Registro de los Data Components del mod. */
public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MariaMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Genetics>> GENETICS =
            DATA_COMPONENTS.register("genetics", () -> DataComponentType.<Genetics>builder()
                    .persistent(Genetics.CODEC)
                    .networkSynchronized(Genetics.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> SUBSTRATE_EFFECT =
            DATA_COMPONENTS.register("substrate_effect", () -> DataComponentType.<ResourceLocation>builder()
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> VARIANT =
            DATA_COMPONENTS.register("variant", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> HARVEST_TIME =
            DATA_COMPONENTS.register("harvest_time", () -> DataComponentType.<Long>builder()
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
                    .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}