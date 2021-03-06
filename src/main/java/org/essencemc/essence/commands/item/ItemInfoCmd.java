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

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essencecore.arguments.StringArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.aliases.ItemAlias;
import org.essencemc.essencecore.aliases.Items;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.entity.EItem;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.parsers.ItemParser;
import org.essencemc.essencecore.util.Util;

import java.util.HashMap;
import java.util.List;

public class ItemInfoCmd extends EssenceCommand {

    public ItemInfoCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addArgument("[item[:data]] [meta]", new StringArg(), ArgumentRequirement.REQUIRED_CONSOLE);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        EItem item = null;
        if (args.length > 0) {
            ItemParser parser = new ItemParser(Util.implode(args, " "), 1, false);
            if (!parser.isValid()) {
                parser.getError().send(sender);
                return true;
            }
            item = parser.getItem();
        } else {
            item = new EItem(castPlayer(sender).getItemInHand());
        }
        if (item == null) {
            item = EItem.AIR;
        }
        ItemAlias itemAlias = Items.getItem(item.getType(), item.getDurability());

        EssMessage.CMD_ITEM_INFO.msg().send(sender, Param.P("name", itemAlias.getName()), Param.P("amount", Integer.toString(item.getAmount())),
                Param.P("aliases", itemAlias.getAliasesStr()), Param.P("type", item.getType().toString()), Param.P("data", Short.toString(item.getDurability())),
                Param.P("string", new ItemParser(item).getString()));
        return true;
    }
}
