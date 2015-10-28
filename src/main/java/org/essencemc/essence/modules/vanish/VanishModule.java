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

package org.essencemc.essence.modules.vanish;

import org.essencemc.essence.Essence;
import org.essencemc.essencecore.database.Column;
import org.essencemc.essencecore.database.Database;
import org.essencemc.essencecore.database.Operator;
import org.essencemc.essencecore.modules.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VanishModule extends SqlStorageModule implements PlayerStorageModule {

    private Map<UUID, VanishData> vanished = new HashMap<UUID, VanishData>();
    private List<UUID> added = new ArrayList<UUID>();
    private List<UUID> removed = new ArrayList<UUID>();

    private boolean silentJoin = true;
    private boolean silentQuit = true;
    private boolean scoreboardTeams = true;
    private boolean invisEffect = true;


    public VanishModule(String name) {
        super(Essence.inst(), name, "Vanish", DataModules.VANISH);
    }

    @Override
    protected Column[] getTableColumns() {
        Database db = getDatabase();
        return new Column[]{
                db.createColumn("id").type("INT").primaryKey().autoIncrement(),
                db.createColumn("uuid").type("CHAR", 36).notNull(),
                db.createColumn("chat").type("BOOLEAN").defaultValue(false).notNull(),
                db.createColumn("attack").type("BOOLEAN").defaultValue(false).notNull(),
                db.createColumn("damage").type("BOOLEAN").defaultValue(false).notNull(),
                db.createColumn("interact").type("BOOLEAN").defaultValue(false).notNull(),
                db.createColumn("pickup").type("BOOLEAN").defaultValue(false).notNull(),
                db.createColumn("target").type("BOOLEAN").defaultValue(false).notNull()
        };
    }

    @Override
    protected void onLoad() {
        final PreparedStatement statement = getDatabase().createQuery().select("*").from(getTable()).getStatement();
        executeQuery(statement, new SqlQueryCallback() {
            @Override
            public void onExecute(ResultSet result) {
                try {
                    Map<UUID, VanishData> loadedData = new HashMap<UUID, VanishData>();
                    while (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        VanishData data = new VanishData(result.getBoolean("chat"), result.getBoolean("attack"), result.getBoolean("damage"),
                                result.getBoolean("interact"), result.getBoolean("pickup"), result.getBoolean("target"));
                        loadedData.put(uuid, data);
                    }
                    vanished = loadedData;
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
                    if (result.next()) {
                        UUID uuid = UUID.fromString(result.getString("uuid"));
                        VanishData data = new VanishData(result.getBoolean("chat"), result.getBoolean("attack"), result.getBoolean("damage"),
                                result.getBoolean("interact"), result.getBoolean("pickup"), result.getBoolean("target"));
                        vanished.put(uuid, data);
                    } else {
                        if (vanished.containsKey(uuid)) {
                            vanished.remove(uuid);
                        }
                    }
                    if (added.contains(uuid)) {
                        added.remove(uuid);
                    }
                    if (removed.contains(uuid)) {
                        removed.remove(uuid);
                    }
                } catch (SQLException e) {}
            }
        });
    }

    @Override
    protected void onSave() {
        List<UUID> addedClone = new ArrayList<UUID>(added);
        for (UUID uuid : addedClone) {
            onSavePlayer(uuid);
        }
        List<UUID> removedClone = new ArrayList<UUID>(removed);
        for (UUID uuid : removedClone) {
            onSavePlayer(uuid);
        }
    }

    @Override
    public void onSavePlayer(UUID uuid) {
        if (added.contains(uuid)) {
            List<Object> values = new ArrayList<Object>();
            values.add(uuid.toString());
            VanishData data = getVanishData(uuid);
            values.add(data.canChat());
            values.add(data.canAttack());
            values.add(data.canDamage());
            values.add(data.canInteract());
            values.add(data.canPickup());
            values.add(data.canBeTargeted());
            final PreparedStatement statement = getDatabase().createQuery().insertInto(getTable()).
                    values(Arrays.asList("uuid", "chat", "attack", "damage", "interact", "pickup", "target"), values).getStatement();
            executeUpdate(statement);
            added.remove(uuid);
        }
        if (removed.contains(uuid)) {
            final PreparedStatement statement = getDatabase().createQuery().delete().from(getTable()).where("uuid", Operator.EQUAL, uuid.toString()).getStatement();
            executeUpdate(statement);
            removed.remove(uuid);
        }
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}

    @Override
    protected void onReload() {}


    public void updateOptions(boolean silentJoin, boolean silentQuit, boolean teams, boolean invisEffect) {
        this.silentJoin = silentJoin;
        this.silentQuit = silentQuit;
        this.scoreboardTeams = teams;
        this.invisEffect = invisEffect;
    }

    public boolean vanish(UUID uuid, VanishData data) {
        if (isVanished(uuid)) {
            return false;
        }
        vanished.put(uuid, data);
        added.add(uuid);
        return true;
    }

    public boolean unvanish(UUID uuid) {
        if (!isVanished(uuid)) {
            return false;
        }
        vanished.remove(uuid);
        removed.add(uuid);
        return true;
    }

    public boolean isVanished(UUID uuid) {
        return vanished.containsKey(uuid);
    }

    public VanishData getVanishData(UUID uuid) {
        if (!isVanished(uuid)) {
            return null;
        }
        return vanished.get(uuid);
    }

    public Map<UUID, VanishData> getVanishedPlayers() {
        return vanished;
    }

    //TODO: Actually vanish the players.

    //TODO: Handle silent join and silent quit.

    //TODO: Scoreboard teams for vanished players

    //TODO:Invis potion effect for vanished players.

    //TODO: Handle all events like cancel chat, interact, damage etc.
}

