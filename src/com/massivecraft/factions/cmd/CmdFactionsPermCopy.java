package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Selector;
import com.massivecraft.factions.cmd.type.TypeMPerm;
import com.massivecraft.factions.cmd.type.TypeSelector;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPermColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.TypeNullable;
import com.massivecraft.massivecore.mson.Mson;
import org.bukkit.ChatColor;

import java.util.Collection;
import java.util.Collections;

public class CmdFactionsPermCopy extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermCopy()
	{
		// Parameters
		this.addParameter(TypeSelector.get(), "selectorFrom");
		this.addParameter(TypeSelector.get(), "selectorTo");
		this.addParameter(TypeNullable.get(TypeMPerm.get()), "permission", "all");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameter
		Selector selectorFrom = this.readArg();
		Selector selectorTo = this.readArg();
		MPerm mperm = this.readArg();
		
		// Copy
		copyPerms(mperm, selectorFrom, selectorTo);
		
		// Inform
		TypeSelector type = TypeSelector.get();
		message(mson(
			"All perms have been copied from ",
			type.getVisualMson(selectorFrom),
			" to ",
			type.getVisualMson(selectorTo),
			Mson.DOT
		).color(ChatColor.GREEN));
	}
	
	private void copyPerms(MPerm mperm, Selector selectorFrom, Selector selectorTo)
	{
		Faction faction = msender.getUsedFaction();
		Collection<MPerm> perms = mperm != null ? Collections.singleton(mperm) : MPermColl.get().getAll();
		
		boolean permitted;
		for (MPerm perm : perms)
		{
			permitted = faction.isPermitted(perm, selectorFrom);
			faction.setPermitted(perm, selectorTo, permitted);
		}
	}
	
}
