package org.essencemc.essence.modules.kits;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.Essence;
import org.essencemc.essence.modules.signs.SignModule;
import org.essencemc.essence.modules.signs.config.SignData;
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

public class KitsMenu extends Menu {

    private KitModule kitModule;
    private Map<UUID, String> playerMenu = new HashMap<UUID, String>();

    public KitsMenu(KitModule kitModule) {
        super(Essence.inst(), "ess-kits", 6, EssMessage.CORE_KITS_MENU_TITLE.msg().getText());
        this.kitModule = kitModule;
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
        UUID uuid = player.getUniqueId();
        String menu = playerMenu.get(uuid);

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
            if (kitModule.getConfig().getKitList().size() > page * 45) {
                playerMenu.put(uuid, "main-" + (page+1));
                updateContent(player);
            }
        } else if (event.getRawSlot() > 8 && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            //Click on kit.
            if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
                kitModule.getKitDisplayMenu().setPlayerKit(player, Util.stripAllColor(new EItem(event.getCurrentItem()).getName()));
                kitModule.getKitDisplayMenu().show(player);
            } else {
                kitModule.giveKit(player, Util.stripAllColor(new EItem(event.getCurrentItem()).getName()));
            }
        }
    }

    private void updateContent(Player player) {
        UUID uuid = player.getUniqueId();
        if (!playerMenu.containsKey(uuid)) {
            playerMenu.put(uuid, "main-0");
        }
        String menu = playerMenu.get(uuid);
        int page = NumberUtil.getInt(menu.split("-")[1]);

        setSlot(0, new EItem(Material.SKULL_ITEM).setName(Message.PREVIOUS_PAGE.msg().getText()).setSkull("MHF_ArrowLeft").addAllFlags(true), player);
        setSlot(4, new EItem(Material.PAPER).setName(Message.INFORMATION.msg().getText()).setLore(EssMessage.CORE_KITS_MENU_INFO.msg().getText()), player);
        setSlot(8, new EItem(Material.SKULL_ITEM).setName(Message.NEXT_PAGE.msg().getText()).setSkull("MHF_ArrowRight").addAllFlags(true), player);

        List<KitData> kits = kitModule.getConfig().getKitList();
        int slot = 9;
        for (int i = page * 45; i < page * 45 + 45 && i < kits.size(); i++) {
            KitData kit = kits.get(i);
            EItem item = kit.getIcon();
            if (item == null || item.getType() == Material.AIR) {
                item = new EItem(Material.ENDER_CHEST);
            }
            item.setName("&a&l" + kit.getName());
            setSlot(slot, item, player);
            slot++;
        }
    }
}
