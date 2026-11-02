package com.termo.mariamod.registry;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.crafting.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// Registro de los tipos de receta con código propio.
public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MariaMod.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SeedSelectionRecipe>> SEED_SELECTION =
            SERIALIZERS.register("seed_selection",
                    () -> new CustomRecipe.Serializer<>(SeedSelectionRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SeedHybridizationRecipe>> SEED_HYBRIDIZATION =
            SERIALIZERS.register("seed_hybridization",
                    () -> new CustomRecipe.Serializer<>(SeedHybridizationRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SeedInfusionRecipe>> SEED_INFUSION =
            SERIALIZERS.register("seed_infusion",
                    () -> new CustomRecipe.Serializer<>(SeedInfusionRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PuroRecipe>> PURO =
            SERIALIZERS.register("puro",
                    () -> new CustomRecipe.Serializer<>(PuroRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PrimordialRecipe>> PRIMORDIAL =
            SERIALIZERS.register("primordial",
                    () -> new CustomRecipe.Serializer<>(PrimordialRecipe::new));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}