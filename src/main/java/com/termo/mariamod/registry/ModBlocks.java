package com.termo.mariamod.registry;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.block.VariantCropBlock;
import com.termo.mariamod.genetics.PlantVariant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

/** Registro de bloques. Los cultivos se crean solos: uno por cada variante de PlantVariant (id: nombre_crop). */
public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MariaMod.MODID);

    public static final Map<PlantVariant, DeferredBlock<VariantCropBlock>> CROPS = new EnumMap<>(PlantVariant.class);

    static {
        for (PlantVariant variant : PlantVariant.values()) {
            DeferredBlock<VariantCropBlock> crop = BLOCKS.register(variant.cropId(),
                    () -> new VariantCropBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHEAT), variant));
            CROPS.put(variant, crop);
        }
    }

    /** Todos los bloques de cultivo (los usa el registro de BlockEntities). */
    public static Block[] cropBlocks() {
        return CROPS.values().stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}