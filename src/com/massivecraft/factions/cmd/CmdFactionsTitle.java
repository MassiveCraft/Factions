package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.FactionsEventTitleChange;
import com.massivecraft.mcore.cmd.arg.ARString;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsTitle extends FCommand
{
	public CmdFactionsTitle()
	{
		this.addAliases("title");
		
		this.addRequiredArg("player");
		this.addOptionalArg("title", "");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.TITLE.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		// Args
		UPlayer you = this.arg(0, ARUPlayer.getStartAny(sender));
		if (you == null) return;
		
		String newTitle = this.argConcatFrom(1, ARString.get(), "");
		if (newTitle == null) return;
		
		// Verify
		if ( ! canIAdministerYou(usender, you)) return;

		// Event
		FactionsEventTitleChange event = new FactionsEventTitleChange(sender, you, newTitle);
		event.run();
		if (event.isCancelled()) return;
		newTitle = event.getNewTitle();

		// Apply
		you.setTitle(newTitle);
		
		// Inform
		usenderFaction.msg("%s<i> changed a title: %s", usender.describeTo(usenderFaction, true), you.describeTo(usenderFaction, true));
	}
	
}
