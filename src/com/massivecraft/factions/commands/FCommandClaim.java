package com.massivecraft.factions.commands;

public class FCommandClaim extends FCommand {
	
	public FCommandClaim() {
		aliases.add("claim");
		
		helpDescription = "Claim the land where you are standing";
	}
	
	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		me.attemptClaim(true);
	}
	
}
