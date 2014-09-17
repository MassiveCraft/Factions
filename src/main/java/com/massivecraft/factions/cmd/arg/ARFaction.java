package com.massivecraft.factions.cmd.arg;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.cmd.arg.ArgReaderAbstract;
import com.massivecraft.massivecore.cmd.arg.ArgResult;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.Txt;

public class ARFaction extends ArgReaderAbstract<Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ARFaction i = new ARFaction();
	public static ARFaction get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public ArgResult<Faction> read(String str, CommandSender sender)
	{
		ArgResult<Faction> result = new ArgResult<Faction>();
		
		// Faction Name Exact 
		result.setResult(FactionColl.get().getByName(str));
		if (result.hasResult()) return result;
		
		// Faction Name Match
		result.setResult(FactionColl.get().getBestNameMatch(str));
		if (result.hasResult()) return result;
		
		// MPlayer Name Exact
		String id = IdUtil.getId(str);
		MPlayer mplayer = MPlayer.get(id);
		if (mplayer != null)
		{
			result.setResult(mplayer.getFaction());
			return result;
		}
		
		result.setErrors(Txt.parse("<b>No faction or player matching \"<p>%s<b>\".", str));
		return result;
	}
	
}
