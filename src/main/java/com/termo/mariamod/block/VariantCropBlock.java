package com.termo.mariamod.block;

import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.registry.ModItems;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

public class VariantCropBlock extends GeneticCropBlock {
    public static final MapCodec<VariantCropBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            PlantVariant.CODEC.fieldOf("variant").forGetter(VariantCropBlock::variant)
    ).apply(instance, VariantCropBlock::new));

    private final PlantVariant variant;

    public VariantCropBlock(Properties properties, PlantVariant variant) {
        super(properties);
        this.variant = variant;
    }

    public PlantVariant variant() {
        return variant;
    }

    @Override
    public MapCodec<? extends CropBlock> codec() {
        return CODEC;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ModItems.SEEDS.get(variant).get();
    }
}