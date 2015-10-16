package org.essencemc.essence.modules.kits;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.essencemc.essencecore.entity.EItem;

public class KitData {

    private String name;
    private boolean enabled;
    private EItem[] items;
    private EItem icon;

    public KitData(String name, boolean enabled, EItem[] items, EItem icon) {
        setName(name);
        setEnabled(enabled);
        setItems(items);
        setIcon(icon);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public EItem[] getItems() {
        return items;
    }

    public EItem getItem(int slot) {
        return items[slot];
    }

    public void setItems(EItem[] items) {
        EItem[] itemArray = new EItem[40];
        for (int i = 0; i < itemArray.length; i++) {
            if (items.length > i && items[i] != null) {
                itemArray[i] = items[i];
            } else {
                itemArray[i] = EItem.AIR;
            }
        }
        this.items = itemArray;
    }


    public EItem getIcon() {
        return icon;
    }

    public void setIcon(EItem icon) {
        if (icon == null || icon.getType() == Material.AIR) {
            icon = new EItem(Material.ENDER_CHEST);
        }
        this.icon = icon;
    }
}
