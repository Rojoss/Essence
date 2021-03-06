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

package org.essencemc.essence.commands.misc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.*;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.EText;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.util.Debug;
import org.essencemc.essencecore.util.Util;

import java.util.ArrayList;
import java.util.List;

public class BroadcastCmd extends EssenceCommand {

    public BroadcastCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("message", new StringArg(), ArgumentRequirement.REQUIRED);

        addModifier("-p", EssMessage.MOD_BROACAST_NO_PREFIX.msg(), "prefix");
        addModifier("-b", EssMessage.MOD_BROACAST_BAR.msg(), "bar");

        addOptionalArgument("world", new WorldArg(), EssMessage.OPT_BROADCAST_WORLD.msg());

        addCommandOption("prefix", EssMessage.OPT_BROADCAST_PREFIX.msg(), new StringArg("&8[&9&lBroadcast&8]&3"), true);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        EText text = new EText((result.hasModifier("-p") ? "" : (String)result.getOptionalArg("prefix") + " ") + Util.implode(args, " "));
        if (result.getOptionalArg("world") != null) {
            World world = (World)result.getOptionalArg("world");
            if (result.hasModifier("-b")) {
                text.sendBar(world.getPlayers(), true);
            } else {
                text.send(world.getPlayers(), true, true);
            }
        } else {
            if (result.hasModifier("-b")) {
                text.sendBar(new ArrayList<Player>(Bukkit.getOnlinePlayers()), true);
            } else {
                text.broadcast(true, true);
            }
        }
        return true;
    }
}
