package com.termo.mariamod.genetics;

import com.termo.mariamod.config.BalanceConfig;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

// Efectos naturales de cada especie. Salen de la configuración y NO se guardan en el genoma.
public final class NaturalEffects {
    private NaturalEffects() {}

    // Los efectos naturales de una especie. Los ids inválidos de la configuración se ignoran.
    public static List<ResourceLocation> of(Species species) {
        List<ResourceLocation> result = new ArrayList<>();
        for (String part : BalanceConfig.naturalEffectsRaw(species).split(",")) {
            String text = part.trim();
            if (text.isEmpty()) {
                continue;
            }
            ResourceLocation id = ResourceLocation.tryParse(text);
            if (id != null) {
                result.add(id);
            }
        }
        return result;
    }

    // Naturales de la especie + efectos de poción del genoma, sin repetir. Con especie null, solo los del genoma.
    public static List<ResourceLocation> allFor(@Nullable Species species, Genetics genetics) {
        LinkedHashSet<ResourceLocation> all = new LinkedHashSet<>();
        if (species != null) {
            all.addAll(of(species));
        }
        all.addAll(genetics.effects());
        return new ArrayList<>(all);
    }
}