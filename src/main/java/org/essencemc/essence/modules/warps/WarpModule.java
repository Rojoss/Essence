package org.essencemc.essence.modules.warps;

import org.bukkit.Location;
import org.essencemc.essencecore.modules.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WarpModule extends Module {

    private WarpsCfg config;

    public WarpModule(String name) {
        super(name);
    }

    @Override
    protected void onEnable() {
        config = new WarpsCfg("plugins/Essence/modules/warps/Warps.yml");
    }

    @Override
    protected void onDisable() {}

    @Override
    protected void onReload() {
        config.load();
    }

    public WarpsCfg getConfig() {
        return config;
    }

    /**
     * Get a warp by name.
     * The name is not case sensitive.
     * It will return null if no warp is found with the given name.
     */
    public Location getWarp(String name) {
        for (Map.Entry<String, Location> warp : config.warps.entrySet()) {
            if (warp.getKey().equalsIgnoreCase(name)) {
                return warp.getValue();
            }
        }
        return null;
    }

    /** Get the map with all the warps. */
    public Map<String, Location> getWarps() {
        return config.warps;
    }

    /** Get a list of all the warp names */
    public List<String> getWarpNames() {
        return new ArrayList<String>(config.warps.keySet());
    }

    /**
     * Set a warp with a name and location.
     * If a warp with this location already exisists it will overwrite the location.
     */
    public void setWarp(String name, Location location) {
        config.warps.put(name, location);
        config.save();
    }

    /**
     * Try to delete a warp with the specified name.
     * The name is not case sensitive.
     * It will return true if it deleted a warp and false if not.
     */
    public boolean delWarp(String name) {
        for (Map.Entry<String, Location> warp : config.warps.entrySet()) {
            if (warp.getKey().equalsIgnoreCase(name)) {
                config.warps.remove(warp.getKey());
                config.save();
                return true;
            }
        }
        return false;
    }

    /**
     * Delete all the warps.
     */
    public void delWarps() {
        config.warps.clear();
        config.save();
    }
}
