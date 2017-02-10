package com.massivecraft.factions.cmd;

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
		// Children
		this.addChild(this.cmdFactionsClaimOne);
		this.addChild(this.cmdFactionsClaimAuto);
		this.addChild(this.cmdFactionsClaimFill);
		this.addChild(this.cmdFactionsClaimSquare);
		this.addChild(this.cmdFactionsClaimCircle);
		this.addChild(this.cmdFactionsClaimAll);
	}
	
}
