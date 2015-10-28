package org.essencemc.essence.modules.shops;

public class MaterialValue {

    private double buy;
    private double sell;

    public MaterialValue(Double buy, Double sell) {
        this.buy = buy == null ? 0 : buy;
        this.sell = sell == null ? 0 : sell;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }


    public double getSell() {
        return sell;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }
}
