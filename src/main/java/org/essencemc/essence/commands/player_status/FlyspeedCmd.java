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

package org.essencemc.essence.commands.player_status;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.IntArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.CmdArgument;

import java.util.List;

public class FlyspeedCmd extends EssenceCommand {


    public FlyspeedCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("speed", new IntArg(0, 100), ArgumentRequirement.REQUIRED_CONSOLE);
        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE, "others");

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }

        Float speed = (Float)result.getArg("speed", 20) / 100F;
        Player player = (Player)result.getArg("player", castPlayer(sender));

        player.setFlySpeed(speed);

        speed *= 100;

        if (!result.hasModifier("-s")) {
            EssMessage.CMD_FEED_OTHER.msg(true, true, castPlayer(sender)).parseArgs(speed.toString()).send(player);
            if (!sender.equals(player)) {
                EssMessage.CMD_FEED_OTHER.msg(true, true, castPlayer(sender)).parseArgs(player.getDisplayName(), speed.toString()).send(sender);
            }
        }

        return true;
    }


}
