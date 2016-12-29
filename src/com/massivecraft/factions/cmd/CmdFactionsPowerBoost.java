package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.primitive.TypeDouble;
import com.massivecraft.massivecore.command.type.primitive.TypeString;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
		// Aliases
		this.addAliases("powerboost");

		// Parameters
		this.addParameter(TypeString.get(), "p|f|player|faction");
		this.addParameter(parameterMplayer);
		this.addParameter(TypeString.get(), "a|r|s|add|remove|set");
		this.addParameter(TypeDouble.get(), "#");

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.POWERBOOST));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override public void perform() throws MassiveException
	{
		String receiverType = this.<String>argAt(0).toLowerCase();
		boolean doPlayer = true;
		if (receiverType.equals("f") || receiverType.equals("faction"))
		{
			doPlayer = false;
		}
		else if (!receiverType.equals("p") && !receiverType.equals("player"))
		{
			msg("<b>You must specify \"p\" or \"player\" to target a player or \"f\" or \"faction\" to target a faction.");
			msg("<b>ex. /f powerboost p SomePlayer a 1  -or-  /f powerboost f SomeFaction r 5 -or- /f powerboost f SomePlayer s 50");
			return;
		}

		GiveType giveType = getGiveType(argAt(2));
		double targetPower = this.readArgAt(3);

		if (giveType == null)
		{
			msg("<b>You must specify \"a\" or \"add\" to add, \"r\" or \"remove\" to remove, or \"s\" or \"set\" to set the powerboost of a player/faction");
			msg("<b>ex. /f powerboost p SomePlayer a 1  -or-  /f powerboost f SomeFaction r 5 -or- /f powerboost f SomePlayer s 50");
			return;
		}

		String target;
		if (doPlayer)
		{
			MPlayer targetPlayer = this.readArgAt(1);

			if (giveType == GiveType.ADD || giveType == GiveType.REMOVE) targetPlayer.addPowerBoost(targetPower);
			if (giveType == GiveType.SET) targetPlayer.setPowerBoost(targetPower);

			targetPower = targetPlayer.getPowerBoost();
			target = "Player \"" + targetPlayer.getName() + "\"";
		}
		else
		{
			this.getParameters().set(1, parameterFaction);
			Faction targetFaction = this.readArgAt(1);

			if (giveType == GiveType.ADD || giveType == GiveType.REMOVE) targetFaction.addPowerBoost(targetPower);
			if (giveType == GiveType.SET) targetFaction.setPowerBoost(targetPower);

			targetPower = round(targetPower);
			target = "Faction \"" + targetFaction.getName() + "\"";
		}
		targetPower = round(targetPower);

		msg("<i>" + target + " now has a power bonus/penalty of " + targetPower + " to min and max power levels.");
		Factions.get().log(msender.getName() + " has set the power bonus/penalty for " + target + " to " + targetPower + ".");
	}

	private double round(double value)
	{
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	private GiveType getGiveType(String type)
	{
		type = type.toLowerCase();

		if (type.equals("a") || type.equals("add")) return GiveType.ADD;
		if (type.equals("r") || type.equals("remove")) return GiveType.REMOVE;
		if (type.equals("s") || type.equals("set")) return GiveType.SET;
		else return null;
	}

	private enum GiveType
	{
		ADD, REMOVE, SET
	}
}
