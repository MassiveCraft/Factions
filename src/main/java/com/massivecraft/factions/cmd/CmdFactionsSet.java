package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;


public class CmdFactionsSet extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsSetOne cmdFactionsSetOne = new CmdFactionsSetOne();
	public CmdFactionsSetAuto cmdFactionsSetAuto = new CmdFactionsSetAuto();
	public CmdFactionsSetFill cmdFactionsSetFill = new CmdFactionsSetFill();
	public CmdFactionsSetSquare cmdFactionsSetSquare = new CmdFactionsSetSquare();
	public CmdFactionsSetCircle cmdFactionsSetCircle = new CmdFactionsSetCircle();
	public CmdFactionsSetTransfer cmdFactionsSetTransfer = new CmdFactionsSetTransfer();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSet()
	{
		// Aliases
		this.addAliases("s", "set");
		
		// Add SubCommands
		this.addSubCommand(this.cmdFactionsSetOne);
		this.addSubCommand(this.cmdFactionsSetAuto);
		this.addSubCommand(this.cmdFactionsSetFill);
		this.addSubCommand(this.cmdFactionsSetSquare);
		this.addSubCommand(this.cmdFactionsSetCircle);
		this.addSubCommand(this.cmdFactionsSetTransfer);
		
		// Requirements
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasPerm.get(Perm.SET.node));
	}
	
}
