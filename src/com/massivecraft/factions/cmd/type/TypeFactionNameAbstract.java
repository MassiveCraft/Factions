package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.Named;
import com.massivecraft.massivecore.command.type.TypeNameAbstract;
import org.bukkit.command.CommandSender;

public class TypeFactionNameAbstract extends TypeNameAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public TypeFactionNameAbstract(boolean strict)
	{
		super(strict);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Named getCurrent(CommandSender sender)
	{
		MPlayer mplayer = MPlayer.get(sender);
		Faction faction = mplayer.getFaction();
		return faction;
	}
	
	@Override
	public boolean isNameTaken(String name)
	{
		return FactionColl.get().isNameTaken(name);
	}
	
	@Override
	public boolean isCharacterAllowed(char character)
	{
		return MiscUtil.substanceChars.contains(String.valueOf(character));
	}
	
	@Override
	public Integer getLengthMin()
	{
		return MConf.get().factionNameLengthMin;
	}
	
	@Override
	public Integer getLengthMax()
	{
		return MConf.get().factionNameLengthMax;
	}
	
}
