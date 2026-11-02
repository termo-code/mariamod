package com.termo.mariamod.crafting;

import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.GeneticsBreeder;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.genetics.Species;
import com.termo.mariamod.registry.ModDataComponents;
import com.termo.mariamod.registry.ModItems;
import com.termo.mariamod.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.termo.mariamod.crafting.SeedRecipeSupport.*;

// Infusión: Sustrato de efecto en el centro y 8 semillas de UNA variante de la línea híbrida = 1 semilla con ese efecto.
// No da resultado si el efecto ya está en alguna semilla o si se pasa del tope de efectos.
public class SeedInfusionRecipe extends CustomRecipe {
    public SeedInfusionRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return infuse(input, RandomSource.create()).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return infuse(input, RandomSource.create()).orElse(ItemStack.EMPTY);
    }

    private static Optional<ItemStack> infuse(CraftingInput input, RandomSource random) {
        if (!isFullGrid(input)) {
            return Optional.empty();
        }
        ItemStack center = input.getItem(CENTER_SLOT);
        if (!center.is(ModItems.EFFECT_SUBSTRATE.get())) {
            return Optional.empty();
        }
        ResourceLocation effect = center.get(ModDataComponents.SUBSTRATE_EFFECT.get());
        if (effect == null) {
            return Optional.empty();
        }
        List<ItemStack> seeds = seedsAround(input);
        if (seeds == null) {
            return Optional.empty();
        }
        PlantVariant variant = commonVariant(seeds);
        if (variant == null || variant.species() != Species.HYBRID_LINE) {
            return Optional.empty();
        }

        List<Genetics> parents = parentsOf(seeds);
        for (Genetics parent : parents) {
            if (parent.effects().contains(effect)) {
                return Optional.empty();
            }
        }
        // El efecto nuevo se suma a una de las semillas: así entra en la unión de efectos y el criador controla el tope.
        parents.set(0, withEffect(parents.get(0), effect));

        Optional<Genetics> child = GeneticsBreeder.breed(parents, freezesRisk(variant), random);
        if (child.isEmpty()) {
            return Optional.empty();
        }
        ItemStack result = new ItemStack(ModItems.SEEDS.get(variant).get());
        result.set(ModDataComponents.GENETICS.get(), child.get());
        return Optional.of(result);
    }

    private static Genetics withEffect(Genetics genetics, ResourceLocation effect) {
        List<ResourceLocation> effects = new ArrayList<>(genetics.effects());
        effects.add(effect);
        return new Genetics(genetics.potency(), genetics.durationLevel(), genetics.badTripRisk(), List.copyOf(effects));
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipeSerializers.SEED_INFUSION.get();
    }

}