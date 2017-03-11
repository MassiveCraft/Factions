package com.massivecraft.factions.cmd.type;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.Named;
import com.massivecraft.massivecore.command.type.TypeNameAbstract;

public class TypeRankName extends TypeNameAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeRankName iStrict = new TypeRankName(true);
	private static TypeRankName iLenient = new TypeRankName(false);
	public static TypeRankName getStrict() { return iStrict; }
	public static TypeRankName getLenient() { return iLenient; }
	
	private TypeRankName(boolean strict)
	{
		super(strict);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Integer getLengthMin()
	{
		return MConf.get().nameLengthRankMin;
	}
	
	@Override
	public Integer getLengthMax()
	{
		return MConf.get().nameLengthRankMax;
	}
	
	@Override
	public Named getCurrent(CommandSender sender, String arg) throws MassiveException
	{
		return TypeRank.get().read(arg, sender);
	}
	
	@Override
	public boolean isNameTaken(CommandSender sender, String name) throws MassiveException
	{
		MPlayer mplayer = MPlayer.get(sender);
		if (mplayer == null) return false;
		
		// TODO: Adjust later for "used" faction
		Faction faction = mplayer.getFaction();
		
		for (Rank rank : faction.getRankCollection())
		{
			if (!rank.getName().equalsIgnoreCase(name)) continue;
			return true;
		}
		
		return false;
	}
}
