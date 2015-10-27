package org.essencemc.essence.modules.shops;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.essencemc.essencecore.entity.EItem;

public class ShopItem {
    private String category;

    private Material material;
    private short data;

    private double buyPrice;
    private double sellPrice;
    private double minMarketPrice;
    private double maxMarketPrice;

    private boolean market;
    private boolean buy;
    private boolean sell;

    public ShopItem(Material material, short data, boolean buy, double buyPrice, boolean sell, double sellPrice, String category, boolean market, double minMarketPrice, double maxMarketPrice) {
        this.material = material;
        this.data = data;
        this.buy = buy;
        this.buyPrice = buyPrice;
        this.sell = sell;
        this.sellPrice = sellPrice;
        this.category = category;
        this.market = market;
        this.minMarketPrice = minMarketPrice;
        this.maxMarketPrice = maxMarketPrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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


    public boolean canBuy() {
        return buy;
    }

    public void setCanBuy(boolean buy) {
        this.buy = buy;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double price) {
        this.buyPrice = price;
    }


    public boolean canSell() {
        return sell;
    }

    public void setCanSell(boolean sell) {
        this.sell = sell;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double price) {
        this.sellPrice = price;
    }


    public boolean isMarket() {
        return market;
    }

    public void setMarket(boolean market) {
        this.market = market;
    }

    public double getMinMarketPrice() {
        return minMarketPrice;
    }

    public void setMinMarketPrice(double minMarketPrice) {
        this.minMarketPrice = minMarketPrice;
    }

    public double getMaxMarketPrice() {
        return maxMarketPrice;
    }

    public void setMaxMarketPrice(double maxMarketPrice) {
        this.maxMarketPrice = maxMarketPrice;
    }
}
