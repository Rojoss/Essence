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
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.EssenceCore;
import org.essencemc.essencecore.aliases.ItemAlias;
import org.essencemc.essencecore.aliases.Items;
import org.essencemc.essencecore.commands.arguments.StringArgument;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.internal.ArgumentRequirement;
import org.essencemc.essencecore.commands.arguments.internal.CmdArgument;

import java.util.List;

public class ItemInfoCmd extends EssenceCommand {

    public ItemInfoCmd(EssenceCore ess, String command, String description, String permission, List<String> aliases) {
        super(ess, command, description, permission, aliases);

        cmdArgs = new CmdArgument[] {
                new StringArgument("[item[:data]]", ArgumentRequirement.REQUIRED_CONSOLE, "others", 1, 100)
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

        String itemString = (String)result.getArg("[item[:data]]");

        ItemAlias item = Items.getItem(itemString);
        if (item == null) {
            sender.sendMessage("Invalid item...");
            return true;
        }

        sender.sendMessage(item.getName());
        sender.sendMessage(item.getType().toString() + "(" + item.getId() + ")");
        sender.sendMessage("" + item.getData());
        sender.sendMessage(item.getAliasesStr());
        return true;
    }
}
