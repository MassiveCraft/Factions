package com.massivecraft.factions.entity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.massivecore.Aspect;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.ps.PSBuilder;
import com.massivecraft.massivecore.util.DiscUtil;
import com.massivecraft.massivecore.xlib.gson.reflect.TypeToken;

public class BoardColls extends XColls<BoardColl, Board> implements BoardInterface
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static BoardColls i = new BoardColls();
	public static BoardColls get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: COLLS
	// -------------------------------------------- //
	
	@Override
	public BoardColl createColl(String collName)
	{
		return new BoardColl(collName);
	}

	@Override
	public Aspect getAspect()
	{
		return Factions.get().getAspect();
	}
	
	@Override
	public String getBasename()
	{
		return Const.COLLECTION_BOARD;
	}
	
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
	
	// -------------------------------------------- //
	// OVERRIDE: BOARD
	// -------------------------------------------- //
	
	@Override
	public TerritoryAccess getTerritoryAccessAt(PS ps)
	{
		BoardColl coll = this.getForWorld(ps.getWorld());
		if (coll == null) return null;
		return coll.getTerritoryAccessAt(ps);
	}
	
	@Override
	public Faction getFactionAt(PS ps)
	{
		BoardColl coll = this.getForWorld(ps.getWorld());
		if (coll == null) return null;
		return coll.getFactionAt(ps);
	}
	
	// SET

	@Override
	public void setTerritoryAccessAt(PS ps, TerritoryAccess territoryAccess)
	{
		BoardColl coll = this.getForWorld(ps.getWorld());
		if (coll == null) return;
		coll.setTerritoryAccessAt(ps, territoryAccess);
	}
	
	@Override
	public void setFactionAt(PS ps, Faction faction)
	{
		BoardColl coll = this.getForWorld(ps.getWorld());
		if (coll == null) return;
		coll.setFactionAt(ps, faction);
	}
	
	// REMOVE
	
	@Override
	public void removeAt(PS ps)
	{
		BoardColl coll = this.getForWorld(ps.getWorld());
		if (coll == null) return;
		coll.removeAt(ps);
	}
	
	@Override
	public void removeAll(Faction faction)
	{
		for (BoardColl coll : this.getColls())
		{
			coll.removeAll(faction);
		}
	}
	
	@Override
	public void clean()
	{
		for (BoardColl coll : this.getColls())
		{
			coll.clean();
		}
	}
	
	// CHUNKS
	@Override
	public Set<PS> getChunks(Faction faction)
	{
		Set<PS> ret = new HashSet<PS>();
		for (BoardColl coll : this.getColls())
		{
			ret.addAll(coll.getChunks(faction));
		}
		return ret;
	}
	
	// COUNT
	@Override
	public int getCount(Faction faction)
	{
		int ret = 0;
		for (BoardColl coll : this.getColls())
		{
			ret += coll.getCount(faction);
		}
		return ret;
	}
	
	// NEARBY DETECTION
	
	@Override
	public boolean isBorderPs(PS ps)
	{
		BoardColl coll = this.getForWorld(ps.getWorld());
		if (coll == null) return false;
		return coll.isBorderPs(ps);
	}

	@Override
	public boolean isConnectedPs(PS ps, Faction faction)
	{
		BoardColl coll = this.getForWorld(ps.getWorld());
		if (coll == null) return false;
		return coll.isConnectedPs(ps, faction);
	}
	
	// MAP GENERATION
	
	@Override
	public ArrayList<String> getMap(RelationParticipator observer, PS centerPs, double inDegrees)
	{
		BoardColl coll = this.getForWorld(centerPs.getWorld());
		if (coll == null) return null;
		return coll.getMap(observer, centerPs, inDegrees);
	}
	
}
