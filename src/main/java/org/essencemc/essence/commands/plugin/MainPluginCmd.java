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

package org.essencemc.essence.commands.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.commands.arguments.StringArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.util.Util;

import java.util.List;

public class MainPluginCmd extends EssenceCommand {

    public MainPluginCmd(EssenceCore ess, String command, String description, String permission, List<String> aliases) {
        super(ess, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new StringArgument("reload", ArgumentRequirement.OPTIONAL, "reload", "reload")
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

        String option = (String)result.getArg("reload", "");
        if (option.equalsIgnoreCase("reload")) {
            ess.getMessages().load();
            ess.getModuleCfg().load();
            ess.getCommandsCfg().load();
            ess.getCmdOptions().load();
            ess.getCommands().registerCommands();
            ess.getModules().registerModules();

            if (!result.hasModifier("-s")) {
                sender.sendMessage(Message.CMD_ESSENCE_RELOAD.msg().getMsg(true));
            }
            return true;
        }

        PluginDescriptionFile pdf = ess.getDescription();
        sender.sendMessage(Util.color(Message.CMD_ESSENCE_INFO.msg().getMsg(false, pdf.getDescription(), pdf.getVersion(), pdf.getWebsite(),
                "&7" + Util.implode(pdf.getAuthors(), "&8, &7", " &8& &7"))));
        return true;
    }

}
