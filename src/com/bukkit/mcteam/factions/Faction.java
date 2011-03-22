package com.bukkit.mcteam.factions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.factions.util.*;
import com.bukkit.mcteam.gson.reflect.TypeToken;
import com.bukkit.mcteam.util.DiscUtil;

public class Faction {
	
	// -------------------------------------------- //
	// Fields
	// -------------------------------------------- //
	
	private static transient Map<Integer, Faction> instances = new HashMap<Integer, Faction>();
	private static transient File file = new File(Factions.instance.getDataFolder(), "factions.json");
	private static transient int nextId;
	
	private transient int id;
	private Map<Integer, Relation> relationWish;
	private Set<String> invites; // Where string is a follower id (lower case name)
	private boolean open;
	private String tag;
	private String description;
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	public Faction() {
		this.relationWish = new HashMap<Integer, Relation>();
		this.invites = new HashSet<String>();
		this.open = true;
		this.tag = "???";
		this.description = "Default faction description :(";
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
		save();
	}
	
	public String getTag() {
		return this.getTag("");
	}
	public String getTag(String prefix) {
		return prefix+this.tag;
	}
	public String getTag(Faction otherFaction) {
		return this.getTag(otherFaction.getRelationColor(this).toString());
	}
	public String getTag(FPlayer otherFollower) {
		return this.getTag(otherFollower.getRelationColor(this).toString());
	}
	public void setTag(String str) {
		if (Conf.factionTagForceUpperCase) {
			str = str.toUpperCase();
		}
		this.tag = str;
		save();
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String value) {
		this.description = value;
		save();
	}
	
	// -------------------------------
	// Invites
	// -------------------------------
	
	public void invite(FPlayer fplayer) {
		this.invites.add(fplayer.getName()); //TODO Lowercase paradigm shit....
	}
	
	public void deinvite(FPlayer fplayer) {
		this.invites.remove(fplayer.getName()); //TODO Lowercase paradigm shit....
	}
	
	public boolean isInvited(FPlayer fplayer) {
		return this.invites.contains(fplayer.getName()); //TODO Lowercase paradigm shit....
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
		Faction.save();
	}
	
	public Relation getRelation(Faction otherFaction) {
		if (otherFaction.getId() == 0 || this.getId() == 0) {
			return Relation.NEUTRAL;
		}
		if (otherFaction.equals(this)) {
			return Relation.MEMBER;
		}
		if(this.getRelationWish(otherFaction).value >= otherFaction.getRelationWish(this).value) {
			return otherFaction.getRelationWish(this);
		}
		return this.getRelationWish(otherFaction);
	}
	
	public Relation getRelation(FPlayer follower) {
		return getRelation(follower.getFaction());
	}
	
	//----------------------------------------------//
	// Power
	//----------------------------------------------//
	public double getPower() {
		double ret = 0;
		for (FPlayer follower : this.getFPlayers()) {
			ret += follower.getPower();
		}
		return ret;
	}
	
	public double getPowerMax() {
		double ret = 0;
		for (FPlayer follower : this.getFPlayers()) {
			ret += follower.getPowerMax();
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
	
	public boolean hasLandInflation() {
		return this.getLandRounded() > this.getPowerRounded();
	}
	
	// -------------------------------
	// Followers
	// -------------------------------
	
	public ArrayList<FPlayer> getFPlayers() {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		for (FPlayer follower : FPlayer.getAll()) {
			if (follower.getFaction() == this) {
				ret.add(follower);
			}
		}
		return ret;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereOnline(boolean online) {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		for (FPlayer fplayer : FPlayer.getAll()) {
			if (fplayer.getFaction() == this && fplayer.isOnline() == online) {
				ret.add(fplayer);
			}
		}
		return ret;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		
		for (FPlayer fplayer : FPlayer.getAll()) {
			if (fplayer.getFaction() == this && fplayer.getRole() == role) {
				ret.add(fplayer);
			}
		}
		
		return ret;
	}
	
	/*
	public void removeFollower(FPlayer follower) {
		if (this.id != follower.factionId) {
			return; // safety check
		}
		
		this.invites.remove(follower.id);
		follower.resetFactionData();
		follower.save();
		this.save();		
	}*/
	
	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> ret = new ArrayList<Player>();
		for (Player player: Factions.instance.getServer().getOnlinePlayers()) {
			FPlayer follower = FPlayer.get(player);
			if (follower.getFaction() == this) {
				ret.add(player);
			}
		}
		return ret;
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
	
	public ChatColor getRelationColor(FPlayer follower) {
		return this.getRelation(follower).getColor();
	}
	

	
	//----------------------------------------------//
	// Persistance and entity management
	//----------------------------------------------//
	
	public static boolean save() {
		Factions.log("Saving factions to disk");
		
		try {
			DiscUtil.write(file, Factions.gson.toJson(instances));
		} catch (IOException e) {
			Factions.log("Failed to save the factions to disk.");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static boolean load() {
		if ( ! file.exists()) {
			Factions.log("No factions to load from disk. Creating new file.");
			save();
		}
		
		try {
			Type type = new TypeToken<Map<Integer, Faction>>(){}.getType();
			instances = Factions.gson.fromJson(DiscUtil.read(file), type);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		fillIds();
		
		// Make sure the default neutral faction exists
		if ( ! instances.containsKey(0)) {
			Faction faction = new Faction();
			faction.tag = "*No faction*";
			faction.description = "\"The faction for the factionless :P\"";
			faction.id = 0;
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
		
		// SAVE files
		Board.save();
		FPlayer.save();
	}
}
