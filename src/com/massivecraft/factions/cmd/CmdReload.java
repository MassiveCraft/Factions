package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;

public class CmdReload extends FCommand
{
	
	public CmdReload()
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
		
		if (file.startsWith("c"))
		{
			ConfServer.load();
			fileName = "conf.json";
		}
		else if (file.startsWith("b"))
		{
			Board.load();
			fileName = "board.json";
		}
		else if (file.startsWith("f"))
		{
			FactionColl.i.loadFromDisc();
			fileName = "factions.json";
		}
		else if (file.startsWith("p"))
		{
			FPlayers.i.loadFromDisc();
			fileName = "players.json";
		}
		else if (file.startsWith("a"))
		{
			fileName = "all";
			ConfServer.load();
			FPlayers.i.loadFromDisc();
			FactionColl.i.loadFromDisc();
			Board.load();
		}
		else
		{
			Factions.get().log("RELOAD CANCELLED - SPECIFIED FILE INVALID");
			msg("<b>Invalid file specified. <i>Valid files: all, conf, board, factions, players");
			return;
		}
		
		long timeReload = (System.currentTimeMillis()-timeInitStart);
		
		msg("<i>Reloaded <h>%s <i>from disk, took <h>%dms<i>.", fileName, timeReload);
	}
	
}
