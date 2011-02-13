package com.bukkit.mcteam.factions.entities;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Factions;
import com.bukkit.mcteam.factions.struct.*;
import com.bukkit.mcteam.factions.util.Log;
import com.bukkit.mcteam.util.ChatFixUtil;

public class Follower {
	public transient String id; // The is the name of the player
	
	public int factionId;
	public Role role;
	private String title;
	private double power;
	private long lastPowerUpdateTime;
	private boolean mapAutoUpdating;
	private boolean factionChatting; 
	
	public boolean isFactionChatting() {
		if (this.factionId == 0) {
			return false;
		}
		return factionChatting;
	}

	public void setFactionChatting(boolean factionChatting) {
		this.factionChatting = factionChatting;
	}

	public Follower() {
		this.resetFactionData();
		this.power = this.getPowerMax();
		this.lastPowerUpdateTime = System.currentTimeMillis();
		this.mapAutoUpdating = false;
	}
	
	protected void resetFactionData() {
		this.factionId = 0; // The default neutral faction
		this.factionChatting = false;
		this.role = Role.NORMAL;
		this.title = "";
	}
	
	public Player getPlayer() {
		return Factions.server.getPlayer(this.getName());
	}
	
	public boolean isOnline() {
		return this.getPlayer() != null;
	}
	
	public boolean isMapAutoUpdating() {
		return mapAutoUpdating;
	}

	public void setMapAutoUpdating(boolean mapAutoUpdating) {
		this.mapAutoUpdating = mapAutoUpdating;
	}
	
	//----------------------------------------------//
	// Title, Name, Faction Tag and Chat
	//----------------------------------------------//
	
	// Base:
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.save();
	}
	
	public String getName() {
		return this.id;
	}
	
	public String getTag() {
		if (this.withoutFaction()) {
			return "";
		}
		return this.getFaction().getTag();
	}
	
	// Base concatenations:
	
	public String getNameAndSomething(String something) {
		String ret = this.role.getPrefix();
		if (something.length() > 0) {
			ret += something+" ";
		}
		ret += this.getName();
		return ret;
	}
	
	public String getNameAndTitle() {
		return this.getNameAndSomething(this.getTitle());
	}
	
	public String getNameAndTag() {
		return this.getNameAndSomething(this.getTag());
	}
	
	// Colored concatenations:
	// These are used in information messages
	
	public String getNameAndTitle(Faction faction) {
		return this.getRelationColor(faction)+this.getNameAndTitle();
	}
	public String getNameAndTitle(Follower follower) {
		return this.getRelationColor(follower)+this.getNameAndTitle();
	}
	
	public String getNameAndTag(Faction faction) {
		return this.getRelationColor(faction)+this.getNameAndTag();
	}
	public String getNameAndTag(Follower follower) {
		return this.getRelationColor(follower)+this.getNameAndTag();
	}
	
	public String getNameAndRelevant(Faction faction) {
		// Which relation?
		Relation rel = this.getRelation(faction);
		
		// For member we show title
		if (rel == Relation.MEMBER) {
			return rel.getColor() + this.getNameAndTitle();
		}
		
		// For non members we show tag
		return rel.getColor() + this.getNameAndTag();
	}
	public String getNameAndRelevant(Follower follower) {
		return getNameAndRelevant(follower.getFaction());
	}
	
	// Chat Tag: 
	// These are injected into the format of global chat messages.
	
	public String getChatTag() {
		if (this.withoutFaction()) {
			return "";
		}
		
		return String.format(Conf.chatTagFormat, this.role.getPrefix()+this.getTag());
	}
	
	// Colored Chat Tag
	public String getChatTag(Faction faction) {
		if (this.withoutFaction()) {
			return "";
		}
		
		return this.getRelation(faction).getColor()+getChatTag();
	}
	public String getChatTag(Follower follower) {
		if (this.withoutFaction()) {
			return "";
		}
		
		return this.getRelation(follower).getColor()+getChatTag();
	}
	
	// -------------------------------
	// Relation and relation colors
	// -------------------------------
	
	public Relation getRelation(Faction faction) {
		return faction.getRelation(this);
	}
	
	public Relation getRelation(Follower follower) {
		return this.getFaction().getRelation(follower);
	}
	
	public ChatColor getRelationColor(Faction faction) {
		return faction.getRelationColor(this);
	}
	
	public ChatColor getRelationColor(Follower follower) {
		return this.getRelation(follower).getColor();
	}
	
	
	//----------------------------------------------//
	// Health
	//----------------------------------------------//
	public void heal(int amnt) {
		Player player = this.getPlayer();
		if (player == null) {
			return;
		}
		player.setHealth(player.getHealth() + amnt);
	}
	
	
	//----------------------------------------------//
	// Power
	//----------------------------------------------//
	public double getPower() {
		this.updatePower();
		return this.power;
	}
	
	protected void alterPower(double delta) {
		this.power += delta;
		if (this.power > this.getPowerMax()) {
			this.power = this.getPowerMax();
		} else if (this.power < this.getPowerMin()) {
			this.power = this.getPowerMin();
		}
		Log.debug("Power of "+this.getName()+" is now: "+this.power);
	}
	
	public double getPowerMax() {
		return Conf.powerPlayerMax;
	}
	
	public double getPowerMin() {
		return Conf.powerPlayerMin;
	}
	
	public int getPowerRounded() {
		return (int) Math.round(this.getPower());
	}
	
	public int getPowerMaxRounded() {
		return (int) Math.round(this.getPowerMax());
	}
	
	public int getPowerMinRounded() {
		return (int) Math.round(this.getPowerMin());
	}
	
	protected void updatePower() {
		long now = System.currentTimeMillis();
		long millisPassed = now - this.lastPowerUpdateTime;
		this.lastPowerUpdateTime = now;
		
		int millisPerMinute = 60*1000;
		this.alterPower(millisPassed * Conf.powerPerMinute / millisPerMinute);
		//this.save(); // This would save to often. So we save this on player quit instead.
	}
	
	public void onDeath() {
		this.updatePower();
		this.alterPower(-Conf.powerPerDeath);
	}
	
	//----------------------------------------------//
	// Territory
	//----------------------------------------------//
	public boolean isInOwnTerritory() {
		return Board.get(this.getPlayer().getWorld()).getFactionAt(this.getCoord()) == this.getFaction();
	}
	
	public boolean isInOthersTerritory() {
		Faction factionHere = Board.get(this.getPlayer().getWorld()).getFactionAt(this.getCoord());
		return factionHere.id != 0 && factionHere != this.getFaction();
	}
	
	public Coord getCoord() {
		return Coord.from(this);
	}
	
	public void sendFactionHereMessage() {
		Faction factionHere = Board.get(this.getPlayer().getWorld()).getFactionAt(this.getCoord());
		String msg = Conf.colorSystem+" ~ "+factionHere.getTag(this);
		if (factionHere.id != 0) {
			msg += " - "+factionHere.getDescription();
		}
		this.sendMessage(msg);
	}
	
	//----------------------------------------------//
	// Faction management
	//----------------------------------------------//
	public Faction getFaction() {
		return EM.factionGet(factionId);
	}
	
	public boolean hasFaction() {
		return factionId != 0;
	}
	public boolean withoutFaction() {
		return factionId == 0;
	}
	
	public ArrayList<String> join(Faction faction) {
		ArrayList<String> errors = new ArrayList<String>();
		if (faction.id == this.factionId) {
			errors.add(Conf.colorSystem+"You are already a member of "+faction.getRelationColor(this)+faction.getTag());
		}
		
		if( ! faction.getOpen() && ! faction.isInvited(this)) {
			errors.add(Conf.colorSystem+"This guild requires invitation.");
		}
		
		if (this.hasFaction()) {
			errors.add(Conf.colorSystem+"You must leave your current faction first.");
		}
		
		if (errors.size() > 0) {
			return errors;
		}

		this.resetFactionData();
		if(faction.getFollowersAll().size() == 0) {
			this.role = Role.ADMIN;
		} else {
			this.role = Role.NORMAL;
		}
		this.factionId = faction.id;
		faction.deinvite(this);
		this.save();
		
		return errors;
	}
	
	public ArrayList<String> leave() {
		ArrayList<String> errors = new ArrayList<String>();
		if (this.role == Role.ADMIN && this.getFaction().getFollowersAll().size() > 1) {
			errors.add(Conf.colorSystem+"You must give the admin role to someone else first.");
		}
		
		if(this.withoutFaction()) {
			errors.add(Conf.colorSystem+"You are not member of any faction.");
		}
		
		if (errors.size() > 0) {
			return errors;
		}
		
		this.resetFactionData();
		this.save();
		
		return errors;
	}
	
	public ArrayList<String> invite(Follower follower) {
		ArrayList<String> errors = new ArrayList<String>();
		
		//Log.debug("this.role: "+this.role);
		//Log.debug("this.role.value: "+this.role.value);
		//Log.debug("FactionRole.MODERATOR.value: "+FactionRole.MODERATOR.value);
		
		if (this.role.value < Role.MODERATOR.value) {
			errors.add(Conf.colorSystem+"You must me be a moderator to invite.");
		}
		
		if(errors.size() > 0) {
			return errors;
		}
		
		return this.getFaction().invite(follower);
	}
	
	public ArrayList<String> deinvite(Follower follower) {
		ArrayList<String> errors = new ArrayList<String>();
		
		if (this.role.value < Role.MODERATOR.value) {
			errors.add(Conf.colorSystem+"You must me be a moderator to deinvite.");
		}
		
		if(errors.size() > 0) {
			return errors;
		}
		
		return this.getFaction().deinvite(follower);
	}
	
	public ArrayList<String> kick(Follower follower) {
		ArrayList<String> errors = new ArrayList<String>();
		
		if ( ! follower.getFaction().equals(this.getFaction())) {
			errors.add(follower.getNameAndRelevant(this)+Conf.colorSystem+" is not a member of "+Conf.colorMember+this.getFaction().getTag());
		} else if (follower.equals(this)) {
			errors.add(Conf.colorSystem+"You can not kick yourself.");
			errors.add(Conf.colorSystem+"You might want to "+Conf.colorCommand+Conf.aliasBase.get(0)+" "+Conf.aliasLeave.get(0));
		} else if (follower.role.value >= this.role.value) { // TODO add more informative messages.
			errors.add(Conf.colorSystem+"Your rank is to low to kick this player.");
		}
		
		if(errors.size() > 0) {
			return errors;
		}
		
		return follower.getFaction().kick(follower);
	}
	
	//----------------------------------------------//
	// Login info
	//----------------------------------------------//
	public void sendJoinInfo() { // TODO Missplaced!?
		// Do we even whant to use message of the day...
		// Perhaps that is up to another plugin...
		//this.getPlayer().sendMessage(ChatColor.GREEN + "This is a faction server! Type "+Conf.colorCommand+"/f"+ChatColor.GREEN +" for more info :D");
	}
	
	//----------------------------------------------//
	// Messages - Directly connected to ChatFixUtil
	//----------------------------------------------//
	public void sendMessage(String message, boolean fix) {
		Player player = this.getPlayer();
		ChatFixUtil.sendMessage(player, message, fix);
	}
	public void sendMessage(List<String> messages, boolean fix) {
		Player player = this.getPlayer();
		ChatFixUtil.sendMessage(player, messages, fix);
	}
	public void sendMessage(String message) {
		Player player = this.getPlayer();
		ChatFixUtil.sendMessage(player, message, true);
	}
	public void sendMessage(List<String> messages) {
		Player player = this.getPlayer();
		ChatFixUtil.sendMessage(player, messages, true);
	}
	
	//----------------------------------------------//
	// Search
	//----------------------------------------------//
	public static Follower find(String name) {
		for (Follower follower : EM.followerGetAll()) {
			if (follower.getName().equalsIgnoreCase(name.trim())) {
				return follower;
			}
		}
		
		return null;
	}
	
	//----------------------------------------------//
	// Persistance and entity management
	//----------------------------------------------//
	
	public boolean save() {
		return EM.followerSave(this.id);
	}
	
	public static Follower get(Player player) {
		return EM.followerGet(player);
	}
	
	public static Collection<Follower> getAll() {
		return EM.followerGetAll();
	}
}