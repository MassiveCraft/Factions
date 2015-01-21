package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

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
		this.addAliases("i", "inv", "invite");
		
		// Add SubCommands
		this.addSubCommand(this.cmdFactionsInviteAdd);
		this.addSubCommand(this.cmdFactionsInviteRemove);
		this.addSubCommand(this.cmdFactionsInviteList);


		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.INVITE.node));
	}
	
}
