package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.type.primitive.TypeDouble;
import com.massivecraft.massivecore.command.type.primitive.TypeString;

public class CmdFactionsPowerBoost extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private Parameter<MPlayer> parameterMplayer = new Parameter<MPlayer>(TypeMPlayer.get(), "name");
	private Parameter<Faction> parameterFaction = new Parameter<Faction>(TypeFaction.get(), "name");
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPowerBoost()
	{
		// Parameters
		this.addParameter(TypeString.get(), "p|f|player|faction");
		this.addParameter(parameterMplayer);
		this.addParameter(TypeDouble.get(), "#");
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
			this.getParameters().set(1, parameterMplayer);
			MPlayer targetPlayer = this.readArgAt(1);
			
			targetPlayer.setPowerBoost(targetPower);
			target = "Player \""+targetPlayer.getName()+"\"";
		}
		else
		{
			this.getParameters().set(1, parameterFaction);
			Faction targetFaction = this.readArgAt(1);
			
			targetFaction.setPowerBoost(targetPower);
			target = "Faction \""+targetFaction.getName()+"\"";
		}

		msg("<i>"+target+" now has a power bonus/penalty of "+targetPower+" to min and max power levels.");
		Factions.get().log(msender.getName()+" has set the power bonus/penalty for "+target+" to "+targetPower+".");
	}
	
}
