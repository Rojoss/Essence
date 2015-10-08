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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.commands.arguments.DoubleArgument;
import org.essencemc.essencecore.commands.arguments.PlayerArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;
import org.essencemc.essencecore.commands.links.RemoveLink;
import org.essencemc.essencecore.message.Message;

import java.util.ArrayList;
import java.util.List;

public class HealCmd extends EssenceCommand {

    public HealCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new PlayerArgument("player", ArgumentRequirement.REQUIRED_CONSOLE, "others"),
                new DoubleArgument("max", ArgumentRequirement.OPTIONAL, "max", 1, 2048, false)
        };

        addCommandOption("feed", EssMessage.OPT_HEAL_FEED.msg(), new BoolArg(true));
        addCommandOption("clear-effects", EssMessage.OPT_HEAL_CLEAR_EFFECTS.msg(), new BoolArg(true));
        addCommandOption("extinguish", EssMessage.OPT_HEAL_EXTINGUISH.msg(), new BoolArg(true));

        addModifier("-h", EssMessage.MOD_HEAL_ONLY.msg());
        addModifier("-m", EssMessage.MOD_HEAL_MAX_ONLY.msg());
        addModifier("-a", EssMessage.MOD_HEAL_ALL.msg(), "all");

        addLink(new RemoveLink("-a", "player"));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        List<Player> players = new ArrayList<Player>();
        if (result.hasModifier("-a")) {
            players.addAll(plugin.getServer().getOnlinePlayers());
        } else {
            players.add((Player)result.getArg("player", castPlayer(sender)));
        }

        for (Player player : players) {
            double max = (Double)result.getArg("max", player.getMaxHealth());

            if (player.isDead() || player.getHealth() == 0) {
                if (!result.hasModifier("-a")) {
                    Message.DEAD_PLAYER.msg(true, true, castPlayer(sender)).parseArgs(args[0]).send(sender);
                }
                return true;
            }

            if (!result.hasModifier("-h")) {
                player.setMaxHealth(max);
            }

            if (!result.hasModifier("-m")) {
                max = Math.min(player.getMaxHealth(), max);
                double amount = max - player.getHealth();
                EntityRegainHealthEvent regainHealthEvent = new EntityRegainHealthEvent(player, amount, EntityRegainHealthEvent.RegainReason.CUSTOM);
                plugin.getServer().getPluginManager().callEvent(regainHealthEvent);
                if (!regainHealthEvent.isCancelled()) {
                    player.setHealth(max);
                }
            }

            if ((Boolean)result.getOptionalArg("feed")) {
                player.setFoodLevel(20);
            }

            if ((Boolean)result.getOptionalArg("extinguish")) {
                player.setFireTicks(0);
            }
            if ((Boolean)result.getOptionalArg("clear-effects")) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
            }

            if (!result.hasModifier("-s")) {
                EssMessage.CMD_HEAL_HEALED.msg(true, true, castPlayer(sender)).send(player);
            }
        }
        if (result.hasModifier("-a")) {
            EssMessage.CMD_HEAL_ALL.msg(true, true, castPlayer(sender)).send(sender);
        } else {
            if (!sender.equals(players.get(0))) {
                EssMessage.CMD_HEAL_OTHER.msg(true, true, castPlayer(sender)).parseArgs(players.get(0).getDisplayName()).send(sender);
            }
        }
        return true;
    }
}
