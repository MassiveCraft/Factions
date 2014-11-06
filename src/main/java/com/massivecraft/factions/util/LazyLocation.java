package com.massivecraft.factions.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;

/*
 * This class provides a lazy-load Location, so that World doesn't need to be initialized
 * yet when an object of this class is created, only when the Location is first accessed.
 */

public class LazyLocation implements Serializable {
    private static final long serialVersionUID = -6049901271320963314L;
    private transient Location location = null;
    private String worldName;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    public LazyLocation(Location loc) {
        setLocation(loc);
    }

    public LazyLocation(final String worldName, final double x, final double y, final double z) {
        this(worldName, x, y, z, 0, 0);
    }

    public LazyLocation(final String worldName, final double x, final double y, final double z, final float yaw, final float pitch) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    // This returns the actual Location
    public final Location getLocation() {
        // make sure Location is initialized before returning it
        initLocation();
        return location;
    }

    // change the Location
    public final void setLocation(Location loc) {
        this.location = loc;
        this.worldName = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }


    // This initializes the Location
    private void initLocation() {
        // if location is already initialized, simply return
        if (location != null) {
            return;
        }

        // get World; hopefully it's initialized at this point
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return;
        }

        // store the Location for future calls, and pass it on
        location = new Location(world, x, y, z, yaw, pitch);
    }


    public final String getWorldName() {
        return worldName;
    }

    public final double getX() {
        return x;
    }

    public final double getY() {
        return y;
    }

    public final double getZ() {
        return z;
    }

    public final double getPitch() {
        return pitch;
    }

    public final double getYaw() {
        return yaw;
    }
}
