package com.massivecraft.factions.integration.dynmap;

import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class TempMarkerSet {

    public String label;
    public int minimumZoom;
    public int priority;
    public boolean hideByDefault;

    public MarkerSet create(MarkerAPI markerApi, String id) {
        MarkerSet ret = markerApi.createMarkerSet(id, this.label, null, false); // ("null, false" at the end means "all icons allowed, not perisistent")

        if (ret == null) {
            return null;
        }

        // Minimum Zoom
        if (this.minimumZoom > 0) {
            ret.setMinZoom(this.minimumZoom);
        }

        // Priority
        ret.setLayerPriority(this.priority);

        // Hide by Default
        ret.setHideByDefault(this.hideByDefault);

        return ret;
    }

    public void update(MarkerSet markerset) {
        // Name
        if (!markerset.getMarkerSetLabel().equals(this.label)) {
            markerset.setMarkerSetLabel(this.label);
        }

        if (this.minimumZoom > 0) {
            if (markerset.getMinZoom() != this.minimumZoom) {
                markerset.setMinZoom(this.minimumZoom);
            }
        }

        if (markerset.getLayerPriority() != this.priority) {
            markerset.setLayerPriority(this.priority);
        }

        if (markerset.getHideByDefault() != this.hideByDefault) {
            markerset.setHideByDefault(this.hideByDefault);
        }
    }

}
