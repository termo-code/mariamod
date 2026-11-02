package com.termo.mariamod.crafting;

import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.GeneticsBreeder;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.registry.ModDataComponents;
import com.termo.mariamod.registry.ModItems;
import com.termo.mariamod.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.termo.mariamod.crafting.SeedRecipeSupport.*;

// Hibridación: Sustrato Volátil en el centro y 8 semillas de DOS variantes que se puedan cruzar (al menos una de cada una).
// El árbol de cruces sale de Species/PlantVariant. Cualquier proporción vale.
// La Ruderalis pierde su efecto al hibridar y la Híbrida Plus nace sin efectos.
public class SeedHybridizationRecipe extends CustomRecipe {
    public SeedHybridizationRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return hybridize(input, RandomSource.create()).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return hybridize(input, RandomSource.create()).orElse(ItemStack.EMPTY);
    }

    private static Optional<ItemStack> hybridize(CraftingInput input, RandomSource random) {
        if (!isFullGrid(input)) {
            return Optional.empty();
        }
        if (!input.getItem(CENTER_SLOT).is(ModItems.VOLATILE_SUBSTRATE.get())) {
            return Optional.empty();
        }
        List<ItemStack> seeds = seedsAround(input);
        if (seeds == null) {
            return Optional.empty();
        }

        // Tiene que haber exactamente dos variantes distintas, y se tienen que poder cruzar.
        Set<PlantVariant> present = EnumSet.noneOf(PlantVariant.class);
        for (ItemStack seed : seeds) {
            present.add(variantOf(seed));
        }
        if (present.size() != 2) {
            return Optional.empty();
        }
        List<PlantVariant> pair = new ArrayList<>(present);
        Optional<PlantVariant> resultVariant = PlantVariant.crossResult(pair.get(0), pair.get(1));
        if (resultVariant.isEmpty()) {
            return Optional.empty();
        }

        // Genética de los padres, quitando los efectos que no se heredan.
        List<Genetics> parents = new ArrayList<>();
        for (ItemStack seed : seeds) {
            parents.add(inherit(resultVariant.get(), variantOf(seed), parentsOf(List.of(seed)).get(0)));
        }
        Optional<Genetics> child = GeneticsBreeder.breed(parents, false, random);
        if (child.isEmpty()) {
            return Optional.empty();
        }

        ItemStack result = new ItemStack(ModItems.SEEDS.get(resultVariant.get()).get());
        result.set(ModDataComponents.GENETICS.get(), child.get());
        return Optional.of(result);
    }

    // La Ruderalis no pasa su efecto, y la Híbrida Plus nace sin efectos.
    private static Genetics inherit(PlantVariant result, PlantVariant parentVariant, Genetics genetics) {
        boolean keepsEffects = result != PlantVariant.HYBRID_PLUS && parentVariant != PlantVariant.RUDERALIS_WILD;
        if (keepsEffects) {
            return genetics;
        }
        return new Genetics(genetics.potency(), genetics.durationLevel(), genetics.badTripRisk(), List.of());
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipeSerializers.SEED_HYBRIDIZATION.get();
    }

}