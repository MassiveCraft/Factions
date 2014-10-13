package com.massivecraft.factions.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsAbstractSender;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsDescriptionChange;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsHomeChange;
import com.massivecraft.factions.event.EventFactionsHomeTeleport;
import com.massivecraft.factions.event.EventFactionsInvitedChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.event.EventFactionsFlagChange;
import com.massivecraft.factions.event.EventFactionsRelationChange;
import com.massivecraft.factions.event.EventFactionsTitleChange;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

public class EngineEcon extends EngineAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineEcon i = new EngineEcon();
	public static EngineEcon get() { return i; }
	public EngineEcon() {}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
	}

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
		// If there is a usender ...
		MPlayer usender = event.getMSender();
		if (usender == null) return;
		
		// ... and economy is enabled ...
		if (!Econ.isEnabled()) return;
		
		// ... then transfer all the faction money to the sender.
		Faction faction = event.getFaction();
	
		double amount = Money.get(faction);
		String amountString = Money.format(amount);
		
		Econ.transferMoney(faction, usender, usender, amount, true);
		
		usender.msg("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
		Factions.get().log(usender.getName() + " has been given bank holdings of "+amountString+" from disbanding "+faction.getName()+".");
	}
	
	// -------------------------------------------- //
	// PAY FOR ACTION
	// -------------------------------------------- //
	
	public static void payForAction(EventFactionsAbstractSender event, Double cost, String desc)
	{
		// If there is a sender ...
		MPlayer usender = event.getMSender();
		if (usender == null) return;
		
		// ... and there is a cost ...
		if (cost == null) return;
		if (cost == 0) return;
		
		// ... that the sender can't afford ...
		if (Econ.payForAction(cost, usender, desc)) return;
		
		// ... then cancel.
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForAction(EventFactionsChunksChange event)
	{
		double cost = 0;
		List<String> typeNames = new ArrayList<String>();
		
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
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsRelationNeutral.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsHomeChange event)
	{
		Double cost = MConf.get().econCostSethome;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsSethome.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsCreate event)
	{
		Double cost = MConf.get().econCostCreate;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsCreate.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsDescriptionChange event)
	{
		Double cost = MConf.get().econCostDescription;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsDescription.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsNameChange event)
	{
		Double cost = MConf.get().econCostName;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsName.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsTitleChange event)
	{
		Double cost = MConf.get().econCostTitle;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsTitle.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsFlagChange event)
	{
		Double cost = MConf.get().econCostFlag;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsFlag.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsInvitedChange event)
	{
		Double cost = event.isNewInvited() ? MConf.get().econCostInvite : MConf.get().econCostDeinvite;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsInvite.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(EventFactionsHomeTeleport event)
	{
		Double cost = MConf.get().econCostHome;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsHome.getDesc();
		
		payForAction(event, cost, desc);
	}
	

}
