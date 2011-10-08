package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.ChatMode;

public class FCommandChat extends FCommand {
	
	public FCommandChat() {
		aliases.add("chat");
		aliases.add("c");
		
		optionalParameters.add("mode");
		
		helpDescription = "Change chat mode";
	}
	
	@Override
	public void perform() {
		if ( ! Conf.factionOnlyChat ) {
			sendMessage("Faction-only chat is disabled on this server.");
			return;
		}
		if ( ! assertHasFaction()) {
			return;
		}
		
		if( this.parameters.size() >= 1 ) {
			String mode = this.parameters.get(0);
			
			if(mode.startsWith("p")) {
				me.setChatMode(ChatMode.PUBLIC);
				sendMessage("Public chat mode.");
			} else if(mode.startsWith("a")) {
				me.setChatMode(ChatMode.ALLIANCE);
				sendMessage("Alliance only chat mode.");
			} else if(mode.startsWith("f")) {
				me.setChatMode(ChatMode.FACTION);
				sendMessage("Faction only chat mode.");
			} else {
				sendMessage("Unrecognised chat mode. Please enter either 'a','f' or 'p'");
			}
			
		} else {
		
			if(me.getChatMode() == ChatMode.PUBLIC) {
				me.setChatMode(ChatMode.ALLIANCE);
				sendMessage("Alliance only chat mode.");
			} else if (me.getChatMode() == ChatMode.ALLIANCE ) {
				me.setChatMode(ChatMode.FACTION);
				sendMessage("Faction only chat mode.");
			} else {
				me.setChatMode(ChatMode.PUBLIC);
				sendMessage("Public chat mode.");
			}
		}
	}
}
