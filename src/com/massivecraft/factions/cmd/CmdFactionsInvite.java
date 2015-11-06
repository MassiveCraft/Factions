package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

public class CmdFactionsInvite extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsInviteList cmdFactionsInviteList = new CmdFactionsInviteList();
	public CmdFactionsInviteAdd cmdFactionsInviteAdd = new CmdFactionsInviteAdd();
	public CmdFactionsInviteRemove cmdFactionsInviteRemove = new CmdFactionsInviteRemove();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsInvite()
	{
		// Aliases
		this.addAliases("invite");
		
		// Children
		this.addChild(this.cmdFactionsInviteAdd);
		this.addChild(this.cmdFactionsInviteRemove);
		this.addChild(this.cmdFactionsInviteList);


		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.INVITE.node));
	}
	
}
