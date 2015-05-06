package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPermList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermList()
	{
		// Aliases
		this.addAliases("l", "list");
		
		// Args
		this.addArg(ARInteger.get(), "page", "1");
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.PERM_LIST.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		int pageHumanBased = this.readArg(1);
		
		// Create messages
		List<String> messages = new ArrayList<String>();
		
		for (MPerm perm : MPerm.getAll())
		{
			if ( ! perm.isVisible() && ! msender.isUsingAdminMode()) continue;
			messages.add(perm.getDesc(true, true));
		}
		
		// Send messages
		sendMessage(Txt.getPage(messages, pageHumanBased, "Available Faction Perms", sender));
	}

}
