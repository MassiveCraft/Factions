package com.massivecraft.factions;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.predicate.Predicate;

public class PredicateRole implements Predicate<MPlayer>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final Rel role;
	public Rel getRole() { return this.role; }
	
	// -------------------------------------------- //
	// INSTANCE AND CONTRUCT
	// -------------------------------------------- //
	
	public static PredicateRole get(Rel role) { return new PredicateRole(role); }
	public PredicateRole(Rel role)
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
