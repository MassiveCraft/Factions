package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.cmd.type.TypeRank;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeBooleanTrue;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRankDelete extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankDelete()
	{
		// Parameters
		this.addParameter(TypeRank.get(), "rank");
		this.addParameter(TypeBooleanTrue.get(), "ensure");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		Rank rank = this.readArg();
		boolean ensure = this.readArg(false);
		
		// Ensure
		if (!ensure) throw new MassiveException().setMsg("<b>You must ensure to delete the rank.");
		
		// Delete
		boolean success = msenderFaction.removeRank(rank);
		
		// On Success
		if (success) this.demoteMembersWithRank(rank);
		
		// Inform
		message(mson(
				"The Rank ",
				TypeRank.get().getVisualMson(rank),
				success ? " could not be " : " has been ",
				"removed."
		).color(success ? ChatColor.GREEN : ChatColor.RED));
	}
	
	private void demoteMembersWithRank(Rank rank)
	{
		// Prepare
		String visualRankOld = TypeRank.get().getVisual(rank);
		String visualRankNew = TypeRank.get().getVisual(msenderFaction.getNextLowerRank(rank));
		String message = Txt.parse("<i>You have been demoted from %s to %s, because your rank has been deleted.", visualRankOld, visualRankNew);
		
		// Demote & Inform
		for (MPlayer mplayer : msenderFaction.getMPlayersWhereRank(rank))
		{
			msenderFaction.demote(mplayer);
			mplayer.msg(message);
		}
	}
	
}
