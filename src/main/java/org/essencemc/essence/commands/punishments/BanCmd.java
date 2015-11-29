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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.ban.Ban;
import org.essencemc.essence.modules.ban.BanModule;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.*;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.CmdArgument;
import org.essencemc.essencecore.commands.links.RemoveLink;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.modules.Module;
import org.essencemc.essencecore.util.Duration;
import org.essencemc.essencecore.util.Util;

import java.util.List;
import java.util.UUID;

public class BanCmd extends EssenceCommand {

    public BanCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addDependency(BanModule.class);

        addArgument("player", new OfflinePlayerArg(), ArgumentRequirement.REQUIRED);
        addArgument("duration", new DurationArg(), ArgumentRequirement.OPTIONAL);
        addArgument("reason", new StringArg(), ArgumentRequirement.OPTIONAL);

        addModifier("-p", EssMessage.MOD_BAN_PERMANENT.msg(), "permanent");

        addCommandOption("default-duration", EssMessage.OPT_BAN_DEFAULT_DURATION.msg(), new DurationArg(new Duration("1d")), false);
        addCommandOption("broadcast-ban", EssMessage.OPT_BAN_BROADCAST.msg(), new BoolArg(true), false);

        addLink(new RemoveLink("-p", "duration"));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        BanModule banmodule = (BanModule)getModule(BanModule.class);

        OfflinePlayer player = (OfflinePlayer)result.getArg("player");
        UUID uuid = player.getUniqueId();
        Duration duration = result.hasModifier("-p") ? new Duration("9999d") : (Duration)result.getArg("duration");
        String reason = Util.implode(args, " ", result.hasModifier("-p") ? 1 : 2).trim();

        if (banmodule.isBanned(uuid)) {
            EssMessage.CMD_BAN_ALREADY_BANNED.msg().send(sender, true, true, Param.P("player", player.getName()));
            return true;
        }

        banmodule.ban(uuid, duration.getMS(), sender instanceof Player ? ((Player) sender).getUniqueId() : null, reason);
        if (player.isOnline()) {
            String kickMsg = EssMessage.CORE_BAN_BANNED.msg().params(
                    Param.P("reason", reason.isEmpty() ? EssMessage.CORE_BAN_NOREASON.msg().getText() : reason),
                    Param.P("remaining", duration.getString()),
                    Param.P("duration", duration.getString()),
                    Param.P("time", Util.getTimeStamp().toString()),
                    Param.P("punisher", sender.getName())).color().getText();
            ((Player)player).kickPlayer(kickMsg);
        }

        if (!result.hasModifier("-s")) {
            EssMessage.CMD_BAN_BANNED.msg().send(sender, true, true, Param.P("player", player.getName()),
                    Param.P("reason", reason.isEmpty() ? EssMessage.CORE_BAN_NOREASON.msg().getText() : reason), Param.P("duration", duration.getString()));
            if ((Boolean)cmdOptions.get("broadcast-ban").getArg().getValue()) {
                EssMessage.CMD_BAN_BROADCAST.msg().broadcast(true, true, Param.P("player", player.getName()), Param.P("punisher", sender.getName()),
                        Param.P("reason", reason.isEmpty() ? EssMessage.CORE_BAN_NOREASON.msg().getText() : reason), Param.P("duration", duration.getString()));
            }
        }
        return true;
    }
}
