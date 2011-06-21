package org.mcteam.factions.commands;

public class FCommandChat extends FBaseCommand {
	
	public FCommandChat() {
		aliases.add("chat");
		aliases.add("c");
		
		helpDescription = "Switch faction only chat on and off";
	}
	
	@Override
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
