package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Selector;
import com.massivecraft.factions.cmd.req.RequirementHasMPerm;
import com.massivecraft.factions.cmd.type.TypeSelector;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionBan;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.TypeNullable;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import org.bukkit.ChatColor;

public class CmdFactionsBanAdd extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsBanAdd()
	{
		// Parameters
		this.addParameter(TypeSelector.get(), "selector");
		this.addParameter(TypeNullable.get(TypeString.get()), "reason");
		
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
		String reason = this.readArg();
		Faction faction = msender.getUsedFaction();
		
		// Add
		FactionBan factionBan = new FactionBan(selector, msender, reason);
		faction.getFactionBans().attach(factionBan, selector.getId());
		
		// Inform
		message(mson(TypeSelector.get().getVisualMson(selector), " was banned.").color(ChatColor.YELLOW));
		if (reason != null) msg("<i>Reason: %s", reason);
	}
	
}
