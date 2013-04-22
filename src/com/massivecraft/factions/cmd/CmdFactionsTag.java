package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.event.FactionsEventTagChange;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsTag extends FCommand
{
	
	public CmdFactionsTag()
	{
		this.addAliases("tag");
		
		this.addRequiredArg("new tag");
		
		this.addRequirements(ReqHasPerm.get(Perm.TAG.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		// Arg
		String newTag = this.arg(0);
		
		// TODO does not first test cover selfcase?
		if (FactionColl.get().isTagTaken(newTag) && ! MiscUtil.getComparisonString(newTag).equals(myFaction.getComparisonTag()))
		{
			msg("<b>That tag is already taken");
			return;
		}

		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(FactionColl.validateTag(newTag));
		if (errors.size() > 0)
		{
			sendMessage(errors);
			return;
		}

		// Event
		FactionsEventTagChange event = new FactionsEventTagChange(sender, myFaction, newTag);
		event.run();
		if (event.isCancelled()) return;
		newTag = event.getNewTag();

		// Apply
		String oldtag = myFaction.getTag();
		myFaction.setTag(newTag);

		// Inform
		myFaction.msg("%s<i> changed your faction tag to %s", fme.describeTo(myFaction, true), myFaction.getTag(myFaction));
		for (Faction faction : FactionColl.get().getAll())
		{
			if (faction == myFaction)
			{
				continue;
			}
			faction.msg("<i>The faction %s<i> changed their name to %s.", fme.getColorTo(faction)+oldtag, myFaction.getTag(faction));
		}

		if (ConfServer.spoutFactionTagsOverNames)
		{
			SpoutFeatures.updateTitle(myFaction, null);
		}
	}
	
}
