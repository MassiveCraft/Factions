package com.massivecraft.factions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.util.extractor.Extractor;

public class ExtractorFactionAccountId implements Extractor
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ExtractorFactionAccountId i = new ExtractorFactionAccountId();
	public static ExtractorFactionAccountId get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: EXTRACTOR
	// -------------------------------------------- //
	
	@Override
	public Object extract(Object o)
	{
		if (o instanceof Faction)
		{
			String factionId = ((Faction)o).getId();
			if (factionId == null) return null;
			return Factions.FACTION_MONEY_ACCOUNT_ID_PREFIX + factionId;
		}
		
		return null;
	}
	
}
