package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.FactionsEventAbstractSender;
import com.massivecraft.factions.event.FactionsEventChunkChange;
import com.massivecraft.factions.event.FactionsEventChunkChangeType;
import com.massivecraft.factions.event.FactionsEventCreate;
import com.massivecraft.factions.event.FactionsEventDescriptionChange;
import com.massivecraft.factions.event.FactionsEventHomeChange;
import com.massivecraft.factions.event.FactionsEventHomeTeleport;
import com.massivecraft.factions.event.FactionsEventInvitedChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.event.FactionsEventOpenChange;
import com.massivecraft.factions.event.FactionsEventRelationChange;
import com.massivecraft.factions.event.FactionsEventTagChange;
import com.massivecraft.factions.event.FactionsEventTitleChange;
import com.massivecraft.factions.integration.Econ;

public class FactionsListenerEcon implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionsListenerEcon i = new FactionsListenerEcon();
	public static FactionsListenerEcon get() { return i; }
	public FactionsListenerEcon() {}
	
	// -------------------------------------------- //
	// SETUP
	// -------------------------------------------- //
	
	public void setup()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}

	// -------------------------------------------- //
	// PAY FOR ACTION
	// -------------------------------------------- //
	
	public static void payForAction(FactionsEventAbstractSender event, Double cost, String desc)
	{
		// If there is a sender ...
		UPlayer usender = event.getUSender();
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
	public void payForAction(FactionsEventChunkChange event)
	{
		Faction newFaction = event.getNewFaction();
		UConf uconf = UConf.get(newFaction);
		FactionsEventChunkChangeType type = event.getType();
		Double cost = uconf.econChunkCost.get(type);
		
		String desc = type.toString().toLowerCase() + " this land";
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForAction(FactionsEventMembershipChange event)
	{
		Double cost = null;		
		String desc = null;
		
		if (event.getReason() == MembershipChangeReason.JOIN)
		{
			cost = UConf.get(event.getSender()).econCostJoin;
			desc = "join a faction";
		}
		else if (event.getReason() == MembershipChangeReason.LEAVE)
		{
			cost = UConf.get(event.getSender()).econCostLeave;
			desc = "leave a faction";
		}
		else if (event.getReason() == MembershipChangeReason.KICK)
		{
			cost = UConf.get(event.getSender()).econCostKick;
			desc = "kick someone from a faction";
		}
		else
		{
			return;
		}
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventRelationChange event)
	{
		Double cost = UConf.get(event.getSender()).econRelCost.get(event.getNewRelation());
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsRelationNeutral.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventHomeChange event)
	{
		Double cost = UConf.get(event.getSender()).econCostSethome;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsSethome.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventCreate event)
	{
		Double cost = UConf.get(event.getSender()).econCostCreate;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsCreate.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventDescriptionChange event)
	{
		Double cost = UConf.get(event.getSender()).econCostDescription;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsDescription.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventTagChange event)
	{
		Double cost = UConf.get(event.getSender()).econCostTag;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsTag.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventTitleChange event)
	{
		Double cost = UConf.get(event.getSender()).econCostTitle;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsTitle.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventOpenChange event)
	{
		Double cost = UConf.get(event.getSender()).econCostOpen;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsOpen.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventInvitedChange event)
	{
		Double cost = event.isNewInvited() ? UConf.get(event.getSender()).econCostInvite : UConf.get(event.getSender()).econCostDeinvite;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsInvite.getDesc();
		
		payForAction(event, cost, desc);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventHomeTeleport event)
	{
		Double cost = UConf.get(event.getSender()).econCostHome;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsHome.getDesc();
		
		payForAction(event, cost, desc);
	}
	

}
