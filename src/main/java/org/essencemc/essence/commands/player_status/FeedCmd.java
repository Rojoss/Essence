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
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.essencemc.essence.Message;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.arguments.IntArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.IntArgument;
import org.essencemc.essencecore.commands.arguments.PlayerArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;

import java.util.List;

public class FeedCmd extends EssenceCommand {

    public FeedCmd(EssenceCore ess, String command, String description, String permission, List<String> aliases) {
        super(ess, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new PlayerArgument("player", ArgumentRequirement.REQUIRED_CONSOLE, "others"),
                new IntArgument("amount", ArgumentRequirement.OPTIONAL, "", 0, 20, false)
        };

        addCommandOption("saturation", Message.OPT_FEED_SATURATION.msg(), new IntArg(5));
        addCommandOption("exhaustion", Message.OPT_FEED_EXHAUSTION.msg(), new BoolArg(true));

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
        int amount = (int)result.getArg("amount", 20);

        FoodLevelChangeEvent foodLevelChangeEvent = new FoodLevelChangeEvent(player, amount);
        plugin.getServer().getPluginManager().callEvent(foodLevelChangeEvent);
        if (foodLevelChangeEvent.isCancelled()) {
            return true;
        }

        player.setFoodLevel(amount);
        player.setSaturation((Integer)result.getOptionalArg("saturation"));
        if ((Boolean)result.getOptionalArg("exhaustion")) {
            player.setExhaustion(0F);
        }

        if (!result.hasModifier("-s")) {
            player.sendMessage(Message.CMD_FEED_FEEDED.msg().getMsg(true));
            if (!sender.equals(player)) {
                sender.sendMessage(Message.CMD_FEED_OTHER.msg().getMsg(true, player.getDisplayName()));
            }
        }
        return true;
    }
}
