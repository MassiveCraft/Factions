package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsShow extends FCommand
{
	public CmdFactionsShow()
	{
		this.addAliases("show", "who");
		
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.SHOW.node));
	}

	@Override
	public void perform()
	{
		Faction faction = this.arg(0, ARFaction.get(), myFaction);
		if (faction == null) return;

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(ConfServer.econCostShow, "to show faction information", "for showing faction information")) return;

		Collection<FPlayer> admins = faction.getFPlayersWhereRole(Rel.LEADER);
		Collection<FPlayer> mods = faction.getFPlayersWhereRole(Rel.OFFICER);
		Collection<FPlayer> normals = faction.getFPlayersWhereRole(Rel.MEMBER);
		Collection<FPlayer> recruits = faction.getFPlayersWhereRole(Rel.RECRUIT);
		
		msg(Txt.titleize(faction.getTag(fme)));
		msg("<a>Description: <i>%s", faction.getDescription());
		
		// Display important flags
		// TODO: Find the non default flags, and display them instead.
		if (faction.getFlag(FFlag.PERMANENT))
		{
			msg("<a>This faction is permanent - remaining even with no members.");
		}
		
		if (faction.getFlag(FFlag.PEACEFUL))
		{
			msg("<a>This faction is peaceful - in truce with everyone.");
		}
		
		msg("<a>Joining: <i>"+(faction.isOpen() ? "no invitation is needed" : "invitation is required"));

		double powerBoost = faction.getPowerBoost();
		String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
		msg("<a>Land / Power / Maxpower: <i> %d/%d/%d %s", faction.getLandCount(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);

		// show the land value
		if (Econ.shouldBeUsed())
		{
			double value = Econ.calculateTotalLandValue(faction.getLandCount());
			double refund = value * ConfServer.econClaimRefundMultiplier;
			if (value > 0)
			{
				String stringValue = Econ.moneyString(value);
				String stringRefund = (refund > 0.0) ? (" ("+Econ.moneyString(refund)+" depreciated)") : "";
				msg("<a>Total land value: <i>" + stringValue + stringRefund);
			}
			
			//Show bank contents
			if(ConfServer.bankEnabled)
			{
				msg("<a>Bank contains: <i>"+Econ.moneyString(Econ.getBalance(faction.getAccountId())));
			}
		}

		String sepparator = Txt.parse("<i>")+", ";
		
		// List the relations to other factions
		Map<Rel, List<String>> relationTags = faction.getFactionTagsPerRelation(fme, true);
		
		if (faction.getFlag(FFlag.PEACEFUL))
		{
			sendMessage(Txt.parse("<a>In Truce with:<i> *everyone*"));
		}
		else
		{
			sendMessage(Txt.parse("<a>In Truce with: ") + Txt.implode(relationTags.get(Rel.TRUCE), sepparator));
		}
		
		sendMessage(Txt.parse("<a>Allied to: ") + Txt.implode(relationTags.get(Rel.ALLY), sepparator));
		sendMessage(Txt.parse("<a>Enemies: ") + Txt.implode(relationTags.get(Rel.ENEMY), sepparator));
		
		// List the members...
		List<String> memberOnlineNames = new ArrayList<String>();
		List<String> memberOfflineNames = new ArrayList<String>();
		
		for (FPlayer follower : admins)
		{
			if (follower.isOnline() && Mixin.isVisible(me, follower.getId()))
			{
				memberOnlineNames.add(follower.getNameAndTitle(fme));
			}
			else
			{
				memberOfflineNames.add(follower.getNameAndTitle(fme));
			}
		}
		
		for (FPlayer follower : mods)
		{
			if (follower.isOnline() && Mixin.isVisible(me, follower.getId()))
			{
				memberOnlineNames.add(follower.getNameAndTitle(fme));
			}
			else
			{
				memberOfflineNames.add(follower.getNameAndTitle(fme));
			}
		}
		
		for (FPlayer follower : normals)
		{
			if (follower.isOnline() && Mixin.isVisible(me, follower.getId()))
			{
				memberOnlineNames.add(follower.getNameAndTitle(fme));
			}
			else
			{
				memberOfflineNames.add(follower.getNameAndTitle(fme));
			}
		}
		
		for (FPlayer follower : recruits)
		{
			if (follower.isOnline())
			{
				memberOnlineNames.add(follower.getNameAndTitle(fme));
			}
			else
			{
				memberOfflineNames.add(follower.getNameAndTitle(fme));
			}
		}
		sendMessage(Txt.parse("<a>Members online: ") + Txt.implode(memberOnlineNames, sepparator));
		sendMessage(Txt.parse("<a>Members offline: ") + Txt.implode(memberOfflineNames, sepparator));
	}
	
}
