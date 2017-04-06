package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.Selector;
import com.massivecraft.factions.cmd.req.RequirementHasMPerm;
import com.massivecraft.factions.cmd.type.TypeMPerm;
import com.massivecraft.factions.cmd.type.TypeSelector;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsPermChange;
import com.massivecraft.massivecore.Button;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mson.Mson;
import org.bukkit.ChatColor;

public abstract class CmdFactionsPermSet extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private boolean add;
	public boolean isAdd() { return this.add; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermSet(boolean add)
	{
		// Add/Remove
		this.add = add;
		
		// Parameters
		this.addParameter(TypeMPerm.get(), "permission");
		this.addParameter(TypeSelector.get(), "selector");
		
		// Requirements
		this.addRequirements(RequirementHasMPerm.get(MPerm.getPermPerms()));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameter
		MPerm perm = this.readArg();
		Selector selector = this.readArg();
		Faction faction = msender.getUsedFaction();
		boolean adding = this.isAdd();
		
		// Is this perm editable?
		if (!msender.isOverriding() && !perm.isEditable())
		{
			msg("<b>The perm <h>%s <b>is not editable.", perm.getName());
			return;
		}
		
		// Visuals
		String visualSelector = TypeSelector.get().getVisual(selector);
		String visualFaction = faction.describeTo(msender);
		String visualPerm = perm.getDesc(true, false);
		
		// No change
		if (faction.isPermitted(perm, selector) == adding)
		{
			String already = "already";
			if (!adding) already += " not";
			msg("%s <i>is %s permitted for faction %s<i> and perm %s<i>.", visualSelector, already, visualFaction, visualPerm);
			return;
		}
		
		// Event
		EventFactionsPermChange event = new EventFactionsPermChange(sender, faction, perm, selector, adding);
		event.run();
		if (event.isCancelled()) return;
		adding = event.getNewValue();
		
		// The following is to make sure the leader always has the right to change perms if that is our goal.
		if (perm == MPerm.getPermPerms() && MPerm.getPermPerms().getStandard().contains(Rel.LEADER) && !adding)
		{
			throw new MassiveException().setMsg("<b>You are not allowed to remove the leader from the perm <h>perms<b>.");
		}
		
		// Apply
		faction.setPermitted(perm, selector, adding);
		
		// Inform
		String addRemove = adding ? "added" : "removed";
		Mson button = new Button()
			.setName("Show")
			.setCommand(CmdFactions.get().cmdFactionsPerm.cmdFactionsPermShow)
			.setArgs(perm.getId())
			.render();
		faction.sendMessage(mson(visualSelector, " was ", addRemove, " to the perm ", visualPerm, ". ", button).color(ChatColor.YELLOW));
	}
	
}
