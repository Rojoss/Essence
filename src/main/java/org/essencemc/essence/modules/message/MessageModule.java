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

package org.essencemc.essence.modules.message;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.essencemc.essence.Essence;
import org.essencemc.essencecore.modules.Module;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageModule extends Module {


    /**
     * KEY: Player that can reply.
     * VALUE: Player that the key can reply to.
     */
    private Map<UUID, UUID> players = new HashMap<>();

    public MessageModule(String name){
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

    public boolean canReply(UUID uuid){
        return players.containsKey(uuid);
    }

    public Player getReplyPlayer(UUID uuid){
        return canReply(uuid) ? Essence.inst().getServer().getPlayer(players.get(uuid)) : null;
    }

    public void setReply(UUID receiver, UUID sender){
        players.put(receiver, sender);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        UUID uuid = e.getPlayer().getUniqueId();
        for(UUID id : players.keySet()){
            if(players.get(id).equals(uuid)){
                players.remove(id);
            }
        }
    }

}
