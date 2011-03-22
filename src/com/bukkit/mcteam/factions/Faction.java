package com.bukkit.mcteam.factions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.entities.EM;
import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.factions.util.*;
import com.bukkit.mcteam.gson.reflect.TypeToken;
import com.bukkit.mcteam.util.DiscUtil;

public class Faction {
	public static transient Map<Integer, Faction> instances = new HashMap<Integer, Faction>();
	public static transient File file = new File(Factions.instance.getDataFolder(), "factions.json");
	public static transient int nextId;
	
	public transient int id;
	protected Map<Integer, Relation> relationWish;
	public Set<String> invites; // Where string is a follower id (lower case name)
	protected boolean open;
	protected String tag;
	protected String description;
	
	public Faction() {
		this.relationWish = new HashMap<Integer, Relation>();
		this.invites = new HashSet<String>();
		this.open = true;
		this.tag = "???";
		this.description = "Default faction description :(";
	}
	
	// -------------------------------
	// Information
	// -------------------------------
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
		this.save();
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String value) {
		this.description = value;
		this.save();
	}
	
	public boolean getOpen() {
		return open;
	}
	
	public void setOpen(boolean isOpen) {
		open = isOpen;
		this.save();
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
		return Board.getFactionCoordCountAllBoards(this);
	}
	
	public boolean hasLandInflation() {
		return this.getLandRounded() > this.getPowerRounded();
	}
	
	// -------------------------------
	// Membership management
	// -------------------------------
	
	
	/*public ArrayList<String> invite(FPlayer follower) { // TODO Move out
		ArrayList<String> errors = new ArrayList<String>();
		
		if (follower.getFaction().equals(this)) { // error här?
			errors.add(Conf.colorSystem+follower.getName()+" is already a member of "+this.getTag());
		}
		
		if(errors.size() > 0) {
			return errors;
		}
		
		this.invites.add(follower.id);
		this.save();
		return errors;
	}
	
	public ArrayList<String> deinvite(FPlayer follower) { // TODO move out!
		ArrayList<String> errors = new ArrayList<String>();
		
		if (follower.getFaction() == this) {
			errors.add(Conf.colorSystem+follower.getName()+" is already a member of "+this.getTag());
			errors.add(Conf.colorSystem+"You might want to "+Conf.colorCommand+Conf.aliasBase.get(0)+" "+Conf.aliasKick.get(0)+Conf.colorParameter+" "+follower.getName());
		}
		
		if(errors.size() > 0) {
			return errors;
		}
		
		this.invites.remove(follower.id);
		this.save();
		return errors;
	}*/
	
	public ArrayList<String> kick(FPlayer follower) {
		ArrayList<String> errors = new ArrayList<String>();
		removeFollower(follower);
		return errors;
	}
	
	
	public boolean isInvited(FPlayer follower) {
		return invites.contains(follower.id);
	}
	
	// -------------------------------
	// Followers
	// -------------------------------
	
	public ArrayList<FPlayer> getFPlayers() {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		for (FPlayer follower : FPlayer.getAll()) {
			if (follower.factionId == this.id) {
				ret.add(follower);
			}
		}
		return ret;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereOnline(boolean online) {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		for (FPlayer follower : FPlayer.getAll()) {
			if (follower.factionId == this.id && follower.isOnline() == online) {
				ret.add(follower);
			}
		}
		return ret;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		
		for (FPlayer follower : FPlayer.getAll()) {
			if (follower.factionId == this.id && follower.role.equals(role)) {
				ret.add(follower);
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
			if (follower.factionId == this.id) {
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
	// Messages - Directly connected to ChatFixUtil
	//----------------------------------------------//
	public void sendMessage(String message, boolean fix) {
		ChatFixUtil.sendMessage(this.getOnlinePlayers(), message, fix);
	}
	public void sendMessage(List<String> messages, boolean fix) {
		ChatFixUtil.sendMessage(this.getOnlinePlayers(), messages, fix);
	}
	public void sendMessage(String message) {
		ChatFixUtil.sendMessage(this.getOnlinePlayers(), message, true);
	}
	public void sendMessage(List<String> messages) {
		ChatFixUtil.sendMessage(this.getOnlinePlayers(), messages, true);
	}
	
	// -------------------------------
	// Relation and relation colors
	// -------------------------------
	
	public Relation getRelationWish(Faction otherFaction) {
		if (this.relationWish.containsKey(otherFaction.id)){
			return this.relationWish.get(otherFaction.id);
		}
		return Relation.NEUTRAL;
	}
	
	public void setRelationWish(Faction otherFaction, Relation relation) {
		if (this.relationWish.containsKey(otherFaction.id) && relation.equals(Relation.NEUTRAL)){
			this.relationWish.remove(otherFaction.id);
		} else {
			this.relationWish.put(otherFaction.id, relation);
		}
		this.save();
	}
	
	public Relation getRelation(Faction otherFaction) {
		if (otherFaction.id == 0 || this.id == 0) {
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
			return true;
		}
		
		try {
			Type type = new TypeToken<Map<String, Faction>>(){}.getType();
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
			Factions.log(Level.WARNING, "Non existing factionId "+factionId+" requested! Issuing board cleaning!");
			Board.cleanAll();
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
	
	public static boolean delete(Integer id) {
		// NOTE that this does not do any security checks.
		// Follower might get orphaned foreign id's
		
		// purge from all boards
		// Board.purgeFactionFromAllBoards(id);
		Board.cleanAll();
		
		// Remove the file
		//File file = new File(folderFaction, id+ext);
		//file.delete();
		
		// Remove the faction
		instances.remove(id);
		
		// TODO REMOVE ALL MEMBERS!
		
		// TODO SAVE files
		return true; // TODO
	}
	
	/*
	public static Faction create() {
		return EM.factionCreate();
	}
	
	public static Faction get(Integer factionId) {
		return EM.factionGet(factionId);
	}
	
	public static Collection<Faction> getAll() {
		return EM.factionGetAll();
	}
	
	public boolean save() {
		return EM.factionSave(this.id);
	}
	*/
}
