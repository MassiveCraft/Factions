package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsClean extends FactionsCommand
{
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		Object message;
		
		// Apply
		int chunks = BoardColl.get().clean();
		int players = MPlayerColl.get().clean();
		
		// Title
		message = Txt.titleize("Factions Cleaner Results");
		message(message);
		
		// Chunks
		message = Txt.parse("<h>%d<i> chunks were cleaned.", chunks);
		message(message);
		
		// Players
		message = Txt.parse("<h>%d<i> players were cleaned.", players);
		message(message);
	}
	
}
