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

package org.essencemc.essence.modules.spawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.modules.Module;

import java.util.Map;
import java.util.UUID;

public class SpawnModule extends Module{

    private SpawnCfg config;

    public SpawnModule(String name){
        super(Essence.inst(), name);
    }

    @Override
    protected void onEnable() {
        config = new SpawnCfg("plugins/Essence/modules/spawns/Spawns.yml");
    }

    @Override
    protected void onDisable() {
        //config.save();
    }

    @Override
    protected void onReload() {
        config.load();
    }

    /**
     * Get spawn config.
     * @return Return spawn config.
     */
    public SpawnCfg getConfig(){
        return config;
    }

    /**
     * Get main spawn location.
     * @return Return main spawn location. Return null if no entry.
     */
    public Location getMainSpawn(){
        return config.spawn;
    }

    /**
     * Set main spawn location.
     * @param loc Spawn location.
     */
    public void setMainSpawn(Location loc){
        config.spawn = loc;
        config.save();
    }

    /**
     * Delete main spawn.
     */
    public void delMainSpawn(){
        config.spawn = null;
        config.save();
    }

    /**
     * Delete all spawns in database.
     */
    public void delAllSpawns(){
        delMainSpawn();
        delAllPlayerSpawns();
    }

    /**
     * Delete all player spawns in database.
     */
    public void delAllPlayerSpawns(){
        config.playerSpawn.clear();
        config.save();
    }
    
    /**
     * Check if main spawn was set.
     * @return Return true if main spawn is set.
     */
    public boolean hasMainSpawn(){
        return config.spawn != null;
    }

    /**
     * Get personal player spawn.
     * @param uuid Player uuid.
     * @return Return personal player spawn. Return null if he doesn't have one.
     */
    public Location getPlayerSpawn(UUID uuid){
        if(config.playerSpawn.containsKey(uuid.toString())){
            return config.playerSpawn.get(uuid.toString());
        }
        return null;
    }

    /**
     * Set personal player spawn.
     * @param uuid Player uuid.
     * @param loc Spawn location.
     */
    public void setPlayerSpawn(UUID uuid, Location loc){
        config.playerSpawn.put(uuid.toString(), loc);
        config.save();
    }

    /**
     * Delete personal player spawn.
     * @param uuid Player uuid.
     */
    public void delPlayerSpawn(UUID uuid){
        if(config.playerSpawn.containsKey(uuid.toString())){
            config.playerSpawn.remove(uuid.toString());
            config.save();
        }
    }

    /**
     * Check if player has a personal spawn.
     * @param uuid Player uuid.
     * @return Return true if player has personal spawn.
     */
    public boolean hasPlayerSpawn(UUID uuid){
        return config.playerSpawn.containsKey(uuid.toString());
    }

    /**
     * Get a spawn point for player.
     * @param uuid Player uuid.
     * @return Return personal player spawn, main spawn or server default spawn.
     */
    public Location getSpawn(UUID uuid){
        Location mainSpawn = hasMainSpawn() ? getMainSpawn() : Essence.inst().getServer().getPlayer(uuid).getWorld().getSpawnLocation();
        return hasPlayerSpawn(uuid) ? getPlayerSpawn(uuid) : mainSpawn;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        Player player = e.getPlayer();
        e.setRespawnLocation(getSpawn(player.getUniqueId()));
    }
}