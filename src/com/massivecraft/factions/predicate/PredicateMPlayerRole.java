package com.massivecraft.factions.predicate;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.predicate.Predicate;

public class PredicateMPlayerRole implements Predicate<MPlayer>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final Rel role;
	public Rel getRole() { return this.role; }
	
	// -------------------------------------------- //
	// INSTANCE AND CONTRUCT
	// -------------------------------------------- //
	
	public static PredicateMPlayerRole get(Rel role) { return new PredicateMPlayerRole(role); }
	public PredicateMPlayerRole(Rel role)
	{
		this.role = role;
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(MPlayer mplayer)
	{
		if (mplayer == null) return false;
		return mplayer.getRole() == this.role;
	}
}
