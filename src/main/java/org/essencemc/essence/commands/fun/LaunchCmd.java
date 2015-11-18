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

package org.essencemc.essence.commands.fun;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.aliases.Alias;
import org.essencemc.essencecore.aliases.AliasType;
import org.essencemc.essencecore.aliases.Aliases;
import org.essencemc.essencecore.arguments.MappedListArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaunchCmd extends EssenceCommand {
    private final Map<String, Class<? extends Projectile>> projectileClasses = new HashMap<>();

    public LaunchCmd(Plugin plugin, String label, String description, String permission, List<String> aliases) {
        super(plugin, label, description, permission, aliases);

        Map<String, List<String>> projectiles = new HashMap<String, List<String>>() {{
            for(Alias alias : Aliases.getAliases(AliasType.ENTITY)) {
                EntityType type = EntityType.valueOf(alias.getKey());

                if(Projectile.class.isAssignableFrom(type.getEntityClass())) {
                    put(alias.getName(), alias.getAliases());
                    projectileClasses.put(alias.getName(), ((Class<? extends Projectile>) type.getEntityClass()));
                }
            }
        }};

        addArgument("projectile", new MappedListArg(projectiles), ArgumentRequirement.REQUIRED);
        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = castPlayer(sender);

        if(player == null) {
            Message.CMD_PLAYER_ONLY.msg().send(sender);
            return true;
        }

        ArgumentParseResults result = parseArgs(this, sender, args);

        if(!result.success) {
            return true;
        }

        player.launchProjectile(projectileClasses.get(((String) result.getArg("projectile"))));
        EssMessage.CMD_LAUNCH.msg().params(Param.P("projectile", Aliases.getName(AliasType.ENTITY, ((String) result.getArg("projectile"))))).send(sender);
        return true;
    }
}
