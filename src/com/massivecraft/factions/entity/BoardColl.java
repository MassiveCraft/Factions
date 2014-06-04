package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.util.MUtil;

public class BoardColl extends Coll<Board> implements BoardInterface
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public BoardColl(String name)
	{
		super(name, Board.class, MStore.getDb(), Factions.get(), false, true, true);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COLL
	// -------------------------------------------- //
	
	@Override
	public String fixId(Object oid)
	{
		if (oid == null) return null;
		if (oid instanceof String) return (String)oid;
		if (oid instanceof Board) return this.getId(oid);
		
		return MUtil.extract(String.class, "worldName", oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: BOARD
	// -------------------------------------------- //
	
	@Override
	public TerritoryAccess getTerritoryAccessAt(PS ps)
	{
		if (ps == null) return null;
		Board board = this.get(ps.getWorld());
		if (board == null) return null;
		return board.getTerritoryAccessAt(ps);
	}
	
	@Override
	public Faction getFactionAt(PS ps)
	{
		if (ps == null) return null;
		Board board = this.get(ps.getWorld());
		if (board == null) return null;
		return board.getFactionAt(ps);
	}
	
	// SET

	@Override
	public void setTerritoryAccessAt(PS ps, TerritoryAccess territoryAccess)
	{
		if (ps == null) return;
		Board board = this.get(ps.getWorld());
		if (board == null) return;
		board.setTerritoryAccessAt(ps, territoryAccess);
	}
	
	@Override
	public void setFactionAt(PS ps, Faction faction)
	{
		if (ps == null) return;
		Board board = this.get(ps.getWorld());
		if (board == null) return;
		board.setFactionAt(ps, faction);
	}
	
	// REMOVE
	
	@Override
	public void removeAt(PS ps)
	{
		if (ps == null) return;
		Board board = this.get(ps.getWorld());
		if (board == null) return;
		board.removeAt(ps);
	}
	
	@Override
	public void removeAll(Faction faction)
	{
		for (Board board : this.getAll())
		{
			board.removeAll(faction);
		}
	}
	
	@Override
	public void clean()
	{
		for (Board board : this.getAll())
		{
			board.clean();
		}
	}
	
	// CHUNKS
	
	@Override
	public Set<PS> getChunks(Faction faction)
	{
		Set<PS> ret = new HashSet<PS>();
		for (Board board : this.getAll())
		{
			ret.addAll(board.getChunks(faction));
		}
		return ret;
	}
	
	// COUNT
	
	@Override
	public int getCount(Faction faction)
	{
		int ret = 0;
		for (Board board : this.getAll())
		{
			ret += board.getCount(faction);
		}
		return ret;
	}
	
	// NEARBY DETECTION
	
	@Override
	public boolean isBorderPs(PS ps)
	{
		if (ps == null) return false;
		Board board = this.get(ps.getWorld());
		if (board == null) return false;
		return board.isBorderPs(ps);
	}

	@Override
	public boolean isConnectedPs(PS ps, Faction faction)
	{
		if (ps == null) return false;
		Board board = this.get(ps.getWorld());
		if (board == null) return false;
		return board.isConnectedPs(ps, faction);
	}
	
	// MAP GENERATION
	
	@Override
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs, double inDegrees)
	{
		if (centerPs == null) return null;
		Board board = this.get(centerPs.getWorld());
		if (board == null) return null;
		return board.getMap(observer, centerPs, inDegrees);
	}
	
}
