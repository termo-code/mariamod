package com.termo.mariamod.registry;

import com.termo.mariamod.MariaMod;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

/** Registro de items. Semillas y cogollos se crean solos: uno de cada por variante (ids: nombre_seeds y nombre_bud). */
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MariaMod.MODID);

    public static final DeferredItem<Item> VOLATILE_SUBSTRATE =
            ITEMS.registerSimpleItem("volatile_substrate", new Item.Properties());

    public static final DeferredItem<Item> EFFECT_SUBSTRATE = ITEMS.register("effect_substrate",
            () -> new EffectSubstrateItem(new Item.Properties()));

    public static final DeferredItem<Item> PRIMORDIAL_SUBSTRATE = ITEMS.registerSimpleItem(
            "primordial_substrate", new Item.Properties().rarity(Rarity.EPIC));

    public static final DeferredItem<Item> TAB_ICON = ITEMS.registerSimpleItem("tab_icon", new Item.Properties());

    public static final DeferredItem<Item> SEED_BAG = ITEMS.register("seed_bag",
            () -> new SeedBagItem(new Item.Properties().stacksTo(1)
                    .component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)));

    public static final DeferredItem<Item> PURO = ITEMS.register("puro",
            () -> new PuroItem(new Item.Properties()));

    public static final Map<PlantVariant, DeferredItem<Item>> SEEDS = new EnumMap<>(PlantVariant.class);
    public static final Map<PlantVariant, DeferredItem<Item>> BUDS = new EnumMap<>(PlantVariant.class);

    static {
        for (PlantVariant variant : PlantVariant.values()) {
            DeferredItem<Item> seeds = ITEMS.register(variant.seedsId(),
                    () -> new GeneticSeedItem(ModBlocks.CROPS.get(variant).get(), geneticProperties(variant), variant));
            SEEDS.put(variant, seeds);

            DeferredItem<Item> bud = ITEMS.register(variant.budId(),
                    () -> new GeneticBudItem(geneticProperties(variant), variant));
            BUDS.put(variant, bud);
        }
    }

    private static Item.Properties geneticProperties(PlantVariant variant) {
        return new Item.Properties().component(ModDataComponents.GENETICS.get(), variant.defaultGenetics());
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}