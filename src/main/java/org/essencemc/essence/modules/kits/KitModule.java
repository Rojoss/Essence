package org.essencemc.essence.modules.kits;

import org.bukkit.entity.Player;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.modules.Module;
import org.essencemc.essencecore.util.InvUtil;

public class KitModule extends Module {

    private KitsCfg config;
    private KitsMenu kitsMenu;
    private KitDisplayMenu kitDisplayMenu;

    public KitModule(String name) {
        super(Essence.inst(), name);
    }

    @Override
    protected void onEnable() {
        config = new KitsCfg("plugins/Essence/modules/kits/Kits.yml");
        kitsMenu = new KitsMenu(this);
        kitDisplayMenu = new KitDisplayMenu(this);
    }

    @Override
    protected void onDisable() {}

    @Override
    protected void onReload() {
        config.load();
    }

    public KitsCfg getConfig() {
        return config;
    }

    public KitsMenu getKitsMenu() {
        return kitsMenu;
    }

    public KitDisplayMenu getKitDisplayMenu() {
        return kitDisplayMenu;
    }

    public boolean giveKit(Player player, String name) {
        KitData kit = config.getKit(name);
        if (kit == null) {
            EssMessage.CMD_KIT_INVALID.msg().send(player, true, true, Param.P("input", name));
            return false;
        }
        if (!kit.isEnabled()) {
            EssMessage.CMD_KIT_DISABLED.msg().send(player, true, true, Param.P("kit", name));
            return false;
        }
        InvUtil.setItems(player.getInventory(), kit.getItems(), true, false);
        return true;
    }
}
