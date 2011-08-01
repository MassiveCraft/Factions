package com.massivecraft.factions.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.util.TextUtil;


public class FCommandList extends FBaseCommand {
	
	public FCommandList() {
		aliases.add("list");
		aliases.add("ls");
		
		senderMustBePlayer = false;
		
		optionalParameters.add("page");
		
		helpDescription = "Show a list of the factions";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}

	@Override
	public void perform() {
		ArrayList<Faction> FactionList = new ArrayList<Faction>(Faction.getAll());
		FactionList.remove(Faction.getNone());
		FactionList.remove(Faction.getSafeZone());
		FactionList.remove(Faction.getWarZone());

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostList)) {
			return;
		}

		int page = 1;
		if (parameters.size() > 0) {
			try {
				page = Integer.parseInt(parameters.get(0));
			} catch (NumberFormatException e) {
				// wasn't an integer
			}
		}
		page -= 1;

		// Sort by total followers first
		Collections.sort(FactionList, new Comparator<Faction>(){
			@Override
			public int compare(Faction f1, Faction f2) {
				if (f1.getFPlayers().size() < f2.getFPlayers().size())
					return 1;
				else if (f1.getFPlayers().size() > f2.getFPlayers().size())
					return -1;
				return 0;
			}
		});

		// Then sort by how many members are online now
		Collections.sort(FactionList, new Comparator<Faction>(){
			@Override
			public int compare(Faction f1, Faction f2) {
				if (f1.getFPlayersWhereOnline(true).size() < f2.getFPlayersWhereOnline(true).size())
					return 1;
				else if (f1.getFPlayersWhereOnline(true).size() > f2.getFPlayersWhereOnline(true).size())
					return -1;
				return 0;
			}
		});

		FactionList.add(0, Faction.getNone());
		
		int maxPage = (int)Math.floor((double)FactionList.size() / 9D);
		if (page < 0 || page > maxPage) {
			sendMessage("The faction list is only " + (maxPage+1) + " page(s) long");
			return;
		}

		String header = "Faction List";
		if (maxPage > 1) header += " (page " + (page+1) + " of " + (maxPage+1) + ")";
		sendMessage(TextUtil.titleize(header));

		int maxPos = (page+1) * 9;
		if (maxPos > FactionList.size()) maxPos = FactionList.size();
		for (int pos = page * 9; pos < maxPos; pos++) {
			Faction faction = FactionList.get(pos);
			if (faction.getId() == 0) {
				sendMessage("Factionless"+Conf.colorSystem+" "+faction.getFPlayersWhereOnline(true).size() + " online");
			} else {
				sendMessage(faction.getTag(me)+Conf.colorSystem+" "+faction.getFPlayersWhereOnline(true).size()+"/"+faction.getFPlayers().size()+" online, "+faction.getLandRounded()+"/"+faction.getPowerRounded()+"/"+faction.getPowerMaxRounded());
			}
		}
	}
	
}
