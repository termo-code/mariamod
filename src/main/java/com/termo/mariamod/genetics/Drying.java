package com.termo.mariamod.genetics;

import com.termo.mariamod.config.BalanceConfig;
import com.termo.mariamod.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

// Secado pasivo del cogollo. El cogollo guarda el momento de cosecha (en ticks del mundo, redondeado hacia abajo)
// y "está seco" se calcula al mirarlo o usarlo. Así funciona en cofres, barriles y shulkers, sin ticks.
public final class Drying {
    private Drying() {}

    // Ticks que faltan para que se seque. 0 = ya está seco. Un cogollo sin fecha se considera seco.
    public static long remainingTicks(ItemStack stack, long gameTime) {
        Long harvestTime = stack.get(ModDataComponents.HARVEST_TIME.get());
        if (harvestTime == null) {
            return 0;
        }
        long elapsed = gameTime - harvestTime;
        return Math.max(0, BalanceConfig.DRYING_TICKS.get() - elapsed);
    }

    public static boolean isDry(ItemStack stack, long gameTime) {
        return remainingTicks(stack, gameTime) == 0;
    }

    // Línea del tooltip: "Seco" en verde, o el tiempo que falta en amarillo.
    public static Component statusLine(ItemStack stack, long gameTime) {
        long remaining = remainingTicks(stack, gameTime);
        if (remaining == 0) {
            return Component.translatable("tooltip.mariamod.dry").withStyle(ChatFormatting.GREEN);
        }
        long totalSeconds = (remaining + 19) / 20;
        String time = String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
        return Component.translatable("tooltip.mariamod.drying", time).withStyle(ChatFormatting.YELLOW);
    }
}