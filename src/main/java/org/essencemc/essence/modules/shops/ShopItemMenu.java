package org.essencemc.essence.modules.shops;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.material.MaterialData;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.aliases.Items;
import org.essencemc.essencecore.arguments.DoubleArg;
import org.essencemc.essencecore.entity.EItem;
import org.essencemc.essencecore.menu.Menu;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.modules.SqlUpdateCallback;
import org.essencemc.essencecore.util.NumberUtil;
import org.essencemc.essencecore.util.Util;

import java.util.*;

public class ShopItemMenu extends Menu {

    private ShopsModule shopsModule;
    private Map<UUID, String> playerMenu = new HashMap<UUID, String>();
    private Map<UUID, String> input = new HashMap<UUID, String>();
    private List<UUID> itemInput = new ArrayList<UUID>();

    public ShopItemMenu(ShopsModule shopsModule) {
        super(Essence.inst(), "shop-items", 6, "&9&lShop Items");
        this.shopsModule = shopsModule;
    }

    @Override
    protected void onDestroy() {}

    @Override
    protected void onShow(InventoryOpenEvent event) {
        updateContent((Player)event.getPlayer());
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {}

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        final Player player = (Player)event.getWhoClicked();
        UUID uuid = player.getUniqueId();

        String menu = playerMenu.get(uuid);

        if (menu.startsWith("main")) {
            //Main menu
            if (event.getRawSlot() == 0) {
                //Previous page
                int page = NumberUtil.getInt(menu.split("-")[1]);
                if (page > 1) {
                    playerMenu.put(uuid, "main-" + (page-1));
                    updateContent(player);
                }
            } else if (event.getRawSlot() == 8) {
                //Next page
                int page = NumberUtil.getInt(menu.split("-")[1]);
                if (shopsModule.getShopItems().size() > page * 45) {
                    playerMenu.put(uuid, "main-" + (page+1));
                    updateContent(player);
                }
            } else if (event.getRawSlot() == 4) {
                //New shop item
                if (!itemInput.contains(uuid)) {
                    itemInput.add(player.getUniqueId());
                    //TODO: Send message
                } else {
                    itemInput.remove(player.getUniqueId());
                    //TODO: Send message
                }
            } else if (event.getRawSlot() == 7) {
                //Close menu
                playerMenu.remove(uuid);
                player.closeInventory();
            } else if (event.getRawSlot() > 8 && event.getRawSlot() < getSlots() && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                //Click on shop item.
                playerMenu.put(uuid, "item-" + Util.stripAllColor(new EItem(event.getCurrentItem()).getName()));
                updateContent(player);
            } else {
                //Adding new item session.
                if (!itemInput.contains(uuid)) {
                    return;
                }
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                    return;
                }
                if (shopsModule.getShopItem(event.getCurrentItem().getType(), event.getCurrentItem().getDurability()) != null) {
                    //TODO: Send message.
                    return;
                }

                itemInput.remove(uuid);
                playerMenu.put(uuid, "item-" + Items.getItem(event.getCurrentItem().getType(), event.getCurrentItem().getDurability()).getName().replace(" ", "") + ":" + event.getCurrentItem().getDurability());
                shopsModule.setShopItem(new ShopItem(event.getCurrentItem().getType(), event.getCurrentItem().getDurability(), true, 0, true, 0, "*", true, 0, 0), new SqlUpdateCallback() {
                    @Override
                    public void onExecute(int rowsChanged) {
                        updateContent(player);
                    }
                });
            }
        } else {
            String name = menu.split("-")[1];
            MaterialData md = Items.getMaterialData(name);
            ShopItem shopItem = shopsModule.getShopItem(md.getItemType(), md.getData());
            if (shopItem == null && (event.getRawSlot() != 0 || event.getRawSlot() != 8)) {
                return;
            }

            //Shop item edit menu
            if (event.getRawSlot() == 0) {
                //Back to list
                playerMenu.put(uuid, "main-0");
                updateContent(player);
            } else if (event.getRawSlot() == 8) {
                //Close menu
                playerMenu.remove(uuid);
                player.closeInventory();
            } else if (event.getRawSlot() == 35) {
                //Delete
                //TODO: Show confirmation menu
                //TODO: Check for player sold items and refund etc.
                shopsModule.removeShopItem(shopItem.getMaterial(), shopItem.getData());
                playerMenu.put(uuid, "main-0");
                updateContent(player);
            }
        }
    }

    private void updateContent(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerMenu.containsKey(uuid)) {
            playerMenu.put(uuid, "main-0");
        }
        String menu = playerMenu.get(uuid);

        clearMenu(player);
        if (menu.startsWith("main")) {
            //Main menu
            setSlot(0, new EItem(Material.SKULL_ITEM).setName(Message.PREVIOUS_PAGE.msg().getText()).setSkull("MHF_ArrowLeft").addAllFlags(true), player);
            setSlot(1, new EItem(Material.PAPER).setName("&6&lInformation").setLore("&7Click on any of the &aitems &7to &aedit &7it.", "&7Click on the &eitemframe &7to create a &enew &7item."), player);
            setSlot(4, new EItem(Material.ITEM_FRAME).setName("&a&lNew Item").setLore("&7Adds a new shop item to the list."), player);
            setSlot(7, new EItem(Material.STAINED_CLAY, 1, (short) 14).setName(Message.CLOSE.msg().getText()).setLore("&7Close the shop item editing menu."), player);
            setSlot(8, new EItem(Material.SKULL_ITEM).setName(Message.NEXT_PAGE.msg().getText()).setSkull("MHF_ArrowRight").addAllFlags(true), player);

            int page = NumberUtil.getInt(menu.split("-")[1]);

            List<ShopItem> shopItems = shopsModule.getShopItems();
            int slot = 9;
            for (int i = page * 45; i < page * 45 + 45 && i < shopItems.size(); i++) {
                ShopItem shopItem = shopItems.get(i);
                //TODO: Information Lore.
                setSlot(slot, shopItem.getItem().clone().setName("&e&l" + Items.getItem(shopItem.getMaterial(), shopItem.getData()).getName().replace(" ", "") + ":" + shopItem.getData()), player);
                slot++;
            }
        } else {
            //Item edit menu
            String name = menu.split("-")[1];
            setSlot(0, new EItem(Material.SKULL_ITEM).setName(Message.BACK.msg().getText()).setSkull("MHF_ArrowLeft").addAllFlags(true), player);
            //TODO: Information lore.
            setSlot(1, new EItem(Material.PAPER).setName(Message.INFORMATION.msg().getText()).setLore(""), player);
            setSlot(8, new EItem(Material.STAINED_CLAY, 1, (short) 14).setName(Message.CLOSE.msg().getText()).setLore("&7Close the shop item editing menu."), player);

            int[] emptySlots = new int[] {9,10,11,12,13,14,15,16,17, 18,19,20,23,25,26, 27,29,30,31,32,34, 36,37,38,41,43,44, 45,46,47,48,49,50,51,52,53};
            for (int slot : emptySlots) {
                setSlot(slot, new EItem(Material.STAINED_GLASS_PANE,1, (short)15).setName("&8#").addAllFlags(true));
            }

            MaterialData md = Items.getMaterialData(name);
            ShopItem shopItem = shopsModule.getShopItem(md.getItemType(), md.getData());
            if (shopItem == null) {
                setSlot(4, new EItem(Material.REDSTONE_BLOCK).setName("&e&l" + name).setLore("&c&lNo item data found!"), player);
                return;
            }
            setSlot(4, shopItem.getItem().clone().setName("&e&l" + name), player);

            setSlot(28, new EItem(Material.NAME_TAG).setName("&a&l" + shopItem.getCategory()).setLore("&7The item category.", "&7Click to edit!"), player);

            //TODO: Messages for these values.
            setSlot(21, new EItem(Material.GOLD_INGOT).setName("&a&l$" + shopItem.getBuyPrice()).addLore("&7The default buy price.", "&7Click to edit!"));
            if (shopItem.canBuy()) {
                setSlot(22, new EItem(Material.INK_SACK, 1, (short)10).setName("&aCan be bought.").setLore("&7Players can buy these items in shops.", "&8Click to disable."), player);
            } else {
                setSlot(22, new EItem(Material.INK_SACK, 1, (short)8).setName("&7Can't be bought.").setLore("&7Players can't buy these items in shops.", "&8Click to enable."), player);
            }

            setSlot(39, new EItem(Material.GOLD_INGOT).setName("&c&l$" + shopItem.getSellPrice()).addLore("&7The default sell price.", "&7Click to edit!"));
            if (shopItem.canSell()) {
                setSlot(40, new EItem(Material.INK_SACK, 1, (short)10).setName("&aCan be sold.").setLore("&7Players can sell these items in shops.", "&8Click to disable."), player);
            } else {
                setSlot(40, new EItem(Material.INK_SACK, 1, (short)8).setName("&7Can't be sold.").setLore("&7Players can't sell these items in shops.", "&8Click to enable."), player);
            }

            setSlot(24, new EItem(Material.GOLD_INGOT).setName("&6&l$" + shopItem.getMinMarketPrice()).addLore("&7The minimum market price.", "&7Click to edit!"));
            setSlot(42, new EItem(Material.GOLD_INGOT).setName("&e&l$" + shopItem.getMaxMarketPrice()).addLore("&7The maximum market price.", "&7Click to edit!"));

            if (shopItem.isMarket()) {
                setSlot(33, new EItem(Material.INK_SACK, 1, (short)10).setName("&aCan be placed on the market.").setLore("&7Players can place this item on the market.", "&8Click to disable."), player);
            } else {
                setSlot(33, new EItem(Material.INK_SACK, 1, (short)8).setName("&7Can't be placed on the market.").setLore("&7Players can't place this item on the market.", "&8Click to enable."), player);
            }

            setSlot(35, new EItem(Material.BARRIER).setName("&4&lDELETE").setLore("&c&lThis will delete the shop item completely!", "&7All data you set will be lost!"), player);
        }
    }

    public boolean hasInput(Player player) {
        return input.containsKey(player.getUniqueId());
    }

    public void setInputResult(Player player, String string) {
        UUID uuid = player.getUniqueId();
        String type = input.get(player.getUniqueId());
        string = string.trim();

        //Disable input when q is typed.
        if (string.equalsIgnoreCase("q")) {
            input.remove(player.getUniqueId());
            EssMessage.INPUT_MODE_DISABLED.msg().send(player, true, true);
            return;
        }

        String menu = playerMenu.get(uuid);
        String name = menu.split("-")[1];
        MaterialData md = Items.getMaterialData(name);
        ShopItem shopItem = shopsModule.getShopItem(md.getItemType(), md.getData());

        if (shopItem == null) {
            //This should never happen but just in case. (Don't want users getting stuck in input mode)
            input.remove(player.getUniqueId());
            EssMessage.INPUT_MODE_DISABLED.msg().send(player, true, true);
            return;
        }

        if (type.equalsIgnoreCase("category")) {
            //Category input
            if (!string.matches("[a-zA-Z]+")) {
                //TODO: Proper message
                EssMessage.CORE_SIGN_INVALID_NAME.msg().send(player, true, true, Param.P("input", string));
                return;
            }

            shopItem.setCategory(string);
        } else {
            DoubleArg arg = new DoubleArg();
            if (!arg.parse(string)) {
                arg.getError().send(player);
                return;
            }

            if (type.equalsIgnoreCase("buy")) {
                shopItem.setBuyPrice((Double) arg.getValue());
            } else if (type.equalsIgnoreCase("sell")) {
                shopItem.setSellPrice((Double) arg.getValue());
            } else if (type.equalsIgnoreCase("min")) {
                //TODO: Set min market price
            } else if (type.equalsIgnoreCase("max")) {
                //TODO: Set max market price
            }
        }

        shopsModule.setShopItem(shopItem);
        input.remove(player.getUniqueId());
        show(player);
        updateContent(player);
    }
}
