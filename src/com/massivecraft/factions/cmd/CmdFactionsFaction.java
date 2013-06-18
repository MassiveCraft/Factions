package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.FactionsEventChunkChangeType;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.PlayerRoleComparator;
import com.massivecraft.factions.Rel;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.money.Money;
import com.massivecraft.mcore.util.TimeDiffUtil;
import com.massivecraft.mcore.util.TimeUnit;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsFaction extends FCommand
{
	public CmdFactionsFaction()
	{
		this.addAliases("f", "faction");
		
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.FACTION.node));
	}

	@Override
	public void perform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get(usenderFaction), usenderFaction);
		if (faction == null) return;
		
		// Data precalculation 
		UConf uconf = UConf.get(faction);
		//boolean none = faction.isNone();
		boolean normal = faction.isNormal();
		
		// INFO: Title
		msg(Txt.titleize(Txt.upperCaseFirst(faction.getUniverse()) + " Faction " + faction.getName(usender)));
		
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
			msg("<a>Open: <i>"+(faction.isOpen() ? "<lime>Yes<i>, anyone can join" : "<rose>No<i>, only invited people can join"));
	
			// INFO: Power
			double powerBoost = faction.getPowerBoost();
			String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
			msg("<a>Land / Power / Maxpower: <i> %d/%d/%d %s", faction.getLandCount(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);
			
			// show the land value
			if (Econ.isEnabled(faction))
			{
				long landCount = faction.getLandCount();
				
				for (FactionsEventChunkChangeType type : FactionsEventChunkChangeType.values())
				{
					Double money = uconf.econChunkCost.get(type);
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
					
					msg("<a>Total land %s %s: <i>%s", type.toString().toLowerCase(), word, Money.format(faction, money));
				}
				
				// Show bank contents
				if (UConf.get(faction).bankEnabled)
				{
					msg("<a>Bank contains: <i>"+Money.format(faction, Money.get(faction)));
				}
			}
			
			// Display important flags
			// TODO: Find the non default flags, and display them instead.
			if (faction.getFlag(FFlag.PERMANENT))
			{
				msg("<a>This faction is permanent - remaining even with no followers.");
			}
			
			if (faction.getFlag(FFlag.PEACEFUL))
			{
				msg("<a>This faction is peaceful - in truce with everyone.");
			}
		}
		
		String sepparator = Txt.parse("<i>")+", ";
		
		// List the relations to other factions
		Map<Rel, List<String>> relationNames = faction.getFactionNamesPerRelation(usender, true);
		
		if (faction.getFlag(FFlag.PEACEFUL))
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
		
		List<UPlayer> followers = faction.getUPlayers();
		Collections.sort(followers, PlayerRoleComparator.get());
		
		for (UPlayer follower : followers)
		{
			if (follower.isOnline() && Mixin.canSee(sender, follower.getId()))
			{
				followerNamesOnline.add(follower.getNameAndTitle(usender));
			}
			else if (normal)
			{
				// For the non-faction we skip the offline members since they are far to many (infinate almost)
				followerNamesOffline.add(follower.getNameAndTitle(usender));
			}
		}
		
		sendMessage(Txt.parse("<a>Followers online (%s): ", followerNamesOnline.size()) + Txt.implode(followerNamesOnline, sepparator));
		
		if (normal)
		{
			sendMessage(Txt.parse("<a>Followers offline (%s): ", followerNamesOffline.size()) + Txt.implode(followerNamesOffline, sepparator));
		}
	}
	
}
