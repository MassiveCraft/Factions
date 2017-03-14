package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsDescriptionChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.mixin.MixinDisplayName;

public class CmdFactionsDescription extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsDescription()
	{
		// Parameters
		this.addParameter(TypeString.get(), "desc", true);

		// Requirements
		this.addRequirements(ReqHasFaction.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{	
		// Args
		String newDescription = this.readArg();
		
		// MPerm
		if ( ! MPerm.getPermDesc().has(msender, msenderFaction, true)) return;
		
		// Event
		EventFactionsDescriptionChange event = new EventFactionsDescriptionChange(sender, msenderFaction, newDescription);
		event.run();
		if (event.isCancelled()) return;
		newDescription = event.getNewDescription();

		// Apply
		msenderFaction.setDescription(newDescription);
		
		// Inform
		for (MPlayer follower : msenderFaction.getMPlayers())
		{
			follower.msg("<i>%s <i>set your faction description to:\n%s", MixinDisplayName.get().getDisplayName(sender, follower), msenderFaction.getDescription());
		}
	}
	
}
