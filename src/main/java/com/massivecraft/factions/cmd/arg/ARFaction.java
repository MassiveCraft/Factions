package com.massivecraft.factions.cmd.arg;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.massivecore.cmd.arg.ArgReaderAbstract;
import com.massivecraft.massivecore.cmd.arg.ArgResult;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.Txt;

public class ARFaction extends ArgReaderAbstract<Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	public static ARFaction get(Object universe) { return new ARFaction(FactionColls.get().get(universe)); }
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
		
		// Faction Name Exact 
		result.setResult(this.getColl().getByName(str));
		if (result.hasResult()) return result;
		
		// Faction Name Match
		result.setResult(this.getColl().getBestNameMatch(str));
		if (result.hasResult()) return result;
		
		// UPlayer Name Exact
		String id = IdUtil.getId(str);
		UPlayer uplayer = UPlayerColls.get().get(this.getColl()).get(id);
		if (uplayer != null)
		{
			result.setResult(uplayer.getFaction());
			return result;
		}
		
		result.setErrors(Txt.parse("<b>No faction or player matching \"<p>%s<b>\".", str));
		return result;
	}
	
}
