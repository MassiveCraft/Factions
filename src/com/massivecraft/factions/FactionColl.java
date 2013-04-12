package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

import com.massivecraft.mcore.store.Coll;
import com.massivecraft.mcore.store.MStore;
import com.massivecraft.mcore.util.DiscUtil;
import com.massivecraft.mcore.util.Txt;
import com.massivecraft.mcore.xlib.gson.reflect.TypeToken;

import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.MiscUtil;

public class FactionColl extends Coll<Faction, String>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionColl i = new FactionColl();
	public static FactionColl get() { return i; }
	private FactionColl()
	{
		super(MStore.getDb(ConfServer.dburi), Factions.get(), "ai", Const.COLLECTION_BASENAME_FACTION, Faction.class, String.class, false);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COLL
	// -------------------------------------------- //
	
	@Override
	public void init()
	{
		super.init();
		
		this.migrate();
		
		this.createDefaultFactions();
		
		// TODO: Refactor and fix with the member-index.
		// populate all faction player lists
		// or as you also could describe it: Reindex of Members
		for (Faction faction : this.getAll())
		{
			faction.refreshFPlayers();
		}		
	}
	
	// TODO: REMEMBER: Deciding on the next AI value is part of the migration routine.
	public void migrate()
	{
		// Create file objects
		File oldFile = new File(Factions.get().getDataFolder(), "factions.json");
		File newFile = new File(Factions.get().getDataFolder(), "factions.json.migrated");
		
		// Already migrated?
		if ( ! oldFile.exists()) return;
		
		// Read the file content through GSON. 
		Type type = new TypeToken<Map<String, Faction>>(){}.getType();
		Map<String, Faction> id2faction = Factions.get().gson.fromJson(DiscUtil.readCatch(oldFile), type);
		
		// Set the data
		for (Entry<String, Faction> entry : id2faction.entrySet())
		{
			String factionId = entry.getKey();
			Faction faction = entry.getValue();
			FactionColl.get().create(factionId).load(faction);
		}
		
		// Mark as migrated
		oldFile.renameTo(newFile);
	}
	
	@Override
	public Faction detachId(Object oid)
	{
		String accountId = this.get(oid).getAccountId();
		
		Faction ret = super.detachId(oid);
		
		if (Econ.shouldBeUsed())
		{
			Econ.setBalance(accountId, 0);
		}
		
		// Clean the board
		// TODO: Use events for this instead?
		BoardColl.get().clean();
		
		// Clean the fplayers
		FPlayerColl.get().clean();
		
		return ret;
	}
	
	// -------------------------------------------- //
	// GET
	// -------------------------------------------- //
	
	// TODO: I hope this one is not crucial anymore.
	// If it turns out to be I will just have to recreate the feature in the proper place.
	/*
	@Override
	public Faction get(String id)
	{
		if ( ! this.exists(id))
		{
			Factions.get().log(Level.WARNING, "Non existing factionId "+id+" requested! Issuing cleaning!");
			BoardColl.get().clean();
			FPlayerColl.get().clean();
		}
		
		return super.get(id);
	}
	*/
	
	public Faction getNone()
	{
		return this.get(Const.FACTIONID_NONE);
	}
	
	/*
	private FactionColl()
	{
		super
		(
			Faction.class,
			new CopyOnWriteArrayList<Faction>(),
			new ConcurrentHashMap<String, Faction>(),
			new File(Factions.get().getDataFolder(), "factions.json"),
			Factions.get().gson
		);
	}
	
	@Override
	public Type getMapType()
	{
		return new TypeToken<Map<String, Faction>>(){}.getType();
	}
	
	*/
	
	
	
	// -------------------------------------------- //
	// FACTION TAG
	// -------------------------------------------- //
	
	public static ArrayList<String> validateTag(String str)
	{
		ArrayList<String> errors = new ArrayList<String>();
		
		if(MiscUtil.getComparisonString(str).length() < ConfServer.factionTagLengthMin)
		{
			errors.add(Txt.parse("<i>The faction tag can't be shorter than <h>%s<i> chars.", ConfServer.factionTagLengthMin));
		}
		
		if(str.length() > ConfServer.factionTagLengthMax)
		{
			errors.add(Txt.parse("<i>The faction tag can't be longer than <h>%s<i> chars.", ConfServer.factionTagLengthMax));
		}
		
		for (char c : str.toCharArray())
		{
			if ( ! MiscUtil.substanceChars.contains(String.valueOf(c)))
			{
				errors.add(Txt.parse("<i>Faction tag must be alphanumeric. \"<h>%s<i>\" is not allowed.", c));
			}
		}
		
		return errors;
	}
	
	public Faction getByTag(String str)
	{
		String compStr = MiscUtil.getComparisonString(str);
		for (Faction faction : this.getAll())
		{
			if (faction.getComparisonTag().equals(compStr))
			{
				return faction;
			}
		}
		return null;
	}
	
	public Faction getBestTagMatch(String searchFor)
	{
		Map<String, Faction> tag2faction = new HashMap<String, Faction>();
		
		// TODO: Slow index building
		for (Faction faction : this.getAll())
		{
			tag2faction.put(ChatColor.stripColor(faction.getTag()), faction);
		}
		
		String tag = Txt.getBestCIStart(tag2faction.keySet(), searchFor);
		if (tag == null) return null;
		return tag2faction.get(tag);
	}
	
	public boolean isTagTaken(String str)
	{
		return this.getByTag(str) != null;
	}

	public void econLandRewardRoutine()
	{
		if ( ! Econ.shouldBeUsed()) return;

		Factions.get().log("Running econLandRewardRoutine...");
		for (Faction faction : this.getAll())
		{
			int landCount = faction.getLandRounded();
			if (!faction.getFlag(FFlag.PEACEFUL) && landCount > 0)
			{
				Set<FPlayer> players = faction.getFPlayers();
				int playerCount = players.size();
				double reward = ConfServer.econLandReward * landCount / playerCount;
				for (FPlayer player : players)
				{
					Econ.modifyMoney(player, reward, "to own faction land", "for faction owning " + landCount + " land divided among " + playerCount + " member(s)");
				}
			}
		}
	}
	
	// -------------------------------------------- //
	// CREATE DEFAULT FACTIONS
	// -------------------------------------------- //
	
	public void createDefaultFactions()
	{
		this.createNoneFaction();
		this.createSafeZoneFaction();
		this.createWarZoneFaction();
	}
	
	public void createNoneFaction()
	{
		if (this.containsId(Const.FACTIONID_NONE)) return;
		
		Faction faction = this.create(Const.FACTIONID_NONE);
		
		faction.setTag(ChatColor.DARK_GREEN+"Wilderness");
		faction.setDescription("");
		faction.setOpen(false);
		
		faction.setFlag(FFlag.PERMANENT, true);
		faction.setFlag(FFlag.PEACEFUL, false);
		faction.setFlag(FFlag.INFPOWER, true);
		faction.setFlag(FFlag.POWERLOSS, true);
		faction.setFlag(FFlag.PVP, true);
		faction.setFlag(FFlag.FRIENDLYFIRE, false);
		faction.setFlag(FFlag.MONSTERS, true);
		faction.setFlag(FFlag.EXPLOSIONS, true);
		faction.setFlag(FFlag.FIRESPREAD, true);
		//faction.setFlag(FFlag.LIGHTNING, true);
		faction.setFlag(FFlag.ENDERGRIEF, true);
		
		faction.setPermittedRelations(FPerm.BUILD, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
	}
	
	public void createSafeZoneFaction()
	{
		if (this.containsId(Const.FACTIONID_SAFEZONE)) return;
		
		Faction faction = this.create(Const.FACTIONID_SAFEZONE);
		
		faction.setTag("SafeZone");
		faction.setDescription("Free from PVP and monsters");
		faction.setOpen(false);
		
		faction.setFlag(FFlag.PERMANENT, true);
		faction.setFlag(FFlag.PEACEFUL, true);
		faction.setFlag(FFlag.INFPOWER, true);
		faction.setFlag(FFlag.POWERLOSS, false);
		faction.setFlag(FFlag.PVP, false);
		faction.setFlag(FFlag.FRIENDLYFIRE, false);
		faction.setFlag(FFlag.MONSTERS, false);
		faction.setFlag(FFlag.EXPLOSIONS, false);
		faction.setFlag(FFlag.FIRESPREAD, false);
		//faction.setFlag(FFlag.LIGHTNING, false);
		faction.setFlag(FFlag.ENDERGRIEF, false);
		
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.TERRITORY, Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
	}
	
	public void createWarZoneFaction()
	{
		if (this.containsId(Const.FACTIONID_WARZONE)) return;
		
		Faction faction = this.create(Const.FACTIONID_WARZONE);
		
		faction.setTag("WarZone");
		faction.setDescription("Not the safest place to be");
		faction.setOpen(false);
		
		faction.setFlag(FFlag.PERMANENT, true);
		faction.setFlag(FFlag.PEACEFUL, true);
		faction.setFlag(FFlag.INFPOWER, true);
		faction.setFlag(FFlag.POWERLOSS, true);
		faction.setFlag(FFlag.PVP, true);
		faction.setFlag(FFlag.FRIENDLYFIRE, true);
		faction.setFlag(FFlag.MONSTERS, true);
		faction.setFlag(FFlag.EXPLOSIONS, true);
		faction.setFlag(FFlag.FIRESPREAD, true);
		//faction.setFlag(FFlag.LIGHTNING, true);
		faction.setFlag(FFlag.ENDERGRIEF, true);
		
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.TERRITORY, Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
	}

}
