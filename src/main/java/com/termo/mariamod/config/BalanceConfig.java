package com.termo.mariamod.config;

import com.termo.mariamod.genetics.Genetics;
import com.termo.mariamod.genetics.Species;
import java.util.EnumMap;
import java.util.Map;
import net.neoforged.neoforge.common.ModConfigSpec;


public final class BalanceConfig {
    private BalanceConfig() {}

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ---------- [duration] ----------
    private static final ModConfigSpec.IntValue[] DURATION_SECONDS =
            new ModConfigSpec.IntValue[Genetics.MAX_DURATION_LEVEL];

    // ---------- [natural_effects] ----------
    private static final Map<Species, ModConfigSpec.ConfigValue<String>> NATURAL_EFFECTS = new EnumMap<>(Species.class);

    // ---------- [wild] ----------
    private static final ModConfigSpec.IntValue[] WILD_POTENCY_WEIGHTS = new ModConfigSpec.IntValue[Genetics.MAX_POTENCY];
    private static final ModConfigSpec.IntValue[] WILD_DURATION_WEIGHTS = new ModConfigSpec.IntValue[Genetics.MAX_DURATION_LEVEL];
    public static final ModConfigSpec.DoubleValue WILD_RISK_MIN;
    public static final ModConfigSpec.DoubleValue WILD_RISK_MAX;
    public static final ModConfigSpec.BooleanValue REQUIRE_MOIST_FARMLAND;

    // ---------- [breeding] ----------
    public static final ModConfigSpec.DoubleValue PULL_TO_BEST;
    public static final ModConfigSpec.DoubleValue LEVEL_MAX_STEP;
    public static final ModConfigSpec.DoubleValue RISK_MAX_STEP;
    public static final ModConfigSpec.DoubleValue RISK_WORSEN_STEP;
    public static final ModConfigSpec.DoubleValue RISK_FLOOR;

    // ---------- [smoking] ----------
    public static final ModConfigSpec.IntValue SMOKE_USE_TICKS;
    public static final ModConfigSpec.IntValue LIGHTER_DAMAGE;

    // ---------- [drying] ----------
    public static final ModConfigSpec.IntValue DRYING_TICKS;
    public static final ModConfigSpec.IntValue HARVEST_TIME_BUCKET_TICKS;

    // ---------- [bad_trip] ----------
    public static final ModConfigSpec.IntValue BAD_TRIP_NAUSEA_SECONDS;
    public static final ModConfigSpec.IntValue BAD_TRIP_WEAKNESS_SECONDS;
    public static final ModConfigSpec.IntValue BAD_TRIP_WITHER_SECONDS;

    // ---------- [tooltip] ----------
    public static final ModConfigSpec.DoubleValue RISK_LOW;
    public static final ModConfigSpec.DoubleValue RISK_MEDIUM;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Duración de los efectos según el nivel de duración de la planta (1 a 3)").push("duration");
        // Una entrada por nivel (la cantidad tiene que coincidir con Genetics.MAX_DURATION_LEVEL)
        int[] defaultSeconds = {30, 60, 90};
        for (int level = 1; level <= Genetics.MAX_DURATION_LEVEL; level++) {
            DURATION_SECONDS[level - 1] = BUILDER
                    .comment("Segundos que dura un efecto con duración nivel " + level)
                    .defineInRange("level_" + level + "_seconds", defaultSeconds[level - 1], 1, 3600);
        }
        BUILDER.pop();

        BUILDER.comment("Selección genética: cómo mejora una semilla en cada generación").push("breeding");
        PULL_TO_BEST = BUILDER
                .comment("Cuánto se acerca el resultado al mejor padre (0.0 = al promedio, 1.0 = al mejor)")
                .defineInRange("pull_to_best", 0.7, 0.0, 1.0);
        LEVEL_MAX_STEP = BUILDER
                .comment("Paso aleatorio máximo a favor de potencia y duración por generación (0.2 = hasta +0.2 de nivel)")
                .defineInRange("level_max_step", 0.2, 0.0, 1.0);
        RISK_MAX_STEP = BUILDER
                .comment("Cuánto baja el riesgo de más por generación, sin efectos de poción (0.02 = hasta 2 puntos)")
                .defineInRange("risk_max_step", 0.02, 0.0, 1.0);
        RISK_WORSEN_STEP = BUILDER
                .comment("Cuánto sube el riesgo por generación cuando la semilla tiene efectos de poción (0.03 = hasta 3 puntos)")
                .defineInRange("risk_worsen_step", 0.03, 0.0, 1.0);
        RISK_FLOOR = BUILDER
                .comment("Riesgo mínimo alcanzable con Sustrato Volátil (0.05 = 5%)")
                .defineInRange("risk_floor", 0.05, 0.0, 1.0);
        BUILDER.pop();

        BUILDER.comment("Efectos naturales de cada especie: ids de efectos separados por coma. Vacío = ninguno.",
                "Se aplican al fumar, con la potencia y la duración de la planta. No cuentan para el tope de 5 efectos.").push("natural_effects");
        for (Species species : Species.values()) {
            NATURAL_EFFECTS.put(species, BUILDER
                    .comment("Efectos naturales de " + species.id())
                    .define(species.id(), defaultNaturalEffects(species)));
        }
        BUILDER.pop();

        BUILDER.comment("Secado del cogollo (pasivo, en cualquier contenedor o inventario)").push("drying");
        DRYING_TICKS = BUILDER
                .comment("Ticks que tarda un cogollo en secarse (20 ticks = 1 segundo). 48000 = 2 días de Minecraft")
                .defineInRange("drying_ticks", 48000, 20, 10_000_000);
        HARVEST_TIME_BUCKET_TICKS = BUILDER
                .comment("La fecha de cosecha se redondea hacia abajo a este tramo, para que los cogollos de una misma cosecha se apilen (1200 = 1 minuto)")
                .defineInRange("harvest_time_bucket_ticks", 1200, 1, 1_000_000);
        BUILDER.pop();

        BUILDER.comment("Fumar el puro").push("smoking");
        SMOKE_USE_TICKS = BUILDER
                .comment("Ticks que dura fumar (20 ticks = 1 segundo). Comer dura 32, y fumar dura el doble")
                .defineInRange("use_ticks", 64, 8, 1200);
        LIGHTER_DAMAGE = BUILDER
                .comment("Durabilidad que gasta el encendedor por cada puro fumado (el mechero de pedernal tiene 64)")
                .defineInRange("lighter_damage", 4, 1, 64);
        BUILDER.pop();

        BUILDER.comment("Efectos negativos del bad trip. Son fijos, de nivel 1, y no dependen de la genética").push("bad_trip");
        BAD_TRIP_NAUSEA_SECONDS = BUILDER
                .comment("Segundos de Náuseas (todas las especies)")
                .defineInRange("nausea_seconds", 60, 1, 3600);
        BAD_TRIP_WEAKNESS_SECONDS = BUILDER
                .comment("Segundos de Debilidad (todas las especies)")
                .defineInRange("weakness_seconds", 60, 1, 3600);
        BAD_TRIP_WITHER_SECONDS = BUILDER
                .comment("Segundos de Wither (solo la línea híbrida: Plus, Máxima y Primordial)")
                .defineInRange("wither_seconds", 30, 1, 3600);
        BUILDER.pop();

        BUILDER.comment("Genética al azar de las plantas silvestres. Los pesos son relativos: 60/35/5 = 60%, 35% y 5%").push("wild");
        int[] potencyWeights = {60, 35, 5};   // una entrada por nivel de potencia
        int[] durationWeights = {50, 35, 15}; // una entrada por nivel de duración
        for (int n = 1; n <= Genetics.MAX_POTENCY; n++) {
            WILD_POTENCY_WEIGHTS[n - 1] = BUILDER
                    .comment("Peso de la potencia nivel " + n)
                    .defineInRange("potency_weight_" + n, potencyWeights[n - 1], 0, 1000);
        }
        for (int n = 1; n <= Genetics.MAX_DURATION_LEVEL; n++) {
            WILD_DURATION_WEIGHTS[n - 1] = BUILDER
                    .comment("Peso de la duración nivel " + n)
                    .defineInRange("duration_weight_" + n, durationWeights[n - 1], 0, 1000);
        }
        WILD_RISK_MIN = BUILDER
                .comment("Riesgo mínimo de una silvestre (0.10 = 10%)")
                .defineInRange("risk_min", 0.10, 0.0, 1.0);
        WILD_RISK_MAX = BUILDER
                .comment("Riesgo máximo de una silvestre (1.00 = 100%)")
                .defineInRange("risk_max", 1.00, 0.0, 1.0);
        BUILDER.pop();

        BUILDER.comment("Plantación").push("planting");
        REQUIRE_MOIST_FARMLAND = BUILDER
                .comment("true = solo se puede plantar sobre tierra arada HUMEDA. false = tierra arada seca o humeda, como el trigo")
                .define("require_moist_farmland", true);
        BUILDER.pop();

        BUILDER.comment("Colores del riesgo de Bad Trip en el tooltip").push("tooltip");
        RISK_LOW = BUILDER
                .comment("Hasta este riesgo (0.0 a 1.0) se muestra en VERDE")
                .defineInRange("risk_low", 0.15, 0.0, 1.0);
        RISK_MEDIUM = BUILDER
                .comment("Hasta este riesgo se muestra en AMARILLO. Por encima, en ROJO")
                .defineInRange("risk_medium", 0.40, 0.0, 1.0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    /** Segundos que dura un efecto para un nivel de duración (1 a 3). */
    public static int durationSeconds(int durationLevel) {
        int level = Math.max(Genetics.MIN_DURATION_LEVEL, Math.min(durationLevel, Genetics.MAX_DURATION_LEVEL));
        return DURATION_SECONDS[level - 1].get();
    }

    // Valores por defecto de los efectos naturales de cada especie.
    private static String defaultNaturalEffects(Species species) {
        return switch (species) {
            case SATIVA_WILD, SATIVA_HYBRID -> "minecraft:speed,minecraft:haste";
            case INDICA_WILD, INDICA_HYBRID -> "minecraft:regeneration";
            case RUDERALIS_WILD -> "minecraft:saturation";
            case HYBRID_LINE -> "";
        };
    }

    /** Texto de la configuración con los efectos naturales de una especie (ids separados por coma). */
    public static String naturalEffectsRaw(Species species) {
        return NATURAL_EFFECTS.get(species).get();
    }

    /** Peso de un nivel de potencia (1 a 3) en el sorteo de las silvestres. */
    public static int wildPotencyWeight(int level) {
        return WILD_POTENCY_WEIGHTS[level - 1].get();
    }

    /** Peso de un nivel de duración (1 a 3) en el sorteo de las silvestres. */
    public static int wildDurationWeight(int level) {
        return WILD_DURATION_WEIGHTS[level - 1].get();
    }
}