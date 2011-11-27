package com.massivecraft.factions.cmd;

import java.util.Set;

import org.bukkit.Location;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdClaim extends FCommand
{
	
	public CmdClaim()
	{
		super();
		this.aliases.add("claim");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("radius", "1");
		
		this.permission = Permission.CLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
		
		aliases.add("claim");
	}
	
	@Override
	public void perform()
	{
		// Read and validate input
		Faction forFaction = this.argAsFaction(0, myFaction);
		double radius = this.argAsDouble(1, 1d);
		radius -= 0.5;
		if (radius <= 0)
		{
			msg("<b>That radius is to small.");
			return;
		}
		
		// Get the FLocations
		Set<FLocation> flocs = new FLocation(me).getCircle(radius);
		p.log(flocs);
		for (FLocation floc : flocs)
		{
			fme.attemptClaim(forFaction, new Location(floc.getWorld(), floc.getX()*16, 1, floc.getZ()*16), true);
		}
	}
	
}
