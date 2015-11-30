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

package org.essencemc.essence.commands.punishments;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.modules.punishments.Ban;
import org.essencemc.essence.modules.punishments.BanModule;
import org.essencemc.essence.modules.punishments.Kick;
import org.essencemc.essence.modules.punishments.KickModule;
import org.essencemc.essencecore.arguments.MappedListArg;
import org.essencemc.essencecore.arguments.OfflinePlayerArg;
import org.essencemc.essencecore.commands.EssenceCommand;
import org.essencemc.essencecore.commands.arguments.ArgumentParseResults;
import org.essencemc.essencecore.commands.arguments.ArgumentRequirement;
import org.essencemc.essencecore.message.Message;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.util.Duration;
import org.essencemc.essencecore.util.Util;

import java.util.*;

public class PunishmentsCmd extends EssenceCommand {

    public PunishmentsCmd(Plugin plugin, String command, String description, String permission, List<String> aliases) {
        super(plugin, command, description, permission, aliases);

        addSoftDependency(BanModule.class);
        addSoftDependency(KickModule.class);

        Map<String, List<String>> types = new HashMap<String, List<String>>();
        types.put("all", Arrays.asList("overview"));
        types.put("ban", Arrays.asList("bans", "banned"));
        types.put("mute", Arrays.asList("mutes", "muted"));
        types.put("jail", Arrays.asList("jails", "jailed"));
        types.put("freeze", Arrays.asList("freezes", "frozen"));
        types.put("warn", Arrays.asList("warns", "warned"));
        types.put("kick", Arrays.asList("kicks", "kicked"));

        addArgument("type", new MappedListArg(types), ArgumentRequirement.REQUIRED);
        addArgument("player", new OfflinePlayerArg(), ArgumentRequirement.OPTIONAL);

        register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        ArgumentParseResults result = parseArgs(this, sender, args);
        if (!result.success) {
            return true;
        }
        args = result.getArgs();

        BanModule banmodule = (BanModule)getModule(BanModule.class);
        KickModule kickModule = (KickModule)getModule(KickModule.class);

        String type = (String)result.getArg("type");
        OfflinePlayer player = (OfflinePlayer)result.getArg("player");
        UUID uuid = player == null ? null : player.getUniqueId();

        if (type.equalsIgnoreCase("all")) {
            //List everything (Must have a player)
            if (banmodule == null && kickModule == null) {
                EssMessage.CMD_PUNISHMENTS_NO_MODULES.msg().send(sender);
                return true;
            }
            if (player == null) {
                EssMessage.CMD_PUNISHMENTS_CANT_LIST_ALL.msg().send(sender, Param.P("type", type));
                return true;
            }

            String entries = "";
            int i = 1;

            //Bans
            if (banmodule != null) {
                List<String> banEntries = new ArrayList<String>();
                List<Ban> bans = banmodule.getBans(uuid);
                for (Ban ban : bans) {
                    banEntries.add(EssMessage.CMD_PUNISHMENTS_BAN_ENTRY.msg().params(
                            Param.P("key", Integer.toString(i)),
                            Param.P("reason", ban.getReason() == null || ban.getReason().isEmpty() ? EssMessage.CORE_NO_REASON.msg().getText() : ban.getReason()),
                            Param.P("punisher", ban.getPunisher() == null ? "console" : plugin.getServer().getOfflinePlayer(ban.getPunisher()).getName()),
                            Param.P("timeleft", new Duration(ban.getRemainingTime()).getString()),
                            Param.P("duration", new Duration(ban.getDuration()).getString()),
                            Param.P("time", ban.getTimestamp().toString())
                    ).getText());
                    i++;
                }
                entries += EssMessage.CMD_PUNISHMENTS_ALL_ENTRY.msg().params(Param.P("type", "Bans"), Param.P("amount", Integer.toString(banEntries.size())),
                        Param.P("punishments", banEntries.size() > 0 ? Util.implode(banEntries, "&8, ") : Message.NONE.msg().getText())).getText() + "\n";
            }

            /*
            //Mutes
            //entries += EssMessage.CMD_PUNISHMENTS_ALL_ENTRY.msg().params(Param.P("type", "Mutes"), Param.P("amount", Integer.toString(banEntries.size())),
                    Param.P("punishments", banEntries.size() > 0 ? Util.implode(banEntries, "&8, ") : Message.NONE.msg().getText())).getText() + "\n";

            //Jails
            //entries += EssMessage.CMD_PUNISHMENTS_ALL_ENTRY.msg().params(Param.P("type", "Jails"), Param.P("amount", Integer.toString(banEntries.size())),
                    Param.P("punishments", banEntries.size() > 0 ? Util.implode(banEntries, "&8, ") : Message.NONE.msg().getText())).getText() + "\n";

            //Freezes
            //entries += EssMessage.CMD_PUNISHMENTS_ALL_ENTRY.msg().params(Param.P("type", "Freezes"), Param.P("amount", Integer.toString(banEntries.size())),
                    Param.P("punishments", banEntries.size() > 0 ? Util.implode(banEntries, "&8, ") : Message.NONE.msg().getText())).getText() + "\n";

            //Warnings
            entries += EssMessage.CMD_PUNISHMENTS_ALL_ENTRY.msg().params(Param.P("type", "Warnings"), Param.P("amount", Integer.toString(banEntries.size())),
                    Param.P("punishments", banEntries.size() > 0 ? Util.implode(banEntries, "&8, ") : Message.NONE.msg().getText())).getText() + "\n";
            */

            //Kicks
            if (kickModule != null) {
                List<String> kickEntries = new ArrayList<String>();
                List<Kick> kicks = kickModule.getKicks(uuid);
                i = 1;
                for (Kick kick : kicks) {
                    kickEntries.add(EssMessage.CMD_PUNISHMENTS_KICK_ENTRY.msg().params(
                            Param.P("key", Integer.toString(i)),
                            Param.P("reason", kick.getReason() == null || kick.getReason().isEmpty() ? EssMessage.CORE_NO_REASON.msg().getText() : kick.getReason()),
                            Param.P("punisher", kick.getPunisher() == null ? "console" : plugin.getServer().getOfflinePlayer(kick.getPunisher()).getName()),
                            Param.P("time", kick.getTimestamp().toString())
                    ).getText());
                    i++;
                }
                entries += EssMessage.CMD_PUNISHMENTS_ALL_ENTRY.msg().params(Param.P("type", "Kicks"), Param.P("amount", Integer.toString(kickEntries.size())),
                        Param.P("punishments", kickEntries.size() > 0 ? Util.implode(kickEntries, "&8, ") : Message.NONE.msg().getText())).getText() + "\n";
            }

            EssMessage.CMD_PUNISHMENTS_ALL_PLAYER.msg().send(sender, Param.P("entries", entries.substring(0, entries.length()-1)), Param.P("player", player.getName()));
            return true;
        }

        if (type.equalsIgnoreCase("ban")) {
            //List bans
            if (banmodule == null) {
                Message.MODULE_DISABLED.msg().send(sender, Param.P("module", "ban"));
            }
            if (player == null) {
                String entries = Message.NONE.msg().getText();
                Map<UUID, List<Ban>> bans = banmodule.getBans();
                List<String> entryList = new ArrayList<String>();
                if (!bans.isEmpty()) {
                    for (UUID u : bans.keySet()) {
                        Ban ban = banmodule.getActiveBan(u);
                        if (ban != null) {
                            entryList.add(EssMessage.CMD_PUNISHMENTS_BAN_ENTRY.msg().params(
                                    Param.P("key", plugin.getServer().getOfflinePlayer(u).getName()),
                                    Param.P("reason", ban.getReason() == null || ban.getReason().isEmpty() ? EssMessage.CORE_NO_REASON.msg().getText() : ban.getReason()),
                                    Param.P("punisher", ban.getPunisher() == null ? "console" : plugin.getServer().getOfflinePlayer(ban.getPunisher()).getName()),
                                    Param.P("timeleft", new Duration(ban.getRemainingTime()).getString()),
                                    Param.P("duration", new Duration(ban.getDuration()).getString()),
                                    Param.P("time", ban.getTimestamp().toString())
                            ).getText());
                        }
                    }
                    entries = Util.implode(entryList, "&8, ");
                }
                EssMessage.CMD_PUNISHMENTS_BAN.msg().send(sender, Param.P("entries", entries));
            } else {
                String entries = Message.NONE.msg().getText();
                List<Ban> bans = banmodule.getBans(uuid);
                if (!bans.isEmpty()) {
                    List<String> entryList = new ArrayList<String>();
                    for (Ban ban : bans) {
                        entryList.add(EssMessage.CMD_PUNISHMENTS_BAN_ENTRY.msg().params(
                                Param.P("key", ban.getTimestamp().toString()),
                                Param.P("reason", ban.getReason() == null || ban.getReason().isEmpty() ? EssMessage.CORE_NO_REASON.msg().getText() : ban.getReason()),
                                Param.P("punisher", ban.getPunisher() == null ? "console" : plugin.getServer().getOfflinePlayer(ban.getPunisher()).getName()),
                                Param.P("timeleft", new Duration(ban.getRemainingTime()).getString()),
                                Param.P("duration", new Duration(ban.getDuration()).getString()),
                                Param.P("time", ban.getTimestamp().toString())
                        ).getText());
                    }
                    entries = Util.implode(entryList, "&8, ");
                }
                EssMessage.CMD_PUNISHMENTS_BAN_PLAYER.msg().send(sender, Param.P("entries", entries), Param.P("player", player.getName()));
            }
            return true;
        }

        if (type.equalsIgnoreCase("mute")) {
            //List mutes
            if (player == null) {

            } else {

            }
            return true;
        }

        if (type.equalsIgnoreCase("jail")) {
            //List jails
            if (player == null) {

            } else {

            }
            return true;
        }

        if (type.equalsIgnoreCase("freeze")) {
            //List freezes
            if (player == null) {

            } else {

            }
            return true;
        }

        if (type.equalsIgnoreCase("warn")) {
            //List warnings (Must have a player)
            if (player == null) {
                EssMessage.CMD_PUNISHMENTS_CANT_LIST_ALL.msg().send(sender, Param.P("type", type));
                return true;
            }

            return true;
        }

        if (type.equalsIgnoreCase("kick")) {
            //List kicks (Must have a player)
            if (kickModule == null) {
                Message.MODULE_DISABLED.msg().send(sender, Param.P("module", "kick"));
            }
            if (player == null) {
                EssMessage.CMD_PUNISHMENTS_CANT_LIST_ALL.msg().send(sender, Param.P("type", type));
                return true;
            }
            String entries = Message.NONE.msg().getText();
            List<Kick> kicks = kickModule.getKicks(uuid);
            if (!kicks.isEmpty()) {
                List<String> entryList = new ArrayList<String>();
                for (Kick kick : kicks) {
                    entryList.add(EssMessage.CMD_PUNISHMENTS_KICK_ENTRY.msg().params(
                            Param.P("key", kick.getTimestamp().toString()),
                            Param.P("reason", kick.getReason() == null || kick.getReason().isEmpty() ? EssMessage.CORE_NO_REASON.msg().getText() : kick.getReason()),
                            Param.P("punisher", kick.getPunisher() == null ? "console" : plugin.getServer().getOfflinePlayer(kick.getPunisher()).getName()),
                            Param.P("time", kick.getTimestamp().toString())
                    ).getText());
                }
                entries = Util.implode(entryList, "&8, ");
            }
            EssMessage.CMD_PUNISHMENTS_KICK_PLAYER.msg().send(sender, Param.P("entries", entries), Param.P("player", player.getName()));
            return true;
        }

        return true;
    }
}
