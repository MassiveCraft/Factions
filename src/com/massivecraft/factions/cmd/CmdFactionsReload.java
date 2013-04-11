package com.massivecraft.factions.cmd;

import com.massivecraft.factions.BoardOld;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;

public class CmdFactionsReload extends FCommand
{
	
	public CmdFactionsReload()
	{
		super();
		this.aliases.add("reload");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("file", "all");
		
		this.permission = Perm.RELOAD.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		long timeInitStart = System.currentTimeMillis();
		String file = this.argAsString(0, "all").toLowerCase();
		
		String fileName;
		
		if (file.startsWith("b"))
		{
			BoardOld.load();
			fileName = "board.json";
		}
		else if (file.startsWith("f"))
		{
			FactionColl.i.loadFromDisc();
			fileName = "factions.json";
		}
		else if (file.startsWith("p"))
		{
			FPlayerColl.i.loadFromDisc();
			fileName = "players.json";
		}
		else if (file.startsWith("a"))
		{
			fileName = "all";
			FPlayerColl.i.loadFromDisc();
			FactionColl.i.loadFromDisc();
			BoardOld.load();
		}
		else
		{
			Factions.get().log("RELOAD CANCELLED - SPECIFIED FILE INVALID");
			msg("<b>Invalid file specified. <i>Valid files: all, board, factions, players");
			return;
		}
		
		long timeReload = (System.currentTimeMillis()-timeInitStart);
		
		msg("<i>Reloaded <h>%s <i>from disk, took <h>%dms<i>.", fileName, timeReload);
	}
	
}
