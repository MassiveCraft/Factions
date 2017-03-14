package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPermList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermList()
	{
		// Parameters
		this.addParameter(Parameter.getPage());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		int page = this.readArg();
		
		// Create messages
		List<String> messages = new ArrayList<String>();
		
		for (MPerm perm : MPerm.getAll())
		{
			if ( ! perm.isVisible() && ! msender.isOverriding()) continue;
			messages.add(perm.getDesc(true, true));
		}
		
		// Send messages
		message(Txt.getPage(messages, page, "Available Faction Perms", this));
	}

}
