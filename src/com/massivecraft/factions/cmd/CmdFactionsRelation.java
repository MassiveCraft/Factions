package com.massivecraft.factions.cmd;

public class CmdFactionsRelation extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	public CmdFactionsRelationSet cmdFactionsRelationSet = new CmdFactionsRelationSet();
	public CmdFactionsRelationList cmdFactionsRelationList = new CmdFactionsRelationList();
	public CmdFactionsRelationWishes cmdFactionsRelationWishes = new CmdFactionsRelationWishes();

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsRelation()
	{
		// Children
		this.addChild(this.cmdFactionsRelationSet);
		this.addChild(this.cmdFactionsRelationList);
		this.addChild(this.cmdFactionsRelationWishes);
	}

}
