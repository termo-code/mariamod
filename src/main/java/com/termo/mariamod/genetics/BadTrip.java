package com.termo.mariamod.genetics;

import com.termo.mariamod.config.BalanceConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

// Efectos negativos del bad trip. Son fijos (nivel 1) y sus duraciones salen de la configuración.
// La línea híbrida (Plus, Máxima y Primordial) suma Wither: tiene el peor bad trip.
public final class BadTrip {
    private BadTrip() {}

    public static void apply(LivingEntity user, Species species) {
        user.addEffect(new MobEffectInstance(MobEffects.CONFUSION,
                ticks(BalanceConfig.BAD_TRIP_NAUSEA_SECONDS.get()), 0));
        user.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,
                ticks(BalanceConfig.BAD_TRIP_WEAKNESS_SECONDS.get()), 0));
        if (species == Species.HYBRID_LINE) {
            user.addEffect(new MobEffectInstance(MobEffects.WITHER,
                    ticks(BalanceConfig.BAD_TRIP_WITHER_SECONDS.get()), 0));
        }
    }

    private static int ticks(int seconds) {
        return seconds * 20;
    }
}