package com.massivecraft.factions;

import java.util.Set;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.entity.UPlayerColl;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.event.EventMassiveCoreUuidUpdate;
import com.massivecraft.massivecore.util.IdUpdateUtil;
import com.massivecraft.massivecore.util.MUtil;

public class EngineIdUpdate extends EngineAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineIdUpdate i = new EngineIdUpdate();
	public static EngineIdUpdate get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
	}
	
	// -------------------------------------------- //
	// LISTENER
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.MONITOR)
	public void update(EventMassiveCoreUuidUpdate event)
	{
		for (FactionColl coll : FactionColls.get().getColls())
		{
			for (Faction entity : coll.getAll())
			{
				update(coll, entity);
			}
		}
		
		IdUpdateUtil.update(MPlayerColl.get());
		
		for (UPlayerColl coll : UPlayerColls.get().getColls())
		{
			IdUpdateUtil.update(coll);
		}
		
		for (BoardColl coll : BoardColls.get().getColls())
		{
			update(coll);
		}
	}
	
	public static void update(FactionColl coll, Faction entity)
	{
		// Before and After
		Set<String> before = entity.getInvitedPlayerIds();
		if (before == null) return;
		Set<String> after = IdUpdateUtil.update(before, true);
		if (after == null) return;
		
		// NoChange
		if (MUtil.equals(before, after)) return;
		
		// Apply
		entity.setInvitedPlayerIds(after);
		entity.sync();
	}
	
	public static void update(BoardColl coll)
	{
		for (Board board : coll.getAll())
		{
			update(board);
		}
	}
	
	public static void update(Board board)
	{
		boolean changed = false;
		for (TerritoryAccess ta : board.getMap().values())
		{
			changed |= update(ta);
		}
		if (changed)
		{
			board.changed();
			board.sync();
		}
	}
	
	public static boolean update(TerritoryAccess entity)
	{
		// Before and After
		Set<String> before = entity.playerIds;
		if (before == null) return false;
		Set<String> after = IdUpdateUtil.update(before, true);
		if (after == null) return false;
		
		// NoChange
		if (MUtil.equals(before, after)) return false;
		
		// Apply
		entity.playerIds = after;
		//entity.sync();
		return true;
	}

}
