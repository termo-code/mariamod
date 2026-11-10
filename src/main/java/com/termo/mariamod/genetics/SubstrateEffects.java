package com.termo.mariamod.genetics;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Stream;

public final class SubstrateEffects {
    private SubstrateEffects() {}

    public static final List<ResourceLocation> ALL = Stream.of(
            "speed", "haste", "strength", "instant_health", "jump_boost", "regeneration",
            "resistance", "fire_resistance", "water_breathing", "invisibility", "night_vision",
            "health_boost", "absorption", "saturation", "luck", "slow_falling",
            "conduit_power", "dolphins_grace", "levitation"
    ).map(ResourceLocation::withDefaultNamespace).toList();
}