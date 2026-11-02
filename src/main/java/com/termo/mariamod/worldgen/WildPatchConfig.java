package com.termo.mariamod.worldgen;

import com.termo.mariamod.genetics.PlantVariant;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

// Parámetros de un parche de plantas silvestres (se leen del JSON de configured_feature).
// variant: qué planta. tries: intentos por parche. spread: dispersión en bloques. minY: altura mínima.
public record WildPatchConfig(PlantVariant variant, int tries, int spread, int minY) implements FeatureConfiguration {
    public static final Codec<WildPatchConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlantVariant.CODEC.fieldOf("variant").forGetter(WildPatchConfig::variant),
            Codec.intRange(1, 64).fieldOf("tries").forGetter(WildPatchConfig::tries),
            Codec.intRange(0, 16).fieldOf("spread").forGetter(WildPatchConfig::spread),
            Codec.INT.optionalFieldOf("min_y", -64).forGetter(WildPatchConfig::minY)
    ).apply(instance, WildPatchConfig::new));
}