package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.FactionListComparator;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.arg.ARInteger;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;


public class CmdFactionsList extends FCommand
{
	
	public CmdFactionsList()
	{
		this.addAliases("ls", "list");
		
		this.addOptionalArg("page", "1");
		
		this.addRequirements(ReqHasPerm.get(Perm.LIST.node));
	}

	@Override
	public void perform()
	{
		Integer pageHumanBased = this.arg(0, ARInteger.get(), 1);
		if (pageHumanBased == null) return;
		
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(ConfServer.econCostList, "to list the factions", "for listing the factions")) return;
		
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
				lines.add(Txt.parse("<i>Factionless<i> %d online", FactionColl.get().getNone().getFPlayersWhereOnline(true).size()));
				continue;
			}
			lines.add(Txt.parse("%s<i> %d/%d online, %d/%d/%d",
				faction.getTag(fme),
				faction.getFPlayersWhereOnline(true).size(),
				faction.getFPlayers().size(),
				faction.getLandCount(),
				faction.getPowerRounded(),
				faction.getPowerMaxRounded())
			);
		}

		sendMessage(lines);
	}
}
