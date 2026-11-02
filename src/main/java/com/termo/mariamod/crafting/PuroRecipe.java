package com.termo.mariamod.crafting;

import com.termo.mariamod.genetics.Drying;
import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.item.GeneticBudItem;
import com.termo.mariamod.registry.ModDataComponents;
import com.termo.mariamod.registry.ModItems;
import com.termo.mariamod.registry.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// Puro: 1 papel + 1 cogollo SECO (sin forma fija). El puro copia la genética y la variante del cogollo.
public class PuroRecipe extends CustomRecipe {
    public PuroRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return craft(input, level).isPresent();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        // matches ya comprobó que el cogollo está seco; acá no hay mundo para volver a medirlo.
        return craft(input, null).orElse(ItemStack.EMPTY);
    }

    private static Optional<ItemStack> craft(CraftingInput input, @Nullable Level level) {
        if (input.ingredientCount() != 2) {
            return Optional.empty();
        }
        boolean hasPaper = false;
        ItemStack bud = ItemStack.EMPTY;
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(Items.PAPER) && !hasPaper) {
                hasPaper = true;
            } else if (stack.getItem() instanceof GeneticBudItem && bud.isEmpty()) {
                bud = stack;
            } else {
                return Optional.empty();
            }
        }
        if (!hasPaper || bud.isEmpty()) {
            return Optional.empty();
        }
        if (level != null && !Drying.isDry(bud, level.getGameTime())) {
            return Optional.empty();
        }

        PlantVariant variant = ((GeneticBudItem) bud.getItem()).variant();
        Genetics genetics = bud.get(ModDataComponents.GENETICS.get());

        ItemStack puro = new ItemStack(ModItems.PURO.get());
        puro.set(ModDataComponents.GENETICS.get(), genetics != null ? genetics : Genetics.WILD);
        puro.set(ModDataComponents.VARIANT.get(), variant.id());
        return Optional.of(puro);
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipeSerializers.PURO.get();
    }
}