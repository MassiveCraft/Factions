package com.massivecraft.factions;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;


public class Faction {
	
	// -------------------------------------------- //
	// Fields
	// -------------------------------------------- //
	
	private static transient Map<Integer, Faction> instances = new HashMap<Integer, Faction>();
	private static transient File file = new File(Factions.instance.getDataFolder(), "factions.json");
	private static transient int nextId;
	
	private transient int id;
	private Map<Integer, Relation> relationWish;
	private Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<FLocation, Set<String>>();
	private Set<String> invites; // Where string is a lowercase player name
	private boolean open;
	private boolean peaceful;
	private boolean peacefulExplosionsEnabled;
	private boolean permanent;
	private String tag;
	private String description;
	private Location home;
	private transient long lastPlayerLoggedOffTime;
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	public Faction() {
		this.relationWish = new HashMap<Integer, Relation>();
		this.invites = new HashSet<String>();
		this.open = Conf.newFactionsDefaultOpen;
		this.tag = "???";
		this.description = "Default faction description :(";
		this.lastPlayerLoggedOffTime = 0;
		this.peaceful = false;
		this.peacefulExplosionsEnabled = false;
		this.permanent = false;
	}
	
	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //
	
	public int getId() {
		return this.id;
	}
	
	public boolean getOpen() {
		return open;
	}
	
	public void setOpen(boolean isOpen) {
		open = isOpen;
	}
	
	public String getTag() {
		return this.getTag("");
	}
	public String getTag(String prefix) {
		return prefix+this.tag;
	}
	public String getTag(Faction otherFaction) {
		if (otherFaction == null)
			return getTag();
		else
			return this.getTag(otherFaction.getRelationColor(this).toString());
	}
	public String getTag(FPlayer otherFplayer) {
		if (otherFplayer == null)
			return getTag();
		else
			return this.getTag(otherFplayer.getRelationColor(this).toString());
	}
	public void setTag(String str) {
		if (Conf.factionTagForceUpperCase) {
			str = str.toUpperCase();
		}
		this.tag = str;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String value) {
		this.description = value;
	}
	
	public void setHome(Location home) {
		this.home = home;
	}

	public Location getHome() {
		confirmValidHome();
		return home;
	}
	
	public boolean hasHome() {
		confirmValidHome();
		return this.home != null;
	}

	public void confirmValidHome() {
		if (!Conf.homesMustBeInClaimedTerritory || this.home == null || Board.getFactionAt(new FLocation(this.home)) == this) {
			return;
		}

		sendMessage("Your faction home has been un-set since it is no longer in your territory.");
		this.home = null;
	}

	// "peaceful" status can only be set by server admins/moderators/ops, and prevents PvP and land capture to/from the faction
	public boolean isPeaceful() {
		return peaceful;
	}
	public void setPeaceful(boolean isPeaceful) {
		peaceful = isPeaceful;
	}

	// "permanent" status can only be set by server admins/moderators/ops, and allows the faction to remain even with 0 members
	public boolean isPermanent() {
		return permanent;
	}
	public void setPermanent(boolean isPermanent) {
		permanent = isPermanent;
	}

	public void setPeacefulExplosions(boolean disable) {
		peacefulExplosionsEnabled = disable;
	}
	public void setPeacefulExplosions() {
		setPeacefulExplosions(!peacefulExplosionsEnabled);
	}

	public boolean noPvPInTerritory() {
		return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisablePVP);
	}

	public boolean noMonstersInTerritory() {
		return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisableMonsters);
	}

	public boolean noExplosionsInTerritory() {
		return peaceful && !peacefulExplosionsEnabled;
	}

	// -------------------------------
	// Understand the types
	// -------------------------------
	
	public boolean isNormal() {
		return this.getId() > 0;
	}
	
	public boolean isNone() {
		return this.getId() == 0;
	}
	
	public boolean isSafeZone() {
		return this.getId() == -1;
	}
	
	public boolean isWarZone() {
		return this.getId() == -2;
	}
	
	// -------------------------------
	// Invites - uses lowercase name
	// -------------------------------
	
	public void invite(FPlayer fplayer) {
		this.invites.add(fplayer.getName().toLowerCase());
	}
	
	public void deinvite(FPlayer fplayer) {
		this.invites.remove(fplayer.getName().toLowerCase());
	}
	
	public boolean isInvited(FPlayer fplayer) {
		return this.invites.contains(fplayer.getName().toLowerCase());
	}
	
	// -------------------------------
	// Relation and relation colors TODO
	// -------------------------------
	
	public Relation getRelationWish(Faction otherFaction) {
		if (this.relationWish.containsKey(otherFaction.getId())){
			return this.relationWish.get(otherFaction.getId());
		}
		return Relation.NEUTRAL;
	}
	
	public void setRelationWish(Faction otherFaction, Relation relation) {
		if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Relation.NEUTRAL)){
			this.relationWish.remove(otherFaction.getId());
		} else {
			this.relationWish.put(otherFaction.getId(), relation);
		}
	}
	
	public Relation getRelation(Faction otherFaction) {
		return getRelation(otherFaction, false);
	}
	
	public Relation getRelation(Faction otherFaction, boolean ignorePeaceful) {
		if (!otherFaction.isNormal() || !this.isNormal()) {
			return Relation.NEUTRAL;
		}
		if (otherFaction.equals(this)) {
			return Relation.MEMBER;
		}
		if (!ignorePeaceful && (this.peaceful || otherFaction.isPeaceful())) {
			return Relation.NEUTRAL;
		}
		if(this.getRelationWish(otherFaction).value >= otherFaction.getRelationWish(this).value) {
			return otherFaction.getRelationWish(this);
		}
		return this.getRelationWish(otherFaction);
	}
	
	public Relation getRelation(FPlayer fplayer) {
		if (fplayer == null)
			return Relation.NEUTRAL;
		else
			return getRelation(fplayer.getFaction());
	}
	
	//----------------------------------------------//
	// Power
	//----------------------------------------------//
	public double getPower() {
		double ret = 0;
		for (FPlayer fplayer : this.getFPlayers()) {
			ret += fplayer.getPower();
		}
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
			ret = Conf.powerFactionMax;
		}
		return ret;
	}
	
	public double getPowerMax() {
		double ret = 0;
		for (FPlayer fplayer : this.getFPlayers()) {
			ret += fplayer.getPowerMax();
		}
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax) {
			ret = Conf.powerFactionMax;
		}
		return ret;
	}
	
	public int getPowerRounded() {
		return (int) Math.round(this.getPower());
	}
	
	public int getPowerMaxRounded() {
		return (int) Math.round(this.getPowerMax());
	}
	
	public int getLandRounded() {
		return Board.getFactionCoordCount(this);
	}
	
	public int getLandRoundedInWorld(String worldName) {
		return Board.getFactionCoordCountInWorld(this, worldName);
	}
	
	public boolean hasLandInflation() {
		return this.getLandRounded() > this.getPowerRounded();
	}
	
	// -------------------------------
	// Fplayers
	// -------------------------------
	
	public ArrayList<FPlayer> getFPlayers() {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		if (id < 0)
			return ret;

		for (FPlayer fplayer : FPlayer.getAll()) {
			if (fplayer.getFaction() == this) {
				ret.add(fplayer);
			}
		}

		return ret;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereOnline(boolean online) {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		if (id < 0)
			return ret;

		for (FPlayer fplayer : FPlayer.getAll()) {
			if (fplayer.getFaction() == this && fplayer.isOnline() == online) {
				ret.add(fplayer);
			}
		}

		return ret;
	}
	
	public FPlayer getFPlayerAdmin() {
		if (id <= 0)
			return null;
		
		for (FPlayer fplayer : FPlayer.getAll()) {
			if (fplayer.getFaction() == this && fplayer.getRole() == Role.ADMIN) {
				return fplayer;
			}
		}
		return null;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		if (id <= 0)
			return ret;
		
		for (FPlayer fplayer : FPlayer.getAll()) {
			if (fplayer.getFaction() == this && fplayer.getRole() == role) {
				ret.add(fplayer);
			}
		}
		
		return ret;
	}
	
	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> ret = new ArrayList<Player>();
		if (id < 0)
			return ret;

		for (Player player: Factions.instance.getServer().getOnlinePlayers()) {
			FPlayer fplayer = FPlayer.get(player);
			if (fplayer.getFaction() == this) {
				ret.add(player);
			}
		}

		return ret;
	}
	
	// slightly faster check than getOnlinePlayers() if you just want to see if there are any players online
	public boolean hasPlayersOnline() {
		// only real factions can have players online, not safe zone / war zone
		if (id < 0)
			return false;
		
		for (Player player: Factions.instance.getServer().getOnlinePlayers()) {
			FPlayer fplayer = FPlayer.get(player);
			if (fplayer.getFaction() == this) {
				return true;
			}
		}
		
		// even if all players are technically logged off, maybe someone was on recently enough to not consider them officially offline yet
		if (Conf.considerFactionsReallyOfflineAfterXMinutes > 0 &&
				System.currentTimeMillis() < lastPlayerLoggedOffTime + (Conf.considerFactionsReallyOfflineAfterXMinutes * 60000)) {
			return true;
		}
		return false;
	}
	
	public void memberLoggedOff() {
		if (this.isNormal()) {
			lastPlayerLoggedOffTime = System.currentTimeMillis();
		}
	}
	
	//----------------------------------------------//
	// Faction tag
	//----------------------------------------------//
	
	public String getComparisonTag() {
		return TextUtil.getComparisonString(this.tag);
	}
	
	public static ArrayList<String> validateTag(String str) {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(TextUtil.getComparisonString(str).length() < Conf.factionTagLengthMin) {
			errors.add(Conf.colorSystem+"The faction tag can't be shorter than "+Conf.factionTagLengthMin+ " chars.");
		}
		
		if(str.length() > Conf.factionTagLengthMax) {
			errors.add(Conf.colorSystem+"The faction tag can't be longer than "+Conf.factionTagLengthMax+ " chars.");
		}
		
		for (char c : str.toCharArray()) {
			if ( ! TextUtil.substanceChars.contains(String.valueOf(c))) {
				errors.add(Conf.colorSystem+"Faction tag must be alphanumeric. \""+c+"\" is not allowed.");
			}
		}
		
		return errors;
	}
	
	public static Faction findByTag(String str) {
		String compStr = TextUtil.getComparisonString(str);
		for (Faction faction : Faction.getAll()) {
			if (faction.getComparisonTag().equals(compStr)) {
				return faction;
			}
		}
		return null;
	}
	
	public static boolean isTagTaken(String str) {
		return Faction.findByTag(str) != null;
	}
	
	//----------------------------------------------//
	// Messages
	//----------------------------------------------//
	public void sendMessage(String message) {
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(message);
		}
	}
	
	public void sendMessage(List<String> messages) {
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(messages);
		}
	}
	
	//----------------------------------------------//
	// Mudd TODO
	//----------------------------------------------//
	
	public ChatColor getRelationColor(Faction otherFaction) {
		return this.getRelation(otherFaction).getColor();
	}
	
	public ChatColor getRelationColor(FPlayer fplayer) {
		return this.getRelation(fplayer).getColor();
	}
	
	//----------------------------------------------//
	// Ownership of specific claims
	//----------------------------------------------//

	public void clearAllClaimOwnership() {
		claimOwnership.clear();
	}

	public void clearClaimOwnership(FLocation loc) {
		claimOwnership.remove(loc);
	}

	public void clearClaimOwnership(String playerName) {
		if (playerName == null || playerName.isEmpty()) {
			return;
		}

		Set<String> ownerData;
		String player = playerName.toLowerCase();

		for (Entry<FLocation, Set<String>> entry : claimOwnership.entrySet()) {
			ownerData = entry.getValue();

			if (ownerData == null) {
				continue;
			}

			Iterator<String> iter = ownerData.iterator();
			while (iter.hasNext()) {
				if (iter.next().equals(player)) {
					iter.remove();
				}
			}

			if (ownerData.isEmpty()) {
				claimOwnership.remove(entry.getKey());
			}
		}
	}

	public int getCountOfClaimsWithOwners() {
		return claimOwnership.isEmpty() ? 0 : claimOwnership.size();
	}

	public boolean doesLocationHaveOwnersSet(FLocation loc) {
		if (claimOwnership.isEmpty() || !claimOwnership.containsKey(loc)) {
			return false;
		}
		Set<String> ownerData = claimOwnership.get(loc);
		return ownerData != null && !ownerData.isEmpty();
	}

	public boolean isPlayerInOwnerList(String playerName, FLocation loc) {
		if (claimOwnership.isEmpty()) {
			return false;
		}
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null) {
			return false;
		}
		if (ownerData.contains(playerName.toLowerCase())) {
			return true;
		}
		return false;
	}

	public void setPlayerAsOwner(String playerName, FLocation loc) {
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null) {
			ownerData = new HashSet<String>();
		}
		ownerData.add(playerName.toLowerCase());
		claimOwnership.put(loc, ownerData);
	}

	public void removePlayerAsOwner(String playerName, FLocation loc) {
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null) {
			return;
		}
		ownerData.remove(playerName.toLowerCase());
		claimOwnership.put(loc, ownerData);
	}

	public Set<String> getOwnerList(FLocation loc) {
		return claimOwnership.get(loc);
	}

	public String getOwnerListString(FLocation loc) {
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null || ownerData.isEmpty()) {
			return "";
		}

		String ownerList = "";

		Iterator<String> iter = ownerData.iterator();
		while (iter.hasNext()) {
			if (!ownerList.isEmpty()) {
				ownerList += ", ";
			}
			ownerList += iter.next();
		}
		return ownerList;
	}

	public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc) {
		// different faction?
		if (fplayer.getFactionId() != id) {
			return false;
		}

		// sufficient role to bypass ownership?
		if (fplayer.getRole().isAtLeast(Conf.ownedAreaModeratorsBypass ? Role.MODERATOR : Role.ADMIN)) {
			return true;
		}

		// make sure claimOwnership is initialized
		if (claimOwnership.isEmpty()) {
			return true;
		}

		// need to check the ownership list, then
		Set<String> ownerData = claimOwnership.get(loc);

		// if no owner list, owner list is empty, or player is in owner list, they're allowed
		if (ownerData == null || ownerData.isEmpty() || ownerData.contains(fplayer.getName().toLowerCase())) {
			return true;
		}

		return false;
	}

	
	//----------------------------------------------//
	// Persistance and entity management
	//----------------------------------------------//
	
	public static boolean save() {
		//Factions.log("Saving factions to disk");
		
		try {
			DiscUtil.write(file, Factions.instance.gson.toJson(instances));
		} catch (IOException e) {
			e.printStackTrace();
			Factions.log("Failed to save the factions to disk due to I/O exception.");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to save the factions to disk.");
			return false;
		}
		
		return true;
	}
	
	public static boolean load() {
		Factions.log("Loading factions from disk");
		
		if ( ! file.exists()) {
			if ( ! loadOld())
				Factions.log("No factions to load from disk. Creating new file.");
			save();
		}
		
		try {
			Type type = new TypeToken<Map<Integer, Faction>>(){}.getType();
			Map<Integer, Faction> instancesFromFile = Factions.instance.gson.fromJson(DiscUtil.read(file), type);
			instances.clear();
			instances.putAll(instancesFromFile);
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to load the factions from disk.");
			return false;
		}
		
		fillIds();
		
		// Make sure the default neutral faction exists
		if ( ! instances.containsKey(0)) {
			Faction faction = new Faction();
			faction.tag = ChatColor.DARK_GREEN+"Wilderness";
			faction.description = "";
			faction.id = 0;
			instances.put(faction.id, faction);
		}
		
		// Make sure the safe zone faction exists
		if ( ! instances.containsKey(-1)) {
			Faction faction = new Faction();
			faction.tag = ChatColor.GOLD+"Safe Zone";
			faction.description = "Free from PVP and monsters";
			faction.id = -1;
			instances.put(faction.id, faction);
		}
		
		// Make sure the war zone faction exists
		if ( ! instances.containsKey(-2)) {
			Faction faction = new Faction();
			faction.tag = ChatColor.DARK_RED+"War Zone";
			faction.description = "Not the safest place to be";
			faction.id = -2;
			instances.put(faction.id, faction);
		}
		
		return true;
	}
	
	public static void fillIds() {
		nextId = 1;
		for(Entry<Integer, Faction> entry : instances.entrySet()) {
			entry.getValue().id = entry.getKey();
			if (nextId < entry.getKey()) {
				nextId = entry.getKey();
			}
		}
		nextId += 1; // make it the next id and not the current highest.
	}

	public static Faction get(Integer factionId) {
		if ( ! instances.containsKey(factionId)) {
			Factions.log(Level.WARNING, "Non existing factionId "+factionId+" requested! Issuing cleaning!");
			Board.clean();
			FPlayer.clean();
		}
		return instances.get(factionId);
	}
	
	public static Faction getNone() {
		return instances.get(0);
	}
	
	public static Faction getSafeZone() {
		return instances.get(-1);
	}
	
	public static Faction getWarZone() {
		return instances.get(-2);
	}
	
	public static boolean exists(Integer factionId) {
		return instances.containsKey(factionId);
	}
	
	public static Collection<Faction> getAll() {
		return instances.values();
	}
	
	//TODO ta parametrar här. All info som behövs ska matas in här och så sparar vi i denna method.
	public static Faction create() {
		Faction faction = new Faction();
		faction.id = nextId;
		nextId += 1;
		instances.put(faction.id, faction);
		Factions.log("created new faction "+faction.id);
		//faction.save();
		return faction;
	}
	
	public static void delete(Integer id) {
		// Remove the faction
		instances.remove(id);
		
		// Clean the board
		Board.clean();
		
		// Clean the fplayers
		FPlayer.clean();
	}

	private static boolean loadOld() {
		File folderFaction = new File(Factions.instance.getDataFolder(), "faction");

		if ( ! folderFaction.isDirectory())
			return false;

		Factions.log("Factions file doesn't exist, attempting to load old pre-1.1 data.");

		String ext = ".json";

		class jsonFileFilter implements FileFilter {
			@Override
			public boolean accept(File file) {
				return (file.getName().toLowerCase().endsWith(".json") && file.isFile());
			}
		}

		File[] jsonFiles = folderFaction.listFiles(new jsonFileFilter());
		for (File jsonFile : jsonFiles) {
			// Extract the name from the filename. The name is filename minus ".json"
			String name = jsonFile.getName();
			name = name.substring(0, name.length() - ext.length());
			int id = Integer.parseInt(name);

			try {
				Faction faction = Factions.instance.gson.fromJson(DiscUtil.read(jsonFile), Faction.class);
				faction.id = id;
				instances.put(faction.id, faction);
				Factions.log("loaded pre-1.1 faction "+id);
			} catch (Exception e) {
				e.printStackTrace();
				Factions.log(Level.WARNING, "Failed to load faction "+id);
			}
		}
		return true;
	}
}
