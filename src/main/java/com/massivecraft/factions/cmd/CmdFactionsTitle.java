package com.massivecraft.factions.cmd;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.EventFactionsTitleChange;
import com.massivecraft.massivecore.cmd.arg.ARString;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsTitle extends FCommand
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
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.TITLE.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		UPlayer you = this.arg(0, ARUPlayer.getAny(sender));
		if (you == null) return;
		
		String newTitle = this.argConcatFrom(1, ARString.get(), "");
		if (newTitle == null) return;
		
		newTitle = Txt.parse(newTitle);
		if (!Perm.TITLE_COLOR.has(sender, false))
		{
			newTitle = ChatColor.stripColor(newTitle);
		}
		
		// Verify
		if ( ! canIAdministerYou(usender, you)) return;

		// Event
		EventFactionsTitleChange event = new EventFactionsTitleChange(sender, you, newTitle);
		event.run();
		if (event.isCancelled()) return;
		newTitle = event.getNewTitle();

		// Apply
		you.setTitle(newTitle);
		
		// Inform
		usenderFaction.msg("%s<i> changed a title: %s", usender.describeTo(usenderFaction, true), you.describeTo(usenderFaction, true));
	}
	
}
