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
import org.essencemc.essence.Essence;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.commands.arguments.StringArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;

import java.util.List;

public class DelWarpCmd extends EssenceCommand {

    public DelWarpCmd(EssenceCore ess, String command, String description, String permission, List<String> aliases) {
        super(ess, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new StringArgument("name", ArgumentRequirement.REQUIRED, "", 2, 32)
        };

        addModifier("-a", EssMessage.MOD_DELWARP_ALL.msg());

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            //TODO: Some way to not parse the argument if -a is specified.
            if (result.hasModifier("-a")) {
                Essence.inst().getWarps().clear();
                if (!result.hasModifier("-s")) {
                    sender.sendMessage(EssMessage.CMD_WARP_DELETED_AlL.msg().getMsg(true));
                }
            }
            return true;
        }
        args = result.getArgs();

        String name = (String)result.getArg("name");
        if (!Essence.inst().getWarps().delWarp(name)) {
            sender.sendMessage(EssMessage.CMD_WARP_INVALID.msg().getMsg(true, name));
            return true;
        }

        if (!result.hasModifier("-s")) {
            sender.sendMessage(EssMessage.CMD_WARP_DELETED.msg().getMsg(true, name));
        }
        return true;
    }

}
