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

package org.essencemc.essence.commands.fun;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.arguments.VectorArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.Param;

import java.util.List;

public class PushCmd extends EssenceCommand {

    public PushCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("velocity", new VectorArg(), ArgumentRequirement.REQUIRED);
        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE, "others");

        addModifier("-r", EssMessage.MOD_PUSH_RELATIVE.msg());

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        Vector velocity = (Vector)result.getArg("velocity");
        Player player = result.getArg("player") == null ? castPlayer(sender) : (Player)result.getArg("player");

        if (result.hasModifier("-r")) {
            Vector dir = player.getLocation().getDirection();
            velocity = new Vector(dir.getX() * velocity.getX(), velocity.getY(), dir.getZ() * velocity.getZ());
        }

        player.setVelocity(velocity);
        if (!result.hasModifier("-s")) {
            EssMessage.CMD_PUSH.msg().send(player, Param.P("velocity", VectorArg.Parse(velocity)));
            if (!sender.equals(player)) {
                EssMessage.CMD_PUSH_OTHER.msg().send(sender, Param.P("player", player.getDisplayName()), Param.P("velocity", VectorArg.Parse(velocity)));
            }
        }
        return true;
    }

}
