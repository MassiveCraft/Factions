package com.massivecraft.factions.cmd;

import java.util.List;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.cmd.type.TypeRank;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRankShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankShow()
	{
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		Rank rank = this.readArg(msender.getRank());
		
		// Prepare
		String title = Txt.parse("Rank show %s", TypeRank.get().getVisual(rank));
		List<Mson> show = TypeRank.get().getShow(rank);
		
		// Inform
		message(Txt.getPage(show, 0, title, sender));
	}
	
}
