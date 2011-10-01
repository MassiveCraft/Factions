package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FCommandInfo extends FBaseCommand {
	
	public FCommandInfo() {
		aliases.add("info");
		aliases.add("i");
		
		requiredParameters.add("player name|faction tag");
		
		helpDescription = "Lookup players or factions.";
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		String filter = parameters.get(0).toLowerCase();
		
		for( FPlayer player : FPlayer.getAll() ) {
			if( player.getName().toLowerCase().contains(filter)) {
				String status = player.isOnline() ? "online" : "offline";
				sendMessage(player.getNameAndRelevant(me)+Conf.colorSystem+" is "+status);
			}
		}
		
		for( Faction faction : Faction.getAll() ) {
			if( faction.getTag().toLowerCase().contains(filter)) {
				String tag = faction.getTag(me);
				int online = faction.getOnlinePlayers().size();
				int maxonline = faction.getFPlayers().size();
				int power = faction.getPowerRounded();
				int maxpower = faction.getPowerMaxRounded();
				int land = faction.getLandRounded();
				
				sendMessage(tag+Conf.colorSystem+" have "+online+"/"+maxonline+" members online, "+land+" land and "+power+"/"+maxpower+" power.");
			}
		}
	}
}