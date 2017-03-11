package com.massivecraft.factions.cmd;

public class CmdFactionsRank extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsRankList cmdFactionsRankList = new CmdFactionsRankList();
	public CmdFactionsRankShow cmdFactionsRankShow = new CmdFactionsRankShow();
	public CmdFactionsRankCreate cmdFactionsRankCreate = new CmdFactionsRankCreate();
	public CmdFactionsRankName cmdFactionsRankName = new CmdFactionsRankName();
	public CmdFactionsRankOrder cmdFactionsRankOrder = new CmdFactionsRankOrder();
	public CmdFactionsRankDelete cmdFactionsRankDelete = new CmdFactionsRankDelete();
	public CmdFactionsRankSet cmdFactionsRankSet = new CmdFactionsRankSet();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRank()
	{
		// Children
		this.addChild(this.cmdFactionsRankList);
		this.addChild(this.cmdFactionsRankShow);
		this.addChild(this.cmdFactionsRankCreate);
		this.addChild(this.cmdFactionsRankDelete);
		this.addChild(this.cmdFactionsRankName);
		this.addChild(this.cmdFactionsRankOrder);
		this.addChild(this.cmdFactionsRankSet);
	}
	
}
