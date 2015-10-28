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

package org.essencemc.essence.commands.module.vanish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.vanish.VanishData;
import org.essencemc.essence.modules.vanish.VanishModule;
import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.links.ConflictLink;
import org.essencemc.essencecore.message.Message;

import java.util.List;
import java.util.UUID;

public class VanishCmd extends EssenceCommand {

    public VanishCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addDependency(VanishModule.class);

        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED_CONSOLE);
        addArgument("state", new BoolArg(), ArgumentRequirement.OPTIONAL);

        addModifier("-fq", EssMessage.MOD_VANISH_FAKE_QUIT.msg(), "fakequit");
        addModifier("-fj", EssMessage.MOD_VANISH_FAKE_JOIN.msg(), "fakejoin");

        addCommandOption("chat", EssMessage.OPT_VANISH_CHAT.msg(), new BoolArg(false), true);
        addCommandOption("attack", EssMessage.OPT_VANISH_ATTACK.msg(), new BoolArg(false), true);
        addCommandOption("damage", EssMessage.OPT_VANISH_DAMAGE.msg(), new BoolArg(false), true);
        addCommandOption("interact", EssMessage.OPT_VANISH_INTERACT.msg(), new BoolArg(false), true);
        addCommandOption("pickup", EssMessage.OPT_VANISH_PICKUP.msg(), new BoolArg(false), true);
        addCommandOption("target", EssMessage.OPT_VANISH_TARGET.msg(), new BoolArg(false), true);
        addCommandOption("silent-join", EssMessage.OPT_VANISH_SILENT_JOIN.msg(), new BoolArg(true), false);
        addCommandOption("silent-quit", EssMessage.OPT_VANISH_SILENT_QUIT.msg(), new BoolArg(true), false);
        addCommandOption("scoreboard-team", EssMessage.OPT_VANISH_SCOREBOARD_TEAM.msg(), new BoolArg(true), false);
        addCommandOption("invisibility-potion", EssMessage.OPT_VANISH_INVIS_POTION.msg(), new BoolArg(true), false);

        addLink(new ConflictLink("-fq", "-fj"));
        addLink(new ConflictLink("-fj", "-fq"));
        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Message.CMD_PLAYER_ONLY.msg().params().send(sender);
            return true;
        }

        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        VanishModule vanish = (VanishModule)getModule(VanishModule.class);
        vanish.updateOptions((Boolean)cmdOptions.get("silent-join").getArg().getValue(), (Boolean)cmdOptions.get("silent-quit").getArg().getValue(),
                (Boolean)cmdOptions.get("scoreboard-team").getArg().getValue(), (Boolean)cmdOptions.get("invisibility-potion").getArg().getValue());

        Player player = (Player)result.getArg("player", castPlayer(sender));
        UUID uuid = player.getUniqueId();
        Boolean state = (Boolean)result.getArg("state");

        if ((state != null && state == false) || vanish.isVanished(uuid)) {
            if (vanish.unvanish(uuid)) {
                if (result.hasModifier("-fj")) {
                    //TODO: Send fake join message
                }
                //TODO: Send message
            } else {
                //TODO: Send message
            }
        } else {
            if (vanish.vanish(uuid, new VanishData((Boolean)result.getOptionalArg("chat"), (Boolean)result.getOptionalArg("attack"),
                    (Boolean)result.getOptionalArg("damage"), (Boolean)result.getOptionalArg("interact"), (Boolean)result.getOptionalArg("pickup"),
                    (Boolean)result.getOptionalArg("target")))) {
                if (result.hasModifier("-fq")) {
                    //TODO: Send fake quit message
                }
                //TODO: Send message
            } else {
                //TODO: Send message
            }
        }
        return true;
    }
}
