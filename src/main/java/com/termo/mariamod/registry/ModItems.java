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

    public static final DeferredItem<Item> EFFECT_SUBSTRATE =
            ITEMS.registerItem("effect_substrate", EffectSubstrateItem::new, new Item.Properties());

    public static final DeferredItem<Item> PRIMORDIAL_SUBSTRATE = ITEMS.registerSimpleItem(
            "primordial_substrate", new Item.Properties().rarity(Rarity.EPIC));

    public static final DeferredItem<Item> SEED_BAG = ITEMS.registerItem("seed_bag",
            SeedBagItem::new,
            new Item.Properties().stacksTo(1)
                    .component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY));

    public static final DeferredItem<Item> PURO =
            ITEMS.registerItem("puro", PuroItem::new, new Item.Properties());

    public static final Map<PlantVariant, DeferredItem<Item>> SEEDS = new EnumMap<>(PlantVariant.class);
    public static final Map<PlantVariant, DeferredItem<Item>> BUDS = new EnumMap<>(PlantVariant.class);

    static {
        for (PlantVariant variant : PlantVariant.values()) {
            DeferredItem<Item> seeds = ITEMS.registerItem(variant.seedsId(),
                    props -> new GeneticSeedItem(ModBlocks.CROPS.get(variant).get(),
                            withGenetics(props, variant), variant),
                    new Item.Properties());
            SEEDS.put(variant, seeds);

            DeferredItem<Item> bud = ITEMS.registerItem(variant.budId(),
                    props -> new GeneticBudItem(withGenetics(props, variant), variant),
                    new Item.Properties());
            BUDS.put(variant, bud);
        }
    }

    // Se llama dentro de la función de registro (tarde), cuando el componente ya existe.
    private static Item.Properties withGenetics(Item.Properties props, PlantVariant variant) {
        return props.component(ModDataComponents.GENETICS.get(), variant.defaultGenetics());
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}