package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.FactionListComparator;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;


public class CmdFactionsList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsList()
	{
		// Aliases
		this.addAliases("l", "list");

		// Args
		this.addOptionalArg("page", "1");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.LIST.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		Integer pageHumanBased = this.arg(0, ARInteger.get(), 1);
		if (pageHumanBased == null) return;
		
		// Create Messages
		List<String> lines = new ArrayList<String>();
		
		ArrayList<Faction> factionList = new ArrayList<Faction>(FactionColl.get().getAll(null, FactionListComparator.get()));

		final int pageheight = 9;
		
		int pagecount = (factionList.size() / pageheight) + 1;
		if (pageHumanBased > pagecount)
			pageHumanBased = pagecount;
		else if (pageHumanBased < 1)
			pageHumanBased = 1;
		int start = (pageHumanBased - 1) * pageheight;
		int end = start + pageheight;
		if (end > factionList.size())
			end = factionList.size();

		lines.add(Txt.titleize("Faction List "+pageHumanBased+"/"+pagecount));

		for (Faction faction : factionList.subList(start, end))
		{
			if (faction.isNone())
			{
				lines.add(Txt.parse("<i>Factionless<i> %d online", FactionColl.get().getNone().getMPlayersWhereOnline(true).size()));
				continue;
			}
			lines.add(Txt.parse("%s<i> %d/%d online, %d/%d/%d",
				faction.getName(msender),
				faction.getMPlayersWhereOnline(true).size(),
				faction.getMPlayers().size(),
				faction.getLandCount(),
				faction.getPowerRounded(),
				faction.getPowerMaxRounded())
			);
		}

		sendMessage(lines);
	}
	
}
