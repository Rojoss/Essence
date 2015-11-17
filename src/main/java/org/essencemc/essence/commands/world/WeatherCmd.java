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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.aliases.AliasType;
import org.essencemc.essencecore.aliases.Aliases;
import org.essencemc.essencecore.arguments.*;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResult;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.EText;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.Duration;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class WeatherCmd extends EssenceCommand {
    public WeatherCmd(Plugin plugin, String label, String description, String permission, List<String> aliases) {
        super(plugin, label, description, permission, aliases);

        addArgument("type", new MappedListArg(Aliases.getAliasesMap(AliasType.WEATHER)), ArgumentRequirement.OPTIONAL);
        addArgument("duration", new IntArg(), ArgumentRequirement.OPTIONAL);
        addOptionalArgument("world", new WorldArg());
        addModifier("-t", new EText("Toggle the weather."));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);

        if(!result.success) {
            return true;
        }

        World world = ((World) result.getOptionalArg("world"));

        if(world == null) {
            world = ((castPlayer(sender) == null) ? Bukkit.getWorlds().get(0) : castPlayer(sender).getWorld());
        }

        String type = (result.hasModifier("-t") ? "TOGGLE" : ((String) result.getArg("type")));

        if(type == null) {
            EssMessage.CMD_WEATHER_INFO.msg().send(sender, Param.P("world", world.getName()), Param.P("type", (world.hasStorm() ? (world.isThundering() ? "thundering" : "rainy") : "sunny")));
            return true;
        }

        int duration = ((int) result.getArg("duration", (300 + (ThreadLocalRandom.current().nextInt(600)))));

        world.setWeatherDuration((duration * 20));
        world.setThunderDuration((duration * 20));

        switch(type.toUpperCase()) {
            case "TOGGLE":
                world.setStorm(!world.hasStorm());
                world.setThundering(false);
                break;
            case "SUN":
                world.setStorm(false);
                world.setThundering(false);
                break;
            case "RAIN":
                world.setStorm(true);
                world.setThundering(false);
                break;
            case "THUNDER":
                world.setStorm(true);
                world.setThundering(true);
                break;
        }

        EssMessage.CMD_WEATHER_CHANGED.msg().send(sender, Param.P("world", world.getName()), Param.P("type", (world.hasStorm() ? (world.isThundering() ? "thundering" : "rainy") : "sunny")), Param.P("duration", String.valueOf(duration)));
        return true;
    }
}
