package com.massivecraft.factions.cmd;

import java.util.Map.Entry;

import com.massivecraft.factions.event.EventFactionsExpansions;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsExpansions extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsExpansions()
	{

	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Event
		EventFactionsExpansions event = new EventFactionsExpansions(sender);
		event.run();
		
		// Title
		Object title = "Factions Expansions";
		title = Txt.titleize(title);
		message(title);
		
		// Lines
		for (Entry<String, Boolean> entry : event.getExpansions().entrySet())
		{
			String name = entry.getKey();
			Boolean installed = entry.getValue();
			String format = (installed ? "<g>[X] <h>%s" : "<b>[ ] <h>%s");
			msg(format, name);
		}
		
		// URL Suggestion
		msg("<i>Learn all about expansions in the online documentation:");
		msg("<aqua>https://www.massivecraft.com/factions");
	}
	
}
