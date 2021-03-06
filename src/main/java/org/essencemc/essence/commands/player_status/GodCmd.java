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
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.god.GodData;
import org.essencemc.essence.modules.god.GodModule;
import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.CmdArgument;
import org.essencemc.essencecore.message.Param;

import java.util.List;
import java.util.UUID;

public class GodCmd extends EssenceCommand {

    public GodCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addDependency(GodModule.class);

        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE, "others");

        addModifier("-r", EssMessage.MOD_GOD_RESET.msg());
        addCommandOption("can-lose-hunger", EssMessage.OPT_NO_HUNGER_LOSS.msg(), new BoolArg(false));
        addCommandOption("can-damage-others", EssMessage.OPT_NO_DAMAGE.msg(), new BoolArg(false));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if(!result.success) {
            return true;
        }

        GodModule gods = (GodModule)getModule(GodModule.class);

        boolean god;
        Player player = (Player)result.getArg("player", castPlayer(sender));
        UUID uuid = player.getUniqueId();

        if(result.hasModifier("-r")){
            player.setFireTicks(0);
            for(PotionEffect effect : player.getActivePotionEffects()){
                player.removePotionEffect(effect.getType());
            }
        }
        if(gods.isGod(uuid)){
            gods.ungod(uuid);
            god = false;
        } else {
            gods.god(uuid, new GodData((Boolean)result.getOptionalArg("can-lose-hunger"), (boolean)result.getOptionalArg("can-damage-others")));
            god = true;
        }

        if(!result.hasModifier("-s")){
            if(god){
                EssMessage.CMD_GOD_RECEIVER_ON.msg().send(player);
                if(!sender.equals(player)){
                    EssMessage.CMD_GOD_SENDER_ON.msg().send(sender, Param.P("player", player.getDisplayName()));
                }
            } else {
                EssMessage.CMD_GOD_RECEIVER_OFF.msg().send(player);
                if(!sender.equals(player)){
                    EssMessage.CMD_GOD_SENDER_OFF.msg().send(sender, Param.P("player", player.getDisplayName()));
                }
            }
        }
        return true;
    }
}
