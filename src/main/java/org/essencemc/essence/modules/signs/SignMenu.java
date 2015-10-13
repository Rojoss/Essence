package org.essencemc.essence.modules.signs;

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
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.NumberUtil;
import org.essencemc.essencecore.util.Util;

import java.util.*;

public class SignMenu extends Menu {

    private SignModule signModule;
    private Map<UUID, String> playerMenu = new HashMap<UUID, String>();
    private Map<UUID, String> input = new HashMap<UUID, String>();

    private String[] actionNames = new String[] {"Left click", "Right click", "Shift + Left click", "Shift + Right click"};

    public SignMenu(SignModule signModule) {
        super(Essence.inst(), "sign-edit", 6, "&9&lSign Editing");
        this.signModule = signModule;
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

        Player player = (Player)event.getWhoClicked();
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
                if (signModule.getConfig().getSignList().size() > page * 45) {
                    playerMenu.put(uuid, "main-" + (page+1));
                    updateContent(player);
                }
            } else if (event.getRawSlot() == 4) {
                //New sign
                input.put(uuid, "name");
                player.closeInventory();
                EssMessage.CORE_SIGN_ADD.msg().send(player, true, true);
            } else if (event.getRawSlot() == 7) {
                //Close menu
                playerMenu.remove(uuid);
                player.closeInventory();
            } else if (event.getRawSlot() > 8 && event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.SIGN) {
                //Click on sign.
                playerMenu.put(uuid, "sign-" + Util.stripAllColor(new EItem(event.getCurrentItem()).getName()));
                updateContent(player);
            }
        } else {
            String name = menu.split("-")[1];
            SignData sign = signModule.getConfig().getSign(name);
            if (sign == null && (event.getRawSlot() != 0 || event.getRawSlot() != 8)) {
                return;
            }

            //Sign edit menu
            if (event.getRawSlot() == 0) {
                //Back to list
                playerMenu.put(uuid, "main-0");
                updateContent(player);
            } else if (event.getRawSlot() == 8) {
                //Close menu
                playerMenu.remove(uuid);
                player.closeInventory();
            } else if (event.getRawSlot() >= 18 && event.getRawSlot() <= 21) {
                //Edit lines
                if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    sign.setLine(event.getRawSlot() - 18, "");
                    signModule.getConfig().setSign(sign, true);
                    updateContent(player);
                } else if (event.getClick() == ClickType.SHIFT_LEFT) {
                    sign.setUniqueLine(event.getRawSlot() - 18);
                    signModule.getConfig().setSign(sign, true);
                    updateContent(player);
                } else {
                    input.put(uuid, "line-" + (event.getRawSlot()-18));
                    player.closeInventory();
                    EssMessage.CORE_SIGN_SET_LINE.msg().send(player, true, true, Param.P("line", Integer.toString(event.getRawSlot()-17)));
                }
            } else if (event.getRawSlot() >= 23 && event.getRawSlot() <= 26) {
                //Edit actions
                if (event.getClick() == ClickType.SHIFT_RIGHT) {
                    sign.setAction(event.getRawSlot() - 23, "");
                    signModule.getConfig().setSign(sign, true);
                    updateContent(player);
                } else {
                    input.put(uuid, "action-" + (event.getRawSlot()-23));
                    player.closeInventory();
                    EssMessage.CORE_SIGN_SET_ACTION.msg().send(player, true, true, Param.P("action", actionNames[event.getRawSlot()-23]));
                }
            } else if (event.getRawSlot() == 37) {
                //Toggle attach
                sign.setIsAttachedBlockAction(!sign.isAttachedBlockAction());
                signModule.getConfig().setSign(sign, true);
                updateContent(player);
            } else if (event.getRawSlot() == 42) {
                //Enable/disable
                sign.setEnabled(!sign.isEnabled());
                signModule.getConfig().setSign(sign, true);
                updateContent(player);
            } else if (event.getRawSlot() == 43) {
                //Delete
                //TODO: Show confirmation menu
                signModule.getConfig().deleteSign(name);

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
            setSlot(0, new EItem(Material.SKULL_ITEM).setName("&6&lPrevious Page").setSkull("MHF_ArrowLeft").addAllFlags(true), player);
            setSlot(1, new EItem(Material.PAPER).setName("&6&lInformation").setLore("&7Click on any of the &asigns &7to &aedit &7it.", "&7Click on the &elog &7to create a &enew &7sign."), player);
            setSlot(4, new EItem(Material.LOG).setName("&a&lNew Sign").setLore("&7Adds a new sign to the list.", "&7The menu will close so you type a name in chat.", "&7When it's added click on the sign", "&7to set it up and enable it."), player);
            setSlot(7, new EItem(Material.STAINED_CLAY, 1, (short) 14).setName("&c&lClose").setLore("&7Close the sign editing menu."), player);
            setSlot(8, new EItem(Material.SKULL_ITEM).setName("&6&lNext Page").setSkull("MHF_ArrowRight").addAllFlags(true), player);

            int page = NumberUtil.getInt(menu.split("-")[1]);

            List<SignData> signs = signModule.getConfig().getSignList();
            int slot = 9;
            for (int i = page * 45; i < page * 45 + 45 && i < signs.size(); i++) {
                SignData sign = signs.get(i);
                String[] lore = new String[] {"&7" + sign.getLine(0), "&7" + sign.getLine(1), "&7" + sign.getLine(2), "&7" + sign.getLine(3)};
                setSlot(slot, new EItem(Material.SIGN).setName("&e&l" + sign.getName()).setLore(lore), player);
                slot++;
            }
        } else {
            //Sign edit menu
            String name = menu.split("-")[1];
            setSlot(0, new EItem(Material.SKULL_ITEM).setName("&6&lBack").setSkull("MHF_ArrowLeft").addAllFlags(true), player);
            setSlot(1, new EItem(Material.PAPER).setName("&6&lInformation").setLore("&7Click on any of the &asigns &7to &aedit &7it.", "&7Click on the &elog &7to create a &enew &7sign."), player);
            setSlot(8, new EItem(Material.STAINED_CLAY, 1, (short) 14).setName("&c&lClose").setLore("&7Close the sign editing menu."), player);

            int[] emptySlots = new int[] {9,10,11,12,13,14,15,16,17, 22, 27,28,29,30,31,32,33,34,35, 36,44, 45,46,47,48,49,50,51,52,53};
            for (int slot : emptySlots) {
                setSlot(slot, new EItem(Material.STAINED_GLASS_PANE,1, (short)15).setName("&8#").addAllFlags(true));
            }

            SignData sign = signModule.getConfig().getSign(name);
            if (sign == null) {
                setSlot(4, new EItem(Material.SIGN).setName("&e&l" + name).setLore("&c&lNo sign data found!"), player);
                return;
            }
            setSlot(4, new EItem(Material.SIGN).setName("&e&l" + name).setLore("&7" + sign.getLine(0), "&7" + sign.getLine(1), "&7" + sign.getLine(2), "&7" + sign.getLine(3)), player);

            for (int i = 0; i < 4; i++) {
                EItem item = new EItem(Material.NAME_TAG, i+1).setName("&6Line " + (i+1)).setLore(sign.getLine(i).isEmpty() ? "&c&oempty" : "&e" + sign.getLine(i),
                        "&7Click to edit this line.", "&7Shift right click to clear it.", "&7Shift left click to make this the unique line.");
                if (sign.getUniqueLine() == i) {
                    item.makeGlowing(true);
                    item.addLore("&dThis is the &d&lunique line&d!", "&dThis means any sign matching this line", "&dwill be considered a " + sign.getName() + " sign!");
                }
                setSlot(i+18, item);
            }

            for (int i = 0; i < 4; i++) {
                setSlot(i + 23, new EItem(Material.COMMAND, i + 1).setName("&6" + actionNames[i]).setLore(sign.getAction(i).isEmpty() ? "&c&onone" : "&e" + sign.getAction(i),
                        "&7Click to edit this action.", "&7Shift right click to clear it."));
            }

            if (sign.isAttachedBlockAction()) {
                setSlot(37, new EItem(Material.SLIME_BALL).setName("&aWorks on attached blocks").setLore("&7The sign action(s) will be executed",
                        "&7When clicking on blocks around the sign.", "&8Click to disable."), player);
            } else {
                setSlot(37, new EItem(Material.FIREWORK_CHARGE).setName("&7Works on signs only").setLore("&7The sign action(s) will only be executed",
                        "&7when clicking on the sign itself.", "&8Click to make it work on nearby blocks too.").addAllFlags(true), player);
            }

            if (sign.isEnabled()) {
                setSlot(42, new EItem(Material.INK_SACK, 1, (short)10).setName("&a&lEnabled").setLore("&7This sign is currently &aenabled&7.", "&8Click to disable the sign."), player);
            } else {
                setSlot(42, new EItem(Material.INK_SACK, 1, (short)8).setName("&c&lDisabled").setLore("&7this sign is currently &cdisabled&7.",
                        "&7&oUsers will still be blocked", "&7&ofrom creating/breaking this sign.", "&8Click to enable the sign."), player);
            }

            setSlot(43, new EItem(Material.BARRIER).setName("&4&lDELETE").setLore("&c&lThis will delete the sign completely!", "&7All data you set will be lost!"), player);
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

        if (type.equalsIgnoreCase("name")) {
            //New sign name input
            if (!string.matches("[a-zA-Z]+")) {
                EssMessage.CORE_SIGN_INVALID_NAME.msg().send(player, true, true, Param.P("input", string));
                return;
            }
            if (signModule.getConfig().getSign(string) != null) {
                EssMessage.CORE_SIGN_NAME_EXISTS.msg().send(player, true, true);
                return;
            }

            //Create the sign and open that menu.
            input.remove(player.getUniqueId());
            signModule.getConfig().setSign(new SignData(string, false, false, 0, new String[] {"", "", "", ""}, new String[] {"", "", "", ""}), true);
            playerMenu.put(player.getUniqueId(), "sign-" + string);

            show(player);
            updateContent(player);
            return;
        }


        String menu = playerMenu.get(uuid);
        String name = menu.split("-")[1];
        SignData sign = signModule.getConfig().getSign(name);
        if (sign == null) {
            //This should never happen but just in case. (Don't want users getting stuck in input mode)
            input.remove(player.getUniqueId());
            EssMessage.INPUT_MODE_DISABLED.msg().send(player, true, true);
            return;
        }

        int index = NumberUtil.getInt(type.split("-")[1]);
        if (type.startsWith("line")) {
            if (sign.getUniqueLine() == index) {
                for (SignData signData : signModule.getConfig().getSignList()) {
                    if (signData.getLine(signData.getUniqueLine()).equalsIgnoreCase(string)) {
                        EssMessage.CORE_SIGN_LINE_NOT_UNIQUE.msg().send(player, true, true, Param.P("sign", signData.getName()));
                        return;
                    }
                }
            }

            input.remove(player.getUniqueId());
            sign.setLine(index, string);
            show(player);
            updateContent(player);
            return;
        }

        if (type.startsWith("action")) {
            input.remove(player.getUniqueId());
            sign.setAction(index, string);
            show(player);
            updateContent(player);
            return;
        }
    }
}
