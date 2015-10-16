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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.kits.KitData;
import org.essencemc.essence.modules.kits.KitModule;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.links.ConflictLink;
import org.essencemc.essencecore.commands.links.RemoveLink;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.modules.Module;
import org.essencemc.essencecore.util.Util;

import java.util.ArrayList;
import java.util.List;

public class KitCmd extends EssenceCommand {

    public KitCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("name", new StringArg(), ArgumentRequirement.REQUIRED);
        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE);

        addModifier("-d", EssMessage.MOD_KIT_DISPLAY.msg(), "display");
        addModifier("-l", EssMessage.MOD_KIT_LIST.msg(), "list");

        addLink(new RemoveLink("-l", "player"));
        addLink(new RemoveLink("-l", "name"));
        addLink(new ConflictLink("-l", "-d"));

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

        if (result.hasModifier("-l")) {
            List<String> kits = new ArrayList<String>();
            for (KitData kit : kitModule.getConfig().getKitList()) {
                if (Util.hasPermission(sender, "essence.kit.use." + kit)) {
                    kits.add(kit.getName());
                }
            }
            EssMessage.CMD_KIT_LIST.msg().send(sender, true, true, Param.P("kits", Util.implode(kits, ", ")));
            return true;
        }

        String name = (String)result.getArg("name");
        Player player = result.getArg("player") == null ? (Player)sender : (Player)result.getArg("player");

        KitData kit = kitModule.getConfig().getKit(name);
        if (kit == null) {
            EssMessage.CMD_KIT_INVALID.msg().send(player, true, true, Param.P("input", name));
            return true;
        }

        if (result.hasModifier("-d")) {
            kitModule.getKitDisplayMenu().setPlayerKit(player, name);
            kitModule.getKitDisplayMenu().show(player);
            return true;
        }

        kitModule.giveKit(player, name);
        if (!result.hasModifier("-s")) {
            EssMessage.CMD_KIT_GIVEN.msg().send(player, true, true, Param.P("kit", name));
            if (!sender.equals(player)) {
                EssMessage.CMD_KIT_GIVEN_OTHER.msg().send(player, true, true, Param.P("kit", name), Param.P("player", player.getDisplayName()));
            }
        }
        return true;
    }

}
