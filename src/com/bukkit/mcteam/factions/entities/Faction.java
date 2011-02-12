package com.bukkit.mcteam.factions.entities;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.factions.util.Log;
import com.bukkit.mcteam.factions.util.TextUtil;
import com.bukkit.mcteam.util.ChatFixUtil;

public class Faction {
	
	public transient int id;
	protected Map<Integer, Relation> relationWish;
	protected Set<String> invites; // Where string is a follower id (lower case name)
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
	public String getTag(Follower otherFollower) {
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
		for (Follower follower : this.getFollowersAll()) {
			ret += follower.getPower();
		}
		return ret;
	}
	
	public double getPowerMax() {
		double ret = 0;
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
	
	public boolean hasLandInflation() {
		return this.getLandRounded() > this.getPowerRounded();
	}
	
	// -------------------------------
	// Membership management
	// -------------------------------
	
	
	public ArrayList<String> invite(Follower follower) { // TODO Move out
		ArrayList<String> errors = new ArrayList<String>();
		
		Log.debug("follower.getFaction().id"+follower.getFaction().id);
		Log.debug("this.id"+this.id);
		
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
	
	public ArrayList<String> deinvite(Follower follower) { // TODO move out!
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
