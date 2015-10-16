package org.essencemc.essence.modules.kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.config.internal.EasyConfig;
import org.essencemc.essencecore.entity.EItem;
import org.essencemc.essencecore.parsers.ItemParser;

import java.util.*;

public class KitsCfg extends EasyConfig {

    public Map<String, List<String>> welcomeKits = new HashMap<String, List<String>>();
    public Map<String, Map<String, String>> KITS = new HashMap<String, Map<String, String>>();
    private Map<String, KitData> kits = new HashMap<String, KitData>();

    public KitsCfg(String fileName) {
        this.setFile(fileName);
        load();
    }

    @Override
    public void load() {
        super.load();
        //If no values load the defaults.
        if (KITS.size() < 1) {
            if (welcomeKits.size() < 1) {
                welcomeKits.put("*", Arrays.asList("welcome"));
            }

            //Default welcome kit.
            EItem[] items = new EItem[40];
            items[36] = new EItem(Material.IRON_BOOTS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            items[37] = new EItem(Material.IRON_LEGGINGS).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            items[38] = new EItem(Material.IRON_CHESTPLATE).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            items[39] = new EItem(Material.IRON_HELMET).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            items[0] = new EItem(Material.IRON_SWORD).addEnchant(Enchantment.DAMAGE_ALL, 1);
            items[1] = new EItem(Material.BOW).addEnchant(Enchantment.ARROW_DAMAGE, 1);
            items[2] = new EItem(Material.GRILLED_PORK, 16);
            items[4] = new EItem(Material.STONE_PICKAXE, 1);
            items[5] = new EItem(Material.STONE_AXE, 1);
            items[6] = new EItem(Material.STONE_SPADE, 1);
            items[8] = new EItem(Material.LOG, 16);
            items[9] = new EItem(Material.ARROW, 32);

            kits.put("welcome", new KitData("welcome", true, items, null));
            save();
        }
        //Load all kits internally so we don't have to convert it every time.
        kits.clear();
        for (Map.Entry<String, Map<String, String>> entry : KITS.entrySet()) {
            Map<String, String> data = entry.getValue();
            EItem[] items = new EItem[40];
            for (int i = 0; i < 40; i++) {
                if (data.containsKey("slot-" + i) && data.get("slot-" + i) != null) {
                    items[i] = new ItemParser(data.get("slot-" + i), 1, true).getItem();
                }
                if (items[i] == null) {
                    items[i] = EItem.AIR;
                }
            }
            KitData kitData = new KitData(
                    entry.getKey(),
                    BoolArg.Parse(data.get("enabled")),
                    items,
                    new ItemParser(data.get("icon"), 1, true).getItem()
            );
            kits.put(entry.getKey(), kitData);
        }
    }

    @Override
    public void save() {
        //Convert the kit data back to config values.
        for (KitData kitData : kits.values()) {
            Map<String, String> data = new TreeMap<String, String>();
            data.put("enabled", Boolean.toString(kitData.isEnabled()));
            data.put("icon", new ItemParser(kitData.getIcon()).getString());

            for (int i = 0; i < 40; i++) {
                if (kitData.getItem(i) == null || kitData.getItem(i).getType() == Material.AIR) {
                    continue;
                }
                data.put("slot-" + i, new ItemParser(kitData.getItem(i)).getString());
            }

            KITS.put(kitData.getName(), data);
        }
        super.save();
    }


    public void deleteKit(String name) {
        if (kits.containsKey(name)) {
            kits.remove(name);
            KITS.remove(name);
            save();
        }
    }

    public void setKit(KitData kitData) {
        kits.put(kitData.getName(), kitData);
        save();
    }

    public void setKit(KitData kitData, boolean save) {
        kits.put(kitData.getName(), kitData);
        if (save) {
            save();
        }
    }

    public KitData getKit(String name) {
        if (kits.containsKey(name)) {
            return kits.get(name);
        }
        return null;
    }

    public Map<String, KitData> getKits() {
        return kits;
    }

    public List<KitData> getKitList() {
        return new ArrayList<>(kits.values());
    }


    public List<String> getWelcomeKits(String group) {
        List<String> kitList = new ArrayList<String>();
        if (welcomeKits.containsKey(group)) {
            kitList.addAll(welcomeKits.get(group));
        }
        if (welcomeKits.containsKey("*")) {
            kitList.addAll(welcomeKits.get("*"));
        }
        return kitList;
    }

    public Map<String, List<String>> getWelcomeKits() {
        return welcomeKits;
    }

}
