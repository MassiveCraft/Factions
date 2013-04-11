package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.bukkit.ChatColor;

import com.massivecraft.mcore.util.Txt;
import com.massivecraft.mcore.xlib.gson.reflect.TypeToken;

import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.persist.EntityCollection;

public class FactionColl extends EntityCollection<Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	public static FactionColl i = new FactionColl();
	// public static FactionColl get() { return i; }
	// TODO: Crap, does not work because of the instance method colliding.
	
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
	
	@Override
	public boolean loadFromDisc()
	{
		if ( ! super.loadFromDisc()) return false;
		
		// -------------------------------------------- //
		// Create Default Special Factions
		// -------------------------------------------- //
		if ( ! this.exists("0"))
		{
			Faction faction = this.create("0");
			faction.setTag(ChatColor.DARK_GREEN+"Wilderness");
			faction.setDescription("");
			this.setFlagsForWilderness(faction);
		}
		if ( ! this.exists("-1"))
		{
			Faction faction = this.create("-1");
			faction.setTag("SafeZone");
			faction.setDescription("Free from PVP and monsters");
			
			this.setFlagsForSafeZone(faction);
		}
		if ( ! this.exists("-2"))
		{
			Faction faction = this.create("-2");
			faction.setTag("WarZone");
			faction.setDescription("Not the safest place to be");
			this.setFlagsForWarZone(faction);
		}
		
		// -------------------------------------------- //
		// Fix From Old Formats
		// -------------------------------------------- //
		Faction wild = this.get("0");
		Faction safeZone = this.get("-1");
		Faction warZone = this.get("-2");
		
		// Remove troublesome " " from old pre-1.6.0 names
		if (safeZone != null && safeZone.getTag().contains(" "))
			safeZone.setTag("SafeZone");
		if (warZone != null && warZone.getTag().contains(" "))
			warZone.setTag("WarZone");
		
		// Set Flags if they are not set already.
		if (wild != null && ! wild.getFlag(FFlag.PERMANENT))
			setFlagsForWilderness(wild);
		if (safeZone != null && ! safeZone.getFlag(FFlag.PERMANENT))
			setFlagsForSafeZone(safeZone);
		if (warZone != null && ! warZone.getFlag(FFlag.PERMANENT))
			setFlagsForWarZone(warZone);

		// populate all faction player lists
		for (Faction faction : i.get())
		{
			faction.refreshFPlayers();
		}

		return true;
	}
	
	// -------------------------------------------- //
	// Flag Setters
	// -------------------------------------------- //
	public void setFlagsForWilderness(Faction faction)
	{
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
	
	public void setFlagsForSafeZone(Faction faction)
	{
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
	
	public void setFlagsForWarZone(Faction faction)
	{
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
	
	
	// -------------------------------------------- //
	// GET
	// -------------------------------------------- //
	
	@Override
	public Faction get(String id)
	{
		if ( ! this.exists(id))
		{
			Factions.get().log(Level.WARNING, "Non existing factionId "+id+" requested! Issuing cleaning!");
			BoardColl.get().clean();
			FPlayerColl.i.clean();
		}
		
		return super.get(id);
	}
	
	public Faction getNone()
	{
		return this.get("0");
	}
	
	// -------------------------------------------- //
	// Faction tag
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
		for (Faction faction : this.get())
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
		for (Faction faction : this.get())
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
		for (Faction faction : this.get())
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

}
