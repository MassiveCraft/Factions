package com.massivecraft.factions.cmd.arg;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.FPlayerColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.mcore.cmd.arg.ArgReaderAbstract;
import com.massivecraft.mcore.cmd.arg.ArgResult;
import com.massivecraft.mcore.util.Txt;

public class ARFaction extends ArgReaderAbstract<Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	public static ARFaction get(Object o) { return new ARFaction(FactionColls.get().get(o)); }
	private ARFaction(FactionColl coll)
	{
		this.coll = coll;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final FactionColl coll;
	public FactionColl getColl() { return this.coll;}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public ArgResult<Faction> read(String str, CommandSender sender)
	{
		ArgResult<Faction> result = new ArgResult<Faction>();
		
		// Faction Tag Exact 
		result.setResult(this.getColl().getByTag(str));
		if (result.hasResult()) return result;
		
		// Faction Tag Match
		result.setResult(this.getColl().getBestTagMatch(str));
		if (result.hasResult()) return result;
		
		// FPlayer Name Exact
		FPlayer fplayer = FPlayerColls.get().get(this.getColl()).get(str);
		if (fplayer != null)
		{
			result.setResult(fplayer.getFaction());
			return result;
		}
		
		result.setErrors(Txt.parse("<b>No faction or player matching \"<p>%s<b>\".", str));
		return result;
	}
	
}
