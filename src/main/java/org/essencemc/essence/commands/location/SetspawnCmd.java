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

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.spawn.SpawnModule;
import org.essencemc.essencecore.arguments.LocationArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.links.LinkLink;
import org.essencemc.essencecore.commands.links.MakeOptionalLink;
import org.essencemc.essencecore.message.Param;

import java.util.List;

public class SetspawnCmd extends EssenceCommand {

    public SetspawnCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addDependency(SpawnModule.class);

        //addModifier();
        addOptionalArgument("player", new PlayerArg());
        addArgument("location", new LocationArg(), ArgumentRequirement.REQUIRED_CONSOLE);

        // TODO: Fix LinkLink order.
        addLink(new LinkLink("-p", "player"));
        addLink(new MakeOptionalLink("player", "location"));
        addLink(new MakeOptionalLink("-p", "location"));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        boolean eSpawn = false;
        boolean playerArg = result.getOptionalArg("player") != null;

        SpawnModule spawns = (SpawnModule)getModule(SpawnModule.class);

        Player player = (Player) result.getOptionalArg("player");
        Location spawn;
        if(result.hasModifier("-p")){
            spawn = player.getLocation();
        } else {
            spawn = (Location)result.getArg("location", player == null ? (sender instanceof Player ? castPlayer(sender).getLocation() : null) : player.getLocation());
        }

       // Location spawn = (Location)result.getArg("location", player == null ? (sender instanceof Player ? castPlayer(sender).getLocation() : null) : player.getLocation());
       // Player player = (Player) (result.getOptionalArg("player") == null ? castPlayer(sender) : result.getOptionalArg("player"));
       // Location spawn = (Location)result.getArg("location", sender instanceof Player ? castPlayer(sender).getLocation() : null);

        //TODO: Add group spawn.
        if(playerArg){
            String uuid = player.getUniqueId().toString();
            spawns.setSpawn(uuid, spawn);
        } else {
            spawns.setSpawn(null, spawn);
            eSpawn = true;
        }

        if (!result.hasModifier("-s")) {
            String var = eSpawn ? getPlugin().getServer().getServerName() : player.getDisplayName();
            EssMessage.CMD_SETSPAWN_SET.msg().send(sender, Param.P("location", parse(spawn)), Param.P("variable", var));
        }
        return true;
    }

    private String parse(Location loc) {
        return "X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ() + ":" + loc.getWorld().getName();
    }
}