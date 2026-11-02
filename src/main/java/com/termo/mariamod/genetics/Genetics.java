package com.termo.mariamod.genetics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * Genoma de una planta, semilla o cogollo. Inmutable: para cambiar algo se crea una copia nueva.
 * La duración se guarda como NIVEL (1 a 3). Los segundos de cada nivel están en BalanceConfig.
 */
public record Genetics(int potency, int durationLevel, float badTripRisk, List<ResourceLocation> effects) {

    // Límites estructurales del diseño. Los números de balance viven en BalanceConfig.
    public static final int MIN_POTENCY = 1;
    public static final int MAX_POTENCY = 3;
    public static final int MIN_DURATION_LEVEL = 1;
    public static final int MAX_DURATION_LEVEL = 3;
    public static final int MAX_EFFECTS = 5;
    public static final float MIN_RISK = 0.0f;
    public static final float MAX_RISK = 1.0f;

    /** Genética base provisoria. Más adelante la reemplazan las silvestres al azar. */
    public static final Genetics WILD = new Genetics(1, 1, 0.25f, List.of());

    // Codec para guardar en disco
    public static final Codec<Genetics> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(MIN_POTENCY, MAX_POTENCY).fieldOf("potency").forGetter(Genetics::potency),
            Codec.intRange(MIN_DURATION_LEVEL, MAX_DURATION_LEVEL).fieldOf("duration_level").forGetter(Genetics::durationLevel),
            Codec.floatRange(MIN_RISK, MAX_RISK).fieldOf("bad_trip_risk").forGetter(Genetics::badTripRisk),
            ResourceLocation.CODEC.listOf().fieldOf("effects").forGetter(Genetics::effects)
    ).apply(instance, Genetics::new));

    // StreamCodec para sincronizar entre servidor y cliente
    public static final StreamCodec<ByteBuf, Genetics> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Genetics::potency,
            ByteBufCodecs.VAR_INT, Genetics::durationLevel,
            ByteBufCodecs.FLOAT, Genetics::badTripRisk,
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), Genetics::effects,
            Genetics::new
    );
}