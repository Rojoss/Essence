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

package org.essencemc.essence.commands.item;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;

import java.util.List;
import java.util.Set;

public class SkullCmd extends EssenceCommand {
    public SkullCmd(Plugin plugin, String label, String description, String permission, List<String> aliases) {
        super(plugin, label, description, permission, aliases);

        addArgument("owner", new StringArg(), ArgumentRequirement.REQUIRED);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Message.CMD_PLAYER_ONLY.msg().send(sender);
            return true;
        }

        ArgumentParseResults result = parseArgs(this, sender, args);

        if(!result.success) {
            return true;
        }

        Block block = castPlayer(sender).getTargetBlock(((Set<Material>) null), 6);

        if((block == null) || block.getType().equals(Material.AIR)) {
            EssMessage.CMD_SKULL_INVALID_BLOCK.msg().send(sender);
            return true;
        }

        String owner = ((String) result.getArg("owner"));

        block.setType(Material.SKULL);

        if(owner.length() > 16) {
            ess.getSkull().setSkullUrl(owner, block);
        } else {
            Skull skull = ((Skull) block.getState());
            skull.setSkullType(SkullType.PLAYER);
            skull.setOwner(owner);
            skull.update(true);
        }

        if(!result.hasModifier("-s")) {
            EssMessage.CMD_SKULL_SET.msg().send(sender, Param.P("owner", owner));
        }

        return true;
    }
}