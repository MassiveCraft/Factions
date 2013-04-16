package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.mcore.cmd.MCommand;

public abstract class FCommand extends MCommand
{
	public FPlayer fme;
	public Faction myFaction;
	
	@Override
	public void fixSenderVars()
	{
		this.fme = FPlayerColl.get().get(this.sender);
		this.myFaction = this.fme.getFaction();
	}
}
