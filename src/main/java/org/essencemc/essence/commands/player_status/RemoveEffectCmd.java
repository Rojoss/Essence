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
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.MappedListArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.aliases.AliasType;
import org.essencemc.essencecore.aliases.Aliases;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.CmdArgument;
import org.essencemc.essencecore.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RemoveEffectCmd extends EssenceCommand {

    public RemoveEffectCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        Map<String, List<String>> effects = Aliases.getAliasesMap(AliasType.POTION_EFFECT);
        effects.put("ALL", Arrays.asList("all", "*"));

        cmdArgs = new CmdArgument[] {
                new CmdArgument("effect", new MappedListArg(effects), ArgumentRequirement.REQUIRED, ""),
                new CmdArgument("player", new PlayerArg(), ArgumentRequirement.OPTIONAL, "others")
        };

        addModifier("-n", EssMessage.MOD_REMOVEEFFECT_NEGATIVE.msg());
        addModifier("-p", EssMessage.MOD_REMOVEEFFECT_POSITIVE.msg());

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        String effectType = (String)result.getArg("effect");
        Player player = (Player)result.getArg("player", castPlayer(sender));
        boolean single = true;

        if (effectType.equals("ALL")) single = false;

        if (single == false) {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (result.hasModifier("-n") && !Util.isNegativePotionEffect(effect.getType())) continue;
                if (result.hasModifier("-p") && Util.isNegativePotionEffect(effect.getType())) continue;
                player.removePotionEffect(effect.getType());
            }
        } else {
            player.removePotionEffect(PotionEffectType.getByName(effectType));
        }

        if (!result.hasModifier("-s")) {
            if (sender.equals(player)) {
                if (single == true) {
                    EssMessage.CMD_REMOVEEFFECT.msg(true, true, player).parseArgs(Aliases.getName(AliasType.POTION_EFFECT, effectType)).send(player);
                } else {
                    EssMessage.CMD_REMOVEEFFECT_ALL.msg(true, true, player).send(player);
                }
            } else {
                if (single == true) {
                    EssMessage.CMD_REMOVEEFFECT_OTHER.msg(true, true, player).parseArgs(player.getDisplayName(), Aliases.getName(AliasType.POTION_EFFECT, effectType)).send(player);
                } else {
                    EssMessage.CMD_REMOVEEFFECT_OTHER_ALL.msg(true, true, player).send(player);
                }
            }
        }

        return true;
    }

}
