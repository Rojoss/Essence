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

package org.essencemc.essence.commands.teleport;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.LocationArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.arguments.WorldArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.links.MakeRequiredConsoleLink;
import org.essencemc.essencecore.commands.links.RemoveLink;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldCmd extends EssenceCommand {

    public WorldCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("world", new WorldArg(), ArgumentRequirement.OPTIONAL);
        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE, "others");

        addModifier("-i", EssMessage.MOD_WORLD_INFO.msg(), "info");

        addLink(new RemoveLink("-i", "player"));
        addLink(new MakeRequiredConsoleLink("-i", "world"));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        World world = result.getArg("world") == null ? null : (World)result.getArg("world");
        if (world == null) {
            if (sender instanceof Player) {
                world = ((Player)sender).getWorld();
            } else if (sender instanceof BlockCommandSender) {
                world = ((BlockCommandSender)sender).getBlock().getWorld();
            }
        }

        if (result.hasModifier("-i")) {
            EssMessage.CMD_WORLD_INFO.msg().send(sender, Param.P("name", world.getName()), Param.P("uuid", world.getUID().toString()), Param.P("type", world.getWorldType().getName()),
                    Param.P("environment", world.getEnvironment().toString()), Param.P("difficulty", world.getDifficulty().name()), Param.P("chunks", Integer.toString(world.getLoadedChunks().length)),
                    Param.P("seed", Long.toString(world.getSeed())), Param.P("spawn", LocationArg.Parse(world.getSpawnLocation())));
            return true;
        }

        Player player = result.getArg("player") == null ? castPlayer(sender) : (Player)result.getArg("player");

        if (args.length < 1) {
            List<String> worlds = new ArrayList<String>();
            for (World w : Bukkit.getWorlds()) {
                worlds.add(w.getName());
            }
            EssMessage.CMD_WORLD_LIST.msg().send(sender, Param.P("world", player.getWorld().getName()), Param.P("worlds", Util.implode(worlds, ", ")));
            return true;
        }

        player.teleport(world.getSpawnLocation());
        if (!result.hasModifier("-s")) {
            EssMessage.CMD_WORLD_TELEPORTED.msg().send(player, Param.P("world", world.getName()));
            if (!sender.equals(player)) {
                EssMessage.CMD_WORLD_TELEPORTED_OTHER.msg().send(sender, Param.P("player", player.getDisplayName()), Param.P("world", world.getName()));
            }
        }
        return true;
    }

}
