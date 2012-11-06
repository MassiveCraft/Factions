package com.massivecraft.factions.cmd;

import java.util.Collection;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.struct.Relation;

public class CmdShow extends FCommand
{
	
	public CmdShow()
	{
		this.aliases.add("show");
		this.aliases.add("who");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction tag", "yours");
		
		this.permission = Permission.SHOW.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		Faction faction = myFaction;
		if (this.argIsSet(0))
		{
			faction = this.argAsFaction(0);
			if (faction == null) return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostShow, "to show faction information", "for showing faction information")) return;

		Collection<FPlayer> admins = faction.getFPlayersWhereRole(Role.ADMIN);
		Collection<FPlayer> mods = faction.getFPlayersWhereRole(Role.MODERATOR);
		Collection<FPlayer> normals = faction.getFPlayersWhereRole(Role.NORMAL);
		
		msg(p.txt.titleize(faction.getTag(fme)));
		msg("<a>Description: <i>%s", faction.getDescription());
		if ( ! faction.isNormal())
		{
			return;
		}
		
		String peaceStatus = "";
		if (faction.isPeaceful())
		{
			peaceStatus = "     "+Conf.colorNeutral+"This faction is Peaceful";
		}
		
		msg("<a>Joining: <i>"+(faction.getOpen() ? "no invitation is needed" : "invitation is required")+peaceStatus);

		double powerBoost = faction.getPowerBoost();
		String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
		msg("<a>Land / Power / Maxpower: <i> %d/%d/%d %s", faction.getLandRounded(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);

		if (faction.isPermanent())
		{
			msg("<a>This faction is permanent, remaining even with no members.");
		}

		// show the land value
		if (Econ.shouldBeUsed())
		{
			double value = Econ.calculateTotalLandValue(faction.getLandRounded());
			double refund = value * Conf.econClaimRefundMultiplier;
			if (value > 0)
			{
				String stringValue = Econ.moneyString(value);
				String stringRefund = (refund > 0.0) ? (" ("+Econ.moneyString(refund)+" depreciated)") : "";
				msg("<a>Total land value: <i>" + stringValue + stringRefund);
			}
			
			//Show bank contents
			if(Conf.bankEnabled) {
				msg("<a>Bank contains: <i>"+Econ.moneyString(Econ.getBalance(faction.getAccountId())));
			}
		}

		String listpart;
		
		// List relation
		String allyList = p.txt.parse("<a>Allies: ");
		String enemyList = p.txt.parse("<a>Enemies: ");
		for (Faction otherFaction : Factions.i.get())
		{
			if (otherFaction == faction) continue;

			Relation rel = otherFaction.getRelationTo(faction);
			if ( ! rel.isAlly() && ! rel.isEnemy()) continue;  // if not ally or enemy, drop out now so we're not wasting time on it; good performance boost

			listpart = otherFaction.getTag(fme)+p.txt.parse("<i>")+", ";
			if (rel.isAlly())
				allyList += listpart;
			else if (rel.isEnemy())
				enemyList += listpart;
		}
		if (allyList.endsWith(", "))
			allyList = allyList.substring(0, allyList.length()-2);
		if (enemyList.endsWith(", "))
			enemyList = enemyList.substring(0, enemyList.length()-2);
		
		sendMessage(allyList);
		sendMessage(enemyList);
		
		// List the members...
		String onlineList = p.txt.parse("<a>")+"Members online: ";
		String offlineList = p.txt.parse("<a>")+"Members offline: ";
		for (FPlayer follower : admins)
		{
			listpart = follower.getNameAndTitle(fme)+p.txt.parse("<i>")+", ";
			if (follower.isOnlineAndVisibleTo(me))
			{
				onlineList += listpart;
			}
			else
			{
				offlineList += listpart;
			}
		}
		for (FPlayer follower : mods)
		{
			listpart = follower.getNameAndTitle(fme)+p.txt.parse("<i>")+", ";
			if
			(follower.isOnlineAndVisibleTo(me))
			{
				onlineList += listpart;
			} else {
				offlineList += listpart;
			}
		}
		for (FPlayer follower : normals) {
			listpart = follower.getNameAndTitle(fme)+p.txt.parse("<i>")+", ";
			if (follower.isOnlineAndVisibleTo(me)) {
				onlineList += listpart;
			} else {
				offlineList += listpart;
			}
		}
		
		if (onlineList.endsWith(", ")) {
			onlineList = onlineList.substring(0, onlineList.length()-2);
		}
		if (offlineList.endsWith(", ")) {
			offlineList = offlineList.substring(0, offlineList.length()-2);
		}
		
		sendMessage(onlineList);
		sendMessage(offlineList);
	}
	
}
