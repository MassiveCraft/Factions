package com.massivecraft.factions.entity;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.store.SenderEntity;

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
		
		return this;
	}
	
	@Override
	public boolean isDefault()
	{
		if (this.isMapAutoUpdating()) return false;
		if (this.isUsingAdminMode()) return false;
		
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
	
}
