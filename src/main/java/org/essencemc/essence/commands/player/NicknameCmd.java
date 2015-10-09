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

package org.essencemc.essence.commands.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.IntArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.CmdArgument;
import org.essencemc.essencecore.util.Util;

import java.util.List;

public class NicknameCmd extends EssenceCommand {

    public NicknameCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addCommandOption("prefix", EssMessage.OPT_NICK_PREFIX.msg(), new StringArg("~"), false);
        addCommandOption("min-characters", EssMessage.OPT_NICK_MIN_CHARS.msg(), new IntArg(3), false);
        addCommandOption("max-characters", EssMessage.OPT_NICK_MAX_CHARS.msg(), new IntArg(16), false);

        addArgument("nickname", new StringArg((Integer)cmdOptions.get("min-characters").getArg().getValue(), (Integer)cmdOptions.get("max-characters").getArg().getValue()), ArgumentRequirement.REQUIRED);
        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE, "others");

        addModifier("-r", EssMessage.MOD_NICK_REMOVE.msg());

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        String nick = (String)result.getArg("nickname");
        Player player = (Player)result.getArg("player", castPlayer(sender));

        if (hasPermission(sender, "color")) {
            player.setDisplayName(Util.color((String)cmdOptions.get("prefix").getArg().getValue() + nick));
        } else {
            player.setDisplayName((String)cmdOptions.get("prefix").getArg().getValue() + nick);
        }

        if (!result.hasModifier("-s")) {
            EssMessage.CMD_NICK_CHANGED.msg(true, true, player).parseArgs(hasPermission(sender, "color") ? player.getDisplayName() : Util.removeColor(player.getDisplayName())).send(player);
            if (!sender.equals(player)) {
                EssMessage.CMD_NICK_OTHER.msg(true, true, player).parseArgs(hasPermission(sender, "color") ? player.getDisplayName() : Util.removeColor(player.getDisplayName()), player.getName()).send(sender);
            }
        }
        return true;
    }

}
