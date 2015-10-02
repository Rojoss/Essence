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

package org.essencemc.essence.config;

import org.bukkit.Location;
import org.essencemc.essencecore.config.internal.EasyConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Warps extends EasyConfig {

    public Map<String, Location> warps = new HashMap<String, Location>();

    public Warps(String fileName) {
        this.setFile(fileName);
        load();
    }

    /**
     * Get a warp by name.
     * The name is not case sensitive.
     * It will return null if no warp is found with the given name.
     */
    public Location getWarp(String name) {
        for (Map.Entry<String, Location> warp : warps.entrySet()) {
            if (warp.getKey().equalsIgnoreCase(name)) {
                return warp.getValue();
            }
        }
        return null;
    }

    /** Get the map with all the warps. */
    public Map<String, Location> getWarps() {
        return warps;
    }

    /** Get a list of all the warp names */
    public List<String> getWarpNames() {
        return new ArrayList<String>(warps.keySet());
    }

    /**
     * Set a warp with a name and location.
     * If a warp with this location already exisists it will overwrite the location.
     */
    public void setWarp(String name, Location location) {
        warps.put(name, location);
        save();
    }

    /**
     * Try to delete a warp with the specified name.
     * The name is not case sensitive.
     * It will return true if it deleted a warp and false if not.
     */
    public boolean delWarp(String name) {
        for (Map.Entry<String, Location> warp : warps.entrySet()) {
            if (warp.getKey().equalsIgnoreCase(name)) {
                warps.remove(warp.getKey());
                save();
                return true;
            }
        }
        return false;
    }

    public void clear() {
        warps.clear();
        save();
    }

}
