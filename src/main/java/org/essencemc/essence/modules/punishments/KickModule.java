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

package org.essencemc.essence.modules.punishments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.essencemc.essence.EssMessage;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.database.Column;
import org.essencemc.essencecore.database.Database;
import org.essencemc.essencecore.database.Operator;
import org.essencemc.essencecore.message.Param;
import org.essencemc.essencecore.modules.*;
import org.essencemc.essencecore.util.Duration;
import org.essencemc.essencecore.util.Util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class KickModule extends SqlStorageModule implements PlayerStorageModule {

    public Map<UUID, List<Kick>> kicks = new HashMap<UUID, List<Kick>>();
    public Map<UUID, List<Kick>> kicks_local = new HashMap<UUID, List<Kick>>();

    public KickModule(String name) {
        super(Essence.inst(), name, "kick", DataModules.KICK);
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    @Override
    protected void onReload() {

    }

    @Override
    public void onLoad() {
        final PreparedStatement statement = getDatabase().createQuery().select("*").from(getTable()).getStatement();
        executeQuery(statement, new SqlQueryCallback() {
            @Override
            public void onExecute(ResultSet result) {
                try {
                    Map<UUID, List<Kick>> loadedKicks = new HashMap<UUID, List<Kick>>();
                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        List<Kick> playerKicks = new ArrayList<Kick>();
                        if (loadedKicks.containsKey(uuid)) {
                            playerKicks = loadedKicks.get(uuid);
                        }
                        Kick kick = new Kick(Timestamp.valueOf(result.getString("timestamp")), UUID.fromString(result.getString("punisher")), result.getString("reason"));
                        playerKicks.add(kick);
                        loadedKicks.put(uuid, playerKicks);
                    }
                    kicks = loadedKicks;
                } catch (SQLException e) {}
            }
        });
    }

    @Override
    public void onLoadPlayer(final UUID uuid) {
        final PreparedStatement statement = getDatabase().createQuery().select("*").from(getTable()).where("uuid", Operator.EQUAL, uuid.toString()).getStatement();
        executeQuery(statement, new SqlQueryCallback() {
            @Override
            public void onExecute(ResultSet result) {
                try {
                    List<Kick> playerKicks = new ArrayList<Kick>();
                    while (result.next()) {
                        Kick kick = new Kick(Timestamp.valueOf(result.getString("timestamp")), UUID.fromString(result.getString("punisher")), result.getString("reason"));
                        playerKicks.add(kick);
                    }

                    if (playerKicks.size() > 0) {
                        kicks.put(uuid, playerKicks);
                    }
                } catch (SQLException e) {}
            }
        });
    }

    @Override
    public void onSave() {
        if (kicks_local.size() < 1) {
            return;
        }
        for (UUID uuid : kicks_local.keySet()) {
            onSavePlayer(uuid);
        }
    }

    @Override
    public void onSavePlayer(final UUID uuid) {
        List<Kick> playerKicks = kicks_local.get(uuid);
        if (playerKicks == null || playerKicks.isEmpty()) {
            return;
        }
        for (final Kick kick : playerKicks) {
            //Try update kick if it exist.
            List<Object> values = new ArrayList<Object>();
            values.add(Util.getTimeStamp().toString());
            final PreparedStatement statement = getDatabase().createQuery().update(getTable()).set(Arrays.asList("last_update"), values).where("uuid", Operator.EQUAL, uuid.toString())
                    .and("timestamp", Operator.EQUAL, kick.getTimestamp().toString())
                    .and("punisher", Operator.EQUAL, kick.getPunisher().toString())
                    .and("reason", Operator.EQUAL, kick.getReason()).getStatement();

            executeUpdate(statement, new SqlUpdateCallback() {
                @Override
                public void onExecute(int rowsChanged) {
                    if (rowsChanged < 1) {
                        return;
                    }

                    //Insert kick if it doesn't exist.
                    List<Object> insertValues = new ArrayList<Object>();
                    insertValues.add(uuid.toString());
                    insertValues.add(kick.getTimestamp().toString());
                    insertValues.add(Util.getTimeStamp().toString());
                    insertValues.add(kick.getPunisher().toString());
                    insertValues.add(kick.getReason());
                    PreparedStatement insertStatement = getDatabase().createQuery().insertInto(getTable())
                            .values(Arrays.asList("uuid", "timestamp", "last_update", "punisher", "reason"), insertValues).getStatement();

                    executeUpdate(insertStatement);
                }
            });
        }
    }

    @Override
    public Column[] getTableColumns() {
        Database db = getDatabase();
        return new Column[]{
                db.createColumn("id").type("INT").primaryKey().autoIncrement(),
                db.createColumn("uuid").type("CHAR", 36).notNull(),
                db.createColumn("timestamp").type("TIMESTAMP").notNull(),
                db.createColumn("last_update").type("TIMESTAMP").notNull(),
                db.createColumn("punisher").type("CHAR", 36),
                db.createColumn("reason").type("VARCHAR", 255)
        };
    }



    // ##################################################
    // ################# IMPLEMENTATION #################
    // ##################################################

    /**
     * Get a list of all Kick instances of the given player.
     * If the player hasn't been kicked before it will return an empty list.
     * @param uuid The player to get the kicks from.
     * @return List<Kick> with all kicks.
     */
    public List<Kick> getKicks(UUID uuid) {
        if (kicks.containsKey(uuid)) {
            return kicks.get(uuid);
        }
        return new ArrayList<Kick>();
    }

    /**
     * Kicks the given player for the specified reason.
     * Please remember that a player can't be kicked if he's offline.
     * @param uuid The player to kick.
     * @param punisher The player that sends the kick. (May be null)
     * @param reason The reason for the kick. (May be null)
     * @return true if the player got kicked and false if the isn't online and the kick didn't happen.
     */
    public boolean kick(UUID uuid, UUID punisher, String reason) {
        Player player = plugin.getServer().getPlayer(uuid);
        if (player == null) {
            return false;
        }

        player.kickPlayer(EssMessage.CORE_KICK_MESSAGE.msg().params(Param.P("punisher", punisher == null ? "console" : plugin.getServer().getOfflinePlayer(punisher).getName()),
                Param.P("reason", (reason == null || reason.isEmpty() ? EssMessage.CORE_NO_REASON.msg().getText() : reason))).color().getText());

        List<Kick> playerKicks = new ArrayList<Kick>();
        if (kicks.containsKey(uuid)) {
            playerKicks = kicks.get(uuid);
        }
        playerKicks.add(new Kick(new Timestamp(System.currentTimeMillis()), punisher, reason));
        kicks.put(uuid, playerKicks);
        kicks_local.put(uuid, playerKicks);
        //TODO: Remove this when saving is implemented!
        onSave();
        return true;
    }

}
