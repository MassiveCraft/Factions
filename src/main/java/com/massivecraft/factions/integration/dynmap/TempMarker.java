package com.massivecraft.factions.integration.dynmap;

import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.util.MUtil;

public class TempMarker
{
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
	
	public Marker create(MarkerAPI markerApi, MarkerSet markerset, String markerId)
	{
		Marker ret = markerset.createMarker(
			markerId,
			this.label,
			this.world,
			this.x,
			this.y,
			this.z,
			getMarkerIcon(markerApi, this.iconName),
			false // not persistent
		);
		
		if (ret == null) return null;
		
		ret.setDescription(this.description);
		
		return ret;
	}
	
	// -------------------------------------------- //
	// UPDATE
	// -------------------------------------------- //
	
	public void update(MarkerAPI markerApi, MarkerSet markerset, Marker marker)
	{
		if
		(
			marker.getWorld() != this.world
			||
			marker.getX() != this.x
			||
			marker.getY() != this.y
			||
			marker.getZ() != this.z
		)
		{
			marker.setLocation(
					this.world,
					this.x,
					this.y,
					this.z
			);
		}
		
		if (!MUtil.equals(marker.getLabel(), this.label))
		{
			marker.setLabel(this.label);
		}
		
		MarkerIcon icon = getMarkerIcon(markerApi, this.iconName);
		if (!MUtil.equals(marker.getMarkerIcon(), icon))
		{
			marker.setMarkerIcon(icon);
		}
		
		if (!MUtil.equals(marker.getDescription(), this.description))
		{
			marker.setDescription(this.description);
		}
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static MarkerIcon getMarkerIcon(MarkerAPI markerApi, String name)
	{
		MarkerIcon ret = markerApi.getMarkerIcon(name);
		if (ret == null) ret = markerApi.getMarkerIcon(MConf.DYNMAP_STYLE_HOME_MARKER);
		return ret;
	}
	
}
