package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Selector;
import com.massivecraft.factions.cmd.req.RequirementHasMPerm;
import com.massivecraft.factions.cmd.type.TypeSelector;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mson.Mson;
import org.bukkit.ChatColor;

public class CmdFactionsBanRemove extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsBanRemove()
	{
		// Parameters
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
		// Parameters
		Selector selector = this.readArg();
		Faction faction = msender.getUsedFaction();
		
		// Remove
		faction.getFactionBans().detachId(selector.getId());
		
		// Inform
		Mson visualSelector = TypeSelector.get().getVisualMson(selector);
		visualSelector = visualSelector.uppercaseFirst();
		message(mson(visualSelector, " was un-banned.").color(ChatColor.YELLOW));
	}

}
