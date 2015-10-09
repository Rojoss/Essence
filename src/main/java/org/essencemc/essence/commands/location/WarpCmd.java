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

package org.essencemc.essence.commands.location;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.CmdArgument;
import org.essencemc.essencecore.message.Message;

import java.util.List;

public class WarpCmd extends EssenceCommand {

    public WarpCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new CmdArgument("name", new StringArg(2, 32), ArgumentRequirement.REQUIRED, ""),
                new CmdArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE, "others")
        };

        addCommandOption("permission-based-warps", EssMessage.OPT_WARP_PERM_BASED.msg(), new BoolArg(true), false);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        String name = (String)result.getArg("name");
        Player player = (Player)result.getArg("player", castPlayer(sender));

        if (Essence.inst().getWarps().getWarp(name) == null) {
            EssMessage.CMD_WARP_INVALID.msg(true, true, castPlayer(sender)).parseArgs(name).send(sender);
            return true;
        }

        if ((Boolean)cmdOptions.get("permission-based-warps").getArg().getValue()) {
            if (!sender.hasPermission("essence.*") && !sender.hasPermission("essence.warps.*") && !sender.hasPermission("essence.warps." + name)) {
                Message.NO_PERM.msg(true, true, castPlayer(sender)).parseArgs("essence.warps." + name).send(sender);
                return true;
            }
        }

        player.teleport(Essence.inst().getWarps().getWarp(name));
        if (!result.hasModifier("-s")) {
            EssMessage.CMD_WARP_USE.msg(true, true, castPlayer(sender)).parseArgs(name).send(sender);
            if (!sender.equals(player)) {
                EssMessage.CMD_WARP_OTHER.msg(true, true, castPlayer(sender)).parseArgs(player.getDisplayName(), name).send(sender);
            }
        }
        return true;
    }

}
