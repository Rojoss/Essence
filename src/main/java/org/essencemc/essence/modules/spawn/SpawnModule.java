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
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.modules.Module;

import java.util.Map;

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
     * Set a spawn point for a specific player uuid. if uuid is null, then set essence spawn instead.
     * @param uuid Player uuid.
     * @param loc Spawn location.
     */
    public void setSpawn(String uuid, Location loc){
        if(uuid == null){
            setEssenceSpawn(loc);
        } else {
            config.playerSpawn.put(uuid, loc);
            config.save();
        }
    }

    /**
     * Set default spawn for server.
     * @param loc Location of default spawn.
     */
    public void setEssenceSpawn(Location loc){
        config.spawn = loc;
        config.save();
    }

    /**
     * Delete player spawn
     * @param uuid Player uuid;
     */
    public void delSpawn(String uuid) {
        for (Map.Entry<String, Location> spawn : config.playerSpawn.entrySet()) {
            if (spawn.getKey().equalsIgnoreCase(uuid)) {
                config.playerSpawn.remove(spawn.getKey());
                config.save();
            }
        }
    }

    /**
     * Delete essence spawn.
     */
    public void delEssenceSpawn(){
        config.spawn = null;
        config.save();
    }

    /**
     * Delete all player spawns.
     */
    public void delPlayerSpawns(){
        config.playerSpawn.clear();
        config.save();
    }

    /**
     * Delete all spawns.
     */
    public void delSpawns(){
        delEssenceSpawn();
        delPlayerSpawns();
    }

    /**
     *  Check if essence spawn is set.
     * @return Return true if essence spawn is set.
     */
    public boolean hasEssenceSpawn(){
        return config.spawn != null;
    }

    /**
     * Check if a player has personal spawn.
     * @param uuid Player uuid.
     * @return returns true if player has personal spawn.
     */
    public boolean hasSpawn(String uuid){
        for (Map.Entry<String, Location> spawn : config.playerSpawn.entrySet()) {
            if (spawn.getKey().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get spawn config.
     * @return Return spawn config.
     */
    public SpawnCfg getConfig(){
        return config;
    }

    /**
     * Get the spawn location for a specific player.
     * @param uuid Player uuid.
     * @return Return spawn location for a specific player.
     */
    public Location getSpawn(String uuid){
        for (Map.Entry<String, Location> spawn : config.playerSpawn.entrySet()) {
            if (spawn.getKey().equalsIgnoreCase(uuid)) {
                return spawn.getValue();
            }
        }
        return config.spawn;
    }

    /**
     * Get essence spawn point.
     * @return Return essence spawn.
     */
    public Location getEssenceSpawn() {
        return config.spawn;
    }
}