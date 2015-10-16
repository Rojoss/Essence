package org.essencemc.essence.modules.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.entity.EItem;
import org.essencemc.essencecore.menu.Menu;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.NumberUtil;
import org.essencemc.essencecore.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KitDisplayMenu extends Menu {

    private KitModule kitModule;
    private Map<UUID, String> playerKit = new HashMap<UUID, String>();

    public KitDisplayMenu(KitModule kitModule) {
        super(Essence.inst(), "ess-kit-display", 6, EssMessage.CORE_KIT_DISPLAY_MENU_TITLE.msg().getText());
        this.kitModule = kitModule;

        setSlot(1, new EItem(Material.PAPER).setName(Message.INFORMATION.msg().getText()).setLore(EssMessage.CORE_KIT_DISPLAY_MENU_INFO.msg().getText()), null);

        for (int i = 9; i < 36; i++) {
            setSlot(i, new EItem(Material.STAINED_GLASS_PANE, 1, (short)7).setName("&7&oSlot " + (i+1)), null);
        }
        for (int i = 36; i < 45; i++) {
            setSlot(i, new EItem(Material.STAINED_GLASS_PANE, 1, (short)15).setName("&7&oHotbar slot " + (i-35)), null);
        }
        setSlot(46, new EItem(Material.CHAINMAIL_HELMET).setName("&7&oHelmet slot"), null);
        setSlot(48, new EItem(Material.CHAINMAIL_CHESTPLATE).setName("&7&oChesplate slot"), null);
        setSlot(50, new EItem(Material.CHAINMAIL_LEGGINGS).setName("&7&oLeggings slot"), null);
        setSlot(52, new EItem(Material.CHAINMAIL_BOOTS).setName("&7&oBoots slot"), null);
    }

    @Override
    protected void onDestroy() {}

    @Override
    protected void onShow(InventoryOpenEvent event) {
        updateContent((Player) event.getPlayer());
    }

    @Override
    protected void onClose(InventoryCloseEvent event) {}

    @Override
    protected void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        Player player = (Player)event.getWhoClicked();
        String kitName = playerKit.get(player.getUniqueId());

        if (event.getRawSlot() == 0) {
            kitModule.giveKit(player, kitName);
        }
    }

    private void updateContent(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerKit.containsKey(uuid)) {
            setSlot(0, new EItem(Material.REDSTONE_BLOCK).setName("&4&lInvalid kit!").setLore("&cNo kit data found for the specified kit."), player);
            return;
        }
        String kitName = playerKit.get(uuid);
        KitData kit = kitModule.getConfig().getKit(kitName);
        if (kit == null) {
            setSlot(0, new EItem(Material.REDSTONE_BLOCK).setName("&4&lInvalid kit!").setLore("&cNo kit data found for the specified kit."), player);
            return;
        }
        EItem icon = kit.getIcon().clone();
        if (icon == null || icon.getType() == Material.AIR) {
            icon = new EItem(Material.ENDER_CHEST);
        }
        icon.setName("&a&l" + kit.getName());
        icon.addLore(EssMessage.CORE_KIT_DISPLAY_MENU_ICON_INFO.msg().params(Param.P("kit", kitName)).getText());
        setSlot(0, icon, player);

        EItem[] items = kit.getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null || items[i].getType() == Material.AIR) {
                continue;
            }
            if (i < 9) {
                setSlot(i+36, items[i], player);
            } else if (i < 36) {
                setSlot(i, items[i], player);
            } else if (i == 36) {
                setSlot(52, items[i], player);
            } else if (i == 37) {
                setSlot(50, items[i], player);
            } else if (i == 38) {
                setSlot(48, items[i], player);
            } else if (i == 39) {
                setSlot(46, items[i], player);
            }
        }
    }

    public void setPlayerKit(Player player, String kit) {
        playerKit.put(player.getUniqueId(), kit);
    }
}
