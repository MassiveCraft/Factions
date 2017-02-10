package com.massivecraft.factions.cmd;

public class CmdFactionsAccessView extends CmdFactionsAccessAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAccessView()
	{

	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void innerPerform()
	{
		this.sendAccessInfo();
	}
	
}
