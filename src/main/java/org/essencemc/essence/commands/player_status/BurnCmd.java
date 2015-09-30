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
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.commands.arguments.IntArgument;
import org.essencemc.essencecore.commands.arguments.PlayerArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;
import org.essencemc.essencecore.message.Message;

import java.util.List;

public class BurnCmd extends EssenceCommand {

    public BurnCmd(EssenceCore ess, String label, String description, String permission, List<String> aliases) {
        super(ess, label, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new IntArgument("duration", ArgumentRequirement.REQUIRED, ""),
                new PlayerArgument("player", ArgumentRequirement.REQUIRED_CONSOLE, "others")
        };

        addModifier("-i", EssMessage.MOD_BURN_INCREMENT.msg());

        addCommandOption("ticks-instead-of-seconds", EssMessage.OPT_BURN_TICKS.msg(), new BoolArg(true));

        register();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        Player player = (Player)result.getArg("player", castPlayer(sender));

        int ticks = (int)result.getArg("duration") * 20;
        if ((Boolean)result.getOptionalArg("ticks-instead-of-seconds")) {
            ticks /= 20;
        }

        if (ticks < 0) {
            result.addModifier("-i");
        }

        if (result.hasModifier("-i")) {
            if (ticks < 0 && ticks > player.getFireTicks()) ticks = 0;

            player.setFireTicks(player.getFireTicks() + ticks);
        } else {
            player.setFireTicks(ticks);
        }

        if (!result.hasModifier("-s")) {
            player.sendMessage(EssMessage.CMD_BURN.msg().getMsg(true, Integer.toString(ticks)));
            if (!sender.equals(player)) {
                sender.sendMessage(EssMessage.CMD_HEAL_OTHER.msg().getMsg(true, player.getDisplayName(), Integer.toString(ticks)));
            }
        }

        return true;
    }

}
