package com.massivecraft.factions.cmd;

import com.massivecraft.factions.BoardOld;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Perm;

public class CmdFactionsSaveAll extends FCommand
{
	
	public CmdFactionsSaveAll()
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
		FPlayerColl.i.saveToDisc();
		FactionColl.i.saveToDisc();
		BoardOld.save();
		msg("<i>Factions saved to disk!");
	}
	
}