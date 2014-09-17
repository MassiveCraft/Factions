package com.massivecraft.factions.entity;

import java.util.*;

import org.bukkit.ChatColor;

import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.util.Txt;
import com.massivecraft.factions.Const;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.MiscUtil;

public class FactionColl extends Coll<Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionColl i = new FactionColl();
	public static FactionColl get() { return i; }
	private FactionColl()
	{
		super(Const.COLLECTION_FACTION, Faction.class, MStore.getDb(), Factions.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COLL
	// -------------------------------------------- //
	
	@Override
	public void init()
	{
		super.init();
		
		this.createSpecialFactions();
	}
	
	@Override
	public Faction get(Object oid)
	{
		Faction ret = super.get(oid);
		
		// We should only trigger automatic clean if the whole database system is initialized.
		// A cleaning can only be successful if all data is available.
		// Example Reason: When creating the special factions for the first time "createSpecialFactions" a clean would be triggered otherwise.
		if (ret == null && Factions.get().isDatabaseInitialized())
		{
			String message = Txt.parse("<b>Non existing factionId <h>%s <b>requested. <i>Cleaning all boards and mplayers.", this.fixId(oid));
			Factions.get().log(message);
			
			BoardColl.get().clean();
			MPlayerColl.get().clean();
		}
		
		return ret;
	}
	
	// -------------------------------------------- //
	// INDEX
	// -------------------------------------------- //
	
	public void reindexMPlayers()
	{
		for (Faction faction : this.getAll())
		{
			faction.reindexMPlayers();
		}
	}
	
	// -------------------------------------------- //
	// SPECIAL FACTIONS
	// -------------------------------------------- //
	
	public void createSpecialFactions()
	{
		this.getNone();
		this.getSafezone();
		this.getWarzone();
	}
	
	public Faction getNone()
	{
		String id = MConf.get().factionIdNone;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.create(id);
		
		faction.setName(ChatColor.DARK_GREEN+"Wilderness");
		faction.setDescription(null);
		faction.setOpen(false);
		
		faction.setFlag(FFlag.PERMANENT, true);
		faction.setFlag(FFlag.PEACEFUL, false);
		faction.setFlag(FFlag.INFPOWER, true);
		faction.setFlag(FFlag.POWERLOSS, true);
		faction.setFlag(FFlag.PVP, true);
		faction.setFlag(FFlag.FRIENDLYFIRE, false);
		faction.setFlag(FFlag.MONSTERS, true);
		faction.setFlag(FFlag.EXPLOSIONS, true);
		faction.setFlag(FFlag.OFFLINE_EXPLOSIONS, true);
		faction.setFlag(FFlag.FIRESPREAD, true);
		faction.setFlag(FFlag.ENDERGRIEF, true);
		
		faction.setPermittedRelations(FPerm.BUILD, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		
		return faction;
	}
	
	public Faction getSafezone()
	{
		String id = MConf.get().factionIdSafezone;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.create(id);
		
		faction.setName("SafeZone");
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
		faction.setFlag(FFlag.OFFLINE_EXPLOSIONS, false);
		faction.setFlag(FFlag.FIRESPREAD, false);
		faction.setFlag(FFlag.ENDERGRIEF, false);
		
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.TERRITORY, Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
		
		return faction;
	}
	
	public Faction getWarzone()
	{
		String id = MConf.get().factionIdWarzone;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.create(id);
		
		faction.setName("WarZone");
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
		faction.setFlag(FFlag.OFFLINE_EXPLOSIONS, true);
		faction.setFlag(FFlag.FIRESPREAD, true);
		faction.setFlag(FFlag.ENDERGRIEF, true);
		
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.TERRITORY, Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
		
		return faction;
	}
	
	// -------------------------------------------- //
	// LAND REWARD
	// -------------------------------------------- //
	
	public void econLandRewardRoutine()
	{
		if (!Econ.isEnabled()) return;
		
		double econLandReward = MConf.get().econLandReward;
		if (econLandReward == 0.0) return;
		
		Factions.get().log("Running econLandRewardRoutine...");
		for (Faction faction : this.getAll())
		{
			int landCount = faction.getLandCount();
			if (!faction.getFlag(FFlag.PEACEFUL) && landCount > 0)
			{
				List<MPlayer> players = faction.getMPlayers();
				int playerCount = players.size();
				double reward = econLandReward * landCount / playerCount;
				for (MPlayer player : players)
				{
					Econ.modifyMoney(player, reward, "own " + landCount + " faction land divided among " + playerCount + " members");
				}
			}
		}
	}
	
	// -------------------------------------------- //
	// FACTION NAME
	// -------------------------------------------- //
	
	public ArrayList<String> validateName(String str)
	{
		ArrayList<String> errors = new ArrayList<String>();
		
		if (MiscUtil.getComparisonString(str).length() < MConf.get().factionNameLengthMin)
		{
			errors.add(Txt.parse("<i>The faction name can't be shorter than <h>%s<i> chars.", MConf.get().factionNameLengthMin));
		}
		
		if (str.length() > MConf.get().factionNameLengthMax)
		{
			errors.add(Txt.parse("<i>The faction name can't be longer than <h>%s<i> chars.", MConf.get().factionNameLengthMax));
		}
		
		for (char c : str.toCharArray())
		{
			if ( ! MiscUtil.substanceChars.contains(String.valueOf(c)))
			{
				errors.add(Txt.parse("<i>Faction name must be alphanumeric. \"<h>%s<i>\" is not allowed.", c));
			}
		}
		
		return errors;
	}
	
	public Faction getByName(String str)
	{
		String compStr = MiscUtil.getComparisonString(str);
		for (Faction faction : this.getAll())
		{
			if (faction.getComparisonName().equals(compStr))
			{
				return faction;
			}
		}
		return null;
	}
	
	public Faction getBestNameMatch(String searchFor)
	{
		Map<String, Faction> name2faction = new HashMap<String, Faction>();
		
		// TODO: Slow index building
		for (Faction faction : this.getAll())
		{
			name2faction.put(ChatColor.stripColor(faction.getName()), faction);
		}
		
		String tag = Txt.getBestCIStart(name2faction.keySet(), searchFor);
		if (tag == null) return null;
		return name2faction.get(tag);
	}
	
	public boolean isNameTaken(String str)
	{
		return this.getByName(str) != null;
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
		File oldFile = new File(Factions.get().getDataFolder(), "factions.json");
		File newFile = new File(Factions.get().getDataFolder(), "factions.json.migrated");
		
		// Already migrated?
		if ( ! oldFile.exists()) return;
		
		// Faction ids /delete
		// For simplicity we just drop the old special factions.
		// They will be replaced with new autogenerated ones per universe.
		Set<String> factionIdsToDelete = MUtil.set("0", "-1", "-2");
		
		// Read the file content through GSON. 
		Type type = new TypeToken<Map<String, Faction>>(){}.getType();
		Map<String, Faction> id2faction = Factions.get().gson.fromJson(DiscUtil.readCatch(oldFile), type);
		
		// The Coll
		FactionColl coll = this.getForUniverse(MassiveCore.DEFAULT);
		
		// Set the data
		for (Entry<String, Faction> entry : id2faction.entrySet())
		{
			String factionId = entry.getKey();
			if (factionIdsToDelete.contains(factionId)) continue;
			Faction faction = entry.getValue();
			coll.attach(faction, factionId);
		}
		
		// Mark as migrated
		oldFile.renameTo(newFile);
	}
	
	// -------------------------------------------- //
	// INDEX
	// -------------------------------------------- //
	
	public void reindexMPlayers()
	{
		for (FactionColl coll : this.getColls())
		{
			coll.reindexMPlayers();
		}
	}
	 */

}
