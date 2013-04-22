package com.massivecraft.factions.entity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.ps.PSBuilder;
import com.massivecraft.mcore.store.Coll;
import com.massivecraft.mcore.store.MStore;
import com.massivecraft.mcore.util.DiscUtil;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.xlib.gson.reflect.TypeToken;

public class BoardColl extends Coll<Board> implements BoardInterface
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static BoardColl i = new BoardColl();
	public static BoardColl get() { return i; }
	private BoardColl()
	{
		super(Const.COLLECTION_BASENAME_BOARD, Board.class, MStore.getDb(ConfServer.dburi), Factions.get(), true, true);
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
	
	@Override
	public void init()
	{
		super.init();

		this.migrate();
	}
	
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
			Board board = this.get(worldName);
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
