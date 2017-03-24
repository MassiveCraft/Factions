package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.mixin.MixinWorld;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;

import java.util.Collections;
import java.util.List;
import java.util.Set;


public class CmdFactionsSetAll extends CmdFactionsSetXAll
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final List<String> LIST_ALL = Collections.unmodifiableList(MUtil.list("a", "al", "all"));
	public static final List<String> LIST_MAP = Collections.singletonList("map");
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetAll(boolean claim)
	{
		// Super
		super(claim);
		
		// Aliases
		this.addAliases("all");
		
		// Requirements
		Perm perm = claim ? Perm.CLAIM_ALL : Perm.UNCLAIM_ALL;
		this.addRequirements(RequirementHasPerm.get(perm));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<PS> getChunks() throws MassiveException
	{
		// World
		String word = (this.isClaim() ? "claim" : "unclaim");
		
		// Create Ret
		Set<PS> chunks = null;
		
		// Args
		Faction oldFaction = this.getOldFaction();
		
		if (LIST_ALL.contains(this.argAt(0).toLowerCase()))
		{
			chunks = BoardColl.get().getChunks(oldFaction);
			this.setFormatOne("<h>%s<i> %s <h>%d <i>chunk using " + word + " all.");
			this.setFormatMany("<h>%s<i> %s <h>%d <i>chunks using " + word + " all.");
		}
		else
		{
			String worldId = null;
			if (LIST_MAP.contains(this.argAt(0).toLowerCase()))
			{
				if (me != null)
				{
					worldId = me.getWorld().getName();
				}
				else
				{
					msg("<b>You must specify which map from console.");
					return null;
				}
			}
			else
			{
				worldId = this.argAt(0);
				if (worldId == null) return null;
			}
			Board board = BoardColl.get().get(worldId);
			chunks = board.getChunks(oldFaction);
			String worldDisplayName = MixinWorld.get().getWorldDisplayName(worldId);
			this.setFormatOne("<h>%s<i> %s <h>%d <i>chunk using " + word + " <h>" + worldDisplayName + "<i>.");
			this.setFormatMany("<h>%s<i> %s <h>%d <i>chunks using " + word + " <h>" + worldDisplayName + "<i>.");
		}
		
		// Return Ret
		return chunks;
	}
	
}
