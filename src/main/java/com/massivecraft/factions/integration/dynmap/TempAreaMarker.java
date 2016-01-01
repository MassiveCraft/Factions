package com.massivecraft.factions.integration.dynmap;

import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;

public class TempAreaMarker {
    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    public String label;
    public String world;
    public double x[];
    public double z[];
    public String description;

    public int lineColor;
    public double lineOpacity;
    public int lineWeight;

    public int fillColor;
    public double fillOpacity;

    public boolean boost;

    // -------------------------------------------- //
    // CREATE
    // -------------------------------------------- //

    public AreaMarker create(MarkerSet markerset, String markerId) {
        AreaMarker ret = markerset.createAreaMarker(markerId, this.label, false, this.world, this.x, this.z, false // not persistent
        );

        if (ret == null) {
            return null;
        }

        // Description
        ret.setDescription(this.description);

        // Line Style
        ret.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);

        // Fill Style
        ret.setFillStyle(this.fillOpacity, this.fillColor);

        // Boost Flag
        ret.setBoostFlag(this.boost);

        return ret;
    }

    // -------------------------------------------- //
    // UPDATE
    // -------------------------------------------- //

    public void update(AreaMarker marker) {
        // Corner Locations
        if (!equals(marker, this.x, this.z)) {
            marker.setCornerLocations(this.x, this.z);
        }

        // Label
        if (!marker.getLabel().equals(this.label)) {
            marker.setLabel(this.label);
        }

        // Description
        if (!marker.getDescription().equals(this.description)) {
            marker.setDescription(this.description);
        }

        // Line Style
        if (marker.getLineWeight() != this.lineWeight ||
                    marker.getLineOpacity() != this.lineOpacity ||
                    marker.getLineColor() != this.lineColor) {
            marker.setLineStyle(this.lineWeight, this.lineOpacity, this.lineColor);
        }

        // Fill Style
        if ((marker.getFillOpacity() != this.fillOpacity) || (marker.getFillColor() != this.fillColor)) {
            marker.setFillStyle(this.fillOpacity, this.fillColor);
        }
        // Boost Flag
        if (marker.getBoostFlag() != this.boost) {
            marker.setBoostFlag(this.boost);
        }
    }

    // -------------------------------------------- //
    // UTIL
    // -------------------------------------------- //

    public static boolean equals(AreaMarker marker, double x[], double z[]) {
        int length = marker.getCornerCount();

        if (x.length != length) {
            return false;
        }
        if (z.length != length) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (marker.getCornerX(i) != x[i]) {
                return false;
            }
            if (marker.getCornerZ(i) != z[i]) {
                return false;
            }
        }

        return true;
    }

}
