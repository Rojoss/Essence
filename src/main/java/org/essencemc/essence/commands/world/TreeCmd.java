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
import org.bukkit.TreeType;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.LocationArg;
import org.essencemc.essencecore.arguments.MappedListArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.aliases.AliasType;
import org.essencemc.essencecore.aliases.Aliases;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.CmdArgument;

import java.util.HashSet;
import java.util.List;

public class TreeCmd extends EssenceCommand {

    public TreeCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new CmdArgument("type", new MappedListArg(Aliases.getAliasesMap(AliasType.TREES)), ArgumentRequirement.REQUIRED, ""),
                new CmdArgument("location", new LocationArg(), ArgumentRequirement.REQUIRED_CONSOLE, "")
        };

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }

        TreeType type = TreeType.valueOf(result.getArg("type").toString());
        Location location = (Location)result.getArg("location");

        if (location == null) {
            location = ((Player)sender).getTargetBlock((HashSet<Byte>)null, 100).getRelative(BlockFace.UP).getLocation();
        }

        boolean success = location.getWorld().generateTree(location, type);

        if (success) {
            EssMessage.CMD_TREE.msg(true, true, castPlayer(sender)).send(sender);
        } else {
            EssMessage.CMD_TREE_FAILURE.msg(true, true, castPlayer(sender)).send(sender);
        }

        return true;
    }


}
