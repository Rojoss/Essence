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
import org.bukkit.potion.PotionEffect;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.EssenceCore;
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

    public HealCmd(EssenceCore ess, String command, String description, String permission, List<String> aliases) {
        super(ess, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new PlayerArgument("player", ArgumentRequirement.REQUIRED_CONSOLE, "others"),
                new DoubleArgument("max", ArgumentRequirement.OPTIONAL, "max", 1, 2048, false)
        };

        addCommandOption("feed", Message.OPT_HEAL_FEED.msg(), new BoolArg(true));
        addCommandOption("clear-effects", Message.OPT_HEAL_CLEAR_EFFECTS.msg(), new BoolArg(true));
        addCommandOption("extinguish", Message.OPT_HEAL_EXTINGUISH.msg(), new BoolArg(true));

        addModifier("-h", Message.MOD_HEAL_ONLY.msg());
        addModifier("-m", Message.MOD_HEAL_MAX_ONLY.msg());
        addModifier("-a", Message.MOD_HEAL_ALL.msg(), "all");

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
            players.addAll(ess.getServer().getOnlinePlayers());
        } else {
            players.add((Player)result.getArg("player", castPlayer(sender)));
        }

        for (Player player : players) {
            double max = (Double)result.getArg("max", player.getMaxHealth());

            if (player.isDead() || player.getHealth() == 0) {
                if (!result.hasModifier("-a")) {
                    sender.sendMessage(Message.DEAD_PLAYER.msg().getMsg(true, args[0]));
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
                ess.getServer().getPluginManager().callEvent(regainHealthEvent);
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
                player.sendMessage(Message.CMD_HEAL_HEALED.msg().getMsg(true));
            }
        }
        if (result.hasModifier("-a")) {
            sender.sendMessage(Message.CMD_HEAL_ALL.msg().getMsg(true));
        } else {
            if (!sender.equals(players.get(0))) {
                sender.sendMessage(Message.CMD_HEAL_OTHER.msg().getMsg(true, players.get(0).getDisplayName()));
            }
        }
        return true;
    }
}
