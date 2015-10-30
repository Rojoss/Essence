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
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.spawn.SpawnModule;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.links.ConflictLink;
import org.essencemc.essencecore.message.Param;

import java.util.List;

public class DelspawnCmd extends EssenceCommand {

    public DelspawnCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addDependency(SpawnModule.class);

        addModifier("-all", EssMessage.MOD_DELSPAWN_ALL.msg());
        addModifier("-allp", EssMessage.MOD_DELSPAWN_ALLPLAYERS.msg());

        addArgument("player", new PlayerArg(), ArgumentRequirement.OPTIONAL);

        addLink(new ConflictLink("-all", "player"));
        addLink(new ConflictLink("-allp", "player"));
        addLink(new ConflictLink("-all", "-allp"));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if(!result.success) {
            return true;
        }

        SpawnModule spawns = (SpawnModule)getModule(SpawnModule.class);
        Player player = (Player)result.getArg("player", null);
        String playerName = "";

        EssMessage msg;

        if(result.hasModifier("-all")){
            spawns.delAllSpawns();
            msg = EssMessage.CMD_DELSPAWN_ALL;
        } else if(result.hasModifier("-allp")){
            spawns.delAllPlayerSpawns();
            msg = EssMessage.CMD_DELSPAWN_ALLPLAYERS;
        } else if(player == null) {
            spawns.delMainSpawn();
            msg = EssMessage.CMD_DELSPAWN_MAIN;
        } else {
            spawns.delPlayerSpawn(player.getUniqueId());
            msg = EssMessage.CMD_DELSPAWN_PLAYER;
            playerName = player.getName();
        }

        if(!result.hasModifier("-s")){
            msg.msg().send(sender, Param.P("player", playerName));
        }
        return true;
    }
}