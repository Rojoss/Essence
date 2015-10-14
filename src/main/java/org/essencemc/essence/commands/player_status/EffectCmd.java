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
import org.essencemc.essencecore.aliases.AliasType;
import org.essencemc.essencecore.aliases.Aliases;
import org.essencemc.essencecore.arguments.IntArg;
import org.essencemc.essencecore.arguments.MappedListArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EffectCmd extends EssenceCommand {

    public EffectCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("effect", new MappedListArg(Aliases.getAliasesMap(AliasType.POTION_EFFECT)), ArgumentRequirement.REQUIRED);
        addArgument("seconds", new IntArg(), ArgumentRequirement.REQUIRED);
        addArgument("amplifier", new IntArg(), ArgumentRequirement.OPTIONAL);
        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE);

        addModifier("-a", EssMessage.MOD_EFFECT_AMBIENT.msg());
        addModifier("-f", EssMessage.MOD_EFFECT_FORCE.msg(), "force");

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        PotionEffectType effect = Aliases.getPotionEffect((String) result.getArg("effect"));
        int seconds = (Integer)result.getArg("seconds");
        int amplifier = result.getArg("amplifier") == null ? 0 : (Integer)result.getArg("amplifier")-1;

        Player player = result.getArg("player") == null ? (Player)sender : (Player)result.getArg("player");

        player.addPotionEffect(new PotionEffect(effect, seconds * 20, amplifier, result.hasModifier("ambient")), result.hasModifier("force"));
        if (!result.hasModifier("-s")) {
            EssMessage.CMD_EFFECT.msg().send(player, Param.P("effect", Aliases.getName(AliasType.POTION_EFFECT, effect.getName())), Param.P("duration", Integer.toString(seconds)),
                    Param.P("amplifier", Integer.toString(amplifier+1)));
            if (!sender.equals(player)) {
                EssMessage.CMD_EFFECT_OTHER.msg().send(sender, Param.P("player", player.getDisplayName()), Param.P("effect", Aliases.getName(AliasType.POTION_EFFECT, effect.getName())),
                        Param.P("duration", Integer.toString(seconds)), Param.P("amplifier", Integer.toString(amplifier)));
            }
        }

        return true;
    }

}