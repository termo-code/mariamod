package com.termo.mariamod.genetics;

import com.termo.mariamod.config.BalanceConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

// Selección genética: junta las semillas padre y produce UNA semilla hija que tiende a mejorar.
// Es una clase pura: no toca el mundo, ni ítems, ni bloques. Los números de balance están en BalanceConfig.
public final class GeneticsBreeder {
    private GeneticsBreeder() {}

    // Devuelve la semilla hija, o vacío si no hay resultado (por ejemplo, más de 5 efectos juntos).
    // freezeRisk = true congela el riesgo (para Híbrida Máxima y Primordial).
    public static Optional<Genetics> breed(List<Genetics> parents, boolean freezeRisk, RandomSource random) {
        if (parents.isEmpty()) {
            return Optional.empty();
        }

        // Efectos: unión de los efectos de todos los padres. Si pasan el tope, no hay resultado.
        LinkedHashSet<ResourceLocation> union = new LinkedHashSet<>();
        for (Genetics parent : parents) {
            union.addAll(parent.effects());
        }
        if (union.size() > Genetics.MAX_EFFECTS) {
            return Optional.empty();
        }
        List<ResourceLocation> effects = List.copyOf(union);

        // Promedio y mejor valor de cada rasgo entre los padres.
        float sumPotency = 0.0f;
        float sumDuration = 0.0f;
        float sumRisk = 0.0f;
        int bestPotency = Genetics.MIN_POTENCY;
        int bestDuration = Genetics.MIN_DURATION_LEVEL;
        float bestRisk = Genetics.MAX_RISK;
        for (Genetics parent : parents) {
            sumPotency += parent.potency();
            sumDuration += parent.durationLevel();
            sumRisk += parent.badTripRisk();
            bestPotency = Math.max(bestPotency, parent.potency());
            bestDuration = Math.max(bestDuration, parent.durationLevel());
            bestRisk = Math.min(bestRisk, parent.badTripRisk());
        }
        float count = parents.size();

        int potency = improvedLevel(sumPotency / count, bestPotency,
                Genetics.MIN_POTENCY, Genetics.MAX_POTENCY, random);
        int durationLevel = improvedLevel(sumDuration / count, bestDuration,
                Genetics.MIN_DURATION_LEVEL, Genetics.MAX_DURATION_LEVEL, random);
        float risk = nextRisk(sumRisk / count, bestRisk, !effects.isEmpty(), freezeRisk, random);

        return Optional.of(new Genetics(potency, durationLevel, risk, effects));
    }

    // Nivel (potencia o duración): promedio, tirón hacia el mejor padre y un paso aleatorio siempre a favor.
    private static int improvedLevel(float mean, int best, int min, int max, RandomSource random) {
        float pull = BalanceConfig.PULL_TO_BEST.get().floatValue();
        float step = random.nextFloat() * BalanceConfig.LEVEL_MAX_STEP.get().floatValue();
        float value = mean + (best - mean) * pull + step;
        return Mth.clamp(roundRandomly(value, random), min, max);
    }

    // Riesgo: sin efectos tiende a bajar, con efectos tiende a subir, y nunca baja del piso.
    private static float nextRisk(float meanRisk, float bestRisk, boolean hasEffects,
                                  boolean freezeRisk, RandomSource random) {
        if (freezeRisk) {
            return bestRisk;
        }
        float risk;
        if (hasEffects) {
            risk = meanRisk + random.nextFloat() * BalanceConfig.RISK_WORSEN_STEP.get().floatValue();
        } else {
            float pull = BalanceConfig.PULL_TO_BEST.get().floatValue();
            float target = meanRisk + (bestRisk - meanRisk) * pull;
            risk = target - random.nextFloat() * BalanceConfig.RISK_MAX_STEP.get().floatValue();
        }
        risk = Mth.clamp(risk, BalanceConfig.RISK_FLOOR.get().floatValue(), Genetics.MAX_RISK);
        return Math.round(risk * 1000.0f) / 1000.0f; // 3 decimales, para que los datos queden legibles
    }

    // Redondea al azar según la parte decimal: 1.5 da 1 o 2 con 50% cada uno.
    private static int roundRandomly(float value, RandomSource random) {
        int floor = (int) Math.floor(value);
        return floor + (random.nextFloat() < value - floor ? 1 : 0);
    }
}