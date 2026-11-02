package com.termo.mariamod.block;

import com.termo.mariamod.block.entity.GeneticCropBlockEntity;
import com.termo.mariamod.config.BalanceConfig;
import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Base de todos los cultivos del mod: 3 etapas, polvo de hueso de a una, hitbox propia y genética guardada en el bloque. */
public abstract class GeneticCropBlock extends CropBlock implements EntityBlock {
    public static final int MAX_AGE = 2;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    /** Hitbox por etapa (índice = age). Alturas en píxeles. Debe tener MAX_AGE + 1 entradas. */
    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(0, 0, 0, 16, 5, 16),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(0, 0, 0, 16, 16, 16)
    };

    protected GeneticCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return 1;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[getAge(state)];
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GeneticCropBlockEntity(pos, state);
    }

    /** Al plantar: la genética de la semilla pasa al bloque. */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (level.getBlockEntity(pos) instanceof GeneticCropBlockEntity crop) {
            crop.setGenetics(stack.getOrDefault(ModDataComponents.GENETICS.get(), Genetics.WILD));
        }
    }

    // Suelos válidos para las plantas: tierra arada, tierra, pasto y bloque de nieve.
    public static boolean isValidSoil(BlockState state) {
        return state.is(Blocks.FARMLAND) || state.is(BlockTags.DIRT) || state.is(Blocks.SNOW_BLOCK);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return isValidSoil(state);
    }

    // El jugador solo puede plantar sobre tierra arada (húmeda, si la configuración lo pide).
    // Las plantas silvestres del mundo se ponen por código y siguen pudiendo vivir sobre pasto o tierra.
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState soil = context.getLevel().getBlockState(context.getClickedPos().below());
        // Permite colocar el cultivo si abajo hay tierra arada (seca o húmeda)
        if (!soil.is(Blocks.FARMLAND)) {
            return null;
        }
        return super.getStateForPlacement(context);
    }
}