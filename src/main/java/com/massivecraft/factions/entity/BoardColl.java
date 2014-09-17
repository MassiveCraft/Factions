package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.massivecraft.factions.Const;
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
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static BoardColl i = new BoardColl();
	public static BoardColl get() { return i; }
	private BoardColl()
	{
		super(Const.COLLECTION_BOARD, Board.class, MStore.getDb(), Factions.get(), false, true, true);
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
	
	/*
	@Override
	public void init()
	{
		super.init();

		this.migrate();
	}
	
	// This method is for the 1.8.X --> 2.0.0 migration
	public void migrate()
	{
		// Create file objects
		File oldFile = new File(Factions.get().getDataFolder(), "board.json");
		File newFile = new File(Factions.get().getDataFolder(), "board.json.migrated");
		
		// Already migrated?
		if ( ! oldFile.exists()) return;
		
		// Read the file content through GSON. 
		Type type = new TypeToken<Map<String,Map<String,TerritoryAccess>>>(){}.getType();
		Map<String,Map<String,TerritoryAccess>> worldCoordIds = Factions.get().gson.fromJson(DiscUtil.readCatch(oldFile), type);
		
		// Set the data
		for (Entry<String,Map<String,TerritoryAccess>> entry : worldCoordIds.entrySet())
		{
			String worldName = entry.getKey();
			BoardColl boardColl = this.getForWorld(worldName);
			Board board = boardColl.get(worldName);
			for (Entry<String,TerritoryAccess> entry2 : entry.getValue().entrySet())
			{
				String[] ChunkCoordParts = entry2.getKey().trim().split("[,\\s]+");
				int chunkX = Integer.parseInt(ChunkCoordParts[0]);
				int chunkZ = Integer.parseInt(ChunkCoordParts[1]);
				PS ps = new PSBuilder().chunkX(chunkX).chunkZ(chunkZ).build();
				
				TerritoryAccess territoryAccess = entry2.getValue();
				
				board.setTerritoryAccessAt(ps, territoryAccess);
			}
		}
		
		// Mark as migrated
		oldFile.renameTo(newFile);
	}
	 */
	
}
