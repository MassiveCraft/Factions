package com.bukkit.mcteam.factions.entities;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.factions.util.Log;
import com.bukkit.mcteam.util.ChatFixUtil;

public class Faction {
	
	public transient int id;
	protected Map<Integer, Relation> relationWish;
	protected Set<String> invites; // Where string is a follower id (lower case name)
	protected boolean open;
	protected String name;
	protected String description;
	
	public Faction() {
		this.relationWish = new HashMap<Integer, Relation>();
		this.invites = new HashSet<String>();
		this.open = true;
		this.name = "Untitled Faction :(";
		this.description = "Default faction description :(";
	}
	
	// -------------------------------
	// Information
	// -------------------------------
	public String getName() {
		return this.getName("");
	}
	public String getName(String prefix) {
		return prefix+this.name;
	}
	public String getName(Faction otherFaction) {
		return this.getName(otherFaction.getRelationColor(this).toString());
	}

	public String getName(Follower otherFollower) {
		return this.getName(otherFollower.getRelationColor(this).toString());
	}
	
	public void setName(String newName) {
		this.name = newName;
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
		double ret = this.getPowerBonus();
		for (Follower follower : this.getFollowersAll()) {
			ret += follower.getPower();
		}
		return ret;
	}
	
	public double getPowerBonus() {
		return Conf.powerDefaultBonus; // TODO this could be modified by commands later on
	}
	
	public double getPowerMax() {
		double ret = this.getPowerBonus();
		for (Follower follower : this.getFollowersAll()) {
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
	
	public double getLandMax() {
		return this.getPower() / Conf.powerPerLand;
	}
	
	public int getLandMaxRounded() {
		return (int) Math.round(this.getLandMax());
	}
	
	public boolean hasLandInflation() {
		return Board.getFactionCoordCount(this) > this.getLandMaxRounded();
	}
	
	// -------------------------------
	// Membership management
	// -------------------------------
	
	
	public ArrayList<String> invite(Follower follower) {
		ArrayList<String> errors = new ArrayList<String>();
		
		Log.debug("follower.getFaction().id"+follower.getFaction().id);
		Log.debug("this.id"+this.id);
		
		if (follower.getFaction().equals(this)) { // error här?
			errors.add(Conf.colorSystem+follower.getFullName()+" is already a member of "+this.getName());
		}
		
		if(errors.size() > 0) {
			return errors;
		}
		
		this.invites.add(follower.id);
		this.save();
		return errors;
	}
	
	public ArrayList<String> deinvite(Follower follower) {
		ArrayList<String> errors = new ArrayList<String>();
		
		if (follower.getFaction().equals(this)) {
			errors.add(Conf.colorSystem+follower.getFullName()+" is already a member of "+this.getName());
			errors.add(Conf.colorSystem+"You might want to "+Conf.colorCommand+Conf.aliasBase.get(0)+" "+Conf.aliasKick.get(0)+Conf.colorParameter+" "+follower.getName());
		}
		
		if(errors.size() > 0) {
			return errors;
		}
		
		this.invites.remove(follower.id);
		this.save();
		return errors;
	}
	
	public ArrayList<String> kick(Follower follower) {
		ArrayList<String> errors = new ArrayList<String>();
		removeFollower(follower);
		return errors;
	}
	
	
	public boolean isInvited(Follower follower) {
		return invites.contains(follower.id);
	}
	
	// -------------------------------
	// Followers
	// -------------------------------
	
	public ArrayList<Follower> getFollowersAll() {
		ArrayList<Follower> ret = new ArrayList<Follower>();
		for (Follower follower : Follower.getAll()) {
			if (follower.factionId == this.id) {
				ret.add(follower);
			}
		}
		return ret;
	}
	
	public ArrayList<Follower> getFollowersWhereOnline(boolean online) {
		ArrayList<Follower> ret = new ArrayList<Follower>();
		for (Follower follower : Follower.getAll()) {
			if (follower.factionId == this.id && follower.isOnline() == online) {
				ret.add(follower);
			}
		}
		return ret;
	}
	
	public ArrayList<Follower> getFollowersWhereRole(Role role) {
		ArrayList<Follower> ret = new ArrayList<Follower>();
		
		for (Follower follower : Follower.getAll()) {
			if (follower.factionId == this.id && follower.role.equals(role)) {
				ret.add(follower);
			}
		}
		
		return ret;
	}
	
	public void removeFollower(Follower follower) {
		if (this.id != follower.factionId) {
			return; // safety check
		}
		
		this.invites.remove(follower.id);
		follower.resetFactionData();
		follower.save();
		this.save();		
	}
	
	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> ret = new ArrayList<Player>();
		for (Player player: Factions.server.getOnlinePlayers()) {
			Follower follower = Follower.get(player);
			if (follower.factionId == this.id) {
				ret.add(player);
			}
		}
		return ret;
	}
	
	//----------------------------------------------//
	// Faction name
	//----------------------------------------------//
	
	private transient static ArrayList<String> nameWhitelist = new ArrayList<String>(Arrays.asList(new String []{
	"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", 
	"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", 
	"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", 
	"s", "t", "u", "v", "w", "x", "y", "z"
	}));
	
	public static String toComparisonName(String name) {
		String ret = "";
		
		for (char c : name.toCharArray()) {
			if (nameWhitelist.contains(String.valueOf(c))) {
				ret += c;
			}
		}
		
		return ret.toLowerCase();
	}
	
	public static ArrayList<String> validateName(String name) {
		ArrayList<String> errors = new ArrayList<String>();
		
		if(Faction.toComparisonName(name).length() < Conf.factionNameMinLength) {
			errors.add(Conf.colorSystem+"That name is to short");
		}
		
		if(name.length() > Conf.factionNameMaxLength) {
			errors.add(Conf.colorSystem+"That name is to long");
		}
		
		return errors;
	}
	
	public String getComparisonName() {
		return Faction.toComparisonName(this.name);
	}
	
	public static Faction find(String name) {
		String compName = Faction.toComparisonName(name);
		for (Faction faction : Faction.getAll()) {
			if (faction.getComparisonName().equals(compName)) {
				return faction;
			}
		}
		return null;
	}
	
	public static boolean isNameTaken(String name) {
		return Faction.find(name) != null;
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
			return;
		}
		this.relationWish.put(otherFaction.id, relation);
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
	
	public Relation getRelation(Follower follower) {
		return getRelation(follower.getFaction());
	}
	
	public ChatColor getRelationColor(Faction otherFaction) {
		return this.getRelation(otherFaction).getColor();
	}
	
	public ChatColor getRelationColor(Follower follower) {
		return this.getRelation(follower).getColor();
	}
	
	//----------------------------------------------//
	// Persistance and entity management
	//----------------------------------------------//
	
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
	
}
