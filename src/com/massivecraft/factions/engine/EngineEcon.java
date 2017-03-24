package com.massivecraft.factions.engine;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsAbstractSender;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsDescriptionChange;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsFlagChange;
import com.massivecraft.factions.event.EventFactionsHomeChange;
import com.massivecraft.factions.event.EventFactionsHomeTeleport;
import com.massivecraft.factions.event.EventFactionsInvitedChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.event.EventFactionsRelationChange;
import com.massivecraft.factions.event.EventFactionsTitleChange;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class EngineEcon extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineEcon i = new EngineEcon();
	public static EngineEcon get() { return i; }

	// -------------------------------------------- //
	// TAKE ON LEAVE
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void takeOnLeave(EventFactionsMembershipChange event)
	{
		// If a player is leaving the faction ...
		if (event.getReason() != MembershipChangeReason.LEAVE) return;
		
		// ... and that player was the last one in the faction ...
		MPlayer mplayer = event.getMPlayer();
		Faction oldFaction = mplayer.getFaction();
		if (oldFaction.getMPlayers().size() > 1) return;
		
		// ... then transfer all money to the player. 
		double money = Money.get(oldFaction);
		if (money == 0) return;
		Econ.transferMoney(mplayer, oldFaction, mplayer, money);
	}
	
	// -------------------------------------------- //
	// TAKE ON DISBAND
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void takeOnDisband(EventFactionsDisband event)
	{
		// If there is a mplayer ...
		MPlayer mplayer = event.getMPlayer();
		if (mplayer == null) return;
		
		// ... and economy is enabled ...
		if (!Econ.isEnabled()) return;
		
		// ... then transfer all the faction money to the sender.
		Faction faction = event.getFaction();
	
		double amount = Money.get(faction);
	
		// Check that there is an amount
		if (amount == 0) return;

		String amountString = Money.format(amount);
		
		Econ.transferMoney(faction, mplayer, mplayer, amount, true);
		
		mplayer.msg("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
		Factions.get().log(mplayer.getName() + " has been given bank holdings of "+amountString+" from disbanding "+faction.getName()+".");
	}
	
	// -------------------------------------------- //
	// PAY FOR ACTION
	// -------------------------------------------- //
	
	public static void payForAction(EventFactionsAbstractSender event, Double cost, String desc)
	{
		// If there is an mplayer ...
		MPlayer mplayer = event.getMPlayer();
		if (mplayer == null) return;
		
		// ... and there is a cost ...
		if (cost == null) return;
		if (cost == 0) return;
		
		// ... that the sender can't afford ...
		if (Econ.payForAction(cost, mplayer, desc)) return;
		
		// ... then cancel.
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForAction(EventFactionsChunksChange event)
	{
		double cost = 0;
		List<String> typeNames = new ArrayList<>();
		
		for (Entry<EventFactionsChunkChangeType, Set<PS>> typeChunks : event.getTypeChunks().entrySet())
		{
			final EventFactionsChunkChangeType type = typeChunks.getKey();
			final Set<PS> chunks = typeChunks.getValue();
			
			Double typeCost = MConf.get().econChunkCost.get(type);
			if (typeCost == null) continue;
			if (typeCost == 0) continue;
			
			typeCost *= chunks.size();
			cost += typeCost;
			
			typeNames.add(type.now);
		}
		
		String desc = Txt.implodeCommaAnd(typeNames) + " this land";
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForAction(EventFactionsMembershipChange event)
	{
		Double cost = null;		
		String desc = null;
		
		if (event.getReason() == MembershipChangeReason.JOIN)
		{
			cost = MConf.get().econCostJoin;
			desc = "join a faction";
		}
		else if (event.getReason() == MembershipChangeReason.LEAVE)
		{
			cost = MConf.get().econCostLeave;
			desc = "leave a faction";
		}
		else if (event.getReason() == MembershipChangeReason.KICK)
		{
			cost = MConf.get().econCostKick;
			desc = "kick someone from a faction";
		}
		else
		{
			return;
		}
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsRelationChange event)
	{
		Double cost = MConf.get().econRelCost.get(event.getNewRelation());
		String desc = CmdFactions.get().cmdFactionsRelation.cmdFactionsRelationSet.getDesc();
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsHomeChange event)
	{
		Double cost = MConf.get().econCostSethome;
		String desc = CmdFactions.get().cmdFactionsSethome.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsCreate event)
	{
		Double cost = MConf.get().econCostCreate;
		String desc = CmdFactions.get().cmdFactionsCreate.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsDescriptionChange event)
	{
		Double cost = MConf.get().econCostDescription;
		String desc = CmdFactions.get().cmdFactionsDescription.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsNameChange event)
	{
		Double cost = MConf.get().econCostName;
		String desc = CmdFactions.get().cmdFactionsName.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsTitleChange event)
	{
		Double cost = MConf.get().econCostTitle;
		String desc = CmdFactions.get().cmdFactionsTitle.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsFlagChange event)
	{
		Double cost = MConf.get().econCostFlag;
		String desc = CmdFactions.get().cmdFactionsFlag.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsInvitedChange event)
	{
		Double cost = event.isNewInvited() ? MConf.get().econCostInvite : MConf.get().econCostDeinvite;
		String desc = CmdFactions.get().cmdFactionsInvite.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsHomeTeleport event)
	{
		Double cost = MConf.get().econCostHome;
		String desc = CmdFactions.get().cmdFactionsHome.getDesc();
		
		payForAction(event, cost, desc);
	}
	

}
