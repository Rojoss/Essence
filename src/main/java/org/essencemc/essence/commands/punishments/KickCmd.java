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
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.punishments.BanModule;
import org.essencemc.essence.modules.punishments.KickModule;
import org.essencemc.essencecore.arguments.*;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.links.RemoveLink;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.Duration;
import org.essencemc.essencecore.util.Util;

import java.util.List;
import java.util.UUID;

public class KickCmd extends EssenceCommand {

    public KickCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addDependency(KickModule.class);

        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED);
        addArgument("reason", new StringArg(), ArgumentRequirement.OPTIONAL);

        addCommandOption("broadcast-kick", EssMessage.OPT_BAN_BROADCAST.msg(), new BoolArg(true), false);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        KickModule kickModule = (KickModule) getModule(KickModule.class);

        OfflinePlayer player = (OfflinePlayer)result.getArg("player");
        UUID uuid = player.getUniqueId();
        String reason = Util.implode(args, " ", 1).trim();

        kickModule.kick(uuid, sender instanceof Player ? ((Player) sender).getUniqueId() : null, reason);

        if (!result.hasModifier("-s")) {
            EssMessage.CMD_KICK_KICKED.msg().send(sender, true, true, Param.P("player", player.getName()),
                    Param.P("reason", reason.isEmpty() ? EssMessage.CORE_NO_REASON.msg().getText() : reason));
            if ((Boolean)cmdOptions.get("broadcast-kick").getArg().getValue()) {
                EssMessage.CMD_KICK_BROADCAST.msg().broadcast(true, true, Param.P("player", player.getName()), Param.P("punisher", sender.getName()),
                        Param.P("reason", reason.isEmpty() ? EssMessage.CORE_NO_REASON.msg().getText() : reason));
            }
        }
        return true;
    }
}
