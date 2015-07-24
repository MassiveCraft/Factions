package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.ArgSetting;
import com.massivecraft.massivecore.cmd.arg.ARDouble;
import com.massivecraft.massivecore.cmd.arg.ARString;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsPowerBoost extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private ArgSetting<MPlayer> settingMplayer = new ArgSetting<MPlayer>(ARMPlayer.get(), "name");
	private ArgSetting<Faction> settingFaction = new ArgSetting<Faction>(ARFaction.get(), "name");
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPowerBoost()
	{
		// Aliases
		this.addAliases("powerboost");

		// Args
		this.addArg(ARString.get(), "p|f|player|faction");
		this.addArg(settingMplayer);
		this.addArg(ARDouble.get(), "#");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.POWERBOOST.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		String type = this.<String>readArg().toLowerCase();
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
		
		double targetPower = this.readArgAt(2);

		String target;

		if (doPlayer)
		{
			this.getArgSettings().set(1, settingMplayer);
			MPlayer targetPlayer = this.readArgAt(1);
			
			targetPlayer.setPowerBoost(targetPower);
			target = "Player \""+targetPlayer.getName()+"\"";
		}
		else
		{
			this.getArgSettings().set(1, settingFaction);
			Faction targetFaction = this.readArgAt(1);
			
			targetFaction.setPowerBoost(targetPower);
			target = "Faction \""+targetFaction.getName()+"\"";
		}

		msg("<i>"+target+" now has a power bonus/penalty of "+targetPower+" to min and max power levels.");
		Factions.get().log(msender.getName()+" has set the power bonus/penalty for "+target+" to "+targetPower+".");
	}
	
}
