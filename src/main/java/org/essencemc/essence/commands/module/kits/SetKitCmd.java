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

package org.essencemc.essence.commands.module.kits;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.kits.KitData;
import org.essencemc.essence.modules.kits.KitModule;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.links.ConflictLink;
import org.essencemc.essencecore.entity.EItem;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.modules.Module;

import java.util.ArrayList;
import java.util.List;

public class SetKitCmd extends EssenceCommand {

    public SetKitCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("name", new StringArg(), ArgumentRequirement.REQUIRED);

        addModifier("-i", EssMessage.MOD_KIT_ICON.msg());
        addModifier("-t", EssMessage.MOD_KIT_TOGGLE.msg());

        addLink(new ConflictLink("-i", "-t"));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }

        Module module = EssenceCore.inst().getModules().getModule(KitModule.class);
        if (module == null) {
            Message.MODULE_DISABLED.msg().send(sender, true, true, Param.P("module", "kits core"));
            return true;
        }
        KitModule kitModule = (KitModule)module;

        String name = (String)result.getArg("name");
        KitData kit = kitModule.getConfig().getKit(name);

        //Toggle kit
        if (result.hasModifier("-t")) {
            if (kit == null) {
                EssMessage.CMD_KIT_INVALID.msg().send(sender, true, true, Param.P("input", name));
                return true;
            }
            kit.setEnabled(!kit.isEnabled());
            kitModule.getConfig().setKit(kit, true);
            EssMessage.CMD_KIT_TOGGLE.msg().send(sender, true, true, Param.P("kit", name), Param.P("state", kit.isEnabled() ? Message.ENABLED.msg().getText() : Message.DISABLED.msg().getText()));
            return true;
        }

        //Console can only toggle kits on/off.
        if (!(sender instanceof Player)) {
            Message.CMD_PLAYER_ONLY.msg().params().send(sender);
            return true;
        }
        Player player = (Player)sender;

        //Set icon
        if (result.hasModifier("-i")) {
            if (kit == null) {
                EssMessage.CMD_KIT_INVALID.msg().send(sender, true, true, Param.P("input", name));
                return true;
            }
            if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                EssMessage.CMD_KIT_ICON_AIR.msg().send(sender, true, true);
                return true;
            }
            kit.setIcon(new EItem(player.getItemInHand()));
            kitModule.getConfig().setKit(kit, true);
            EssMessage.CMD_KIT_ICON.msg().send(sender, true, true, Param.P("kit", name));
            return true;
        }

        //Create/update kit contents
        EItem[] items = new EItem[40];
        for (int i = 0; i < items.length; i++) {
            items[i] = new EItem(player.getInventory().getItem(i));
        }

        if (kit == null) {
            if (!hasPermission(sender, "create")) {
                Message.NO_PERM.msg().send(sender, true, true, Param.P("perm", getPermission() + ".create"));
                return true;
            }
            kit = new KitData(name, true, items, null);
            if (!result.hasModifier("-s")) {
                EssMessage.CMD_KIT_CREATED.msg().send(sender, true, true, Param.P("kit", name));
            }
        } else {
            kit.setItems(items);
            if (!result.hasModifier("-s")) {
                EssMessage.CMD_KIT_SET.msg().send(sender, true, true, Param.P("kit", name));
            }
        }
        kitModule.getConfig().setKit(kit, true);
        return true;
    }

}
