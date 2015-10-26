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
        new BukkitRunnable() {
            @Override
            public void run() {
                Map<UUID, List<Ban>> loadedBans = new HashMap<UUID, List<Ban>>();
                Connection sql = ess.getSql();
                if (sql == null) {
                    //TODO: Better logging system for this. (Want to warn staff in game etc)
                    ess.logError("Failed to load bans data!");
                    return;
                }
                Database db = ess.getDB();
                try {
                    String table = ess.getDataStorageCfg().table_name.replace("{type}", "ban").replace("{suffix}", ess.getDataStorageCfg().storage_modules.get("ban").get("suffix"));
                    String query = db.createQuery().select("*").from(table).get();
                    Statement statement = sql.createStatement();
                    ResultSet result = statement.executeQuery(query);

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
                    statement.close();
                } catch (SQLException e) {
                    //TODO: Better logging system for this. (Want to warn staff in game etc)
                    ess.logError("Failed to load bans data!");
                    ess.logError(e.getMessage());
                }
            }
        }.runTaskAsynchronously(ess);
    }

    @Override
    public void onLoadPlayer(final UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Ban> playerBans = new ArrayList<Ban>();
                Connection sql = ess.getSql();
                if (sql == null) {
                    //TODO: Better logging system for this. (Want to warn staff in game etc)
                    ess.logError("Failed to load users bans data!");
                    return;
                }
                Database db = ess.getDB();

                try {
                    String table = ess.getDataStorageCfg().table_name.replace("{type}", "ban").replace("{suffix}", ess.getDataStorageCfg().storage_modules.get("ban").get("suffix"));
                    String query = db.createQuery().select("*").from(table).where("uuid", Operator.EQUAL, uuid.toString()).get();
                    Statement statement = sql.createStatement();
                    ResultSet result = statement.executeQuery(query);

                    while (result.next()) {
                        Ban ban = new Ban(Timestamp.valueOf(result.getString("timestamp")), result.getLong("duration"), UUID.fromString(result.getString("punisher")), result.getString("reason"), Boolean.valueOf(result.getString("state")));
                        playerBans.add(ban);
                    }

                    if (playerBans.size() > 0) {
                        bans.put(uuid, playerBans);
                    }
                    statement.close();
                } catch (SQLException e) {
                    //TODO: Better logging system for this. (Want to warn staff in game etc)
                    ess.logError("Failed to load bans data!");
                    ess.logError(e.getMessage());
                }
            }
        }.runTaskAsynchronously(ess);
    }

    @Override
    public void onSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (bans_local.size() < 1) {
                    return;
                }
                Connection sql = ess.getSql();
                if (sql == null) {
                    //TODO: Better logging system for this. (Want to warn staff in game, save the data in a local file so there is no data loss etc)
                    ess.logError("Failed to save bans data!");
                    return;
                }
                for (Map.Entry<UUID, List<Ban>> entry : bans_local.entrySet()) {
                    onSavePlayer(entry.getKey());
                }
            }
        }.runTaskAsynchronously(ess);
    }

    @Override
    public void onSavePlayer(final UUID uuid) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (bans_local.size() < 1 || !bans_local.containsKey(uuid) || bans_local.get(uuid).size() < 1) {
                    return;
                }
                Connection sql = ess.getSql();
                if (sql == null) {
                    //TODO: Better logging system for this. (Want to warn staff in game, save the data in a local file so there is no data loss etc)
                    ess.logError("Failed to save users bans data!");
                    return;
                }
                Database db = ess.getDB();
                String table = ess.getDataStorageCfg().table_name.replace("{type}", "ban").replace("{suffix}", ess.getDataStorageCfg().storage_modules.get("ban").get("suffix"));

                try {
                    List<Ban> playerBans = bans_local.get(uuid);
                    for (Ban ban : playerBans) {
                        //Update ban
                        List<Object> values = new ArrayList<Object>();
                        values.add(Boolean.toString(ban.isActive()));
                        values.add(Util.getTimeStamp().toString());
                        String updateQuery = db.createQuery().update(table).set(Arrays.asList("state", "last_update"), values).where("uuid", Operator.EQUAL, uuid.toString())
                                .and("timestamp", Operator.EQUAL, ban.getTimestamp().toString())
                                .and("punisher", Operator.EQUAL, ban.getPunisher().toString())
                                .and("reason", Operator.EQUAL, ban.getReason()).get();
                        Statement updateS = sql.createStatement();
                        int rows = updateS.executeUpdate(updateQuery);
                        updateS.close();

                        if (rows == 0) {
                            //Insert if nothing got updated.
                            List<Object> insertValues = new ArrayList<Object>();
                            insertValues.add(uuid.toString());
                            insertValues.add(ban.getTimestamp().toString());
                            insertValues.add(Util.getTimeStamp().toString());
                            insertValues.add(ban.getDuration());
                            insertValues.add(ban.getPunisher().toString());
                            insertValues.add(ban.getReason());
                            insertValues.add(Boolean.toString(true));
                            String insertQuery = db.createQuery().insertInto(table).values(Arrays.asList("uuid", "timestamp", "last_update", "duration", "punisher", "reason", "state"), insertValues).get();
                            Statement insertS = sql.createStatement();
                            insertS.executeUpdate(insertQuery);
                            insertS.close();
                        }
                    }
                } catch (SQLException e) {
                    //TODO: Better logging system for this. (Want to warn staff in game, save the data in a local file so there is no data loss etc)
                    ess.logError("Failed to save users bans data!");
                    ess.logError(e.getMessage());
                }
            }
        }.runTaskAsynchronously(ess);
    }

    @Override
    public Column[] getTableColumns() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Connection sql = ess.getSql();
                if (sql == null) {
                    ess.warn("Disabling the ban module because there is no database connection.");
                    //TODO: Disable it properly.
                    onDisable();
                    return;
                }
                Database db = ess.getDB();
                try {
                    String table = ess.getDataStorageCfg().table_name.replace("{type}", "ban").replace("{suffix}", ess.getDataStorageCfg().storage_modules.get("ban").get("suffix"));
                    String query = db.createQuery().createTable(table, true, new Column[]{
                            db.createColumn("id").type("INT").primaryKey().autoIncrement(),
                            db.createColumn("uuid").type("CHAR", 36).notNull(),
                            db.createColumn("timestamp").type("TIMESTAMP").notNull(),
                            db.createColumn("last_update").type("TIMESTAMP").notNull(),
                            db.createColumn("duration").type("BIGINT").notNull(),
                            db.createColumn("punisher").type("CHAR", 36),
                            db.createColumn("reason").type("VARCHAR", 255),
                            db.createColumn("state").type("BOOLEAN").notNull()
                    }).get();

                    Statement statement = sql.createStatement();
                    statement.executeUpdate(query);
                    statement.close();
                } catch (SQLException e) {
                    onDisable();
                    ess.logError("Disabling the ban module because it failed to create the database table.");
                    ess.logError(e.getMessage());
                }
            }
        }.runTaskAsynchronously(ess);
        return null;
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
