package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsFlagList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlagList()
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
		
		//Create messages
		List<String> messages = new ArrayList<String>();
		
		for (MFlag flag : MFlag.getAll())
		{
			if ( ! flag.isVisible() && ! msender.isOverriding()) continue;
			messages.add(flag.getStateDesc(false, false, true, true, true, false));
		}
		
		//Send messages
		message(Txt.getPage(messages, page, "Available Faction Flags", this));
	}
	
}
