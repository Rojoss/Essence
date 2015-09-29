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

package org.essencemc.essence.commands.world;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.commands.arguments.LocationArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;
import org.essencemc.essencecore.message.Message;

import java.util.List;

public class LightningCmd extends EssenceCommand {

    public LightningCmd(EssenceCore ess, String command, String description, String permission, List<String> aliases) {
        super(ess, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new LocationArgument("location", ArgumentRequirement.REQUIRED, "")
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

        Location location = (Location) result.getArg("location");
        location.setY(location.getWorld().getHighestBlockAt(location).getY());

        location.getWorld().strikeLightning(location);

        if (!result.hasModifier("-s")) {
            sender.sendMessage(Message.CMD_LIGHTNING.msg().getMsg(true));
        }

        return true;
    }

}
