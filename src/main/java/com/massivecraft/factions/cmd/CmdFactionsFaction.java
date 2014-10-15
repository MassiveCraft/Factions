package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.PlayerRoleComparator;
import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsFaction extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFaction()
	{
		// Aliases
		this.addAliases("f", "faction");

		// Args
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.FACTION.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get(), msenderFaction);
		if (faction == null) return;
		
		// Data precalculation 
		//boolean none = faction.isNone();
		boolean normal = faction.isNormal();
		
		// INFO: Title
		msg(Txt.titleize("Faction " + faction.getName(msender)));
		
		// INFO: Id (admin mode output only)
		if (msender.isUsingAdminMode())
		{
			msg("<a>ID: <i>%s", faction.getId());
		}
		
		// INFO: Description
		msg("<a>Description: <i>%s", faction.getDescription());	
		
		if (normal)
		{
			// INFO: Age
			long ageMillis = faction.getCreatedAtMillis() - System.currentTimeMillis();
			LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
			String ageString = TimeDiffUtil.formatedVerboose(ageUnitcounts, "<i>");
			msg("<a>Age: <i>%s", ageString);
			
			// INFO: Open
			// TODO: Why hardcode displaying the open flag only? We should rather display everything publicly editable.
			msg("<a>Open: <i>"+(faction.getFlag(MFlag.getFlagOpen()) ? "<lime>Yes<i>, anyone can join" : "<rose>No<i>, only invited people can join"));
	
			// INFO: Power
			double powerBoost = faction.getPowerBoost();
			String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
			msg("<a>Land / Power / Maxpower: <i> %d/%d/%d %s", faction.getLandCount(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);
			
			// show the land value
			if (Econ.isEnabled())
			{
				long landCount = faction.getLandCount();
				
				for (EventFactionsChunkChangeType type : EventFactionsChunkChangeType.values())
				{
					Double money = MConf.get().econChunkCost.get(type);
					if (money == null) continue;
					if (money == 0D) continue;
					money *= landCount;
					
					String word = null;
					if (money > 0)
					{
						word = "cost";
					}
					else
					{
						word = "reward";
						money *= -1;
					}
					
					msg("<a>Total land %s %s: <i>%s", type.toString().toLowerCase(), word, Money.format(money));
				}
				
				// Show bank contents
				if (MConf.get().bankEnabled)
				{
					msg("<a>Bank contains: <i>"+Money.format(Money.get(faction)));
				}
			}
			
			// Display important flags
			// TODO: Find the non default flags, and display them instead.
			if (faction.getFlag(MFlag.getFlagPermanent()))
			{
				msg("<a>This faction is permanent - remaining even with no followers.");
			}
			
			if (faction.getFlag(MFlag.getFlagPeaceful()))
			{
				msg("<a>This faction is peaceful - in truce with everyone.");
			}
		}
		
		String sepparator = Txt.parse("<i>")+", ";
		
		// List the relations to other factions
		Map<Rel, List<String>> relationNames = faction.getFactionNamesPerRelation(msender, true);
		
		if (faction.getFlag(MFlag.getFlagPeaceful()))
		{
			sendMessage(Txt.parse("<a>In Truce with:<i> *everyone*"));
		}
		else
		{
			sendMessage(Txt.parse("<a>In Truce with: ") + Txt.implode(relationNames.get(Rel.TRUCE), sepparator));
		}
		
		sendMessage(Txt.parse("<a>Allies: ") + Txt.implode(relationNames.get(Rel.ALLY), sepparator));
		sendMessage(Txt.parse("<a>Enemies: ") + Txt.implode(relationNames.get(Rel.ENEMY), sepparator));
		
		// List the followers...
		List<String> followerNamesOnline = new ArrayList<String>();
		List<String> followerNamesOffline = new ArrayList<String>();
		
		List<MPlayer> followers = faction.getMPlayers();
		Collections.sort(followers, PlayerRoleComparator.get());
		
		for (MPlayer follower : followers)
		{
			if (follower.isOnline() && Mixin.canSee(sender, follower.getId()))
			{
				followerNamesOnline.add(follower.getNameAndTitle(msender));
			}
			else if (normal)
			{
				// For the non-faction we skip the offline members since they are far to many (infinate almost)
				followerNamesOffline.add(follower.getNameAndTitle(msender));
			}
		}
		
		sendMessage(Txt.parse("<a>Followers online (%s): ", followerNamesOnline.size()) + Txt.implode(followerNamesOnline, sepparator));
		
		if (normal)
		{
			sendMessage(Txt.parse("<a>Followers offline (%s): ", followerNamesOffline.size()) + Txt.implode(followerNamesOffline, sepparator));
		}
	}
	
}
