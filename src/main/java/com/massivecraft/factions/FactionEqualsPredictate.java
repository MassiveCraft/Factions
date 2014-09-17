package com.massivecraft.factions;

import java.io.Serializable;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.Predictate;

public class FactionEqualsPredictate implements Predictate<CommandSender>, Serializable
{
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final String factionId;
	public String getFactionId() { return this.factionId; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionEqualsPredictate(Faction faction)
	{
		this.factionId = faction.getId();
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean apply(CommandSender sender)
	{
		MPlayer mplayer = MPlayer.get(sender);
		return this.factionId.equals(mplayer.getFactionId());
	}

}