package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.MPerm;
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
		this.addOptionalArg("page", "1");
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.PERM_LIST.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Integer pageHumanBased = this.arg(0, ARInteger.get(), 1);
		if (pageHumanBased == null) return;
		
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
