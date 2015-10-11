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

package org.essencemc.essence.commands.item;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.aliases.AliasType;
import org.essencemc.essencecore.aliases.Aliases;
import org.essencemc.essencecore.aliases.ItemAlias;
import org.essencemc.essencecore.aliases.Items;
import org.essencemc.essencecore.arguments.BoolArg;
import org.essencemc.essencecore.arguments.IntArg;
import org.essencemc.essencecore.arguments.PlayerArg;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.entity.EItem;
import org.essencemc.essencecore.entity.ItemTag;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.parsers.ItemParser;
import org.essencemc.essencecore.util.InvUtil;
import org.essencemc.essencecore.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GiveCmd extends EssenceCommand {

    public GiveCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("player", new PlayerArg(), ArgumentRequirement.REQUIRED);
        addArgument("item[:data]] [amount] [meta...", new StringArg(), ArgumentRequirement.OPTIONAL);

        addCommandOption("default-amount", EssMessage.OPT_ITEM_AMOUNT.msg(), new IntArg(1, 1, 64), false);
        addCommandOption("stack", EssMessage.OPT_ITEM_STACK.msg(), new BoolArg(false), true);
        addCommandOption("drop", EssMessage.OPT_ITEM_DROP.msg(), new BoolArg(false), true);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        Player player = (Player)result.getArg("player");

        args[0] = "";
        ItemParser parser = new ItemParser(Util.implode(args, " ").trim(), (Integer)cmdOptions.get("default-amount").getArg().getValue(), false);
        if (!parser.isValid()) {
            parser.getError().send(sender);
            return true;
        }
        EItem item = parser.getItem();

        if (item == null || item.getType() == Material.AIR) {
            EssMessage.CANT_SPAWN_AIR.msg().send(sender);
            return true;
        }
        InvUtil.addItems(player.getInventory(), item, (Boolean)result.getOptionalArg("drop"), !(Boolean)result.getOptionalArg("stack"));

        ItemAlias itemAlias = Items.getItem(item.getType(), item.getDurability());
        if (!result.hasModifier("-s")) {
            EssMessage.CMD_ITEM_GIVE.msg().send(player, Param.P("item", itemAlias.getName()), Param.P("amount", Integer.toString(item.getAmount())));
            if (!sender.equals(player)) {
                EssMessage.CMD_ITEM_GIVE_OTHER.msg().send(sender, Param.P("player", player.getDisplayName()), Param.P("item", itemAlias.getName()), Param.P("amount", Integer.toString(item.getAmount())));
            }
        }
        return true;
    }

    @Override
    public void showHelp(CommandSender sender) {
        super.showHelp(sender);
        ItemCmd.sendMetaHelp(sender);
    }
}
