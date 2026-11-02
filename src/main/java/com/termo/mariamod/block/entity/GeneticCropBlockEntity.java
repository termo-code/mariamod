package com.termo.mariamod.block.entity;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.config.BalanceConfig;
import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.WildGenetics;
import com.termo.mariamod.registry.ModBlockEntities;
import com.termo.mariamod.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// Guarda la genética de una planta colocada. Es genérico: lo usan todas las variantes.
public class GeneticCropBlockEntity extends BlockEntity {
    private static final String TAG_GENETICS = "genetics";

    // null = planta generada por el mundo que todavía no tiene genética. Se sortea la primera vez que hace falta y se guarda.
    @Nullable
    private Genetics genetics;

    public GeneticCropBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GENETIC_CROP.get(), pos, state);
    }

    public Genetics getGenetics() {
        if (genetics == null) {
            Level currentLevel = getLevel();
            genetics = WildGenetics.roll(currentLevel != null ? currentLevel.getRandom() : RandomSource.create());
            setChanged();
        }
        return genetics;
    }

    public void setGenetics(Genetics genetics) {
        this.genetics = genetics;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (genetics == null) {
            return;
        }
        Genetics.CODEC.encodeStart(NbtOps.INSTANCE, genetics)
                .resultOrPartial(err -> MariaMod.LOGGER.error("No se pudo guardar la genética: {}", err))
                .ifPresent(encoded -> tag.put(TAG_GENETICS, encoded));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_GENETICS)) {
            Genetics.CODEC.parse(NbtOps.INSTANCE, tag.get(TAG_GENETICS))
                    .resultOrPartial(err -> MariaMod.LOGGER.error("No se pudo leer la genética: {}", err))
                    .ifPresent(loaded -> this.genetics = loaded);
        }
    }

    // Expone la genética y la fecha de cosecha como componentes, para que la loot table los copie a los drops.
    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(ModDataComponents.GENETICS.get(), getGenetics());
        Level currentLevel = getLevel();
        if (currentLevel != null) {
            long bucket = BalanceConfig.HARVEST_TIME_BUCKET_TICKS.get();
            builder.set(ModDataComponents.HARVEST_TIME.get(), currentLevel.getGameTime() / bucket * bucket);
        }
    }

    // Camino inverso: aplica la genética de un ítem al bloque.
    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        Genetics fromItem = input.get(ModDataComponents.GENETICS.get());
        if (fromItem != null) {
            this.genetics = fromItem;
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove(TAG_GENETICS);
    }
}