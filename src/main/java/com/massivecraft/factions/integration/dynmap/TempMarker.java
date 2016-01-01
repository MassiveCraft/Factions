package com.massivecraft.factions.integration.dynmap;

import com.massivecraft.factions.Conf;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

public class TempMarker {
    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    public String label;
    public String world;
    public double x;
    public double y;
    public double z;
    public String iconName;
    public String description;

    // -------------------------------------------- //
    // CREATE
    // -------------------------------------------- //

    public Marker create(MarkerAPI markerApi, MarkerSet markerset, String markerId) {
        Marker ret = markerset.createMarker(markerId, this.label, this.world, this.x, this.y, this.z, getMarkerIcon(markerApi, this.iconName), false // not persistent
        );

        if (ret == null) {
            return null;
        }

        ret.setDescription(this.description);

        return ret;
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    public void update(MarkerAPI markerApi, Marker marker) {
        if (!this.world.equals(marker.getWorld()) || this.x != marker.getX() || this.y != marker.getY() || this.z != marker.getZ()) {
            marker.setLocation(this.world, this.x, this.y, this.z);
        }

        if (!marker.getLabel().equals(this.label)) {
            marker.setLabel(this.label);
        }

        MarkerIcon icon = getMarkerIcon(markerApi, this.iconName);
        if (marker.getMarkerIcon() == null || marker.getMarkerIcon().equals(icon)) {
            marker.setMarkerIcon(icon);
        }

        if (!marker.getDescription().equals(this.description)) {
            marker.setDescription(this.description);
        }
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    public static MarkerIcon getMarkerIcon(MarkerAPI markerApi, String name) {
        MarkerIcon ret = markerApi.getMarkerIcon(name);
        if (ret == null) {
            ret = markerApi.getMarkerIcon(Conf.DYNMAP_STYLE_HOME_MARKER);
        }
        return ret;
    }

}
