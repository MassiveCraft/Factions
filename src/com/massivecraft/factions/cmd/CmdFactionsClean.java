package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.Invitation;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.EntityInternalMap;
import com.massivecraft.massivecore.util.Txt;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class CmdFactionsClean extends FactionsCommand
{
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		Object message;
		int count;
		
		// Title
		message = Txt.titleize("Factions Cleaner Results");
		message(message);
		
		// Yada
		cleanMessage(this.cleanPlayer(), "player");
		cleanMessage(this.cleanFactionInvites(), "faction invites");
		cleanMessage(this.cleanFactionRelationWhishes(), "faction relation whishes");
		cleanMessage(this.cleanBoardHost(), "chunk whole");
		cleanMessage(this.cleanBoardGrant(), "chunk access");
	}
	
	// -------------------------------------------- //
	// CLEAN
	// -------------------------------------------- //
	
	private void cleanMessage(int count, String name)
	{
		msg("<v>%d<k> %s", count, name);
	}
	
	private int cleanPlayer()
	{
		int ret = 0;
		
		for (MPlayer mplayer : MPlayerColl.get().getAll())
		{
			if (!mplayer.isFactionOrphan()) continue;
			
			mplayer.resetFactionData();
			ret += 1;
		}
		
		return ret;
	}
	
	private int cleanFactionInvites()
	{
		int ret = 0;
		
		for (Faction faction : FactionColl.get().getAll())
		{
			EntityInternalMap<Invitation> invitations = faction.getInvitations();
			if (invitations.isEmpty()) continue;
			
			ret += invitations.size();
			Set<String> invitationIds = new MassiveSet<>(invitations.keySet());
			for (String inviteId : invitationIds)
			{
				invitations.detachIdFixed(inviteId);
			}
			
			faction.changed();
		}
		
		return ret;
	}
	
	private int cleanFactionRelationWhishes()
	{
		int ret = 0;
		
		for (Faction faction : FactionColl.get().getAll())
		{
			for (Iterator<Entry<String, Rel>> iterator = faction.getRelationWishes().entrySet().iterator(); iterator.hasNext();)
			{
				Entry<String, Rel> entry = iterator.next();
				String factionId = entry.getKey();
				if (FactionColl.get().containsId(factionId)) continue;
				
				iterator.remove();
				ret += 1;
				faction.changed();
			}
		}
		
		return ret;
	}
	
	private int cleanBoardHost()
	{
		int ret = 0;
		
		for (Board board : BoardColl.get().getAll())
		{
			for (Entry<PS, TerritoryAccess> entry : board.getMap().entrySet())
			{
				PS ps = entry.getKey();
				TerritoryAccess territoryAccess = entry.getValue();
				String factionId = territoryAccess.getHostFactionId();
				
				if (FactionColl.get().containsId(factionId)) continue;
				
				board.removeAt(ps);
				ret += 1;
			}
		}
		
		return ret;
	}
	
	private int cleanBoardGrant()
	{
		int ret = 0;
		
		for (Board board : BoardColl.get().getAll())
		{
			for (Entry<PS, TerritoryAccess> entry : board.getMap().entrySet())
			{
				PS ps = entry.getKey();
				TerritoryAccess territoryAccess = entry.getValue();
				boolean changed = false;
				
				for (String factionId : territoryAccess.getFactionIds())
				{
					if (FactionColl.get().containsId(factionId)) continue;
					
					territoryAccess = territoryAccess.withFactionId(factionId, false);
					ret += 1;
					changed = true;
				}
				
				if (changed)
				{
					board.setTerritoryAccessAt(ps, territoryAccess);
				}
			}
		}
		
		return ret;
	}
	
}
