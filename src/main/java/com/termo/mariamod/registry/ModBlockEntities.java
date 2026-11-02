package com.termo.mariamod.registry;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.block.entity.GeneticCropBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.Set;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MariaMod.MODID);

    /** Un solo tipo de BlockEntity para los cultivos de TODAS las variantes (se toman de ModBlocks.CROPS). */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneticCropBlockEntity>> GENETIC_CROP =
            BLOCK_ENTITIES.register("genetic_crop", () ->
                    new BlockEntityType<>(GeneticCropBlockEntity::new,
                            Set.copyOf(Arrays.asList(ModBlocks.cropBlocks()))));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}