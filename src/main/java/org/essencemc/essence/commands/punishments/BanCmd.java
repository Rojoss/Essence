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

package org.essencemc.essence.commands.punishments;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.modules.ban.BanModule;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.IntArgument;
import org.essencemc.essencecore.commands.arguments.OfflinePlayerArgument;
import org.essencemc.essencecore.commands.arguments.StringArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;
import org.essencemc.essencecore.modules.Module;

import java.util.List;

public class BanCmd extends EssenceCommand {

    //TODO: Implement this properly.........

    public BanCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new OfflinePlayerArgument("player", ArgumentRequirement.REQUIRED, ""),
                //TODO: Change to DurationArgument like 1h30m10s
                new IntArgument("duration", ArgumentRequirement.OPTIONAL, ""),
                new StringArgument("reason", ArgumentRequirement.OPTIONAL, "")
        };

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        OfflinePlayer player = (OfflinePlayer)result.getArg("player");
        //TODO: Get default duration.
        int duration = (Integer)result.getArg("duration", 30000);
        //TODO: Get default reason.
        String reason = (String)result.getArg("reason", "IT WORKS!");

        Module module = EssenceCore.inst().getModules().getModule(BanModule.class);
        if (module == null) {
            sender.sendMessage("No ban module... (shouldn't happen EVER (I think?))");
            return true;
        }
        BanModule banModule = (BanModule)module;

        //TODO: Don't need unbanning here... and proper messages etc...
        if (banModule.isBanned(player.getUniqueId())) {
            if (banModule.unban(player.getUniqueId())) {
                sender.sendMessage("Unbanned!!!");
            } else {
                sender.sendMessage("Unban failed...");
            }
        } else {
            if (banModule.ban(player.getUniqueId(), (long)duration, ((Player)sender).getUniqueId(), reason)) {
                sender.sendMessage("Banned!!!");
            } else {
                sender.sendMessage("Ban failed...");
            }
        }
        return true;
    }
}
