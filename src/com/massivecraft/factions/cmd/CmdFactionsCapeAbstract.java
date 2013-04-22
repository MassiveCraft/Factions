package com.massivecraft.factions.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;

public abstract class CmdFactionsCapeAbstract extends FCommand
{
	public Faction capeFaction;
	public String currentCape;
	
	public CmdFactionsCapeAbstract()
	{
		this.addOptionalArg("faction", "you");
	}

	@Override
	public boolean validCall(CommandSender sender, List<String> args)
	{
		this.capeFaction = null;
		this.currentCape = null;
		
		if (this.myFaction == null && ! this.argIsSet(this.requiredArgs.size()))
		{
			msg("<b>You must specify a faction from console.");
			return false;
		}
		
		this.capeFaction = this.arg(this.requiredArgs.size(), ARFaction.get(myFaction), myFaction);
		if (this.capeFaction == null) return false;
		
		// Do we have permission to manage the cape of that faction? 
		if (fme != null && ! FPerm.CAPE.has(fme, capeFaction)) return false;
		
		this.currentCape = this.capeFaction.getCape();
		
		return true;
	}
}
