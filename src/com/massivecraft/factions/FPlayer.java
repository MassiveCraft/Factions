package com.massivecraft.factions;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.zcore.persist.PlayerEntity;
import com.nijikokun.register.payment.Method.MethodAccount;


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

public class FPlayer extends PlayerEntity implements EconomyParticipator
{	
	//private transient String playerName;
	private transient FLocation lastStoodAt = new FLocation(); // Where did this player stand the last time we checked?
	
	// FIELD: factionId
	private String factionId;
	public Faction getFaction() { if(this.factionId == null) {return null;} return Factions.i.get(this.factionId); }
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
	
	private transient boolean isAdminBypassing = false;
	public boolean isAdminBypassing() { return this.isAdminBypassing; }
	public void setIsAdminBypassing(boolean val) { this.isAdminBypassing = val; }
	
	// FIELD: loginPvpDisabled
	private transient boolean loginPvpDisabled;
	
	// FIELD: deleteMe
	private transient boolean deleteMe;
	
	// FIELD: chatMode
	private ChatMode chatMode;
	
	// FIELD: account
	public MethodAccount getAccount()
	{
		if ( ! Econ.shouldBeUsed()) return null;
		return Econ.getMethod().getAccount(this.getId());
	}
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	// GSON need this noarg constructor.
	public FPlayer()
	{
		this.resetFactionData(false);
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
	
	public final void resetFactionData(boolean doSpotUpdate)
	{
		// clean up any territory ownership in old faction, if there is one
		if (Factions.i.exists(this.getFactionId()))
		{
			Faction currentFaction = this.getFaction();
			if (currentFaction.isNormal())
			{
				currentFaction.clearClaimOwnership(this.getId());
			}
		}
		
		this.factionId = "0"; // The default neutral faction
		this.chatMode = ChatMode.PUBLIC;
		this.role = Role.NORMAL;
		this.title = "";
		this.autoClaimEnabled = false;

		if (doSpotUpdate)
		{
			SpoutFeatures.updateAppearances(this.getPlayer());
		}
	}
	
	public void resetFactionData()
	{
		this.resetFactionData(true);
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
		Relation rel = this.getRelationTo(faction);
		
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
		
		return this.getRelationTo(faction).getColor()+getChatTag();
	}
	
	public String getChatTag(FPlayer fplayer)
	{
		if ( ! this.hasFaction()) {
			return "";
		}
		
		return this.getRelationTo(fplayer).getColor()+getChatTag();
	}
	
	// -------------------------------
	// Relation and relation colors
	// -------------------------------
	
	@Override
	public String describeTo(RelationParticipator that, boolean ucfirst)
	{
		return RelationUtil.describeThatToMe(this, that, ucfirst);
	}
	
	@Override
	public String describeTo(RelationParticipator that)
	{
		return RelationUtil.describeThatToMe(this, that);
	}
	
	@Override
	public Relation getRelationTo(RelationParticipator rp)
	{
		return RelationUtil.getRelationTo(this, rp);
	}
	
	@Override
	public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful)
	{
		return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
	}
	
	public Relation getRelationToLocation()
	{
		return Board.getFactionAt(new FLocation(this)).getRelationTo(this);
	}
	
	@Override
	public ChatColor getRelationColor(RelationParticipator rp)
	{
		return RelationUtil.getRelationColor(this, rp);
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
		return Board.getFactionAt(new FLocation(this)).getRelationTo(this).isAlly();
	}

	public boolean isInNeutralTerritory()
	{
		return Board.getFactionAt(new FLocation(this)).getRelationTo(this).isNeutral();
	}

	public boolean isInEnemyTerritory()
	{
		return Board.getFactionAt(new FLocation(this)).getRelationTo(this).isEnemy();
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
			msg("<b>You must give the admin role to someone else first.");
			return;
		}

		if (!Conf.CanLeaveWithNegativePower && this.getPower() < 0)
		{
			msg("<b>You cannot leave until your power is positive.");
			return;
		}

		// if economy is enabled and they're not on the bypass list, make 'em pay
		if (makePay && Econ.shouldBeUsed() && ! this.isAdminBypassing())
		{
			double cost = Conf.econCostLeave;
			if ( ! Econ.modifyMoney(this, -cost, "to leave your faction.", "for leaving your faction.")) return;
		}

		// Am I the last one in the faction?
		ArrayList<FPlayer> fplayers = myFaction.getFPlayers();
		if (fplayers.size() == 1 && fplayers.get(0) == this)
		{
			// Transfer all money
			if (Econ.shouldBeUsed())
				Econ.transferMoney(this, myFaction, this, myFaction.getAccount().balance());
		}
		
		if (myFaction.isNormal())
		{
			for (FPlayer fplayer : myFaction.getFPlayersWhereOnline(true))
			{
				fplayer.msg("%s<i> left %s<i>.", this.describeTo(fplayer, true), myFaction.describeTo(fplayer));
			}
		}
		
		this.resetFactionData();

		if (myFaction.isNormal() && !perm && myFaction.getFPlayers().isEmpty())
		{
			// Remove this faction
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				fplayer.msg("<i>%s<i> was disbanded.", myFaction.describeTo(fplayer, true));
			}

			myFaction.detach();
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
			msg("<b>This land is protected");
			return false;
		}

		if (myFaction == otherFaction)
		{
			if (notifyFailure)
				msg("<i>You already own this land.");
			return false;
		}

		if (this.getRole().value < Role.MODERATOR.value)
		{
			msg("<i>You must be "+Role.MODERATOR+" to claim land.");
			return false;
		}

		if (myFaction.getFPlayers().size() < Conf.claimsRequireMinFactionMembers && ! this.isAdminBypassing())
		{
			msg("<b>Your faction must have at least <h>%s<b> members to claim land.", Conf.claimsRequireMinFactionMembers);
			return false;
		}

		if (Conf.worldsNoClaiming.contains(flocation.getWorldName()))
		{
			msg("<b>Sorry, this world has land claiming disabled.");
			return false;
		}
		
		if (otherFaction.isSafeZone())
		{
			if (notifyFailure)
				msg("<b>You can not claim a Safe Zone.");
			return false;
		}
		else if (otherFaction.isWarZone())
		{
			if (notifyFailure)
				msg("<b>You can not claim a War Zone.");
			return false;
		}

		int ownedLand = myFaction.getLandRounded();
		if (ownedLand >= myFaction.getPowerRounded())
		{
			msg("<b>You can't claim more land! You need more power!");
			return false;
		}

		if (otherFaction.getRelationTo(this) == Relation.ALLY)
		{
			if (notifyFailure)
				msg("<b>You can't claim the land of your allies.");
			return false;
		}

		if
		(
			Conf.claimsMustBeConnected
			&& ! this.isAdminBypassing()
			&& myFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0
			&& !Board.isConnectedLocation(flocation, myFaction)
			&& (!Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction || !otherFaction.isNormal())
		)
		{
			if (Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction)
				msg("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!");
			else
				msg("<b>You can only claim additional land which is connected to your first claim!");
			return false;
		}

		if (otherFaction.isNormal())
		{
			if (myFaction.isPeaceful())
			{
				msg("%s<i> owns this land. Your faction is peaceful, so you cannot claim land from other factions.", otherFaction.getTag(this));
				return false;
			}
			
			if (otherFaction.isPeaceful())
			{
				msg("%s<i> owns this land, and is a peaceful faction. You cannot claim land from them.", otherFaction.getTag(this));
				return false;
			}

			if ( ! otherFaction.hasLandInflation())
			{
				 // TODO more messages WARN current faction most importantly
				msg("%s<i> owns this land and is strong enough to keep it.", otherFaction.getTag(this));
				return false;
			}

			if ( ! Board.isBorderLocation(flocation))
			{
				msg("<b>You must start claiming land at the border of the territory.");
				return false;
			}
		}

		// if economy is enabled and they're not on the bypass list, make 'em pay
		if (Econ.shouldBeUsed() && ! this.isAdminBypassing())
		{
			double cost = Econ.calculateClaimCost(ownedLand, otherFaction.isNormal());
			//String costString = Econ.moneyString(cost);
			
			if(Conf.bankFactionPaysLandCosts && this.hasFaction())
			{
				Faction faction = this.getFaction();
				if ( ! Econ.modifyMoney(faction, -cost, "to claim this land", "for claiming this land")) return false;
				/*
				if( ! faction.removeMoney(cost))
				{
					msg("<b>It costs <h>%s<b> to claim this land, which your faction can't currently afford.", costString);
					return false;
				}
				else
				{
					// TODO: Only I can see this right?
					msg("%s<i> has paid <h>%s<i> to claim some land.", faction.getTag(this), costString);
				}*/
			}
			else
			{
				if ( ! Econ.modifyMoney(this, -cost, "to claim this land", "for claiming this land")) return false;
				/*if ( ! Econ.deductMoney(this.getId(), cost))
				{
					msg("<b>Claiming this land will cost <h>%s<b>, which you can't currently afford.", costString);
					return false;
				}
				sendMessage("You have paid "+costString+" to claim this land.");*/
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
	
	public void msg(String str, Object... args)
	{
		this.sendMessage(P.p.txt.parse(str, args));
	}
}