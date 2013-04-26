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
import com.massivecraft.factions.event.FactionsEventDisband;
import com.massivecraft.factions.event.FactionsEventHomeChange;
import com.massivecraft.factions.event.FactionsEventHomeTeleport;
import com.massivecraft.factions.event.FactionsEventInvitedChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.event.FactionsEventOpenChange;
import com.massivecraft.factions.event.FactionsEventRelationChange;
import com.massivecraft.factions.event.FactionsEventNameChange;
import com.massivecraft.factions.event.FactionsEventTitleChange;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.money.Money;

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
	// TAKE ON LEAVE
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void takeOnLeave(FactionsEventMembershipChange event)
	{
		// If a player is leaving the faction ...
		if (event.getReason() != MembershipChangeReason.LEAVE) return;
		
		// ... and that player was the last one in the faction ...
		UPlayer uplayer = event.getUPlayer();
		Faction oldFaction = uplayer.getFaction();
		if (oldFaction.getUPlayers().size() > 1) return;
		
		// ... then transfer all money to the player. 
		Econ.transferMoney(uplayer, oldFaction, uplayer, Money.get(oldFaction));
	}
	
	// -------------------------------------------- //
	// TAKE ON DISBAND
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void takeOnDisband(FactionsEventDisband event)
	{
		// If there is a usender ...
		UPlayer usender = event.getUSender();
		if (usender == null) return;
		
		// ... and economy is enabled ...
		if (!Econ.isEnabled(usender)) return;
		
		// ... then transfer all the faction money to the sender.
		Faction faction = event.getFaction();
	
		double amount = Money.get(faction);
		String amountString = Money.format(faction, amount);
		
		Econ.transferMoney(usender, faction, usender, amount, true);
		
		usender.msg("<i>You have been given the disbanded faction's bank, totaling %s.", amountString);
		Factions.get().log(usender.getName() + " has been given bank holdings of "+amountString+" from disbanding "+faction.getName()+".");
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
		
		UConf uconf = UConf.get(event.getSender());
		if (uconf == null) return;
		
		if (event.getReason() == MembershipChangeReason.JOIN)
		{
			cost = uconf.econCostJoin;
			desc = "join a faction";
		}
		else if (event.getReason() == MembershipChangeReason.LEAVE)
		{
			cost = uconf.econCostLeave;
			desc = "leave a faction";
		}
		else if (event.getReason() == MembershipChangeReason.KICK)
		{
			cost = uconf.econCostKick;
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
	public void payForCommand(FactionsEventNameChange event)
	{
		Double cost = UConf.get(event.getSender()).econCostName;
		String desc = Factions.get().getOuterCmdFactions().cmdFactionsName.getDesc();
		
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
