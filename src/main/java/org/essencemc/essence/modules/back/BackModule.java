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

package org.essencemc.essence.modules.back;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.modules.Module;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackModule extends Module {

    private Map<UUID, Location> players = new HashMap<>();

    public BackModule(String name){
        super(Essence.inst(), name);
    }

    @Override
    protected void onEnable() {
        for(Player player : Essence.inst().getServer().getOnlinePlayers()){
            players.put(player.getUniqueId(), null);
        }
    }

    @Override
    protected void onDisable() {

    }

    @Override
    protected void onReload() {

    }

    public void setPrevious(UUID uuid, Location loc){
        players.put(uuid, loc);
    }

    public Location getPrevious(UUID uuid){
        return players.get(uuid);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        setPrevious(e.getPlayer().getUniqueId(), null);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        players.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        setPrevious(e.getPlayer().getUniqueId(), e.getFrom());
    }
}
