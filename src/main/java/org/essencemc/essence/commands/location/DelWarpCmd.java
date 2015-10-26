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

package org.essencemc.essence.commands.location;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.StringUtil;
import org.essencemc.essence.Essence;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.warps.WarpModule;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.commands.links.RemoveLink;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;

import java.util.ArrayList;
import java.util.List;

public class DelWarpCmd extends EssenceCommand {

    public DelWarpCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addDependency(WarpModule.class);

        addArgument("name", new StringArg(), ArgumentRequirement.REQUIRED);

        addModifier("-a", EssMessage.MOD_DELWARP_ALL.msg());

        addLink(new RemoveLink("-a", "name"));

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();
        WarpModule warps = (WarpModule)getModule(WarpModule.class);

        if (result.hasModifier("-a")) {
            warps.delWarps();
            if (!result.hasModifier("-s")) {
                EssMessage.CMD_WARP_DELETED_AlL.msg().send(sender);
            }
            return true;
        }

        String name = (String)result.getArg("name");
        if (!warps.delWarp(name)) {
            EssMessage.CMD_WARP_INVALID.msg().send(sender, Param.P("warp", name));
            return true;
        }

        if (!result.hasModifier("-s")) {
            EssMessage.CMD_WARP_DELETED.msg().send(sender, Param.P("warp", name));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String message, String[] args) {
        if (!hasPermission(sender)) {
            return null;
        }

        WarpModule warps = (WarpModule)getModule(WarpModule.class);
        List<String> warpList = new ArrayList<String>();
        for (String warp : warps.getWarpNames()) {
            if (StringUtil.startsWithIgnoreCase(warp, args[0])) {
                warpList.add(warp);
            }
        }
        return warpList;
    }

}
