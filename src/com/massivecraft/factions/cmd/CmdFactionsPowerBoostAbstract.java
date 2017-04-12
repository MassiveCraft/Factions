package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsParticipator;
import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.Type;
import com.massivecraft.massivecore.command.type.TypeNullable;
import com.massivecraft.massivecore.command.type.primitive.TypeDouble;
import com.massivecraft.massivecore.util.Txt;

public abstract class CmdFactionsPowerBoostAbstract extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	protected CmdFactionsPowerBoostAbstract(Type<? extends FactionsParticipator> parameterType, String parameterName)
	{
		// Parameters
		this.addParameter(parameterType, parameterName);
		this.addParameter(TypeNullable.get(TypeDouble.get()), "amount", "show");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		FactionsParticipator factionsParticipator = this.readArg();
		Double powerBoost = this.readArg(factionsParticipator.getPowerBoost());
		
		// Try set the powerBoost
		boolean updated = this.trySet(factionsParticipator, powerBoost);
		
		// Inform
		this.informPowerBoost(factionsParticipator, powerBoost, updated);
	}
	
	private boolean trySet(FactionsParticipator factionsParticipator, Double powerBoost) throws MassiveException
	{
		// Trying to set?
		if (!this.argIsSet(1)) return false;
		
		// Check set permissions
		if (!Perm.POWERBOOST_SET.has(sender, true)) throw new MassiveException();
		
		// Set
		factionsParticipator.setPowerBoost(powerBoost);
		
		// Return
		return true;
	}
	
	private void informPowerBoost(FactionsParticipator factionsParticipator, Double powerBoost, boolean updated)
	{
		// Prepare
		String participatorDescribe = factionsParticipator.describeTo(msender, true);
		powerBoost = powerBoost == null ? factionsParticipator.getPowerBoost() : powerBoost;
		String powerDescription = Txt.parse(Double.compare(powerBoost, 0D) >= 0 ? "<g>bonus" : "<b>penalty");
		String when = updated ? "now " : "";
		String verb = factionsParticipator.equals(msender) ? "have" : "has";
		
		// Create message
		String messagePlayer = Txt.parse("<i>%s<i> %s%s a power %s<i> of <h>%.2f<i> to min and max power levels.", participatorDescribe, when, verb, powerDescription, powerBoost);
		String messageLog = Txt.parse("%s %s set the power %s<i> for %s<i> to <h>%.2f<i>.", msender.getName(), verb, powerDescription, factionsParticipator.getName(), powerBoost);
		
		// Inform
		msender.message(messagePlayer);
		if (updated) Factions.get().log(messageLog);
	}
	
}
