package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.LandUnclaimEvent;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;

/**
	Low-level unclaim command intended for admin, script or custom game mechanic purposes.
*/
public class CmdUnclaimChunk extends FCommand
{
	public CmdUnclaimChunk()
	{
		this.aliases.add("unclaimchunk");
		this.aliases.add("declaimchunk");
		this.aliases.add("uc");
		this.aliases.add("uck");

		this.requiredArgs.add("world"); // world containing chunk
		this.requiredArgs.add("x"); // chunk x coord
		this.requiredArgs.add("z"); // chunk z coord
		//this.optionalArgs.put("", "");

		this.permission = Permission.UNCLAIM_CHUNK.node;
		this.disableOnLock = true;

		senderMustBePlayer = false;
		senderMustBeMember = false;
	}

	@Override
	public void perform()
	{
		String world = argAsString(0);
		Integer x = argAsInt(1), z = argAsInt(2);

		FLocation flocation = new FLocation(world, x, z);
		Faction otherFaction = Board.getFactionAt(flocation);

		LandUnclaimEvent unclaimEvent = new LandUnclaimEvent(flocation, otherFaction, fme);
		Bukkit.getServer().getPluginManager().callEvent(unclaimEvent);
		if(unclaimEvent.isCancelled()) return;

		Board.removeAt(flocation);
		SpoutFeatures.updateTerritoryDisplayLoc(flocation);

		if (Conf.logLandUnclaims)
			P.p.log("Land at ("+flocation.getCoordString()+") unclaimed from the faction: "+otherFaction.getTag());
	}

}