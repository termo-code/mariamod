package com.termo.mariamod.genetics;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Stream;

// Los 30 efectos de poción que se pueden infundir. Es la lista única del mod:
// la pestaña creativa y, más adelante, la infusión leen de acá.
public final class SubstrateEffects {
    private SubstrateEffects() {}

    public static final List<ResourceLocation> ALL = Stream.of(
            // Positivos (18)
            "speed", "haste", "strength", "instant_health", "jump_boost", "regeneration",
            "resistance", "fire_resistance", "water_breathing", "invisibility", "night_vision",
            "health_boost", "absorption", "saturation", "luck", "slow_falling",
            "conduit_power", "dolphins_grace",
            // Negativos (12)
            "slowness", "mining_fatigue", "instant_damage", "nausea", "blindness", "hunger",
            "weakness", "poison", "wither", "levitation", "unluck", "darkness"
    ).map(ResourceLocation::withDefaultNamespace).toList();
}