package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Permission;

public class CmdBoom extends FCommand
{
	public CmdBoom()
	{
		super();
		this.aliases.add("noboom");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flipp");
		
		this.permission = Permission.NO_BOOM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		if ( ! myFaction.isPeaceful())
		{
			fme.sendMessageParsed("<b>This command is only usable by factions which are specially designated as peaceful.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostNoBoom)) return;

		myFaction.setPeacefulExplosionsEnabled(this.argAsBool(0, ! myFaction.getPeacefulExplosionsEnabled()));

		String enabled = myFaction.noExplosionsInTerritory() ? "disabled" : "enabled";

		// Inform
		myFaction.sendMessageParsed("%s<i> has "+enabled+" explosions in your faction's territory.", fme.getNameAndRelevant(myFaction));
	}
}
