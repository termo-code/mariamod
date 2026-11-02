package com.termo.mariamod.worldgen;

import com.termo.mariamod.block.GeneticCropBlock;
import com.termo.mariamod.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

// Genera un parche de plantas silvestres maduras alrededor de un punto. La genética se sortea después, en la planta.
public class WildPatchFeature extends Feature<WildPatchConfig> {
    public WildPatchFeature() {
        super(WildPatchConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<WildPatchConfig> context) {
        WildPatchConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        BlockState state = ModBlocks.CROPS.get(config.variant()).get().defaultBlockState()
                .setValue(GeneticCropBlock.AGE, GeneticCropBlock.MAX_AGE);

        int placed = 0;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < config.tries(); i++) {
            pos.setWithOffset(origin,
                    random.nextInt(config.spread() + 1) - random.nextInt(config.spread() + 1),
                    random.nextInt(3) - random.nextInt(3),
                    random.nextInt(config.spread() + 1) - random.nextInt(config.spread() + 1));
            if (pos.getY() < config.minY()) {
                continue;
            }
            if (level.isEmptyBlock(pos) && GeneticCropBlock.isValidSoil(level.getBlockState(pos.below()))) {
                level.setBlock(pos, state, 2);
                placed++;
            }
        }
        return placed > 0;
    }
}