package com.bukkit.mcteam.factions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.struct.Relation;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.gson.reflect.TypeToken;
import com.bukkit.mcteam.util.DiscUtil;

/**
 * Logged in players always have exactly one FPlayer instance.
 * Logged out players may or may not have an FPlayer instance. They will always have one if they are part of a faction.
 * This is because only players with a faction are saved to disk (in order to not waste disk space).
 * 
 * The FPlayer is linked to a minecraft player using the player name in lowercase form.
 * Lowercase is enforced while loading from disk TODO
 * 
 * The same instance is always returned for the same player.
 * This means you can use the == operator. No .equals method necessary.
 */

public class FPlayer {
	
	// -------------------------------------------- //
	// Fields
	// -------------------------------------------- //
	
	private static transient Map<String, FPlayer> instances = new HashMap<String, FPlayer>();
	private static transient File file = new File(Factions.instance.getDataFolder(), "players.json");
	
	private transient String playerName;
	private transient FLocation lastStoodAt = new FLocation(); // Where did this player stand the last time we checked?
	
	private int factionId;
	private Role role;
	private String title;
	private double power;
	private long lastPowerUpdateTime;
	private transient boolean mapAutoUpdating;
	private boolean factionChatting; 
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	public FPlayer(Player player) {
		this.playerName = player.getName().toLowerCase();
	}
	
	public FPlayer(String playerName) {
		this.playerName = playerName.toLowerCase();
	}
	
	// GSON need this noarg constructor.
	public FPlayer() {
		this.resetFactionData();
		this.power = this.getPowerMax();
		this.lastPowerUpdateTime = System.currentTimeMillis();
		this.mapAutoUpdating = false;
	}
	
	public void resetFactionData() {
		this.factionId = 0; // The default neutral faction
		this.factionChatting = false;
		this.role = Role.NORMAL;
		this.title = "";
	}
	
	// -------------------------------------------- //
	// Minecraft Player
	// -------------------------------------------- //
	
	public Player getPlayer() {
		return Factions.instance.getServer().getPlayer(playerName);
	}
	
	
	// TODO lowercase vs mixedcase for logged in chars...
	public String getPlayerName() {
		return this.playerName;
	}
	
	public boolean isOnline() {
		return Factions.instance.getServer().getPlayer(playerName) != null;
	}
	
	public boolean isOffline() {
		return ! isOnline();
	}
	
	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //
	
	public Faction getFaction() {
		return Faction.get(factionId);
	}
	
	public void setFaction(Faction faction) {
		this.factionId = faction.getId();
	}
	
	public boolean hasFaction() {
		return factionId != 0;
	}
	
	public Role getRole() {
		return this.role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}
	
	public boolean isFactionChatting() {
		if (this.factionId == 0) {
			return false;
		}
		return factionChatting;
	}

	public void setFactionChatting(boolean factionChatting) {
		this.factionChatting = factionChatting;
	}
	
	public boolean isMapAutoUpdating() {
		return mapAutoUpdating;
	}

	public void setMapAutoUpdating(boolean mapAutoUpdating) {
		this.mapAutoUpdating = mapAutoUpdating;
	}
	
	public FLocation getLastStoodAt() {
		return this.lastStoodAt;
	}
	
	public void setLastStoodAt(FLocation flocation) {
		this.lastStoodAt = flocation;
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
		save();
	}
	
	public String getName() {
		return this.playerName;
	}
	
	public String getTag() {
		if ( ! this.hasFaction()) {
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
	public String getNameAndTitle(FPlayer follower) {
		return this.getRelationColor(follower)+this.getNameAndTitle();
	}
	
	public String getNameAndTag(Faction faction) {
		return this.getRelationColor(faction)+this.getNameAndTag();
	}
	public String getNameAndTag(FPlayer follower) {
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
	public String getNameAndRelevant(FPlayer follower) {
		return getNameAndRelevant(follower.getFaction());
	}
	
	// Chat Tag: 
	// These are injected into the format of global chat messages.
	
	public String getChatTag() {
		if ( ! this.hasFaction()) {
			return "";
		}
		
		return String.format(Conf.chatTagFormat, this.role.getPrefix()+this.getTag());
	}
	
	// Colored Chat Tag
	public String getChatTag(Faction faction) {
		if ( ! this.hasFaction()) {
			return "";
		}
		
		return this.getRelation(faction).getColor()+getChatTag();
	}
	public String getChatTag(FPlayer follower) {
		if ( ! this.hasFaction()) {
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
	
	public Relation getRelation(FPlayer follower) {
		return this.getFaction().getRelation(follower);
	}
	
	public ChatColor getRelationColor(Faction faction) {
		return faction.getRelationColor(this);
	}
	
	public ChatColor getRelationColor(FPlayer follower) {
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
		//Log.debug("Power of "+this.getName()+" is now: "+this.power);
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
		return Board.getFactionAt(new FLocation(this)) == this.getFaction();
	}
	
	public boolean isInOthersTerritory() {
		int idHere = Board.getIdAt(new FLocation(this));
		return idHere != 0 && idHere != this.factionId;
	}
	
	public void sendFactionHereMessage() {
		Faction factionHere = Board.getFactionAt(new FLocation(this));
		String msg = Conf.colorSystem+" ~ "+factionHere.getTag(this);
		if (factionHere.getId() != 0) {
			msg += " - "+factionHere.getDescription();
		}
		this.sendMessage(msg);
	}
	
	// -------------------------------------------- //
	// Messages
	// -------------------------------------------- //
	public void sendMessage(String message) {
		this.getPlayer().sendMessage(Conf.colorSystem + message);
	}
	
	public void sendMessage(List<String> messages) {
		for(String message : messages) {
			this.sendMessage(message);
		}
	}

	// -------------------------------------------- //
	// Get and search
	// -------------------------------------------- //
	public static FPlayer get(String playerName) {
		playerName = playerName.toLowerCase();
		if (instances.containsKey(playerName)) {
			return instances.get(playerName);
		}
		
		FPlayer vplayer = new FPlayer(playerName);
		instances.put(playerName, vplayer);
		return vplayer;
	}
	
	// You should use this one to be sure you do not spell the player name wrong.
	public static FPlayer get(Player player) {
		return get(player.getName());
	}
	
	public static Set<FPlayer> getAllOnline() {
		Set<FPlayer> fplayers = new HashSet<FPlayer>();
		for (Player player : Factions.instance.getServer().getOnlinePlayers()) {
			fplayers.add(FPlayer.get(player));
		}
		return fplayers;
	}
	
	public static Collection<FPlayer> getAll() {
		return instances.values();
	}
	
	public static FPlayer find(String playername) {
		for (Entry<String, FPlayer> entry : instances.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(playername)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	public boolean shouldBeSaved() {
		return this.factionId != 0;
	}
	
	public static boolean save() {
		Factions.log("Saving players to disk");
		
		// We only wan't to save the vplayers with non default values
		Map<String, FPlayer> vplayersToSave = new HashMap<String, FPlayer>();
		for (Entry<String, FPlayer> entry : instances.entrySet()) {
			if (entry.getValue().shouldBeSaved()) {
				vplayersToSave.put(entry.getKey(), entry.getValue());
			}
		}
		
		try {
			DiscUtil.write(file, Factions.gson.toJson(vplayersToSave));
		} catch (IOException e) {
			Factions.log("Failed to save the players to disk.");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static boolean load() {
		if ( ! file.exists()) {
			Factions.log("No players to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Type type = new TypeToken<Map<String, FPlayer>>(){}.getType();
			Map<String, FPlayer> instancesFromFile = Factions.gson.fromJson(DiscUtil.read(file), type);
			
			instances = new HashMap<String, FPlayer>();
			for (Entry<String, FPlayer> instanceFromFile : instancesFromFile.entrySet()) {
				instances.put(instanceFromFile.getKey().toLowerCase(), instanceFromFile.getValue());
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		fillPlayernames();
			
		return true;
	}
	
	public static void fillPlayernames() {
		for(Entry<String, FPlayer> entry : instances.entrySet()) {
			entry.getValue().playerName = entry.getKey();
		}
	}
	
}