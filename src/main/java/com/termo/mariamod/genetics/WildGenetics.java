package com.termo.mariamod.genetics;

import com.termo.mariamod.config.BalanceConfig;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.function.IntUnaryOperator;

// Sortea la genética de una planta silvestre. Se usa UNA vez, cuando la planta se genera, y el resultado se guarda en ella.
// No incluye efectos de poción: los efectos naturales salen de la especie (NaturalEffects).
public final class WildGenetics {
    private WildGenetics() {}

    public static Genetics roll(RandomSource random) {
        int potency = pickLevel(random, Genetics.MAX_POTENCY, BalanceConfig::wildPotencyWeight);
        int durationLevel = pickLevel(random, Genetics.MAX_DURATION_LEVEL, BalanceConfig::wildDurationWeight);

        float min = BalanceConfig.WILD_RISK_MIN.get().floatValue();
        float max = BalanceConfig.WILD_RISK_MAX.get().floatValue();
        if (max < min) {
            float swap = min;
            min = max;
            max = swap;
        }
        float risk = min + random.nextFloat() * (max - min);
        risk = Math.round(risk * 1000.0f) / 1000.0f; // 3 decimales

        return new Genetics(potency, durationLevel, risk, List.of());
    }

    // Elige un nivel de 1 a "levels" según sus pesos. Si todos los pesos son 0, devuelve 1.
    private static int pickLevel(RandomSource random, int levels, IntUnaryOperator weightOf) {
        int total = 0;
        for (int n = 1; n <= levels; n++) {
            total += weightOf.applyAsInt(n);
        }
        if (total <= 0) {
            return 1;
        }
        int roll = random.nextInt(total);
        for (int n = 1; n <= levels; n++) {
            roll -= weightOf.applyAsInt(n);
            if (roll < 0) {
                return n;
            }
        }
        return levels;
    }
}