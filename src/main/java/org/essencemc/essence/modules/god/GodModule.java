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

package org.essencemc.essence.modules.god;



import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.modules.Module;

import java.util.*;

public class GodModule extends Module {

    private Map<UUID, GodData> gods = new HashMap<UUID, GodData>();

    public GodModule(String name){
        super(Essence.inst(), name);
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

    public void god(UUID uuid, GodData data){
        if(isGod(uuid)){
            return;
        }
        gods.put(uuid, data);
    }

    public void ungod(UUID uuid){
        if(!isGod(uuid)){
            return;
        }
        gods.remove(uuid);
    }

    public boolean isGod(UUID uuid){
        return gods.containsKey(uuid);
    }

    public GodData getGodData(UUID uuid){
        if(isGod(uuid)){
            return gods.get(uuid);
        }
        return null;
    }

    public Map<UUID, GodData> getGodPlayers(){
        return gods;
    }

    @EventHandler
    public void onDamageByPlayer(EntityDamageByEntityEvent e){
        if(!(e.getEntity() instanceof Player)){
            return;
        }

        if(e.getDamager() instanceof Player){

            Player damager = (Player) e.getDamager();
            UUID uuid = damager.getUniqueId();

            if(isGod(uuid) && !getGodData(uuid).canAttack()){
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){
        if(!(e.getEntity() instanceof Player)){
            return;
        }

        Player player = (Player)e.getEntity();
        UUID uuid = player.getUniqueId();

        if(isGod(uuid)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e){
        if(!(e.getEntity() instanceof Player)){
            return;
        }

        Player player = (Player)e.getEntity();
        UUID uuid = player.getUniqueId();

        if(isGod(uuid) && !getGodData(uuid).canStarve()){
            e.setFoodLevel(20);
        }
    }

}
