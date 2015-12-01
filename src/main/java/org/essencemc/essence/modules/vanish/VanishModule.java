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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
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

    //TODO: Set the booleans to a configurable variable.
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

    public boolean vanish(Player player, VanishData data) {
        if (isVanished(player.getUniqueId())) {
            return false;
        }

        vanished.put(player.getUniqueId(), data);
        added.add(player.getUniqueId());

        if(invisEffect) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        }

        updateShownPlayers(player);
        return true;
    }

    public boolean unvanish(Player player) {
        if (!isVanished(player.getUniqueId())) {
            return false;
        }

        vanished.remove(player.getUniqueId());
        removed.add(player.getUniqueId());

        if(invisEffect) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }

        updateShownPlayers(player);
        return true;
    }

    //TODO: This method can probably be improved.
    private void updateShownPlayers(Player player) {
        boolean vanished = isVanished(player.getUniqueId());

        for(Player target : Bukkit.getOnlinePlayers()) {
            if(scoreboardTeams) {
                updateScoreboard(target);
            }

            if(player.getUniqueId().equals(target.getUniqueId())) {
                continue;
            }

            if(vanished) {
                player.showPlayer(target);

                if(isVanished(target.getUniqueId())) {
                    target.showPlayer(player);
                } else {
                    target.hidePlayer(player);
                }
            } else {
                target.showPlayer(player);

                if(isVanished(target.getUniqueId())) {
                    player.hidePlayer(target);
                } else {
                    player.showPlayer(target);
                }
            }
        }
    }

    //TODO: This method can probably be improved.
    private void updateScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();

        if(!isVanished(player.getUniqueId()) && (scoreboard != null)) {
            Team team = scoreboard.getTeam("vanished");

            if(team != null) {
                team.unregister();
            }

            return;
        }

        if(scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        Team team = ((scoreboard.getTeam("vanished") == null) ? scoreboard.registerNewTeam("vanished") : scoreboard.getTeam("vanished"));

        for(String entry : new HashSet<>(team.getEntries())) {
            team.removeEntry(entry);
        }

        for(Player target : Bukkit.getOnlinePlayers()) {
            if(isVanished(target.getUniqueId())) {
                team.addEntry(target.getName());
            }
        }

        team.setCanSeeFriendlyInvisibles(true);

        if(player.getScoreboard() == null){
            player.setScoreboard(scoreboard);
        }
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateShownPlayers(event.getPlayer());

        if(silentJoin) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        updateShownPlayers(event.getPlayer());

        if(silentQuit) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        VanishData data = getVanishData(event.getPlayer().getUniqueId());

        if((data == null) || data.canChat()) {
            return;
        }

        for(Player recipient : new HashSet<>(event.getRecipients())) {
            if(!isVanished(recipient.getUniqueId())) {
                event.getRecipients().remove(recipient);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }

        VanishData data = getVanishData(event.getDamager().getUniqueId());

        if((data == null) || data.canAttack()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        VanishData data = getVanishData(event.getEntity().getUniqueId());

        if((data == null) || data.canDamage()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        VanishData data = getVanishData(event.getPlayer().getUniqueId());

        if((data == null) || data.canInteract()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        VanishData data = getVanishData(event.getPlayer().getUniqueId());

        if((data == null) || data.canInteract()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        VanishData data = getVanishData(event.getPlayer().getUniqueId());

        if((data == null) || data.canInteract()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        VanishData data = getVanishData(event.getPlayer().getUniqueId());

        if((data == null) || data.canInteract()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        VanishData data = getVanishData(event.getPlayer().getUniqueId());

        if((data == null) || data.canPickup()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        VanishData data = getVanishData(event.getPlayer().getUniqueId());

        if((data == null) || data.canPickup()) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if(!(event.getTarget() instanceof Player)) {
            return;
        }

        VanishData data = getVanishData(event.getTarget().getUniqueId());

        if((data == null) || data.canBeTargeted()) {
            return;
        }

        event.setCancelled(true);
    }
}

