package com.termo.mariamod;

import com.termo.mariamod.config.BalanceConfig;
import com.termo.mariamod.genetics.PlantVariant;
import com.termo.mariamod.genetics.SubstrateEffects;
import com.termo.mariamod.registry.ModBlockEntities;
import com.termo.mariamod.registry.ModBlocks;
import com.termo.mariamod.registry.ModDataComponents;
import com.termo.mariamod.registry.ModFeatures;
import com.termo.mariamod.registry.ModItems;
import com.termo.mariamod.registry.ModRecipeSerializers;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

// Clase principal del mod: solo conecta los registros y arma la pestaña creativa.
@Mod(MariaMod.MODID)
public class MariaMod {
    public static final String MODID = "mariamod";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Pestaña creativa del mod: semillas, cogollos, puro, saco y todos los sustratos.
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MARIA_TAB = CREATIVE_MODE_TABS.register("maria_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.mariamod"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.SEEDS.get(PlantVariant.SATIVA_WILD).get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        ModItems.SEEDS.values().forEach(seed -> output.accept(seed.get()));
                        ModItems.BUDS.values().forEach(bud -> output.accept(bud.get()));
                        output.accept(ModItems.PURO.get());
                        output.accept(ModItems.SEED_BAG.get());
                        output.accept(ModItems.VOLATILE_SUBSTRATE.get());
                        output.accept(ModItems.PRIMORDIAL_SUBSTRATE.get());
                        for (ResourceLocation effectId : SubstrateEffects.ALL) {
                            ItemStack substrate = new ItemStack(ModItems.EFFECT_SUBSTRATE.get());
                            substrate.set(ModDataComponents.SUBSTRATE_EFFECT.get(), effectId);
                            output.accept(substrate);
                        }
                    }).build());

    public MariaMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);
        ModFeatures.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, BalanceConfig.SPEC, "mariamod-balance.toml");
    }
}