package com.platymuus.bukkit.nether;

import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.World.Environment;

/**
 * Travel agent for Agent mode
 */
class NetherTravelAgent implements TravelAgent {

    private final NetherPlugin plugin;
    private final String player;
    private final boolean destNether;

    private int searchRadius = 16;
    private int creationRadius = 12;
    private boolean canCreate = true;

    public NetherTravelAgent(NetherPlugin plugin, String player, Environment sourceEnvironment) {
        this.plugin = plugin;
        this.player = player;
        destNether = sourceEnvironment != Environment.NETHER;
    }

    private String destText() {
        return destNether ? "Nether" : "normal world";
    }

    public TravelAgent setSearchRadius(int radius) {
        searchRadius = radius;
        return this;
    }

    public int getSearchRadius() {
        return searchRadius;
    }

    public TravelAgent setCreationRadius(int radius) {
        creationRadius = radius;
        return this;
    }

    public int getCreationRadius() {
        return creationRadius;
    }

    public void setCanCreatePortal(boolean create) {
        canCreate = create;
    }

    public boolean getCanCreatePortal() {
        return canCreate;
    }

    public Location findOrCreate(Location location) {
        Location foundLoc = findPortal(location);
        if (foundLoc == null && createPortal(location)) {
            foundLoc = findPortal(location);
            if (foundLoc == null) {
                // So apparently we created an unfindable portal.
                plugin.logMessage(player + " failed to portal to " + destText());
                return location;
            } else {
                plugin.logMessage(player + " portalled to " + destText() + " [NEW]");
                return foundLoc;
            }
        } else {
            plugin.logMessage(player + " portalled to " + destText());
            return foundLoc;
        }
    }

    public Location findPortal(Location location) {
        NetherPortal portal = NetherPortal.findPortal(location.getBlock(), searchRadius);
        if (portal == null) return null;
        return portal.getVerifiedSpawn();
    }

    public boolean createPortal(Location location) {
        if (!canCreate) {
            return false;
        }

        NetherPortal.createPortal(location.getBlock());
        return true;
    }

}
