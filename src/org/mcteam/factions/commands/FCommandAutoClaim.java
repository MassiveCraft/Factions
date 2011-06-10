package org.mcteam.factions.commands;

import org.mcteam.factions.Conf;
import org.mcteam.factions.FLocation;
import org.mcteam.factions.Faction;
import org.mcteam.factions.struct.Role;

public class FCommandAutoClaim extends FBaseCommand {

	public FCommandAutoClaim() {
		aliases.add("autoclaim");

		optionalParameters.add("on|off");

		helpDescription = "Auto-claim land as you walk around";
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

		// default: toggle existing value
		boolean enable = !me.autoClaimEnabled();

		// if on|off is specified, use that instead
		if (parameters.size() > 0)
			enable = parseBool(parameters.get(0));

		me.enableAutoClaim(enable);

		if (!enable) {
			sendMessage("Auto-claiming of land disabled.");
			return;
		}

		Faction myFaction = me.getFaction();
		FLocation flocation = new FLocation(me);

		if ( ! assertMinRole(Role.MODERATOR)) {
			me.enableAutoClaim(false);
			return;
		}

		if (Conf.worldsNoClaiming.contains(flocation.getWorldName())) {
			sendMessage("Sorry, this world has land claiming disabled.");
			me.enableAutoClaim(false);
			return;
		}

		if (myFaction.getLandRounded() >= myFaction.getPowerRounded()) {
			sendMessage("You can't claim more land! You need more power!");
			me.enableAutoClaim(false);
			return;
		}

		sendMessage("Auto-claiming of land enabled.");
		me.attemptClaim(false);
	}
	
}
