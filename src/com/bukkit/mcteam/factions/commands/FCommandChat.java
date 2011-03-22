package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

public class FCommandChat extends FBaseCommand {
	
	public FCommandChat() {
		aliases = new ArrayList<String>();
		aliases.add("chat");
		aliases.add("c");
		
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Switch faction only chat on and off";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! me.isFactionChatting()) {
			// Turn on
			me.setFactionChatting(true);
			sendMessage("Faction-only chat ENABLED.");
		} else {
			// Turn off
			me.setFactionChatting(false);
			sendMessage("Faction-only chat DISABLED.");
		}
	}
	
}
