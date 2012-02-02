package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;

public class CmdPowerBoost extends FCommand
{
	public CmdPowerBoost()
	{
		super();
		this.aliases.add("powerboost");
		
		this.requiredArgs.add("p|f|player|faction");
		this.requiredArgs.add("name");
		this.requiredArgs.add("#");
		
		this.permission = Permission.POWERBOOST.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		String type = this.argAsString(0).toLowerCase();
		boolean doPlayer = true;
		if (type.equals("f") || type.equals("faction"))
		{
			doPlayer = false;
		}
		else if (!type.equals("p") && !type.equals("player"))
		{
			msg("<b>You must specify \"p\" or \"player\" to target a player or \"f\" or \"faction\" to target a faction.");
			msg("<b>ex. /f powerboost p SomePlayer 0.5  -or-  /f powerboost f SomeFaction -5");
			return;
		}
		
		Double targetPower = this.argAsDouble(2);
		if (targetPower == null)
		{
			msg("<b>You must specify a valid numeric value for the power bonus/penalty amount.");
			return;
		}

		String target;

		if (doPlayer)
		{
			FPlayer targetPlayer = this.argAsBestFPlayerMatch(1);
			if (targetPlayer == null) return;
			targetPlayer.setPowerBoost(targetPower);
			target = "Player \""+targetPlayer.getName()+"\"";
		}
		else
		{
			Faction targetFaction = this.argAsFaction(1);
			if (targetFaction == null) return;
			targetFaction.setPowerBoost(targetPower);
			target = "Faction \""+targetFaction.getTag()+"\"";
		}

		msg("<i>"+target+" now has a power bonus/penalty of "+targetPower+" to min and max power levels.");
		if (!senderIsConsole)
			P.p.log(fme.getName()+" has set the power bonus/penalty for "+target+" to "+targetPower+".");
	}
}
