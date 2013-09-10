package com.massivecraft.factions.entity;

import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.store.SenderEntity;
import com.massivecraft.mcore.xlib.gson.JsonObject;

public class MPlayer extends SenderEntity<MPlayer>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static MPlayer get(Object oid)
	{
		return MPlayerColl.get().get(oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public MPlayer load(MPlayer that)
	{
		this.mapAutoUpdating = that.mapAutoUpdating;
		this.usingAdminMode = that.usingAdminMode;
		this.setCustomData(that.getCustomData());
		
		return this;
	}
	
	@Override
	public boolean isDefault()
	{
		if (this.isMapAutoUpdating()) return false;
		if (this.isUsingAdminMode()) return false;
		if (this.getCustomData() != null && this.getCustomData().entrySet().size() > 0) return false;
		
		return true;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private boolean mapAutoUpdating = false;
	public boolean isMapAutoUpdating() { return this.mapAutoUpdating; }
	public void setMapAutoUpdating(boolean mapAutoUpdating) { this.mapAutoUpdating = mapAutoUpdating; this.changed(); }
	
	private boolean usingAdminMode = false;
	public boolean isUsingAdminMode()
	{
		if (this.usingAdminMode && this.getSender() != null && !Perm.ADMIN.has(this.getSender(), false))
		{
			// If we are using admin mode but don't have permissions for it we deactivate it.
			this.setUsingAdminMode(false);
		}
		return this.usingAdminMode;
	}
	public void setUsingAdminMode(boolean usingAdminMode) { this.usingAdminMode = usingAdminMode; this.changed(); }
	
	// Custom Data - Since JsonObject is mutable there is not point to using fancy getters/setters.
	private JsonObject customData = null;
	public JsonObject getCustomData() { return this.customData; }
	public void setCustomData(JsonObject customData) { this.customData = customData; }
	
}
