package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsTitleChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsTitle extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsTitle()
	{
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeString.get(), "title", "", true);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		MPlayer you = this.readArg();
		String newTitle = this.readArg("");
		
		newTitle = Txt.parse(newTitle);
		if (!Perm.TITLE_COLOR.has(sender, false))
		{
			newTitle = ChatColor.stripColor(newTitle);
		}
		
		// MPerm
		if ( ! MPerm.getPermTitle().has(msender, you.getFaction(), true)) return;
		
		// Rank Check
		if (!msender.isOverriding() && you.getRole().isMoreThan(msender.getRole()))
		{
			msg("<b>You can not edit titles for higher ranks.");
			return;
		}

		// Event
		EventFactionsTitleChange event = new EventFactionsTitleChange(sender, you, newTitle);
		event.run();
		if (event.isCancelled()) return;
		newTitle = event.getNewTitle();

		// Apply
		you.setTitle(newTitle);
		
		// Inform
		msenderFaction.msg("%s<i> changed a title: %s", msender.describeTo(msenderFaction, true), you.describeTo(msenderFaction, true));
	}
	
}
