package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.event.FactionsEventAbstractSender;
import com.massivecraft.factions.event.FactionsEventCreate;
import com.massivecraft.factions.event.FactionsEventDescriptionChange;
import com.massivecraft.factions.event.FactionsEventHomeChange;
import com.massivecraft.factions.event.FactionsEventHomeTeleport;
import com.massivecraft.factions.event.FactionsEventInvitedChange;
import com.massivecraft.factions.event.FactionsEventOpenChange;
import com.massivecraft.factions.event.FactionsEventRelationChange;
import com.massivecraft.factions.event.FactionsEventTagChange;
import com.massivecraft.factions.event.FactionsEventTitleChange;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.mcore.cmd.MCommand;

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
	// PAY FOR COMMAND
	// -------------------------------------------- //
	
	public void payForCommand(FactionsEventAbstractSender event, double cost, MCommand command)
	{
		// If there is a sender ...
		if (event.getSender() == null) return;
		
		// ... and the sender can't afford ...
		if (Econ.payForAction(cost, event.getSender(), command.getDesc())) return;
		
		// ... then cancel.
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventHomeChange event)
	{
		payForCommand(event, ConfServer.econCostSethome, Factions.get().getOuterCmdFactions().cmdFactionsSethome);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventCreate event)
	{
		payForCommand(event, ConfServer.econCostCreate, Factions.get().getOuterCmdFactions().cmdFactionsCreate);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventDescriptionChange event)
	{
		payForCommand(event, ConfServer.econCostDescription, Factions.get().getOuterCmdFactions().cmdFactionsDescription);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventTagChange event)
	{
		payForCommand(event, ConfServer.econCostTag, Factions.get().getOuterCmdFactions().cmdFactionsTag);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventTitleChange event)
	{
		payForCommand(event, ConfServer.econCostTitle, Factions.get().getOuterCmdFactions().cmdFactionsTitle);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventRelationChange event)
	{
		payForCommand(event, event.getNewRelation().getRelationCost(), Factions.get().getOuterCmdFactions().cmdFactionsRelationNeutral);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventOpenChange event)
	{
		payForCommand(event, ConfServer.econCostOpen, Factions.get().getOuterCmdFactions().cmdFactionsOpen);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventInvitedChange event)
	{
		double cost = event.isNewInvited() ? ConfServer.econCostInvite : ConfServer.econCostDeinvite;
		payForCommand(event, cost, Factions.get().getOuterCmdFactions().cmdFactionsInvite);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void payForCommand(FactionsEventHomeTeleport event)
	{	
		payForCommand(event, ConfServer.econCostHome, Factions.get().getOuterCmdFactions().cmdFactionsHome);
	}
	
}
