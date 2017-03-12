package com.massivecraft.factions.cmd;

public class CmdFactionsAccessView extends CmdFactionsAccessAbstract
{
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void innerPerform()
	{
		this.sendAccessInfo();
	}
	
}
