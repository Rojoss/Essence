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

package org.essencemc.essence.modules.signs;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.essencemc.essencecore.entity.EItem;
import org.essencemc.essencecore.modules.Module;
import org.essencemc.essencecore.util.ItemUtil;

import java.util.*;

public class LoreSignModule extends Module {

    Map<UUID, List<String>> text = new HashMap<UUID, List<String>>();

    public LoreSignModule(String name) {
        super(name);
    }

    @Override
    protected void onEnable() {}

    @Override
    protected void onDisable() {}

    @Override
    protected void onReload() {}


    @EventHandler
    private void signPlace(BlockPlaceEvent event) {
        EItem hand = new EItem(event.getPlayer().getItemInHand());
        if (hand == null || hand.getType() != Material.SIGN) {
            return;
        }
        //TODO: Better permission check.
        if (!event.getPlayer().hasPermission("essence.signs.lore.place")) {
            return;
        }
        if (hand.getLore() != null && !hand.getLore().isEmpty()) {
            text.put(event.getPlayer().getUniqueId(), hand.getLore());
        }
    }

    @EventHandler
    private void signChange(SignChangeEvent event) {
        if (!text.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        List<String> loreText = text.get(event.getPlayer().getUniqueId());
        for (int i = 0; i < loreText.size() && i < 4; i++) {
            event.setLine(i, loreText.get(i));
        }
        //TODO: find a way to update the sign UI itself.
        text.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void blockBreak(BlockBreakEvent event) {
        event.getBlock().getDrops().clear();
        Block block = event.getBlock();
        if (!(block.getState() instanceof Sign)) {
            return;
        }
        //TODO: Better permission check.
        if (!event.getPlayer().hasPermission("essence.signs.lore.break")) {
            return;
        }
        Sign sign = (Sign)block.getState();

        //TODO: Find out a proper way to modify drops.
        event.setCancelled(true);
        block.setType(Material.AIR);;
        block.breakNaturally();
        ItemUtil.dropItem(block.getLocation().add(0.5f, 0.5f, 0.5f), new EItem(Material.SIGN).setLore(sign.getLines()));
    }

}
