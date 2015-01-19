package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsDescriptionChange;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.mixin.Mixin;

public class CmdFactionsDescription extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsDescription()
	{
		// Aliases
		this.addAliases("desc");

		// Args
		this.addRequiredArg("desc");
		this.setErrorOnToManyArgs(false);

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.DESCRIPTION.node));
		this.addRequirements(ReqHasFaction.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{	
		// Args
		String newDescription = this.argConcatFrom(0);
		
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
			follower.msg("<i>%s <i>set your faction description to:\n%s", Mixin.getDisplayName(sender, follower), msenderFaction.getDescription());
		}
	}
	
}
