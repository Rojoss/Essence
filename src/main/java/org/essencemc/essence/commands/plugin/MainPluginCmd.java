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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.arguments.internal.MatchString;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.CmdArgument;

import java.util.List;

public class MainPluginCmd extends EssenceCommand {

    public MainPluginCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new CmdArgument("reload", new StringArg(new MatchString("reload")), ArgumentRequirement.OPTIONAL, "reload")
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
            EssenceCore core = Essence.core();
            core.getMessages().load();
            core.getModuleCfg().load();
            core.getCommandsCfg().load();
            core.getCmdOptions().load();
            Essence.inst().registerCommands();
            Essence.inst().registerModules();

            if (!result.hasModifier("-s")) {
                EssMessage.CMD_ESSENCE_RELOAD.msg(true, true, castPlayer(sender)).send(sender);
            }
            return true;
        }

        PluginDescriptionFile pdf = plugin.getDescription();
        //sender.sendMessage(Util.color(EssMessage.CMD_ESSENCE_INFO.msg().getText(false, pdf.getDescription(), pdf.getVersion(), pdf.getWebsite(), "&7" + Util.implode(pdf.getAuthors(), "&8, &7", " &8& &7"))));
        //TODO: Don't allow editing these values.
        return true;
    }

}
