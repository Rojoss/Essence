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

package org.essencemc.essence.modules.ban;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.database.Column;
import org.essencemc.essencecore.database.Database;
import org.essencemc.essencecore.database.Operator;
import org.essencemc.essencecore.modules.*;
import org.essencemc.essencecore.util.Util;

import java.sql.*;
import java.util.*;

public class BanModule extends SqlStorageModule implements PlayerStorageModule {

    public Map<UUID, List<Ban>> bans = new HashMap<UUID, List<Ban>>();
    public Map<UUID, List<Ban>> bans_local = new HashMap<UUID, List<Ban>>();

    public BanModule(String name) {
        super(Essence.inst(), name, "ban", DataModules.BAN);
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
                    Map<UUID, List<Ban>> loadedBans = new HashMap<UUID, List<Ban>>();
                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        List<Ban> playerBans = new ArrayList<Ban>();
                        if (loadedBans.containsKey(uuid)) {
                            playerBans = loadedBans.get(uuid);
                        }
                        Ban ban = new Ban(Timestamp.valueOf(result.getString("timestamp")), result.getLong("duration"), UUID.fromString(result.getString("punisher")), result.getString("reason"), Boolean.valueOf(result.getString("state")));
                        playerBans.add(ban);
                        loadedBans.put(uuid, playerBans);
                    }
                    bans = loadedBans;
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
                    List<Ban> playerBans = new ArrayList<Ban>();
                    while (result.next()) {
                        Ban ban = new Ban(Timestamp.valueOf(result.getString("timestamp")), result.getLong("duration"), UUID.fromString(result.getString("punisher")), result.getString("reason"), Boolean.valueOf(result.getString("state")));
                        playerBans.add(ban);
                    }

                    if (playerBans.size() > 0) {
                        bans.put(uuid, playerBans);
                    }
                } catch (SQLException e) {}
            }
        });
    }

    @Override
    public void onSave() {
        if (bans_local.size() < 1) {
            return;
        }
        for (UUID uuid : bans_local.keySet()) {
            onSavePlayer(uuid);
        }
    }

    @Override
    public void onSavePlayer(final UUID uuid) {
        List<Ban> playerBans = bans_local.get(uuid);
        for (final Ban ban : playerBans) {
            //Try update ban if it exist.
            List<Object> values = new ArrayList<Object>();
            values.add(Boolean.toString(ban.isActive()));
            values.add(Util.getTimeStamp().toString());
            final PreparedStatement statement = getDatabase().createQuery().update(getTable()).set(Arrays.asList("state", "last_update"), values).where("uuid", Operator.EQUAL, uuid.toString())
                    .and("timestamp", Operator.EQUAL, ban.getTimestamp().toString())
                    .and("punisher", Operator.EQUAL, ban.getPunisher().toString())
                    .and("reason", Operator.EQUAL, ban.getReason()).getStatement();

            executeUpdate(statement, new SqlUpdateCallback() {
                @Override
                public void onExecute(int rowsChanged) {
                    if (rowsChanged < 1) {
                        return;
                    }

                    //Insert ban if it doesn't exist.
                    List<Object> insertValues = new ArrayList<Object>();
                    insertValues.add(uuid.toString());
                    insertValues.add(ban.getTimestamp().toString());
                    insertValues.add(Util.getTimeStamp().toString());
                    insertValues.add(ban.getDuration());
                    insertValues.add(ban.getPunisher().toString());
                    insertValues.add(ban.getReason());
                    insertValues.add(Boolean.toString(true));
                    PreparedStatement insertStatement = getDatabase().createQuery().insertInto(getTable())
                            .values(Arrays.asList("uuid", "timestamp", "last_update", "duration", "punisher", "reason", "state"), insertValues).getStatement();

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
                db.createColumn("duration").type("BIGINT").notNull(),
                db.createColumn("punisher").type("CHAR", 36),
                db.createColumn("reason").type("VARCHAR", 255),
                db.createColumn("state").type("BOOLEAN").notNull()
        };
    }



    // ##################################################
    // ################# IMPLEMENTATION #################
    // ##################################################

    /**
     * Get a Ban instance if the player is currently banned.
     * If the player doesn't have an active ban it will return null.
     * @param uuid The player to get the ban from.
     * @return Ban instance or null.
     */
    public Ban getActiveBan(UUID uuid) {
        if (bans.containsKey(uuid)) {
            for (Ban ban : bans.get(uuid)) {
                if (ban.isActive()) {
                    return ban;
                }
            }
        }
        return null;
    }

    /**
     * Get a list of all Ban instances of the given player.
     * If the player doesn't have any bans it will return an empty list.
     * @param uuid The player to get the bans from.
     * @return List<Ban> with all bans.
     */
    public List<Ban> getBans(UUID uuid) {
        if (bans.containsKey(uuid)) {
            return bans.get(uuid);
        }
        return new ArrayList<Ban>();
    }

    /**
     * Checks if the given player is banned or not.
     * This only returns true if the player has no ACTIVE ban.
     * If the player is banned before but the time has run out this will be false.
     * @param uuid The player to check.
     * @return true if banned and false if the player has no active ban.
     */
    public boolean isBanned(UUID uuid) {
        return getActiveBan(uuid) != null;
    }

    /**
     * Bans the given player for the specified duration and reason.
     * Please remember that a player can't be banned while he already has an active ban.
     * @param uuid The player to ban.
     * @param duration The duration in milliseconds to ban the player.
     * @param punisher The player that sends the ban. (May be null)
     * @param reason The reason for the ban. (May be null)
     * @return true if the player got banned and false if the player was already banned and it didn't ban the player.
     */
    public boolean ban(UUID uuid, Long duration, UUID punisher, String reason) {
        if (isBanned(uuid)) {
            return false;
        }
        List<Ban> playerBans = new ArrayList<Ban>();
        if (bans.containsKey(uuid)) {
            playerBans = bans.get(uuid);
        }
        playerBans.add(new Ban(new Timestamp(System.currentTimeMillis()), duration, punisher, reason, true));
        bans.put(uuid, playerBans);
        bans_local.put(uuid, playerBans);
        //TODO: Remove this when saving is implemented!
        onSave();
        return true;
    }

    /**
     * Unban the given player if he has an active ban.
     * If the player isn't banned nothing will happen.
     * @param uuid The player to unban.
     * @return true if the player was unbanned and false if not.
     */
    public boolean unban(UUID uuid) {
        if (!isBanned(uuid)) {
            return false;
        }
        getActiveBan(uuid).setState(false);
        bans_local.put(uuid, getBans(uuid));
        //TODO: Remove this when saving is implemented!
        onSave();
        return true;
    }

    private Long getOnlineTime(UUID uuid) {
        //TODO: Get the time the player was online.
        return 0l;
    }


    // Listeners

    @EventHandler
    private void login(PlayerLoginEvent event) {
        Ban activeBan = getActiveBan(event.getPlayer().getUniqueId());
        if (activeBan == null) {
            return;
        }
        //TODO: Get proper message with formatting and punisher/time remaining etc.
        event.setKickMessage(activeBan.getReason());
        event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
    }

}
