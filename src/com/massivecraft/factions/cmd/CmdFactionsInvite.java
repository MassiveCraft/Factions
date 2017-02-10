package com.massivecraft.factions.cmd;

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
		// Children
		this.addChild(this.cmdFactionsInviteAdd);
		this.addChild(this.cmdFactionsInviteRemove);
		this.addChild(this.cmdFactionsInviteList);
	}
	
}
