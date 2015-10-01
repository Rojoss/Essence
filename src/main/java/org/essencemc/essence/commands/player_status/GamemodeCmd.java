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

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.aliases.AliasType;
import org.essencemc.essencecore.aliases.Aliases;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.MappedListArgument;
import org.essencemc.essencecore.commands.arguments.PlayerArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;

import java.util.List;

public class GamemodeCmd extends EssenceCommand {

    public GamemodeCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new MappedListArgument("mode", ArgumentRequirement.REQUIRED, "", Aliases.getAliasesMap(AliasType.GAME_MODE)),
                new PlayerArgument("player", ArgumentRequirement.OPTIONAL, "others")
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

        String mode = (String)result.getArg("mode");
        Player player = (Player)result.getArg("player", castPlayer(sender));

        GameMode gm = null;
        for (GameMode gameMode : GameMode.values()) {
            if (gameMode.toString().equalsIgnoreCase(mode)) {
                gm = gameMode;
            }
        }

        PlayerGameModeChangeEvent gamemodeChangeEvent = new PlayerGameModeChangeEvent(player, gm);
        plugin.getServer().getPluginManager().callEvent(gamemodeChangeEvent);
        if (gamemodeChangeEvent.isCancelled()) {
            return true;
        }

        player.setGameMode(gm);

        if (!result.hasModifier("-s")) {
            player.sendMessage(EssMessage.CMD_GAMEMODE_CHANGED.msg().getMsg(true, mode));
            if (!sender.equals(player)) {
                sender.sendMessage(EssMessage.CMD_GAMEMODE_OTHER.msg().getMsg(true, mode, player.getDisplayName()));
            }
        }
        return true;
    }
}
