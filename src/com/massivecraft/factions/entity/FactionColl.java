package com.massivecraft.factions.entity;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.util.Txt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FactionColl extends Coll<Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionColl i = new FactionColl();
	public static FactionColl get() { return i; }

	// -------------------------------------------- //
	// STACK TRACEABILITY
	// -------------------------------------------- //
	
	@Override
	public void onTick()
	{
		super.onTick();
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COLL
	// -------------------------------------------- //
	
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
		
		if (!active) return;
		
		this.createSpecialFactions();
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
		String id = Factions.ID_NONE;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.create(id);
		
		faction.setName(Factions.NAME_NONE_DEFAULT);
		faction.setDescription("It's dangerous to go alone.");
		
		faction.setFlag(MFlag.getFlagOpen(), false);
		faction.setFlag(MFlag.getFlagPermanent(), true);
		faction.setFlag(MFlag.getFlagPeaceful(), false);
		faction.setFlag(MFlag.getFlagInfpower(), true);
		faction.setFlag(MFlag.getFlagPowerloss(), true);
		faction.setFlag(MFlag.getFlagPvp(), true);
		faction.setFlag(MFlag.getFlagFriendlyire(), false);
		faction.setFlag(MFlag.getFlagMonsters(), true);
		faction.setFlag(MFlag.getFlagAnimals(), true);
		faction.setFlag(MFlag.getFlagExplosions(), true);
		faction.setFlag(MFlag.getFlagOfflineexplosions(), true);
		faction.setFlag(MFlag.getFlagFirespread(), true);
		faction.setFlag(MFlag.getFlagEndergrief(), true);
		faction.setFlag(MFlag.getFlagZombiegrief(), true);
		
		faction.setPermittedRelations(MPerm.getPermBuild(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermDoor(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermContainer(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermButton(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermLever(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermDeposit(), Rel.LEADER, Rel.OFFICER); // Wilderness deposit should be limited as an anti spam meassure.
		
		return faction;
	}
	
	public Faction getSafezone()
	{
		String id = Factions.ID_SAFEZONE;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.create(id);
		
		faction.setName(Factions.NAME_SAFEZONE_DEFAULT);
		faction.setDescription("Free from PVP and monsters");
		
		faction.setFlag(MFlag.getFlagOpen(), false);
		faction.setFlag(MFlag.getFlagPermanent(), true);
		faction.setFlag(MFlag.getFlagPeaceful(), true);
		faction.setFlag(MFlag.getFlagInfpower(), true);
		faction.setFlag(MFlag.getFlagPowerloss(), false);
		faction.setFlag(MFlag.getFlagPvp(), false);
		faction.setFlag(MFlag.getFlagFriendlyire(), false);
		faction.setFlag(MFlag.getFlagMonsters(), false);
		faction.setFlag(MFlag.getFlagAnimals(), true);
		faction.setFlag(MFlag.getFlagExplosions(), false);
		faction.setFlag(MFlag.getFlagOfflineexplosions(), false);
		faction.setFlag(MFlag.getFlagFirespread(), false);
		faction.setFlag(MFlag.getFlagEndergrief(), false);
		faction.setFlag(MFlag.getFlagZombiegrief(), false);
		
		faction.setPermittedRelations(MPerm.getPermDoor(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermContainer(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermButton(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermLever(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermTerritory(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
		
		return faction;
	}
	
	public Faction getWarzone()
	{
		String id = Factions.ID_WARZONE;
		Faction faction = this.get(id);
		if (faction != null) return faction;
		
		faction = this.create(id);
		
		faction.setName(Factions.NAME_WARZONE_DEFAULT);
		faction.setDescription("Not the safest place to be");
		
		faction.setFlag(MFlag.getFlagOpen(), false);
		faction.setFlag(MFlag.getFlagPermanent(), true);
		faction.setFlag(MFlag.getFlagPeaceful(), true);
		faction.setFlag(MFlag.getFlagInfpower(), true);
		faction.setFlag(MFlag.getFlagPowerloss(), true);
		faction.setFlag(MFlag.getFlagPvp(), true);
		faction.setFlag(MFlag.getFlagFriendlyire(), true);
		faction.setFlag(MFlag.getFlagMonsters(), true);
		faction.setFlag(MFlag.getFlagAnimals(), true);
		faction.setFlag(MFlag.getFlagExplosions(), true);
		faction.setFlag(MFlag.getFlagOfflineexplosions(), true);
		faction.setFlag(MFlag.getFlagFirespread(), true);
		faction.setFlag(MFlag.getFlagEndergrief(), true);
		faction.setFlag(MFlag.getFlagZombiegrief(), true);
		
		faction.setPermittedRelations(MPerm.getPermDoor(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermContainer(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermButton(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermLever(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, Rel.TRUCE, Rel.NEUTRAL, Rel.ENEMY);
		faction.setPermittedRelations(MPerm.getPermTerritory(), Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
		
		return faction;
	}
	
	// -------------------------------------------- //
	// LAND REWARD
	// -------------------------------------------- //
	
	public void econLandRewardRoutine()
	{
		// If econ is enabled ...
		if (!Econ.isEnabled()) return;
		
		// ... and the land reward is non zero ...
		double econLandReward = MConf.get().econLandReward;
		if (econLandReward == 0.0) return;
		
		// ... log initiation ...
		Factions.get().log("Running econLandRewardRoutine...");
		MFlag flagPeaceful = MFlag.getFlagPeaceful();
		
		// ... and for each faction ...
		for (Faction faction : this.getAll())
		{
			// ... get the land count ...
			int landCount = faction.getLandCount();
			
			// ... and if the faction isn't peaceful and has land ...
			if (faction.getFlag(flagPeaceful) || landCount > 0) continue;
			
			// ... get the faction's members ...
			List<MPlayer> players = faction.getMPlayers();
			
			// ... calculate the reward ...
			int playerCount = players.size();
			double reward = econLandReward * landCount / playerCount;
			
			// ... and grant the reward.
			String description = String.format("own %s faction land divided among %s members", landCount, playerCount);
			for (MPlayer player : players)
			{
				Econ.modifyMoney(player, reward, description);
			}
			
		}
	}
	
	// -------------------------------------------- //
	// FACTION NAME
	// -------------------------------------------- //
	
	public ArrayList<String> validateName(String str)
	{
		// Create
		ArrayList<String> errors = new ArrayList<>();
		
		// Fill
		// Check minimum length
		if (MiscUtil.getComparisonString(str).length() < MConf.get().factionNameLengthMin)
		{
			errors.add(Txt.parse("<i>The faction name can't be shorter than <h>%s<i> chars.", MConf.get().factionNameLengthMin));
		}
		
		// Check maximum length
		if (str.length() > MConf.get().factionNameLengthMax)
		{
			errors.add(Txt.parse("<i>The faction name can't be longer than <h>%s<i> chars.", MConf.get().factionNameLengthMax));
		}
		
		// Check characters used
		for (char c : str.toCharArray())
		{
			if (!MiscUtil.substanceChars.contains(String.valueOf(c)))
			{
				errors.add(Txt.parse("<i>Faction name must be alphanumeric. \"<h>%s<i>\" is not allowed.", c));
			}
		}
		
		// Return
		return errors;
	}
	
	@Override
	public Faction getByName(String name)
	{
		String compStr = MiscUtil.getComparisonString(name);
		for (Faction faction : this.getAll())
		{
			if (faction.getComparisonName().equals(compStr))
			{
				return faction;
			}
		}
		return null;
	}

	// -------------------------------------------- //
	// PREDICATE LOGIC
	// -------------------------------------------- //

	public Map<Rel, List<String>> getRelationNames(Faction faction, Set<Rel> rels)
	{
		// Create
		Map<Rel, List<String>> ret = new LinkedHashMap<>();
		MFlag flagPeaceful = MFlag.getFlagPeaceful();
		boolean peaceful = faction.getFlag(flagPeaceful);
		for (Rel rel : rels)
		{
			ret.put(rel, new ArrayList<String>());
		}

		// Fill
		for (Faction fac : FactionColl.get().getAll())
		{
			if (fac.getFlag(flagPeaceful)) continue;

			Rel rel = fac.getRelationTo(faction);
			List<String> names = ret.get(rel);
			if (names == null) continue;

			String name = fac.describeTo(faction, true);
			names.add(name);
		}

		// Replace TRUCE if peaceful
		if (!peaceful) return ret;

		List<String> names = ret.get(Rel.TRUCE);
		if (names == null) return ret;

		ret.put(Rel.TRUCE, Collections.singletonList(MConf.get().colorTruce.toString() + Txt.parse("<italic>*EVERYONE*")));

		// Return
		return ret;
	}

}
