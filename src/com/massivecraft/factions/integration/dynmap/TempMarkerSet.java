package com.massivecraft.factions.integration.dynmap;

import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import com.massivecraft.massivecore.util.MUtil;

public class TempMarkerSet
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public String label;
	public int minimumZoom;
	public int priority;
	public boolean hideByDefault;
	
	// -------------------------------------------- //
	// CREATE
	// -------------------------------------------- //
	
	public MarkerSet create(MarkerAPI markerApi, String id)
	{
		MarkerSet ret = markerApi.createMarkerSet(id, this.label, null, false); // ("null, false" at the end means "all icons allowed, not perisistent")
		
		if (ret == null) return null;
		
		// Minimum Zoom
		if (this.minimumZoom > 0)
		{
			ret.setMinZoom(this.minimumZoom);
		}

		// Priority
		ret.setLayerPriority(this.priority);

		// Hide by Default
		ret.setHideByDefault(this.hideByDefault);
		
		return ret;
	}
	
	// -------------------------------------------- //
	// UPDATE
	// -------------------------------------------- //
	
	public void update(MarkerAPI markerApi, MarkerSet markerset)
	{
		// Name
		if (!MUtil.equals(markerset.getMarkerSetLabel(), this.label))
		{
			markerset.setMarkerSetLabel(this.label);
		}

		// Minimum Zoom
		if (this.minimumZoom > 0)
		{
			if (!MUtil.equals(markerset.getMinZoom(), this.minimumZoom))
			{
				markerset.setMinZoom(this.minimumZoom);
			}
		}

		// Priority
		if (!MUtil.equals(markerset.getLayerPriority(), this.priority))
		{
			markerset.setLayerPriority(this.priority);
		}

		// Hide by Default
		if (!MUtil.equals(markerset.getHideByDefault(), this.hideByDefault))
		{
			markerset.setHideByDefault(this.hideByDefault);
		}
	}
	
}
