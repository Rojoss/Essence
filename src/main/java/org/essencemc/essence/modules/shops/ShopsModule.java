package org.essencemc.essence.modules.shops;

import org.bukkit.Material;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.database.Column;
import org.essencemc.essencecore.database.Database;
import org.essencemc.essencecore.database.Operator;
import org.essencemc.essencecore.modules.DataModules;
import org.essencemc.essencecore.modules.SqlQueryCallback;
import org.essencemc.essencecore.modules.SqlStorageModule;
import org.essencemc.essencecore.modules.SqlUpdateCallback;
import org.essencemc.essencecore.util.Debug;

import java.sql.*;
import java.util.*;

public class ShopsModule extends SqlStorageModule {

    private Map<String, ShopItem> shopItems = new HashMap<String, ShopItem>();
    private ShopItemMenu itemMenu = null;

    public ShopsModule(String name) {
        super(Essence.inst(), name, "ShopItems", DataModules.SHOP_ITEMS);
    }

    @Override
    public void onLoad() {
        Debug.bc("onLoad");
        //TODO: Load vault

        itemMenu = new ShopItemMenu(this);

        final PreparedStatement statement = getDatabase().createQuery().select("*").from(getTable()).getStatement();
        Debug.bc(statement);
        executeQuery(statement, new SqlQueryCallback() {
            @Override
            public void onExecute(ResultSet result) {
                Debug.bc("onExecute");
                try {
                    Map<String, ShopItem> loadedItems = new HashMap<String, ShopItem>();

                    while (result.next()) {
                        Debug.bc("Result");
                        Material material = Material.valueOf(result.getString("material"));
                        short data = result.getShort("data");

                        ShopItem shopItem = new ShopItem(material, data, result.getDouble("buy"), result.getDouble("sell"), result.getString("category"), result.getBoolean("market"));
                        loadedItems.put(getKey(material, data), shopItem);
                    }

                    Debug.bc("Setting shop items...");
                    Debug.bc(loadedItems.size());
                    shopItems = loadedItems;
                } catch (SQLException e) {}
            }
        });
    }

    @Override
    protected void onSave() {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    protected void onReload() {

    }

    @Override
    public Column[] getTableColumns() {
        Database db = getDatabase();
        return new Column[]{
                db.createColumn("id").type("INT").primaryKey().autoIncrement(),
                db.createColumn("material").type("VARCHAR", 64).notNull(),
                db.createColumn("data").type("SMALLINT").notNull(),
                db.createColumn("buy").type("DOUBLE").defaultValue(-1).notNull(),
                db.createColumn("sell").type("DOUBLE").defaultValue(-1).notNull(),
                db.createColumn("category").type("VARCHAR", 128).defaultValue("*").notNull(),
                db.createColumn("market").type("BOOLEAN").defaultValue(true).notNull()
        };
    }


    public ShopItemMenu getItemMenu() {
        return itemMenu;
    }

    public void setShopItem(ShopItem item) {
        setShopItem(item, null);
    }

    public void setShopItem(ShopItem item, SqlUpdateCallback callback) {
        Material material = item.getMaterial();
        short data = item.getData();
        String key = getKey(material, data);

        List<String> columns = Arrays.asList("material", "data", "buy", "sell", "category", "market");
        List<Object> values = new ArrayList<Object>();
        values.add(material.toString());
        values.add(data);
        values.add(item.getBuyPrice());
        values.add(item.getSellPrice());
        values.add(item.getCategory());
        values.add(item.isMarket());

        PreparedStatement statement = null;
        if (shopItems.containsKey(key)) {
            statement = getDatabase().createQuery().update(getTable()).set(columns, values).where("material", Operator.EQUAL, material.toString()).and("data", Operator.EQUAL, data).getStatement();
        } else {
            statement = getDatabase().createQuery().insertInto(getTable()).values(columns, values).getStatement();
        }
        shopItems.put(key, item);

        if (callback == null) {
            executeUpdate(statement);
        } else {
            executeUpdate(statement, callback);
        }
    }

    public void removeShopItem(final Material material, final short data) {
        String key = getKey(material, data);
        if (shopItems.containsKey(key)) {
            shopItems.remove(key);

            PreparedStatement statement = getDatabase().createQuery().delete().from(getTable()).where("material", Operator.EQUAL, material.toString()).and("data", Operator.EQUAL, data).getStatement();
            executeUpdate(statement);
        }
    }

    public ShopItem getShopItem(Material material, short data) {
        if (shopItems.containsKey(getKey(material, data))) {
            return shopItems.get(getKey(material, data));
        }
        return null;
    }

    public List<ShopItem> getShopItems(String category) {
        List<ShopItem> categorizedItems = new ArrayList<ShopItem>();
        for (ShopItem item : shopItems.values()) {
            if (item.getCategory().equalsIgnoreCase(category)) {
                categorizedItems.add(item);
            }
        }
        return categorizedItems;
    }

    public List<ShopItem> getShopItems() {
        return new ArrayList<>(shopItems.values());
    }

    private String getKey(Material material, short data) {
        return material.toString() + "-" + data;
    }

}
