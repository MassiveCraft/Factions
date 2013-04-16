package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.event.FactionRenameEvent;
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
		
		senderMustBeOfficer = true;
	}
	
	@Override
	public void perform()
	{
		String tag = this.arg(0);
		
		// TODO does not first test cover selfcase?
		if (FactionColl.get().isTagTaken(tag) && ! MiscUtil.getComparisonString(tag).equals(myFaction.getComparisonTag()))
		{
			msg("<b>That tag is already taken");
			return;
		}

		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(FactionColl.validateTag(tag));
		if (errors.size() > 0)
		{
			sendMessage(errors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if ( ! canAffordCommand(ConfServer.econCostTag, "to change the faction tag")) return;

		// trigger the faction rename event (cancellable)
		FactionRenameEvent renameEvent = new FactionRenameEvent(fme, tag);
		Bukkit.getServer().getPluginManager().callEvent(renameEvent);
		if(renameEvent.isCancelled()) return;

		// then make 'em pay (if applicable)
		if ( ! payForCommand(ConfServer.econCostTag, "to change the faction tag", "for changing the faction tag")) return;

		String oldtag = myFaction.getTag();
		myFaction.setTag(tag);

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
