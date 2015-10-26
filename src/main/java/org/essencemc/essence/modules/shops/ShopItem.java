package org.essencemc.essence.modules.shops;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.essencemc.essencecore.entity.EItem;

public class ShopItem {

    private Material material;
    private short data;
    private double buy;
    private double sell;
    private String category;
    private boolean market;

    public ShopItem(Material material, short data, double buy, double sell, String category, boolean market) {
        this.material = material;
        this.data = data;
        this.buy = buy;
        this.sell = sell;
        this.category = category;
        this.market = market;
    }


    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }


    public short getData() {
        return data;
    }

    public void setData(short data) {
        this.data = data;
    }


    public MaterialData getMaterialData() {
        return new MaterialData(material, (byte)data);
    }

    public EItem getItem() {
        return new EItem(material, 1, data);
    }


    public double getBuyPrice() {
        return buy;
    }

    public void setBuy(double price) {
        this.buy = price;
    }


    public double getSellPrice() {
        return sell;
    }

    public void setSell(double price) {
        this.sell = price;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public boolean isMarket() {
        return market;
    }

    public void setMarket(boolean market) {
        this.market = market;
    }
}
