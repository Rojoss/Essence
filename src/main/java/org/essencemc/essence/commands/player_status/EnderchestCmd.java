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
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.commands.arguments.PlayerArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;
import org.essencemc.essencecore.message.Message;

import java.util.List;

public class EnderchestCmd extends EssenceCommand {

    public EnderchestCmd(EssenceCore ess, String label, String description, String permission, List<String> aliases) {
        super(ess, label, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new PlayerArgument("player", ArgumentRequirement.OPTIONAL, "others")
        };

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Message.CMD_PLAYER_ONLY.msg().getMsg(true));
            return true;
        }

        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }

        // TODO: Offline player support

        Player player = (Player)sender;
        Player targetPlayer = (Player)result.getArg("player", player);

        player.openInventory(targetPlayer.getEnderChest());

        if (!result.hasModifier("-s")) {
            sender.sendMessage(Message.CMD_ENDERCHEST.msg().getMsg(true));
            if (!sender.equals(player)) {
                sender.sendMessage(Message.CMD_ENDERCHEST_OTHER.msg().getMsg(true, player.getName()));
            }
        }

        return true;
    }

}
