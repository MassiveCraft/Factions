package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.FactionListComparator;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.mcore.cmd.arg.ARInteger;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;


public class CmdFactionsList extends FCommand
{
	public CmdFactionsList()
	{
		this.addAliases("l", "list");
		
		this.addOptionalArg("page", "1");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.LIST.node));
	}

	@Override
	public void perform()
	{
		Integer pageHumanBased = this.arg(0, ARInteger.get(), 1);
		if (pageHumanBased == null) return;
		
		// Create Messages
		List<String> lines = new ArrayList<String>();
		
		ArrayList<Faction> factionList = new ArrayList<Faction>(FactionColls.get().get(sender).getAll(null, FactionListComparator.get()));

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
				lines.add(Txt.parse("<i>Factionless<i> %d online", FactionColls.get().get(sender).getNone().getUPlayersWhereOnline(true).size()));
				continue;
			}
			lines.add(Txt.parse("%s<i> %d/%d online, %d/%d/%d",
				faction.getName(usender),
				faction.getUPlayersWhereOnline(true).size(),
				faction.getUPlayers().size(),
				faction.getLandCount(),
				faction.getPowerRounded(),
				faction.getPowerMaxRounded())
			);
		}

		sendMessage(lines);
	}
}
