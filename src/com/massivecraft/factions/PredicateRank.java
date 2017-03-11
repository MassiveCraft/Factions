package com.massivecraft.factions;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.predicate.Predicate;

public class PredicateRank implements Predicate<MPlayer>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final Rank rank;
	public Rank getRank() { return this.rank; }
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	public static PredicateRank get(Rank role) { return new PredicateRank(role); }
	public PredicateRank(Rank rank)
	{
		this.rank = rank;
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(MPlayer mplayer)
	{
		if (mplayer == null) return false;
		return mplayer.getRank().equals(this.getRank());
	}
	
}
