package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;


public class CmdList extends FCommand
{
	
	public CmdList()
	{
		super();
		this.aliases.add("list");
		this.aliases.add("ls");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("page", "1");
		
		this.permission = Permission.LIST.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}

	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostList, "to list the factions", "for listing the factions")) return;
		
		ArrayList<Faction> factionList = new ArrayList<Faction>(Factions.i.get());

		factionList.remove(Factions.i.getNone());
		// TODO: Add flag SECRET To factions instead.
		//factionList.remove(Factions.i.getSafeZone());
		//factionList.remove(Factions.i.getWarZone());
		
		// Sort by total followers first
		Collections.sort(factionList, new Comparator<Faction>(){
			@Override
			public int compare(Faction f1, Faction f2) {
				int f1Size = f1.getFPlayers().size();
				int f2Size = f2.getFPlayers().size();
				if (f1Size < f2Size)
					return 1;
				else if (f1Size > f2Size)
					return -1;
				return 0;
			}
		});

		// Then sort by how many members are online now
		Collections.sort(factionList, new Comparator<Faction>(){
			@Override
			public int compare(Faction f1, Faction f2) {
				int f1Size = f1.getFPlayersWhereOnline(true).size();
				int f2Size = f2.getFPlayersWhereOnline(true).size();
				if (f1Size < f2Size)
					return 1;
				else if (f1Size > f2Size)
					return -1;
				return 0;
			}
		});
		
		ArrayList<String> lines = new ArrayList<String>();
		lines.add(p.txt.parse("<i>Factionless<i> %d online", Factions.i.getNone().getFPlayersWhereOnline(true).size()));
		for (Faction faction : factionList)
		{
			lines.add(p.txt.parse("%s<i> %d/%d online, %d/%d/%d",
				faction.getTag(fme),
				faction.getFPlayersWhereOnline(true).size(),
				faction.getFPlayers().size(),
				faction.getLandRounded(),
				faction.getPowerRounded(),
				faction.getPowerMaxRounded())
			);
		}
		
		sendMessage(p.txt.getPage(lines, this.argAsInt(0, 1), "Faction List"));
	}
	
}
