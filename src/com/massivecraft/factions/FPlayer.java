package com.massivecraft.factions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.persist.PlayerEntity;


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

public class FPlayer extends PlayerEntity
{	
	//private transient String playerName;
	private transient FLocation lastStoodAt = new FLocation(); // Where did this player stand the last time we checked?
	
	// FIELD: factionId
	private String factionId;
	public Faction getFaction() { return Factions.i.get(this.factionId); }
	public String getFactionId() { return this.factionId; }
	public boolean hasFaction() { return ! factionId.equals("0"); }
	public void setFaction(Faction faction)
	{
		this.factionId = faction.getId();
		SpoutFeatures.updateAppearances(this.getPlayer());
	}
	
	// FIELD: role
	private Role role;
	public Role getRole() { return this.role; }
	public void setRole(Role role) { this.role = role; SpoutFeatures.updateAppearances(this.getPlayer()); }
	
	// FIELD: title
	private String title;
	
	// FIELD: power
	private double power;
	
	// FIELD: lastPowerUpdateTime
	private long lastPowerUpdateTime;
	
	// FIELD: lastLoginTime
	private long lastLoginTime;
	
	// FIELD: mapAutoUpdating
	private transient boolean mapAutoUpdating;
	
	// FIELD: autoClaimEnabled
	private transient boolean autoClaimEnabled;
	public boolean isAutoClaimEnabled()
	{
		if (this.factionId.equals("0")) return false;
		return autoClaimEnabled;
	}
	public void setIsAutoClaimEnabled(boolean enabled)
	{
		this.autoClaimEnabled = enabled;
		if (enabled)
		{
			this.autoSafeZoneEnabled = false;
			this.autoWarZoneEnabled = false;
		}
	}
	
	// FIELD: autoSafeZoneEnabled
	private transient boolean autoSafeZoneEnabled;
	public boolean isAutoSafeClaimEnabled() { return autoSafeZoneEnabled; }
	public void setIsAutoSafeClaimEnabled(boolean enabled)
	{
		this.autoSafeZoneEnabled = enabled;
		if (enabled)
		{
			this.autoClaimEnabled = false;
			this.autoWarZoneEnabled = false;
		}
	}

	// FIELD: autoWarZoneEnabled
	private transient boolean autoWarZoneEnabled;
	public boolean isAutoWarClaimEnabled() { return autoWarZoneEnabled; }
	public void setIsAutoWarClaimEnabled(boolean enabled)
	{
		this.autoWarZoneEnabled = enabled;
		if (enabled)
		{
			this.autoClaimEnabled = false;
			this.autoSafeZoneEnabled = false;
		}
	}
	
	// FIELD: loginPvpDisabled
	private transient boolean loginPvpDisabled;
	
	// FIELD: deleteMe
	private transient boolean deleteMe;
	
	// FIELD: chatMode
	private ChatMode chatMode;
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	// GSON need this noarg constructor.
	public FPlayer()
	{
		this.resetFactionData();
		this.power = this.getPowerMax();
		this.lastPowerUpdateTime = System.currentTimeMillis();
		this.lastLoginTime = System.currentTimeMillis();
		this.mapAutoUpdating = false;
		this.autoClaimEnabled = false;
		this.autoSafeZoneEnabled = false;
		this.autoWarZoneEnabled = false;
		this.loginPvpDisabled = (Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0) ? true : false;
		this.deleteMe = false;

		if ( ! Conf.newPlayerStartingFactionID.equals("0") && Factions.i.exists(Conf.newPlayerStartingFactionID))
		{
			this.factionId = Conf.newPlayerStartingFactionID;
		}
	}
	
	public void resetFactionData()
	{
		// clean up any territory ownership in old faction, if there is one
		Faction currentFaction = this.getFaction();
		
		if (currentFaction != null && currentFaction.isNormal())
		{
			currentFaction.clearClaimOwnership(this.getId());
		}
		
		this.factionId = "0"; // The default neutral faction
		this.chatMode = ChatMode.PUBLIC;
		this.role = Role.NORMAL;
		this.title = "";
		this.autoClaimEnabled = false;

		SpoutFeatures.updateAppearances(this.getPlayer());
	}
	
	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //
	
	
	
	
	
	
	public ChatMode getChatMode()
	{
		if(this.factionId.equals("0"))
		{
			return ChatMode.PUBLIC;
		}
		return chatMode;
	}

	public void setChatMode(ChatMode chatMode)
	{
		this.chatMode = chatMode;
	}
	
	public long getLastLoginTime()
	{
		return lastLoginTime;
	}

	

	public void setLastLoginTime(long lastLoginTime)
	{
		losePowerFromBeingOffline();
		this.lastLoginTime = lastLoginTime;
		this.lastPowerUpdateTime = lastLoginTime;
		if (Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0)
		{
			this.loginPvpDisabled = true;
		}
	}

	public boolean isMapAutoUpdating()
	{
		return mapAutoUpdating;
	}

	public void setMapAutoUpdating(boolean mapAutoUpdating)
	{
		this.mapAutoUpdating = mapAutoUpdating;
	}

	public boolean hasLoginPvpDisabled()
	{
		if (!loginPvpDisabled)
		{
			return false;
		}
		if (this.lastLoginTime + (Conf.noPVPDamageToOthersForXSecondsAfterLogin * 1000) < System.currentTimeMillis())
		{
			this.loginPvpDisabled = false;
			return false;
		}
		return true;
	}
	
	public FLocation getLastStoodAt()
	{
		return this.lastStoodAt;
	}
	
	public void setLastStoodAt(FLocation flocation)
	{
		this.lastStoodAt = flocation;
	}

	public void markForDeletion(boolean delete)
	{
		deleteMe = delete;
	}
	
	//----------------------------------------------//
	// Title, Name, Faction Tag and Chat
	//----------------------------------------------//
	
	// Base:
	
	public String getTitle()
	{
		return this.title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	public String getName()
	{
		return this.getId(); // TODO: ... display name or remove completeley
	}
	
	public String getTag()
	{
		if ( ! this.hasFaction())
		{
			return "";
		}
		return this.getFaction().getTag();
	}
	
	// Base concatenations:
	
	public String getNameAndSomething(String something)
	{
		String ret = this.role.getPrefix();
		if (something.length() > 0) {
			ret += something+" ";
		}
		ret += this.getName();
		return ret;
	}
	
	public String getNameAndTitle()
	{
		return this.getNameAndSomething(this.getTitle());
	}
	
	public String getNameAndTag()
	{
		return this.getNameAndSomething(this.getTag());
	}
	
	// Colored concatenations:
	// These are used in information messages
	
	public String getNameAndTitle(Faction faction)
	{
		return this.getRelationColor(faction)+this.getNameAndTitle();
	}
	public String getNameAndTitle(FPlayer fplayer)
	{
		return this.getRelationColor(fplayer)+this.getNameAndTitle();
	}
	
	public String getNameAndTag(Faction faction)
	{
		return this.getRelationColor(faction)+this.getNameAndTag();
	}
	public String getNameAndTag(FPlayer fplayer)
	{
		return this.getRelationColor(fplayer)+this.getNameAndTag();
	}
	
	public String getNameAndRelevant(Faction faction)
	{
		// Which relation?
		Relation rel = this.getRelation(faction);
		
		// For member we show title
		if (rel == Relation.MEMBER) {
			return rel.getColor() + this.getNameAndTitle();
		}
		
		// For non members we show tag
		return rel.getColor() + this.getNameAndTag();
	}
	public String getNameAndRelevant(FPlayer fplayer)
	{
		return getNameAndRelevant(fplayer.getFaction());
	}
	
	// Chat Tag: 
	// These are injected into the format of global chat messages.
	
	public String getChatTag()
	{
		if ( ! this.hasFaction()) {
			return "";
		}
		
		return String.format(Conf.chatTagFormat, this.role.getPrefix()+this.getTag());
	}
	
	// Colored Chat Tag
	public String getChatTag(Faction faction)
	{
		if ( ! this.hasFaction()) {
			return "";
		}
		
		return this.getRelation(faction).getColor()+getChatTag();
	}
	
	public String getChatTag(FPlayer fplayer)
	{
		if ( ! this.hasFaction()) {
			return "";
		}
		
		return this.getRelation(fplayer).getColor()+getChatTag();
	}
	
	// -------------------------------
	// Relation and relation colors
	// -------------------------------
	
	public Relation getRelation(Faction faction)
	{
		return faction.getRelation(this);
	}
	
	public Relation getRelation(FPlayer fplayer)
	{
		return this.getFaction().getRelation(fplayer);
	}
	
	public Relation getRelationToLocation()
	{
		return Board.getFactionAt(new FLocation(this)).getRelation(this);
	}
	
	public ChatColor getRelationColor(Faction faction)
	{
		return faction.getRelationColor(this);
	}
	
	public ChatColor getRelationColor(FPlayer fplayer)
	{
		return this.getRelation(fplayer).getColor();
	}
	
	
	//----------------------------------------------//
	// Health
	//----------------------------------------------//
	public void heal(int amnt)
	{
		Player player = this.getPlayer();
		if (player == null)
		{
			return;
		}
		player.setHealth(player.getHealth() + amnt);
	}
	
	
	//----------------------------------------------//
	// Power
	//----------------------------------------------//
	public double getPower()
	{
		this.updatePower();
		return this.power;
	}
	
	protected void alterPower(double delta)
	{
		this.power += delta;
		if (this.power > this.getPowerMax())
		{
			this.power = this.getPowerMax();
		} else if (this.power < this.getPowerMin())
		{
			this.power = this.getPowerMin();
		}
		//Log.debug("Power of "+this.getName()+" is now: "+this.power);
	}
	
	public double getPowerMax()
	{
		return Conf.powerPlayerMax;
	}
	
	public double getPowerMin()
	{
		return Conf.powerPlayerMin;
	}
	
	public int getPowerRounded()
	{
		return (int) Math.round(this.getPower());
	}
	
	public int getPowerMaxRounded()
	{
		return (int) Math.round(this.getPowerMax());
	}
	
	public int getPowerMinRounded()
	{
		return (int) Math.round(this.getPowerMin());
	}
	
	protected void updatePower()
	{
		if (this.isOffline())
		{
			losePowerFromBeingOffline();
			if (!Conf.powerRegenOffline)
			{
				return;
			}
		}
		long now = System.currentTimeMillis();
		long millisPassed = now - this.lastPowerUpdateTime;
		this.lastPowerUpdateTime = now;
		
		int millisPerMinute = 60*1000;
		this.alterPower(millisPassed * Conf.powerPerMinute / millisPerMinute);
	}

	protected void losePowerFromBeingOffline()
	{
		if (Conf.powerOfflineLossPerDay > 0.0 && this.power > Conf.powerOfflineLossLimit)
		{
			long now = System.currentTimeMillis();
			long millisPassed = now - this.lastPowerUpdateTime;
			this.lastPowerUpdateTime = now;

			double loss = millisPassed * Conf.powerOfflineLossPerDay / (24*60*60*1000);
			if (this.power - loss < Conf.powerOfflineLossLimit)
			{
				loss = this.power;
			}
			this.alterPower(-loss);
		}
	}
	
	public void onDeath()
	{
		this.updatePower();
		this.alterPower(-Conf.powerPerDeath);
	}
	
	//----------------------------------------------//
	// Territory
	//----------------------------------------------//
	public boolean isInOwnTerritory()
	{
		return Board.getFactionAt(new FLocation(this)) == this.getFaction();
	}
	
	public boolean isInOthersTerritory()
	{
		Faction factionHere = Board.getFactionAt(new FLocation(this));
		return factionHere != null && factionHere.isNormal() && factionHere != this.getFaction();
	}

	public boolean isInAllyTerritory()
	{
		return Board.getFactionAt(new FLocation(this)).getRelation(this).isAlly();
	}

	public boolean isInNeutralTerritory()
	{
		return Board.getFactionAt(new FLocation(this)).getRelation(this).isNeutral();
	}

	public boolean isInEnemyTerritory()
	{
		return Board.getFactionAt(new FLocation(this)).getRelation(this).isEnemy();
	}

	public void sendFactionHereMessage()
	{
		if (SpoutFeatures.updateTerritoryDisplay(this))
		{
			return;
		}
		Faction factionHere = Board.getFactionAt(new FLocation(this));
		String msg = P.p.txt.parse("<i>")+" ~ "+factionHere.getTag(this);
		if (factionHere.getDescription().length() > 0)
		{
			msg += " - "+factionHere.getDescription();
		}
		this.sendMessage(msg);
	}
	
	// -------------------------------
	// Actions
	// -------------------------------
	
	public void leave(boolean makePay)
	{
		Faction myFaction = this.getFaction();
		boolean perm = myFaction.isPermanent();
		
		if (!perm && this.getRole() == Role.ADMIN && myFaction.getFPlayers().size() > 1)
		{
			sendMessage("You must give the admin role to someone else first.");
			return;
		}

		if (!Conf.CanLeaveWithNegativePower && this.getPower() < 0)
		{
			sendMessage("You cannot leave until your power is positive.");
			return;
		}

		// if economy is enabled and they're not on the bypass list, make 'em pay
		if (makePay && Econ.enabled() && !Conf.adminBypassPlayers.contains(this.getId()))
		{
			double cost = Conf.econCostLeave;
			// pay up
			if (cost > 0.0) {
				String costString = Econ.moneyString(cost);
				if (!Econ.deductMoney(this.getName(), cost)) {
					sendMessage("It costs "+costString+" to leave your faction, which you can't currently afford.");
					return;
				}
				sendMessage("You have paid "+costString+" to leave your faction.");
			}
			// wait... we pay you to leave?
			else if (cost < 0.0)
			{
				String costString = Econ.moneyString(-cost);
				Econ.addMoney(this.getName(), -cost);
				sendMessage("You have been paid "+costString+" for leaving your faction.");
			}
		}

		if (myFaction.isNormal())
		{
			myFaction.sendMessage(P.p.txt.parse(this.getNameAndRelevant(myFaction) + "<i> left your faction."));
		}

		this.resetFactionData();

		if (myFaction.isNormal() && !perm && myFaction.getFPlayers().isEmpty())
		{
			// Remove this faction
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				fplayer.sendMessage(P.p.txt.parse("The faction "+myFaction.getTag(fplayer)+"<i> was disbanded."));
			}
			//Faction.delete(myFaction.getId());
			this.detach();
		}
	}
	
	public boolean attemptClaim(boolean notifyFailure)
	{
		// notifyFailure is false if called by auto-claim; no need to notify on every failure for it
		// return value is false on failure, true on success

		Faction myFaction = getFaction();
		Location loc = this.getPlayer().getLocation();
		FLocation flocation = new FLocation(loc);
		Faction otherFaction = Board.getFactionAt(flocation);

		if (Conf.worldGuardChecking && Worldguard.checkForRegionsInChunk(loc))
		{
			// Checks for WorldGuard regions in the chunk attempting to be claimed
			sendMessage("This land is protected");
			return false;
		}

		if (myFaction == otherFaction)
		{
			if (notifyFailure)
				sendMessage("You already own this land.");
			return false;
		}

		if (this.getRole().value < Role.MODERATOR.value)
		{
			sendMessage("You must be "+Role.MODERATOR+" to claim land.");
			return false;
		}

		if (myFaction.getFPlayers().size() < Conf.claimsRequireMinFactionMembers && !Conf.adminBypassPlayers.contains(this.getId()))
		{
			sendMessage("Your faction must have at least "+Conf.claimsRequireMinFactionMembers+" members to claim land.");
			return false;
		}

		if (Conf.worldsNoClaiming.contains(flocation.getWorldName()))
		{
			sendMessage("Sorry, this world has land claiming disabled.");
			return false;
		}
		
		if (otherFaction.isSafeZone())
		{
			if (notifyFailure)
				sendMessage("You can not claim a Safe Zone.");
			return false;
		}
		else if (otherFaction.isWarZone())
		{
			if (notifyFailure)
				sendMessage("You can not claim a War Zone.");
			return false;
		}

		int ownedLand = myFaction.getLandRounded();
		if (ownedLand >= myFaction.getPowerRounded())
		{
			sendMessage("You can't claim more land! You need more power!");
			return false;
		}

		if (otherFaction.getRelation(this) == Relation.ALLY)
		{
			if (notifyFailure)
				sendMessage("You can't claim the land of your allies.");
			return false;
		}

		if
		(
			Conf.claimsMustBeConnected
			&& !Conf.adminBypassPlayers.contains(this.getId())
			&& myFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0
			&& !Board.isConnectedLocation(flocation, myFaction)
			&& (!Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction || !otherFaction.isNormal())
		)
		{
			if (Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction)
				sendMessage("You can only claim additional land which is connected to your first claim or controlled by another faction!");
			else
				sendMessage("You can only claim additional land which is connected to your first claim!");
			return false;
		}

		if (otherFaction.isNormal())
		{
			if (myFaction.isPeaceful())
			{
				sendMessage(P.p.txt.parse(this.getRelationColor(otherFaction)+otherFaction.getTag()+"<i> owns this land. Your faction is peaceful, so you cannot claim land from other factions."));
				return false;
			}
			
			if (otherFaction.isPeaceful())
			{
				sendMessage(P.p.txt.parse(this.getRelationColor(otherFaction)+otherFaction.getTag()+"<i> owns this land, and is a peaceful faction. You cannot claim land from them."));
				return false;
			}

			if ( ! otherFaction.hasLandInflation())
			{
				 // TODO more messages WARN current faction most importantly
				sendMessage(P.p.txt.parse(this.getRelationColor(otherFaction)+otherFaction.getTag()+"<i> owns this land and is strong enough to keep it."));
				return false;
			}

			if ( ! Board.isBorderLocation(flocation))
			{
				sendMessage("You must start claiming land at the border of the territory.");
				return false;
			}
		}

		// if economy is enabled and they're not on the bypass list, make 'em pay
		if (Econ.enabled() && !Conf.adminBypassPlayers.contains(this.getId()))
		{
			double cost = Econ.calculateClaimCost(ownedLand, otherFaction.isNormal());
			String costString = Econ.moneyString(cost);
			
			if(Conf.bankFactionPaysLandCosts && this.hasFaction())
			{
				Faction faction = this.getFaction();
				
				if(!faction.removeMoney(cost))
				{
					sendMessage("It costs "+costString+" to claim this land, which your faction can't currently afford.");
					return false;
				}
				else
				{
					sendMessage(faction.getTag()+" has paid "+costString+" to claim some land.");
				}
			}
			else
			{
				if (!Econ.deductMoney(this.getId(), cost))
				{
					sendMessage("Claiming this land will cost "+costString+", which you can't currently afford.");
					return false;
				}
				sendMessage("You have paid "+costString+" to claim this land.");
			}
		}

		// announce success
		if (otherFaction.isNormal())
		{
			// ASDF claimed some of your land 450 blocks NNW of you.
			// ASDf claimed some land from FACTION NAME
			otherFaction.sendMessage(P.p.txt.parse(this.getNameAndRelevant(otherFaction)+"<i> stole some of your land :O"));
			myFaction.sendMessage(P.p.txt.parse(this.getNameAndRelevant(myFaction)+"<i> claimed some land from "+otherFaction.getTag(myFaction)));
		}
		else
		{
			myFaction.sendMessage(P.p.txt.parse(this.getNameAndRelevant(myFaction)+"<i> claimed some new land :D"));
		}

		Board.setFactionAt(myFaction, flocation);
		return true;
	}

	// -------------------------------------------- //
	// Get and search
	// -------------------------------------------- //
	
	/*private static FPlayer get(String playerName)
	{
		if (instances.containsKey(playerName))
		{
			return instances.get(playerName);
		}
		
		FPlayer vplayer = new FPlayer();
		vplayer.playerName = playerName;
		
		instances.put(playerName, vplayer);
		return vplayer;
	}*/
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	@Override
	public boolean shouldBeSaved()
	{
		return ! this.deleteMe;
	}
	
	/*
	public static boolean save()
	{
		//Factions.log("Saving players to disk");
		
		// We only wan't to save the players with non default values
		Map<String, FPlayer> playersToSave = new HashMap<String, FPlayer>();
		for (Entry<String, FPlayer> entry : instances.entrySet()) {
			if (entry.getValue().shouldBeSaved()) {
				playersToSave.put(entry.getKey(), entry.getValue());
			}
		}
		
		try {
			DiscUtil.write(file, P.p.gson.toJson(playersToSave));
		} catch (Exception e) {
			e.printStackTrace();
			P.log("Failed to save the players to disk.");
			return false;
		}
		return true;
	}
	
	public static boolean load() {
		P.log("Loading players from disk");
		if ( ! file.exists()) {
			if ( ! loadOld())
				P.log("No players to load from disk. Creating new file.");
			save();
			return true;
		}
		
		try {
			Type type = new TypeToken<Map<String, FPlayer>>(){}.getType();
			Map<String, FPlayer> instancesFromFile = P.p.gson.fromJson(DiscUtil.read(file), type);
			instances.clear();
			instances.putAll(instancesFromFile);
		} catch (Exception e) {
			e.printStackTrace();
			P.log("Failed to load the players from disk.");
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
	*/
	

	/*private static boolean loadOld()
	{
		File folderFollower = new File(P.p.getDataFolder(), "follower");

		if ( ! folderFollower.isDirectory()) return false;

		p.log("Players file doesn't exist, attempting to load old pre-1.1 data.");

		String ext = ".json";

		class jsonFileFilter implements FileFilter
		{
			@Override
			public boolean accept(File file)
			{
				return (file.getName().toLowerCase().endsWith(".json") && file.isFile());
			}
		}

		File[] jsonFiles = folderFollower.listFiles(new jsonFileFilter());

		for (File jsonFile : jsonFiles) {
			// Extract the name from the filename. The name is filename minus ".json"
			String name = jsonFile.getName();
			name = name.substring(0, name.length() - ext.length());
			try {
				FPlayer follower = P.p.gson.fromJson(DiscUtil.read(jsonFile), FPlayer.class);
				follower.playerName = name;
				follower.lastLoginTime = System.currentTimeMillis();
				instances.put(follower.playerName, follower);
				P.log("loaded pre-1.1 follower "+name);
			} catch (Exception e) {
				e.printStackTrace();
				P.log(Level.WARNING, "failed to load follower "+name);
			}
		}
		return true;
	}*/
	
	public void sendMessageParsed(String str, Object... args)
	{
		this.sendMessage(P.p.txt.parse(str, args));
	}
}