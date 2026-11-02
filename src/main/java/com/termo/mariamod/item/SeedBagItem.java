package com.termo.mariamod.item;

import net.minecraft.world.item.BundleItem;

// Saco para guardar semillas y cogollos de distinta genética en un solo lugar del inventario.
// Reutiliza la lógica del Bundle de vanilla, que en 1.21.1 es experimental. Este ítem propio no lo necesita.
public class SeedBagItem extends BundleItem {
    public SeedBagItem(Properties properties) {
        super(properties);
    }
}