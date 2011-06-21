package org.mcteam.factions;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mcteam.factions.gson.reflect.TypeToken;
import org.mcteam.factions.struct.Relation;
import org.mcteam.factions.struct.Role;
import org.mcteam.factions.util.DiscUtil;


/**
 * Logged in players always have exactly one FPlayer instance.
 * Logged out players may or may not have an FPlayer instance. They will always have one if they are part of a faction.
 * This is because only players with a faction are saved to disk (in order to not waste disk space).
 * 
 * The FPlayer is linked to a minecraft player using the player name.
 * 
 * The same instance is always returned for the same player.
 * This means you can use the == operator. No .equals method necessary.
 */

public class FPlayer {
	
	// -------------------------------------------- //
	// Fields
	// -------------------------------------------- //
	
	private static transient TreeMap<String, FPlayer> instances = new TreeMap<String, FPlayer>(String.CASE_INSENSITIVE_ORDER);
	private static transient File file = new File(Factions.instance.getDataFolder(), "players.json");
	
	private transient String playerName;
	private transient FLocation lastStoodAt = new FLocation(); // Where did this player stand the last time we checked?
	
	private int factionId;
	private Role role;
	private String title;
	private double power;
	private long lastPowerUpdateTime;
	private long lastLoginTime;
	private transient boolean mapAutoUpdating;
	private transient boolean autoClaimEnabled;
	private transient boolean autoSafeZoneEnabled;
	private transient boolean autoWarZoneEnabled;
	private boolean factionChatting; 
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	// GSON need this noarg constructor.
	public FPlayer() {
		this.resetFactionData();
		this.power = this.getPowerMax();
		this.lastPowerUpdateTime = System.currentTimeMillis();
		this.lastLoginTime = System.currentTimeMillis();
		this.mapAutoUpdating = false;
		this.autoClaimEnabled = false;
		this.autoSafeZoneEnabled = false;
		this.autoWarZoneEnabled = false;
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
	
	private int getFactionId() {
		return factionId;
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
	
	public long getLastLoginTime() {
		return lastLoginTime;
	}

	public boolean autoClaimEnabled() {
		if (this.factionId == 0)
			return false;
		return autoClaimEnabled;
	}
	public void enableAutoClaim(boolean enabled) {
		this.autoClaimEnabled = enabled;
		if (enabled) {
			this.autoSafeZoneEnabled = false;
			this.autoWarZoneEnabled = false;
		}
	}

	public boolean autoSafeZoneEnabled() {
		if (this.factionId == 0)
			return false;
		return autoSafeZoneEnabled;
	}
	public void enableAutoSafeZone(boolean enabled) {
		this.autoSafeZoneEnabled = enabled;
		if (enabled) {
			this.autoClaimEnabled = false;
			this.autoWarZoneEnabled = false;
		}
	}

	public boolean autoWarZoneEnabled() {
		if (this.factionId == 0)
			return false;
		return autoWarZoneEnabled;
	}
	public void enableAutoWarZone(boolean enabled) {
		this.autoWarZoneEnabled = enabled;
		if (enabled) {
			this.autoClaimEnabled = false;
			this.autoSafeZoneEnabled = false;
		}
	}

	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
		this.lastPowerUpdateTime = lastLoginTime;
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
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
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
	public String getNameAndTitle(FPlayer fplayer) {
		return this.getRelationColor(fplayer)+this.getNameAndTitle();
	}
	
	public String getNameAndTag(Faction faction) {
		return this.getRelationColor(faction)+this.getNameAndTag();
	}
	public String getNameAndTag(FPlayer fplayer) {
		return this.getRelationColor(fplayer)+this.getNameAndTag();
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
	public String getNameAndRelevant(FPlayer fplayer) {
		return getNameAndRelevant(fplayer.getFaction());
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
	public String getChatTag(FPlayer fplayer) {
		if ( ! this.hasFaction()) {
			return "";
		}
		
		return this.getRelation(fplayer).getColor()+getChatTag();
	}
	
	// -------------------------------
	// Relation and relation colors
	// -------------------------------
	
	public Relation getRelation(Faction faction) {
		return faction.getRelation(this);
	}
	
	public Relation getRelation(FPlayer fplayer) {
		return this.getFaction().getRelation(fplayer);
	}
	
	public ChatColor getRelationColor(Faction faction) {
		return faction.getRelationColor(this);
	}
	
	public ChatColor getRelationColor(FPlayer fplayer) {
		return this.getRelation(fplayer).getColor();
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
		if (this.isOffline() && !Conf.powerRegenOffline) {
			return;
		}
		long now = System.currentTimeMillis();
		long millisPassed = now - this.lastPowerUpdateTime;
		this.lastPowerUpdateTime = now;
		
		int millisPerMinute = 60*1000;
		this.alterPower(millisPassed * Conf.powerPerMinute / millisPerMinute);
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
		return idHere > 0 && idHere != this.factionId;
	}

	public boolean isInEnemyTerritory() {
		return Board.getFactionAt(new FLocation(this)).getRelation(this) == Relation.ENEMY;
	}
	
	public void sendFactionHereMessage() {
		Faction factionHere = Board.getFactionAt(new FLocation(this));
		String msg = Conf.colorSystem+" ~ "+factionHere.getTag(this);
		if (factionHere.getDescription().length() > 0) {
			msg += " - "+factionHere.getDescription();
		}
		this.sendMessage(msg);
	}
	
	// -------------------------------
	// Actions
	// -------------------------------
	
	public void leave() {
		Faction myFaction = this.getFaction();
		
		if (this.getRole() == Role.ADMIN && myFaction.getFPlayers().size() > 1) {
			sendMessage("You must give the admin role to someone else first.");
			return;
		}

		if (!Conf.CanLeaveWithNegativePower && this.getPower() < 0) {
			sendMessage("You cannot leave until your power is positive.");
			return;
		}
		
		if (myFaction.isNormal()) {
			myFaction.sendMessage(this.getNameAndRelevant(myFaction) + Conf.colorSystem + " left your faction.");
		}

		this.resetFactionData();

		if (myFaction.isNormal() && myFaction.getFPlayers().size() == 0) {
			// Remove this faction
			for (FPlayer fplayer : FPlayer.getAllOnline()) {
				fplayer.sendMessage("The faction "+myFaction.getTag(fplayer)+Conf.colorSystem+" was disbanded.");
			}
			Faction.delete(myFaction.getId());
		}
	}
	
	public boolean attemptClaim(boolean notifyFailure) {
		// notifyFailure is false if called by auto-claim; not need to notify on every failure for it
		// return value is false on failure, true on success

		Faction myFaction = getFaction();
		FLocation flocation = new FLocation(this);
		Faction otherFaction = Board.getFactionAt(flocation);

		if (myFaction == otherFaction) {
			if (notifyFailure)
				sendMessage("You already own this land.");
			return false;
		}

		if (this.getRole().value < Role.MODERATOR.value) {
			sendMessage("You must be "+Role.MODERATOR+" to claim land.");
			return false;
		}

		if (Conf.worldsNoClaiming.contains(flocation.getWorldName())) {
			sendMessage("Sorry, this world has land claiming disabled.");
			return false;
		}

		if (otherFaction.isSafeZone()) {
			if (notifyFailure)
				sendMessage("You can not claim a Safe Zone.");
			return false;
		}
		else if (otherFaction.isWarZone()) {
			if (notifyFailure)
				sendMessage("You can not claim a War Zone.");
			return false;
		}

		if (myFaction.getLandRounded() >= myFaction.getPowerRounded()) {
			sendMessage("You can't claim more land! You need more power!");
			return false;
		}

		if (otherFaction.getRelation(this) == Relation.ALLY) {
			if (notifyFailure)
				sendMessage("You can't claim the land of your allies.");
			return false;
		}

		if (Conf.claimsMustBeConnected && !Board.isConnectedLocation(flocation, myFaction)) {
			sendMessage("You can only claim additional land which is connected to your first claim!");
			return false;
		}

		if (otherFaction.isNone()) {
			myFaction.sendMessage(this.getNameAndRelevant(myFaction)+Conf.colorSystem+" claimed some new land :D");
		} else { //if (otherFaction.isNormal()) {

			if ( ! otherFaction.hasLandInflation()) {
				 // TODO more messages WARN current faction most importantly
				sendMessage(this.getRelationColor(otherFaction)+otherFaction.getTag()+Conf.colorSystem+" owns this land and is strong enough to keep it.");
				return false;
			}

			if ( ! Board.isBorderLocation(flocation)) {
				sendMessage("You must start claiming land at the border of the territory.");
				return false;
			}

			// ASDF claimed some of your land 450 blocks NNW of you.
			// ASDf claimed some land from FACTION NAME
			otherFaction.sendMessage(this.getNameAndRelevant(otherFaction)+Conf.colorSystem+" stole some of your land :O");
			myFaction.sendMessage(this.getNameAndRelevant(myFaction)+Conf.colorSystem+" claimed some land from "+otherFaction.getTag(myFaction));
		}

		Board.setFactionAt(myFaction, flocation);
		return true;
	}
	
	// -------------------------------------------- //
	// Messages
	// -------------------------------------------- //
	public void sendMessage(String message) {
		if (this.getPlayer() != null)
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
	
	// You should use this one to be sure you do not spell the player name wrong.
	public static FPlayer get(Player player) {
		return get(player.getName());
	}
	
	private static FPlayer get(String playerName) {
		if (instances.containsKey(playerName)) {
			return instances.get(playerName);
		}
		
		FPlayer vplayer = new FPlayer();
		vplayer.playerName = playerName;
		
		instances.put(playerName, vplayer);
		return vplayer;
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
//			if (entry.getKey().equalsIgnoreCase(playername)) {
			if (entry.getKey().startsWith(playername)) {
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
		//Factions.log("Saving players to disk");
		
		// We only wan't to save the players with non default values
		Map<String, FPlayer> playersToSave = new HashMap<String, FPlayer>();
		for (Entry<String, FPlayer> entry : instances.entrySet()) {
			if (entry.getValue().shouldBeSaved()) {
				playersToSave.put(entry.getKey(), entry.getValue());
			}
		}
		
		try {
			DiscUtil.write(file, Factions.gson.toJson(playersToSave));
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to save the players to disk.");
			return false;
		}
		return true;
	}
	
	public static boolean load() {
		Factions.log("Loading players from disk");
		if ( ! file.exists()) {
			if ( ! loadOld())
				Factions.log("No players to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Type type = new TypeToken<Map<String, FPlayer>>(){}.getType();
			Map<String, FPlayer> instancesFromFile = Factions.gson.fromJson(DiscUtil.read(file), type);
			instances.clear();
			instances.putAll(instancesFromFile);
		} catch (Exception e) {
			e.printStackTrace();
			Factions.log("Failed to load the players from disk.");
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
	
	public static void clean() {
		for (FPlayer fplayer : instances.values()) {
			if ( ! Faction.exists(fplayer.getFactionId())) {
				Factions.log("Reset faction data (invalid faction) for player "+fplayer.getName());
				fplayer.resetFactionData();
			}
		}
	}
	
	public static void autoLeaveOnInactivityRoutine() {
		long now = System.currentTimeMillis();
		double toleranceMillis = Conf.autoLeaveAfterDaysOfInactivity * 24 * 60 * 60 * 1000;
		
		for (FPlayer fplayer : FPlayer.getAll()) {
			if (now - fplayer.getLastLoginTime() > toleranceMillis) {
				fplayer.leave();
			}
		}
	}

	private static boolean loadOld() {
		File folderFollower = new File(Factions.instance.getDataFolder(), "follower");

		if ( ! folderFollower.isDirectory())
			return false;

		Factions.log("Players file doesn't exist, attempting to load old pre-1.1 data.");

		String ext = ".json";

		class jsonFileFilter implements FileFilter {
			@Override
			public boolean accept(File file) {
				return (file.getName().toLowerCase().endsWith(".json") && file.isFile());
			}
		}

		File[] jsonFiles = folderFollower.listFiles(new jsonFileFilter());

		for (File jsonFile : jsonFiles) {
			// Extract the name from the filename. The name is filename minus ".json"
			String name = jsonFile.getName();
			name = name.substring(0, name.length() - ext.length());
			try {
				FPlayer follower = Factions.gson.fromJson(DiscUtil.read(jsonFile), FPlayer.class);
				follower.playerName = name;
				follower.lastLoginTime = System.currentTimeMillis();
				instances.put(follower.playerName, follower);
				Factions.log("loaded pre-1.1 follower "+name);
			} catch (Exception e) {
				e.printStackTrace();
				Factions.log(Level.WARNING, "failed to load follower "+name);
			}
		}
		return true;
	}
}