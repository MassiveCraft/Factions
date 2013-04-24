package com.massivecraft.factions.entity;

import java.util.*;

import org.bukkit.ChatColor;

import com.massivecraft.mcore.money.Money;
import com.massivecraft.mcore.store.Coll;
import com.massivecraft.mcore.store.MStore;
import com.massivecraft.mcore.util.Txt;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.MiscUtil;

public class FactionColl extends Coll<Faction>
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionColl(String name)
	{
		super(name, Faction.class, MStore.getDb(ConfServer.dburi), Factions.get());
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
	
	/*
	@Override
	protected synchronized String attach(Faction faction, Object oid, boolean noteChange)
	{
		String ret = super.attach(faction, oid, noteChange);
		
		// Factions start with 0 money.
		// TODO: Can this be done here?
		// TODO: Or will it be a to heavy operation to do this often?
		
		//System.out.println("faction "+faction);
		//System.out.println("faction.getId() "+faction.getId());
		// TODO: Why does this happen for Wilderness?
		if (faction.getId() == null) return ret;
		
		if (!Money.exists(faction))
		{
			Money.set(faction, 0);
		}
		
		return ret;
	}*/
	
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
	
	@Override
	public Faction detachId(Object oid)
	{
		Faction faction = this.get(oid);
		Money.set(faction, 0);
		String universe = faction.getUniverse();
		
		Faction ret = super.detachId(oid);
		
		// Clean the board
		BoardColls.get().getForUniverse(universe).clean();
		
		// Clean the uplayers
		UPlayerColls.get().getForUniverse(universe).clean();
		
		return ret;
	}

	// -------------------------------------------- //
	// INDEX
	// -------------------------------------------- //
	
	public void reindexUPlayers()
	{
		for (Faction faction : this.getAll())
		{
			faction.reindexUPlayers();
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
		String id = UConf.get(this).factionIdNone;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.createNewInstance();
		
		faction.setTag(ChatColor.DARK_GREEN+"Wilderness");
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
		faction.setFlag(FFlag.FIRESPREAD, true);
		faction.setFlag(FFlag.ENDERGRIEF, true);
		
		faction.setPermittedRelations(FPerm.BUILD, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		
		this.attach(faction, id);
		
		return faction;
	}
	
	public Faction getSafezone()
	{
		String id = UConf.get(this).factionIdSafezone;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.createNewInstance();
		
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
		faction.setFlag(FFlag.ENDERGRIEF, false);
		
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.TERRITORY, Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
		
		this.attach(faction, id);
		
		return faction;
	}
	
	public Faction getWarzone()
	{
		String id = UConf.get(this).factionIdWarzone;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.createNewInstance();
		
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
		faction.setFlag(FFlag.ENDERGRIEF, true);
		
		faction.setPermittedRelations(FPerm.DOOR, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.CONTAINER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.BUTTON, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.LEVER, Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(FPerm.TERRITORY, Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
		
		this.attach(faction, id);
		
		return faction;
	}
	
	// -------------------------------------------- //
	// LAND REWARD
	// -------------------------------------------- //
	
	public void econLandRewardRoutine()
	{
		if (!Econ.isEnabled(this.getUniverse())) return;
		
		double econLandReward = UConf.get(this).econLandReward;
		if (econLandReward == 0.0) return;
		
		Factions.get().log("Running econLandRewardRoutine...");
		for (Faction faction : this.getAll())
		{
			int landCount = faction.getLandCount();
			if (!faction.getFlag(FFlag.PEACEFUL) && landCount > 0)
			{
				List<UPlayer> players = faction.getUPlayers();
				int playerCount = players.size();
				double reward = econLandReward * landCount / playerCount;
				for (UPlayer player : players)
				{
					Econ.modifyMoney(player, reward, "own " + landCount + " faction land divided among " + playerCount + " members");
				}
			}
		}
	}
	
	// -------------------------------------------- //
	// FACTION TAG
	// -------------------------------------------- //
	
	public ArrayList<String> validateTag(String str)
	{
		ArrayList<String> errors = new ArrayList<String>();
		
		if (MiscUtil.getComparisonString(str).length() < UConf.get(this).factionTagLengthMin)
		{
			errors.add(Txt.parse("<i>The faction tag can't be shorter than <h>%s<i> chars.", UConf.get(this).factionTagLengthMin));
		}
		
		if (str.length() > UConf.get(this).factionTagLengthMax)
		{
			errors.add(Txt.parse("<i>The faction tag can't be longer than <h>%s<i> chars.", UConf.get(this).factionTagLengthMax));
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

}
