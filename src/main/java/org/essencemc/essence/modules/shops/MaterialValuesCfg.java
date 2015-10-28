/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Essence <http://essencemc.org>
 * Copyright (c) 2015 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.essencemc.essence.modules.shops;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;
import org.bukkit.material.MaterialData;
import org.essencemc.essencecore.config.internal.EasyConfig;
import org.essencemc.essencecore.util.Debug;
import org.essencemc.essencecore.util.NumberUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaterialValuesCfg extends EasyConfig {

    public Map<String, Map<String, String>> MATERIAL_VALUES = new HashMap<String, Map<String, String>>();
    private Map<MaterialData, MaterialValue> materialValues = new HashMap<MaterialData, MaterialValue>();

    public MaterialValuesCfg(String fileName) {
        this.setFile(fileName);
        load();
    }

    @Override
    public void load() {
        super.load();
        //If no values load the defaults.
        if (MATERIAL_VALUES.size() < 1) {
            loadDefaults();
            save();
        }

        //Load all values internally so we don't have to convert it every time.
        materialValues.clear();
        for (Map.Entry<String, Map<String, String>> entry : MATERIAL_VALUES.entrySet()) {
            Map<String, String> data = entry.getValue();
            MaterialValue matVal = new MaterialValue(NumberUtil.getDouble(data.get("buy")), NumberUtil.getDouble(data.get("sell")));

            String[] split = entry.getKey().split("-");
            Material material = Material.valueOf(split[0]);
            byte matData = (split.length > 1 ? Byte.valueOf(split[1]) : (byte)0);
            materialValues.put(new MaterialData(material, matData), matVal);
        }
    }

    @Override
    public void save() {
        //Convert the material value data back to config values.
        for (Map.Entry<MaterialData, MaterialValue> entry : materialValues.entrySet()) {
            Map<String, String> data = new HashMap<String, String>();
            data.put("buy", Double.toString(entry.getValue().getBuy()));
            data.put("sell", Double.toString(entry.getValue().getSell()));
            MATERIAL_VALUES.put(entry.getKey().getItemType().toString() + "-" + entry.getKey().getData(), data);
        }
        super.save();
    }

    private void loadDefaults() {
        //All raw materials that can't be crafted.
        materialValues.put(new MaterialData(Material.GRASS), new MaterialValue(60d, 2d));
        materialValues.put(new MaterialData(Material.DIRT), new MaterialValue(1d, 0.5d));
        materialValues.put(new MaterialData(Material.DIRT, (byte)1), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.GRASS, (byte)2), new MaterialValue(3d, 1d));
        materialValues.put(new MaterialData(Material.COBBLESTONE), new MaterialValue(1d, 0.5d));
        materialValues.put(new MaterialData(Material.SAPLING), new MaterialValue(6d, 2d));
        materialValues.put(new MaterialData(Material.SAPLING, (byte)1), new MaterialValue(6d, 2d));
        materialValues.put(new MaterialData(Material.SAPLING, (byte)2), new MaterialValue(6d, 2d));
        materialValues.put(new MaterialData(Material.SAPLING, (byte)3), new MaterialValue(6d, 2d));
        materialValues.put(new MaterialData(Material.SAPLING, (byte)4), new MaterialValue(6d, 2d));
        materialValues.put(new MaterialData(Material.SAPLING, (byte)5), new MaterialValue(6d, 2d));
        materialValues.put(new MaterialData(Material.SAND), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SAND, (byte)1), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.GRAVEL), new MaterialValue(1d, 0.5d));
        materialValues.put(new MaterialData(Material.GOLD_ORE), new MaterialValue(40d, 30d));
        materialValues.put(new MaterialData(Material.IRON_ORE), new MaterialValue(50d, 25d));
        materialValues.put(new MaterialData(Material.COAL_ORE), new MaterialValue(20d, 10d));
        materialValues.put(new MaterialData(Material.COAL), new MaterialValue(5d, 2d));
        materialValues.put(new MaterialData(Material.LOG), new MaterialValue(8d, 4d));
        materialValues.put(new MaterialData(Material.LOG, (byte)1), new MaterialValue(8d, 4d));
        materialValues.put(new MaterialData(Material.LOG, (byte)2), new MaterialValue(8d, 4d));
        materialValues.put(new MaterialData(Material.LOG, (byte)3), new MaterialValue(8d, 4d));
        materialValues.put(new MaterialData(Material.LOG, (byte)4), new MaterialValue(8d, 4d));
        materialValues.put(new MaterialData(Material.LOG, (byte)5), new MaterialValue(8d, 4d));
        materialValues.put(new MaterialData(Material.LEAVES), new MaterialValue(1d, 1d));
        materialValues.put(new MaterialData(Material.LEAVES, (byte)1), new MaterialValue(1d, 1d));
        materialValues.put(new MaterialData(Material.LEAVES, (byte)2), new MaterialValue(1d, 1d));
        materialValues.put(new MaterialData(Material.LEAVES, (byte)3), new MaterialValue(1d, 1d));
        materialValues.put(new MaterialData(Material.SPONGE, (byte)1), new MaterialValue(100d, 80d));
        materialValues.put(new MaterialData(Material.LAPIS_ORE), new MaterialValue(30d, 15d));
        materialValues.put(new MaterialData(Material.WEB), new MaterialValue(20d, 10d));
        materialValues.put(new MaterialData(Material.LONG_GRASS), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.LONG_GRASS, (byte)1), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.LONG_GRASS, (byte)2), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.DEAD_BUSH), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.YELLOW_FLOWER), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE, (byte)1), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE, (byte)2), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE, (byte)3), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE, (byte)4), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE, (byte)5), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE, (byte)6), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE, (byte)7), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.RED_ROSE, (byte)8), new MaterialValue(2d, 2d));
        materialValues.put(new MaterialData(Material.BROWN_MUSHROOM), new MaterialValue(10d, 2d));
        materialValues.put(new MaterialData(Material.RED_MUSHROOM), new MaterialValue(10d, 2d));
        materialValues.put(new MaterialData(Material.MOSSY_COBBLESTONE), new MaterialValue(4d, 4d));
        materialValues.put(new MaterialData(Material.OBSIDIAN), new MaterialValue(20d, 5d));
        materialValues.put(new MaterialData(Material.DIAMOND_ORE), new MaterialValue(150d, 80d));
        materialValues.put(new MaterialData(Material.SOIL), new MaterialValue(10d, 2d));
        materialValues.put(new MaterialData(Material.REDSTONE_ORE), new MaterialValue(30d, 15d));
        materialValues.put(new MaterialData(Material.CACTUS), new MaterialValue(4d, 1d));
        materialValues.put(new MaterialData(Material.PUMPKIN), new MaterialValue(8d, 1d));
        materialValues.put(new MaterialData(Material.NETHERRACK), new MaterialValue(2d, 0.5d));
        materialValues.put(new MaterialData(Material.SOUL_SAND), new MaterialValue(4d, 0.75d));
        materialValues.put(new MaterialData(Material.HUGE_MUSHROOM_1), new MaterialValue(6d, 1d));
        materialValues.put(new MaterialData(Material.HUGE_MUSHROOM_2), new MaterialValue(6d, 1d));
        materialValues.put(new MaterialData(Material.VINE), new MaterialValue(3d, 1d));
        materialValues.put(new MaterialData(Material.WATER_LILY), new MaterialValue(3d, 3d));
        materialValues.put(new MaterialData(Material.ENDER_STONE), new MaterialValue(6d, 2d));
        materialValues.put(new MaterialData(Material.DRAGON_EGG), new MaterialValue(5000d, 2500d));
        materialValues.put(new MaterialData(Material.EMERALD_ORE), new MaterialValue(60d, 40d));

        materialValues.put(new MaterialData(Material.QUARTZ_ORE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.HARD_CLAY), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.LEAVES_2), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.LEAVES_2, (byte)1), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.LOG_2), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.LOG_2, (byte)1), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.PACKED_ICE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.DOUBLE_PLANT), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.DOUBLE_PLANT, (byte)1), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.DOUBLE_PLANT, (byte)2), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.DOUBLE_PLANT, (byte)3), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.DOUBLE_PLANT, (byte)4), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.DOUBLE_PLANT, (byte)5), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.APPLE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.STRING), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.FEATHER), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SULPHUR), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SEEDS), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.WHEAT), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.CHAINMAIL_HELMET), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.CHAINMAIL_CHESTPLATE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.CHAINMAIL_LEGGINGS), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.CHAINMAIL_BOOTS), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.FLINT), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.PORK), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SADDLE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.REDSTONE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SNOW_BALL), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.CLAY), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SUGAR_CANE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SLIME_BALL), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.EGG), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.GLOWSTONE_DUST), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RAW_FISH), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RAW_FISH, (byte)1), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RAW_FISH, (byte)2), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RAW_FISH, (byte)3), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.INK_SACK), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.INK_SACK, (byte)3), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.INK_SACK, (byte)4), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.BONE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.MELON), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RAW_BEEF), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RAW_CHICKEN), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.ROTTEN_FLESH), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.ENDER_PEARL), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.BLAZE_ROD), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.GHAST_TEAR), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.NETHER_STALK), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SPIDER_EYE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.CARROT), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.POTATO), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.POISONOUS_POTATO), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SKULL_ITEM), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SKULL_ITEM, (byte)1), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SKULL_ITEM, (byte)2), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SKULL_ITEM, (byte)3), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.SKULL_ITEM, (byte)4), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.NETHER_STAR), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.PRISMARINE_SHARD), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.PRISMARINE_CRYSTALS), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RABBIT), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RABBIT_FOOT), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RABBIT_HIDE), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.IRON_BARDING), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.GOLD_BARDING), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.DIAMOND_BARDING), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.NAME_TAG), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.MUTTON), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.GOLD_RECORD), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.GREEN_RECORD), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_3), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_4), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_5), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_6), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_7), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_8), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_9), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_10), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_11), new MaterialValue(2d, 1d));
        materialValues.put(new MaterialData(Material.RECORD_12), new MaterialValue(2d, 1d));
    }

    public double getPrice(ItemStack item, boolean buy) {
        MaterialData materialData = item.getData();
        if (materialData.getData() < 0) {
            materialData.setData((byte)0);
        }
        Debug.bc("getPrice " + item.getType().toString() + ":" + materialData.getData() + " - " + (buy ? "BUY" : "SELL"));
        if (materialValues.containsKey(materialData)) {
            Debug.bc("Raw material value found!");
            if (buy) {
                return materialValues.get(materialData).getBuy() * item.getAmount();
            } else {
                return materialValues.get(materialData).getSell() * item.getAmount();
            }
        }

        List<Recipe> recipes = Bukkit.getRecipesFor(item);
        if (recipes.isEmpty()) {
            Debug.bc("No recipe found!");
            return 0;
        }
        Recipe recipe = recipes.get(0);

        //Furnace recipe
        if (recipe instanceof FurnaceRecipe) {
            Debug.bc("Furnace recipe!");
            return getPrice(((FurnaceRecipe)recipe).getInput(), buy) / recipe.getResult().getAmount();
        }

        //Shapeless recipe
        if (recipe instanceof ShapelessRecipe) {
            Debug.bc("Shapeless recipe!");
            List<ItemStack> ingredients = ((ShapelessRecipe)recipe).getIngredientList();
            double price = 0;
            for (ItemStack ingredient : ingredients) {
                int amount = ingredient.getAmount();
                ingredient.setAmount(1);
                price += getPrice(ingredient, buy) * amount;
            }
            return price / recipe.getResult().getAmount();
        }

        //Shaped recipe
        if (recipe instanceof ShapedRecipe) {
            Debug.bc("Shaped recipe!");
            double price = 0;
            for (String str : ((ShapedRecipe) recipe).getShape()) {
                for (Character ch : str.toCharArray()) {
                    if (((ShapedRecipe) recipe).getIngredientMap().containsKey(ch)) {
                        ItemStack ingredient = ((ShapedRecipe) recipe).getIngredientMap().get(ch).clone();
                        int amount = ingredient.getAmount();
                        ingredient.setAmount(1);
                        price += getPrice(ingredient, buy) * amount;
                    }
                }
            }
            return price / recipe.getResult().getAmount();
        }

        Debug.bc("Undefined recipe...");
        return 0;
    }
}
