package com.massivecraft.factions.integration.dynmap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PlayerSet;
import org.dynmap.utils.TileFlags;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.EngineAbstract;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

// This source code is a heavily modified version of mikeprimms plugin Dynmap-Factions.
public class EngineDynmap extends EngineAbstract
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public final static int BLOCKS_PER_CHUNK = 16;
	
	public final static String DYNMAP_INTEGRATION = Txt.parse("<h>Dynmap Integration: <i>");
	
	public final static String FACTIONS = "factions";
	public final static String FACTIONS_ = FACTIONS + "_";
	
	public final static String FACTIONS_MARKERSET = FACTIONS_ + "markerset";
	
	public final static String FACTIONS_HOME = FACTIONS_ + "home";
	public final static String FACTIONS_HOME_ = FACTIONS_HOME + "_";
	
	public final static String FACTIONS_PLAYERSET = FACTIONS_ + "playerset";
	public final static String FACTIONS_PLAYERSET_ = FACTIONS_PLAYERSET + "_";
	
	public final static String FACTIONS_AREA = FACTIONS_ + "area";
	public final static String FACTIONS_AREA_ = FACTIONS_AREA + "_";

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineDynmap i = new EngineDynmap();
	public static EngineDynmap get() { return i; }
	private EngineDynmap() {}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
	}
	
	@Override
	public Long getPeriod()
	{
		// Every 15 seconds
		return 15 * 20L;
	}
	
	@Override
	public boolean isSync()
	{
		return false;
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	public DynmapAPI dynmapApi;
	public MarkerAPI markerApi;
	public MarkerSet markerset;

	// -------------------------------------------- //
	// RUN: UPDATE
	// -------------------------------------------- //
	
	// Thread Safe / Asynchronous: Yes
	@Override
	public void run()
	{
		// Should we even use dynmap?
		if (!MConf.get().dynmapUse)
		{
			if (this.markerset != null)
			{
				this.markerset.deleteMarkerSet();
				this.markerset = null;
			}
			return;
		}
		
		long before = System.currentTimeMillis();
		
		// We do what we can here.
		// You /can/ run this method from the main server thread but it's not recommended at all.
		// This method is supposed to be run async to avoid locking the main server thread.
		final Map<String, TempMarker> homes = createHomes();
		final Map<String, TempAreaMarker> areas = createAreas();
		final Map<String, Set<String>> playerSets = createPlayersets();
		
		long after = System.currentTimeMillis();
		long duration = after-before;
		updateLog("Async", duration);
		
		// Shedule non thread safe sync at the end!
		Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				long before = System.currentTimeMillis();
				
				if (!updateCore()) return;
				
				// createLayer() is thread safe but it makes use of fields set in updateCore() so we must have it after. 
				if (!updateLayer(createLayer())) return;
				
				updateHomes(homes);
				updateAreas(areas);
				updatePlayersets(playerSets);
				
				long after = System.currentTimeMillis();
				long duration = after-before;
				updateLog("Sync", duration);
			}
		});
	}
	
	// Thread Safe / Asynchronous: Yes
	public static void updateLog(String name, long millis)
	{
		if (!MConf.get().dynmapUpdateLog) return;
		String message = Txt.parse("<i>%s took <h>%dms<i>.", "Faction Dynmap " + name, millis);
		Factions.get().log(message);
	}

	// -------------------------------------------- //
	// UPDATE: CORE
	// -------------------------------------------- //
	
	// Thread Safe / Asynchronous: No
	public boolean updateCore()
	{
		// Get DynmapAPI
		this.dynmapApi = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
		if (this.dynmapApi == null)
		{
			severe("Could not retrieve the DynmapAPI.");
			return false;
		}
		
		// Get MarkerAPI
		this.markerApi = this.dynmapApi.getMarkerAPI();
		if (this.markerApi == null)
		{
			severe("Could not retrieve the MarkerAPI.");
			return false;
		}
		
		return true;
	}
	
	// -------------------------------------------- //
	// UPDATE: Layer
	// -------------------------------------------- //
	
	// Thread Safe / Asynchronous: Yes
	public TempMarkerSet createLayer()
	{
		TempMarkerSet ret = new TempMarkerSet();
		ret.label = MConf.get().dynmapLayerName;
		ret.minimumZoom = MConf.get().dynmapLayerMinimumZoom;
		ret.priority = MConf.get().dynmapLayerPriority;
		ret.hideByDefault = !MConf.get().dynmapLayerVisible;
		return ret;
	}
	
	// Thread Safe / Asynchronous: No
	public boolean updateLayer(TempMarkerSet temp)
	{
		this.markerset = this.markerApi.getMarkerSet(FACTIONS_MARKERSET);
		if (this.markerset == null)
		{
			this.markerset = temp.create(this.markerApi, FACTIONS_MARKERSET);
			if (this.markerset == null)
			{
				severe("Could not create the Faction Markerset/Layer");
				return false;
			}
		}
		else
		{
			temp.update(this.markerApi, this.markerset);
		}
		return true;
	}

	// -------------------------------------------- //
	// UPDATE: HOMES
	// -------------------------------------------- //
	
	// Thread Safe / Asynchronous: Yes
	public Map<String, TempMarker> createHomes()
	{
		Map<String, TempMarker> ret = new HashMap<String, TempMarker>();
		
		// Loop current factions
		for (FactionColl coll : FactionColls.get().getColls())
		{
			for (Faction faction : coll.getAll())
			{
				PS ps = faction.getHome();
				if (ps == null) continue;
				
				DynmapStyle style = getStyle(faction);
				
				String markerId = FACTIONS_HOME_ + faction.getId();
				
				TempMarker temp = new TempMarker();
				temp.label = ChatColor.stripColor(faction.getName());
				temp.world = ps.getWorld();
				temp.x = ps.getLocationX();
				temp.y = ps.getLocationY();
				temp.z = ps.getLocationZ();
				temp.iconName = style.getHomeMarker();
				temp.description = getDescription(faction);
				
				ret.put(markerId, temp);
			}
		}
		
		return ret;
	}
	
	// Thread Safe / Asynchronous: No
	// This method places out the faction home markers into the factions markerset.
	public void updateHomes(Map<String, TempMarker> homes)
	{
		// Put all current faction markers in a map
		Map<String, Marker> markers = new HashMap<String, Marker>();
		for (Marker marker : this.markerset.getMarkers())
		{
			markers.put(marker.getMarkerID(), marker);
		}
		
		// Loop homes
		for (Entry<String, TempMarker> entry : homes.entrySet())
		{
			String markerId = entry.getKey();
			TempMarker temp = entry.getValue();
			
			// Get Creative
			// NOTE: I remove from the map created just in the beginning of this method.
			// NOTE: That way what is left at the end will be outdated markers to remove.
			Marker marker = markers.remove(markerId);
			if (marker == null)
			{
				marker = temp.create(this.markerApi, this.markerset, markerId);
				if (marker == null)
				{
					EngineDynmap.severe("Could not get/create the home marker " + markerId);
				}
			}
			else
			{
				temp.update(this.markerApi, this.markerset, marker);
			}
		}
		
		// Delete Deprecated Markers
		// Only old markers should now be left
		for (Marker marker : markers.values())
		{
			marker.deleteMarker();
		}
	}
	
	// -------------------------------------------- //
	// UPDATE: AREAS
	// -------------------------------------------- //
	
	// Thread Safe: YES
	public Map<String, TempAreaMarker> createAreas()
	{
		Map<String, Map<Faction, Set<PS>>> worldFactionChunks = createWorldFactionChunks();
		return createAreas(worldFactionChunks);
	}
	
	// Thread Safe: YES
	public Map<String, Map<Faction, Set<PS>>> createWorldFactionChunks()
	{
		// Create map "world name --> faction --> set of chunk coords"
		Map<String, Map<Faction, Set<PS>>> worldFactionChunks = new HashMap<String, Map<Faction, Set<PS>>>();
		for (BoardColl coll : BoardColls.get().getColls())
		{
			// Note: The board is the world. The board id is the world name.
			for (Board board : coll.getAll())
			{
				String world = board.getId();
				
				// Get the factionChunks creatively.
				Map<Faction, Set<PS>> factionChunks = worldFactionChunks.get(world);
				if (factionChunks == null)
				{
					factionChunks = new HashMap<Faction, Set<PS>>();
					worldFactionChunks.put(world, factionChunks);
				}
						
				// Populate the factionChunks
				for (Entry<PS, TerritoryAccess> entry : board.getMap().entrySet())
				{
					PS chunk = entry.getKey();
					TerritoryAccess territoryAccess = entry.getValue();
					String factionId = territoryAccess.getHostFactionId();
					Faction faction = FactionColls.get().getForWorld(world).get(factionId);
					if (faction == null) continue;
					
					// Get the chunks creatively.
					Set<PS> chunks = factionChunks.get(faction);
					if (chunks == null)
					{
						chunks = new HashSet<PS>();
						factionChunks.put(faction, chunks);
					}
					
					chunks.add(chunk);
				}
			}
		}
		return worldFactionChunks;
	}
	
	// Thread Safe: YES
	public Map<String, TempAreaMarker> createAreas(Map<String, Map<Faction, Set<PS>>> worldFactionChunks)
	{
		Map<String, TempAreaMarker> ret = new HashMap<String, TempAreaMarker>();
		
		// For each world
		for (Entry<String, Map<Faction, Set<PS>>> entry : worldFactionChunks.entrySet())
		{
			String world = entry.getKey();
			Map<Faction, Set<PS>> factionChunks = entry.getValue();
			
			// For each faction and its chunks in that world
			for (Entry<Faction, Set<PS>> entry1 : factionChunks.entrySet())
			{
				Faction faction = entry1.getKey();
				Set<PS> chunks = entry1.getValue();
				Map<String, TempAreaMarker> worldFactionMarkers = createAreas(world, faction, chunks);
				ret.putAll(worldFactionMarkers);
			}
		}
		
		return ret;
	}

	// Thread Safe: YES
	// Handle specific faction on specific world
	// "handle faction on world"
	public Map<String, TempAreaMarker> createAreas(String world, Faction faction, Set<PS> chunks)
	{	
		Map<String, TempAreaMarker> ret = new HashMap<String, TempAreaMarker>();
		
		// If the faction is visible ...
		if (!isVisible(faction, world)) return ret;
		
		// ... and has any chunks ...
		if (chunks.isEmpty()) return ret;
		
		// Index of polygon for given faction
		int markerIndex = 0; 

		// Create the info window
		String description = getDescription(faction);
		
		// Fetch Style
		DynmapStyle style = this.getStyle(faction);
		
		// Loop through chunks: set flags on chunk map
		TileFlags allChunkFlags = new TileFlags();
		LinkedList<PS> allChunks = new LinkedList<PS>();
		for (PS chunk : chunks)
		{
			allChunkFlags.setFlag(chunk.getChunkX(), chunk.getChunkZ(), true); // Set flag for chunk
			allChunks.addLast(chunk);
		}
		
		// Loop through until we don't find more areas
		while (allChunks != null)
		{
			TileFlags ourChunkFlags = null;
			LinkedList<PS> ourChunks = null;
			LinkedList<PS> newChunks = null;
			
			int minimumX = Integer.MAX_VALUE;
			int minimumZ = Integer.MAX_VALUE;
			for (PS chunk : allChunks)
			{
				int chunkX = chunk.getChunkX();
				int chunkZ = chunk.getChunkZ();
				
				// If we need to start shape, and this block is not part of one yet
				if (ourChunkFlags == null && allChunkFlags.getFlag(chunkX, chunkZ))
				{
					ourChunkFlags = new TileFlags(); // Create map for shape
					ourChunks = new LinkedList<PS>();
					floodFillTarget(allChunkFlags, ourChunkFlags, chunkX, chunkZ); // Copy shape
					ourChunks.add(chunk); // Add it to our chunk list
					minimumX = chunkX;
					minimumZ = chunkZ;
				}
				// If shape found, and we're in it, add to our node list
				else if (ourChunkFlags != null && ourChunkFlags.getFlag(chunkX, chunkZ))
				{
					ourChunks.add(chunk);
					if (chunkX < minimumX)
					{
						minimumX = chunkX;
						minimumZ = chunkZ;
					}
					else if (chunkX == minimumX && chunkZ < minimumZ)
					{
						minimumZ = chunkZ;
					}
				}
				// Else, keep it in the list for the next polygon
				else
				{
					if (newChunks == null) newChunks = new LinkedList<PS>();
					newChunks.add(chunk);
				}
			}
			
			// Replace list (null if no more to process)
			allChunks = newChunks;
			
			if (ourChunkFlags == null) continue;

			// Trace outline of blocks - start from minx, minz going to x+
			int initialX = minimumX;
			int initialZ = minimumZ;
			int currentX = minimumX;
			int currentZ = minimumZ;
			Direction direction = Direction.XPLUS;
			ArrayList<int[]> linelist = new ArrayList<int[]>();
			linelist.add(new int[]{ initialX, initialZ }); // Add start point
			while ((currentX != initialX) || (currentZ != initialZ) || (direction != Direction.ZMINUS))
			{
				switch (direction)
				{
					case XPLUS: // Segment in X+ direction
						if (!ourChunkFlags.getFlag(currentX + 1, currentZ))
						{ // Right turn?
							linelist.add(new int[]{ currentX + 1, currentZ }); // Finish line
							direction = Direction.ZPLUS; // Change direction
						}
						else if (!ourChunkFlags.getFlag(currentX + 1, currentZ - 1))
						{ // Straight?
							currentX++;
						}
						else
						{ // Left turn
							linelist.add(new int[]{ currentX + 1, currentZ }); // Finish line
							direction = Direction.ZMINUS;
							currentX++;
							currentZ--;
						}
					break;
					case ZPLUS: // Segment in Z+ direction
						if (!ourChunkFlags.getFlag(currentX, currentZ + 1))
						{ // Right turn?
							linelist.add(new int[]{ currentX + 1, currentZ + 1 }); // Finish line
							direction = Direction.XMINUS; // Change direction
						}
						else if (!ourChunkFlags.getFlag(currentX + 1, currentZ + 1))
						{ // Straight?
							currentZ++;
						}
						else
						{ // Left turn
							linelist.add(new int[]{ currentX + 1, currentZ + 1 }); // Finish line
							direction = Direction.XPLUS;
							currentX++;
							currentZ++;
						}
					break;
					case XMINUS: // Segment in X- direction
						if (!ourChunkFlags.getFlag(currentX - 1, currentZ))
						{ // Right turn?
							linelist.add(new int[]{ currentX, currentZ + 1 }); // Finish line
							direction = Direction.ZMINUS; // Change direction
						}
						else if (!ourChunkFlags.getFlag(currentX - 1, currentZ + 1))
						{ // Straight?
							currentX--;
						}
						else
						{ // Left turn
							linelist.add(new int[] { currentX, currentZ + 1 }); // Finish line
							direction = Direction.ZPLUS;
							currentX--;
							currentZ++;
						}
					break;
					case ZMINUS: // Segment in Z- direction
						if (!ourChunkFlags.getFlag(currentX, currentZ - 1))
						{ // Right turn?
							linelist.add(new int[]{ currentX, currentZ }); // Finish line
							direction = Direction.XPLUS; // Change direction
						}
						else if (!ourChunkFlags.getFlag(currentX - 1, currentZ - 1))
						{ // Straight?
							currentZ--;
						}
						else
						{ // Left turn
							linelist.add(new int[] { currentX, currentZ }); // Finish line
							direction = Direction.XMINUS;
							currentX--;
							currentZ--;
						}
					break;
				}
			}
			
			int sz = linelist.size();
			double[] x = new double[sz];
			double[] z = new double[sz];
			for (int i = 0; i < sz; i++)
			{
				int[] line = linelist.get(i);
				x[i] = (double) line[0] * (double) BLOCKS_PER_CHUNK;
				z[i] = (double) line[1] * (double) BLOCKS_PER_CHUNK;
			}
			
			// Build information for specific area
			String markerId = FACTIONS_ + world + "__" + faction.getId() + "__" + markerIndex;
			
			TempAreaMarker temp = new TempAreaMarker();
			temp.label = faction.getName();
			temp.world = world;
			temp.x = x;
			temp.z = z;
			temp.description = description;
			
			temp.lineColor = style.getLineColor();
			temp.lineOpacity = style.getLineOpacity();
			temp.lineWeight = style.getLineWeight();
			
			temp.fillColor = style.getFillColor();
			temp.fillOpacity = style.getFillOpacity();
			
			temp.boost = style.getBoost();
			
			ret.put(markerId, temp);
			
			markerIndex++;
		}
		
		return ret;
	}
	
	// Thread Safe: NO
	public void updateAreas(Map<String, TempAreaMarker> areas)
	{
		// Map Current
		Map<String, AreaMarker> markers = new HashMap<String, AreaMarker>();
		for (AreaMarker marker : this.markerset.getAreaMarkers())
		{
			markers.put(marker.getMarkerID(), marker);
		}
		
		// Loop New
		for (Entry<String, TempAreaMarker> entry : areas.entrySet())
		{
			String markerId = entry.getKey();
			TempAreaMarker temp = entry.getValue();
			
			// Get Creative
			// NOTE: I remove from the map created just in the beginning of this method.
			// NOTE: That way what is left at the end will be outdated markers to remove.
			AreaMarker marker = markers.remove(markerId);
			if (marker == null)
			{
				marker = temp.create(this.markerApi, this.markerset, markerId);
				if (marker == null)
				{
					severe("Could not get/create the area marker " + markerId);
				}
			}
			else
			{
				temp.update(this.markerApi, this.markerset, marker);
			}
		}
		
		// Only old/outdated should now be left. Delete them.
		for (AreaMarker marker : markers.values())
		{
			marker.deleteMarker();
		}
	}

	// -------------------------------------------- //
	// UPDATE: PLAYERSET
	// -------------------------------------------- //

	// Thread Safe / Asynchronous: Yes
	public String createPlayersetId(Faction faction)
	{
		if (faction == null) return null;
		if (faction.isNone()) return null;
		String factionId = faction.getId();
		if (factionId == null) return null;
		return FACTIONS_PLAYERSET_ + factionId;
	}
	
	// Thread Safe / Asynchronous: Yes
	public Set<String> createPlayerset(Faction faction)
	{
		if (faction == null) return null;
		if (faction.isNone()) return null;
		
		Set<String> ret = new HashSet<String>();
		
		for (UPlayer uplayer : faction.getUPlayers())
		{
			// NOTE: We add both UUID and name. This might be a good idea for future proofing.
			ret.add(uplayer.getId());
			ret.add(uplayer.getName());
		}
		
		return ret;
	}
	
	// Thread Safe / Asynchronous: Yes
	public Map<String, Set<String>> createPlayersets()
	{
		if (!MConf.get().dynmapVisibilityByFaction) return null;
		
		Map<String, Set<String>> ret = new HashMap<String, Set<String>>();

		for (FactionColl coll : FactionColls.get().getColls())
		{
			for (Faction faction : coll.getAll())
			{
				String playersetId = createPlayersetId(faction);
				if (playersetId == null) continue;
				Set<String> playerIds = createPlayerset(faction);
				if (playerIds == null) continue;
				ret.put(playersetId, playerIds);
			}
		}
		
		return ret;
	}
	
	// Thread Safe / Asynchronous: No
	public void updatePlayersets(Map<String, Set<String>> playersets)
	{
		// Remove
		for (PlayerSet set : this.markerApi.getPlayerSets())
		{
			if (!set.getSetID().startsWith(FACTIONS_PLAYERSET_)) continue;
			
			// (Null means remove all)
			if (playersets != null && playersets.containsKey(set.getSetID())) continue;
			
			set.deleteSet();
		}
		
		// Add / Update
		for (Entry<String, Set<String>> entry : playersets.entrySet())
		{
			// Extract from Entry
			String setId = entry.getKey();
			Set<String> playerIds = entry.getValue();
			
			// Get Creatively
			PlayerSet set = this.markerApi.getPlayerSet(setId);
			if (set == null) set = this.markerApi.createPlayerSet(
				setId, // id
				true, // symmetric
				playerIds, // players
				false // persistent
			);
			if (set == null)
			{
				severe("Could not get/create the player set " + setId);
				continue;
			}
			
			// Set Content
			set.setPlayers(playerIds);
		}
	}
	
	// -------------------------------------------- //
	// UTIL & SHARED
	// -------------------------------------------- //
	
	// Thread Safe / Asynchronous: Yes
	private String getDescription(Faction faction)
	{
		String ret = "<div class=\"regioninfo\">" + MConf.get().dynmapDescription + "</div>";
		
		// Name
		String name = faction.getName();
		name = ChatColor.stripColor(name);
		name = escapeHtml(name);
		ret = ret.replace("%name%", name);
		
		// Description
		String description = faction.getDescription();
		description = ChatColor.stripColor(description);
		description = escapeHtml(description);
		ret = ret.replace("%description%", description);
		
		// Age
		long ageMillis = faction.getCreatedAtMillis() - System.currentTimeMillis();
		LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillisSecondsAndMinutes()), 3);
		String age = TimeDiffUtil.formatedVerboose(ageUnitcounts, "");
		age = ChatColor.stripColor(age);
		ret = ret.replace("%age%", age);
		
		// Money
		String money = "unavailable";
		if (UConf.get(faction).bankEnabled && MConf.get().dynmapDescriptionMoney)
		{
			money = Money.format(Money.get(faction));
		}
		ret = ret.replace("%money%", money);
		
		// Flags and Open
		Map<String, Boolean> flags = new HashMap<String, Boolean>();
		flags.put("open", faction.isOpen());
		for (FFlag fflag : FFlag.values())
		{
			flags.put(fflag.getNicename(), faction.getFlag(fflag));
		}
		for (Entry<String, Boolean> entry : flags.entrySet())
		{
			String flag = entry.getKey();
			boolean value = entry.getValue();
			
			String bool = String.valueOf(value);
			String color = boolcolor(flag, value);
			String boolcolor = boolcolor(String.valueOf(value), value);
			
			ret = ret.replace("%" + flag + ".bool%", bool);
			ret = ret.replace("%" + flag + ".color%", color);
			ret = ret.replace("%" + flag + ".boolcolor%", boolcolor);
		}
		
		// Players
		List<UPlayer> playersList = faction.getUPlayers();
		String playersCount = String.valueOf(playersList.size());
		String players = getPlayerString(playersList);
		
		UPlayer playersLeaderObject = faction.getLeader();
		String playersLeader = getPlayerName(playersLeaderObject);
		
		List<UPlayer> playersOfficersList = faction.getUPlayersWhereRole(Rel.OFFICER);
		String playersOfficersCount = String.valueOf(playersOfficersList.size());
		String playersOfficers = getPlayerString(playersOfficersList);
		
		List<UPlayer> playersMembersList = faction.getUPlayersWhereRole(Rel.MEMBER);
		String playersMembersCount = String.valueOf(playersMembersList.size());
		String playersMembers = getPlayerString(playersMembersList);
		
		List<UPlayer> playersRecruitsList = faction.getUPlayersWhereRole(Rel.RECRUIT);
		String playersRecruitsCount = String.valueOf(playersRecruitsList.size());
		String playersRecruits = getPlayerString(playersRecruitsList);
		
		
		ret = ret.replace("%players%", players);
		ret = ret.replace("%players.count%", playersCount);
		ret = ret.replace("%players.leader%", playersLeader);
		ret = ret.replace("%players.officers%", playersOfficers);
		ret = ret.replace("%players.officers.count%", playersOfficersCount);
		ret = ret.replace("%players.members%", playersMembers);
		ret = ret.replace("%players.members.count%", playersMembersCount);
		ret = ret.replace("%players.recruits%", playersRecruits);
		ret = ret.replace("%players.recruits.count%", playersRecruitsCount);
		
		return ret;
	}
	
	public static String getPlayerString(List<UPlayer> uplayers)
	{
		String ret = "";
		for (UPlayer uplayer : uplayers)
		{
			if (ret.length() > 0) ret += ", ";
			ret += getPlayerName(uplayer);
		}
		return ret;
	}
	
	public static String getPlayerName(UPlayer uplayer)
	{
		if (uplayer == null) return "none";
		return escapeHtml(uplayer.getName());
	}
	
	public static String boolcolor(String string, boolean bool)
	{
		return "<span style=\"color: " + (bool ? "#008000" : "#800000") + ";\">" + string + "</span>";
	}
	
	public static String escapeHtml(String string)
	{
		StringBuilder out = new StringBuilder(Math.max(16, string.length()));
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&')
			{
				out.append("&#");
				out.append((int) c);
				out.append(';');
			}
			else
			{
				out.append(c);
			}
		}
		return out.toString();
	}

	// Thread Safe / Asynchronous: Yes
	private boolean isVisible(Faction faction, String world)
	{
		if (faction == null) return false;
		final String factionId = faction.getId();
		if (factionId == null) return false;
		final String factionName = faction.getName();
		if (factionName == null) return false;
		
		Set<String> visible = MConf.get().dynmapVisibleFactions;
		Set<String> hidden = MConf.get().dynmapHiddenFactions;
		
		if (visible.size() > 0)
		{
			if (!visible.contains(factionId) && !visible.contains(factionName) && !visible.contains("world:" + world))
			{
				return false;
			}
		}

		if (hidden.size() > 0)
		{
			if (hidden.contains(factionId) || hidden.contains(factionName) || hidden.contains("world:" + world))
			{
				return false;
			}
		}

		return true;
	}
	
	// Thread Safe / Asynchronous: Yes
	public DynmapStyle getStyle(Faction faction)
	{
		DynmapStyle ret;

		ret = MConf.get().dynmapFactionStyles.get(faction.getId());
		if (ret != null) return ret;

		ret = MConf.get().dynmapFactionStyles.get(faction.getName());
		if (ret != null) return ret;

		return MConf.get().dynmapDefaultStyle;
	}

	// Thread Safe / Asynchronous: Yes
	public static void info(String msg)
	{
		String message = DYNMAP_INTEGRATION + msg;
		Factions.get().log(message);
	}

	// Thread Safe / Asynchronous: Yes
	public static void severe(String msg)
	{
		String message = DYNMAP_INTEGRATION + ChatColor.RED.toString() + msg;
		Factions.get().log(message);
	}
	
	enum Direction
	{
		XPLUS, ZPLUS, XMINUS, ZMINUS
	};

	// Find all contiguous blocks, set in target and clear in source
	private int floodFillTarget(TileFlags source, TileFlags destination, int x, int y)
	{
		int cnt = 0;
		ArrayDeque<int[]> stack = new ArrayDeque<int[]>();
		stack.push(new int[] { x, y });

		while (stack.isEmpty() == false)
		{
			int[] nxt = stack.pop();
			x = nxt[0];
			y = nxt[1];
			if (source.getFlag(x, y))
			{ // Set in src
				source.setFlag(x, y, false); // Clear source
				destination.setFlag(x, y, true); // Set in destination
				cnt++;
				if (source.getFlag(x + 1, y)) stack.push(new int[] { x + 1, y });
				if (source.getFlag(x - 1, y)) stack.push(new int[] { x - 1, y });
				if (source.getFlag(x, y + 1)) stack.push(new int[] { x, y + 1 });
				if (source.getFlag(x, y - 1)) stack.push(new int[] { x, y - 1 });
			}
		}
		return cnt;
	}
	
	
	
}
