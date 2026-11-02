package com.termo.mariamod.genetics;

import org.jetbrains.annotations.Nullable;

/**
 * Las 6 especies, para las REGLAS del juego (cruces, bad trip, plantación).
 * Plus, Máxima y Primordial son una sola especie (HYBRID_LINE) con tres variantes: ver PlantVariant.
 *
 * Árbol de cruces:
 *   SATIVA_HYBRID = SATIVA_WILD + RUDERALIS_WILD
 *   INDICA_HYBRID = INDICA_WILD + RUDERALIS_WILD
 *   HYBRID_LINE   = SATIVA_HYBRID + INDICA_HYBRID
 * Las silvestres no tienen padres.
 */
public enum Species {
    SATIVA_WILD("sativa_wild", null, null),
    INDICA_WILD("indica_wild", null, null),
    RUDERALIS_WILD("ruderalis_wild", null, null),
    SATIVA_HYBRID("sativa_hybrid", SATIVA_WILD, RUDERALIS_WILD),
    INDICA_HYBRID("indica_hybrid", INDICA_WILD, RUDERALIS_WILD),
    HYBRID_LINE("hybrid_line", SATIVA_HYBRID, INDICA_HYBRID);

    private final String id;
    @Nullable
    private final Species parentA;
    @Nullable
    private final Species parentB;

    Species(String id, @Nullable Species parentA, @Nullable Species parentB) {
        this.id = id;
        this.parentA = parentA;
        this.parentB = parentB;
    }

    public String id() {
        return id;
    }

    /** Las silvestres son las que no nacen de un cruce. */
    public boolean isWild() {
        return parentA == null;
    }

    @Nullable
    public Species parentA() {
        return parentA;
    }

    @Nullable
    public Species parentB() {
        return parentB;
    }
}