package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;

public class CmdSaveAll extends FCommand
{
	
	public CmdSaveAll()
	{
		super();
		this.aliases.add("saveall");
		this.aliases.add("save");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Perm.SAVE.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		FPlayers.i.saveToDisc();
		Factions.i.saveToDisc();
		Board.save();
		Conf.save();
		msg("<i>Factions saved to disk!");
	}
	
}