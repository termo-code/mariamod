package com.termo.mariamod.genetics;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.Optional;

public enum PlantVariant implements StringRepresentable {
    //                  id                   especie                  pot dur riesgo
    SATIVA_WILD(        "sativa_wild",       Species.SATIVA_WILD,     1,  1,  0.55f),
    INDICA_WILD(        "indica_wild",       Species.INDICA_WILD,     1,  1,  0.55f),
    RUDERALIS_WILD(     "ruderalis_wild",    Species.RUDERALIS_WILD,  1,  1,  0.55f),
    SATIVA_HYBRID(      "sativa_hybrid",     Species.SATIVA_HYBRID,   1,  1,  0.55f),
    INDICA_HYBRID(      "indica_hybrid",     Species.INDICA_HYBRID,   1,  1,  0.55f),
    HYBRID_PLUS(        "hybrid_plus",       Species.HYBRID_LINE,     1,  1,  0.55f),
    HYBRID_MAX(         "hybrid_max",        Species.HYBRID_LINE,     3,  3,  0.05f),
    HYBRID_PRIMORDIAL(  "hybrid_primordial", Species.HYBRID_LINE,     3,  3,  0.00f);

    public static final Codec<PlantVariant> CODEC = StringRepresentable.fromEnum(PlantVariant::values);

    private final String id;
    private final Species species;
    private final Genetics defaultGenetics;

    PlantVariant(String id, Species species, int potency, int durationLevel, float risk) {
        this.id = id;
        this.species = species;
        this.defaultGenetics = new Genetics(potency, durationLevel, risk, List.of());
    }

    public String id() {
        return id;
    }

    public Species species() {
        return species;
    }

    // Genética con la que nacen los ítems de esta variante (creativo y /give). Las plantas del mundo la sortean aparte.
    public Genetics defaultGenetics() {
        return defaultGenetics;
    }

    public String seedsId() {
        return id + "_seeds";
    }

    public String budId() {
        return id + "_bud";
    }

    public String cropId() {
        return id + "_crop";
    }

    @Override
    public String getSerializedName() {
        return id;
    }

    // Variante que nace al cruzar a con b, o vacío si no se pueden cruzar. Usa el árbol de especies de Species.
    public static Optional<PlantVariant> crossResult(PlantVariant a, PlantVariant b) {
        for (PlantVariant candidate : values()) {
            Species parentA = candidate.species.parentA();
            Species parentB = candidate.species.parentB();
            if (parentA == null || parentB == null || entryOf(candidate.species) != candidate) {
                continue;
            }
            boolean straight = parentA == a.species && parentB == b.species;
            boolean reversed = parentA == b.species && parentB == a.species;
            if (straight || reversed) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    // La primera variante declarada de una especie: es la que se obtiene al cruzar (por ejemplo HYBRID_PLUS).
    private static PlantVariant entryOf(Species species) {
        for (PlantVariant variant : values()) {
            if (variant.species == species) {
                return variant;
            }
        }
        throw new IllegalStateException("Especie sin variantes: " + species);
    }

    // Busca una variante por su id de texto (el que guardan los ítems). Vacío si no existe.
    public static Optional<PlantVariant> byId(String id) {
        for (PlantVariant variant : values()) {
            if (variant.id.equals(id)) {
                return Optional.of(variant);
            }
        }
        return Optional.empty();
    }
}