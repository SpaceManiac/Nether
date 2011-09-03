package com.platymuus.bukkit.nether;

import org.bukkit.Location;
import org.bukkit.TravelAgent;

class NetherTravelAgent implements TravelAgent {
    
    private int searchRadius = 5;
    private int creationRadius = 5;
    private boolean canCreate = true;

    public NetherTravelAgent(NetherPlugin plugin) {
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
            return location;
        }
        return foundLoc;
    }

    public Location findPortal(Location location) {
        return null;
    }

    public boolean createPortal(Location location) {
        if (!canCreate) return false;
        
        return true;
    }
    
}
