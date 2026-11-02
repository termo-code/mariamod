package com.termo.mariamod.registry;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.block.VariantCropBlock;
import com.termo.mariamod.genetics.PlantVariant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
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
            DeferredBlock<VariantCropBlock> crop = BLOCKS.registerBlock(variant.cropId(),
                    props -> new VariantCropBlock(props, variant),
                    cropProperties());
            CROPS.put(variant, crop);
        }
    }

    // Mismas propiedades que el trigo, escritas a mano (sin ofFullCopy, que arrastra lambdas con la propiedad age de vanilla).
    private static BlockBehaviour.Properties cropProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY);
    }

    /** Todos los bloques de cultivo (los usa el registro de BlockEntities). */
    public static Block[] cropBlocks() {
        return CROPS.values().stream().map(DeferredBlock::get).toArray(Block[]::new);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}