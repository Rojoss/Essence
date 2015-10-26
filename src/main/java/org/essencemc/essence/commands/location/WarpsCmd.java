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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.Essence;
import org.essencemc.essence.modules.warps.WarpModule;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.arguments.WorldArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WarpsCmd extends EssenceCommand {

    public WarpsCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("world", new WorldArg(), ArgumentRequirement.OPTIONAL);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        WarpModule warps = (WarpModule) EssenceCore.inst().getModules().getModule(WarpModule.class);
        if (warps == null) {
            Message.MODULE_DISABLED.msg().send(sender, true, true, Param.P("module", "warps core"));
            return true;
        }

        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        World world = (World)result.getArg("world");

        List<String> warpList = warps.getWarpNames();
        if (world != null) {
            warpList.clear();
            Map<String, Location> warpsMap = warps.getWarps();
            for (Map.Entry<String, Location> warp : warpsMap.entrySet()) {
                if (warp.getValue().getWorld().equals(world)) {
                    warpList.add(warp.getKey());
                }
            }
        }

        EssenceCommand warpCmd = EssenceCore.inst().getCommands().getCommand(WarpCmd.class);
        if (warpCmd != null && (Boolean)((WarpCmd)warpCmd).cmdOptions.get("permission-based-warps").getArg().getValue()) {
            List<String> warpsClone = new ArrayList<String>(warpList);
            for (String warp : warpsClone) {
                if (!sender.hasPermission("essence.*") && !sender.hasPermission("essence.warps.*") && !sender.hasPermission("essence.warps." + warp.toLowerCase())) {
                    warpList.remove(warp);
                }
            }
        }

        EssMessage.CMD_WARPS.msg().send(sender, Param.P("warps", warpList.size() <= 0 ? EssMessage.CMD_WARPS_NONE.msg().color().getText() : Util.implode(warpList, ", ")));
        return true;
    }

}
