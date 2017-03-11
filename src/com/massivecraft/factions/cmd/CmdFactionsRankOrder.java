package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.cmd.type.TypeRank;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.mson.Mson;

public class CmdFactionsRankOrder extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankOrder()
	{
		// Parameters
		this.addParameter(TypeRank.get(), "rank");
		this.addParameter(TypeInteger.get(), "order", "show");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		Rank rank = this.readArg();
		
		// Are we showing?
		if (!this.argIsSet(1))
		{
			this.inform(rank, false);
			return;
		}
		
		int order = this.readArg();
		Faction faction = msenderFaction;
		
		// Already this rank?
		if (rank.getOrder() == order) throw new MassiveException().setMsg("<b>Rank order for %s is already <h>%d<i>.", rank.getName(), rank.getOrder());
		
		// Adjust rank
		faction.adjustRankOrder(rank, order);
		
		// Inform
		this.inform(rank, true);
	}
	
	private void inform(Rank rank, boolean updated)
	{
		message(mson(
			"The rank order of ",
			TypeRank.get().getVisualMson(rank),
			" is ",
			updated ? "now " : Mson.EMPTY,
			mson(String.valueOf(rank.getOrder())).color(ChatColor.LIGHT_PURPLE)
		).color(ChatColor.YELLOW));
	}
	
}
