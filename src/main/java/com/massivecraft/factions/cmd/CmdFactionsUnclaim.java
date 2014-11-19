package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;


public class CmdFactionsUnclaim extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsSetOne cmdFactionsUnclaimOne = new CmdFactionsSetOne(false);
	public CmdFactionsSetAuto cmdFactionsUnclaimAuto = new CmdFactionsSetAuto(false);
	public CmdFactionsSetFill cmdFactionsUnclaimFill = new CmdFactionsSetFill(false);
	public CmdFactionsSetSquare cmdFactionsUnclaimSquare = new CmdFactionsSetSquare(false);
	public CmdFactionsSetCircle cmdFactionsUnclaimCircle = new CmdFactionsSetCircle(false);
	public CmdFactionsSetAll cmdFactionsUnclaimAll = new CmdFactionsSetAll(false);
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsUnclaim()
	{
		// Aliases
		this.addAliases("unclaim");
		
		// Add SubCommands
		this.addSubCommand(this.cmdFactionsUnclaimOne);
		this.addSubCommand(this.cmdFactionsUnclaimAuto);
		this.addSubCommand(this.cmdFactionsUnclaimFill);
		this.addSubCommand(this.cmdFactionsUnclaimSquare);
		this.addSubCommand(this.cmdFactionsUnclaimCircle);
		this.addSubCommand(this.cmdFactionsUnclaimAll);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.UNCLAIM.node));
	}
	
}
