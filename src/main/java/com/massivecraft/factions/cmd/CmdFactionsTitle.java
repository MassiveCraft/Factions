package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsTitleChange;
import com.massivecraft.massivecore.cmd.arg.ARString;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsTitle extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsTitle()
	{
		// Aliases
		this.addAliases("title");

		// Args
		this.addRequiredArg("player");
		this.addOptionalArg("title", "");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.TITLE.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		MPlayer you = this.arg(0, ARMPlayer.getAny());
		if (you == null) return;
		
		String newTitle = this.argConcatFrom(1, ARString.get(), "");
		if (newTitle == null) return;
		
		newTitle = Txt.parse(newTitle);
		if (!Perm.TITLE_COLOR.has(sender, false))
		{
			newTitle = ChatColor.stripColor(newTitle);
		}
		
		// MPerm
		if ( ! MPerm.getPermTitle().has(msender, you.getFaction(), true)) return;
		
		// Verify
		if ( ! canIAdministerYou(msender, you)) return;

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
