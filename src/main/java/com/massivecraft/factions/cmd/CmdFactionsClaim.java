package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;


public class CmdFactionsClaim extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsSetOne cmdFactionsClaimOne = new CmdFactionsSetOne(true);
	public CmdFactionsSetAuto cmdFactionsClaimAuto = new CmdFactionsSetAuto(true);
	public CmdFactionsSetFill cmdFactionsClaimFill = new CmdFactionsSetFill(true);
	public CmdFactionsSetSquare cmdFactionsClaimSquare = new CmdFactionsSetSquare(true);
	public CmdFactionsSetCircle cmdFactionsClaimCircle = new CmdFactionsSetCircle(true);
	public CmdFactionsSetAll cmdFactionsClaimAll = new CmdFactionsSetAll(true);
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsClaim()
	{
		// Aliases
		this.addAliases("claim");
		
		// Add SubCommands
		this.addSubCommand(this.cmdFactionsClaimOne);
		this.addSubCommand(this.cmdFactionsClaimAuto);
		this.addSubCommand(this.cmdFactionsClaimFill);
		this.addSubCommand(this.cmdFactionsClaimSquare);
		this.addSubCommand(this.cmdFactionsClaimCircle);
		this.addSubCommand(this.cmdFactionsClaimAll);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.CLAIM.node));
	}
	
}
