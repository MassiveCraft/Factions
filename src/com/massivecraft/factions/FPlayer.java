package com.massivecraft.factions;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.RelationUtil;
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
		Faction oldFaction = this.getFaction();
		if (oldFaction != null) oldFaction.removeFPlayer(this);
		faction.addFPlayer(this);
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

	// FIELD: powerBoost
	// special increase/decrease to min and max power for this player
	private double powerBoost;
	public double getPowerBoost() { return this.powerBoost; }
	public void setPowerBoost(double powerBoost) { this.powerBoost = powerBoost; }

	// FIELD: lastPowerUpdateTime
	private long lastPowerUpdateTime;
	
	// FIELD: lastLoginTime
	private long lastLoginTime;
	
	// FIELD: mapAutoUpdating
	private transient boolean mapAutoUpdating;
	
	// FIELD: autoClaimEnabled
	private transient Faction autoClaimFor;
	public Faction getAutoClaimFor()
	{
		return autoClaimFor;
	}
	public void setAutoClaimFor(Faction faction)
	{
		this.autoClaimFor = faction;
		if (this.autoClaimFor != null)
		{
			// TODO: merge these into same autoclaim
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
			this.autoClaimFor = null;
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
			this.autoClaimFor = null;
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
	public void setChatMode(ChatMode chatMode) { this.chatMode = chatMode; }
	public ChatMode getChatMode()
	{
		if(this.factionId.equals("0") || ! Conf.factionOnlyChat)
		{
			this.chatMode = ChatMode.PUBLIC;
		}
		return chatMode;
	}

	// FIELD: chatSpy
	private transient boolean spyingChat = false;
	public void setSpyingChat(boolean chatSpying) { this.spyingChat = chatSpying; }
	public boolean isSpyingChat() { return spyingChat; }

	// FIELD: account
	public String getAccountId() { return this.getId(); }
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	// GSON need this noarg constructor.
	public FPlayer()
	{
		this.resetFactionData(false);
		this.power = Conf.powerPlayerStarting;
		this.lastPowerUpdateTime = System.currentTimeMillis();
		this.lastLoginTime = System.currentTimeMillis();
		this.mapAutoUpdating = false;
		this.autoClaimFor = null;
		this.autoSafeZoneEnabled = false;
		this.autoWarZoneEnabled = false;
		this.loginPvpDisabled = (Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0) ? true : false;
		this.deleteMe = false;
		this.powerBoost = 0.0;

		if ( ! Conf.newPlayerStartingFactionID.equals("0") && Factions.i.exists(Conf.newPlayerStartingFactionID))
		{
			this.factionId = Conf.newPlayerStartingFactionID;
		}
	}
	
	public final void resetFactionData(boolean doSpoutUpdate)
	{
		// clean up any territory ownership in old faction, if there is one
		if (Factions.i.exists(this.getFactionId()))
		{
			Faction currentFaction = this.getFaction();
			currentFaction.removeFPlayer(this);
			if (currentFaction.isNormal())
			{
				currentFaction.clearClaimOwnership(this.getId());
			}
		}
		
		this.factionId = "0"; // The default neutral faction
		this.chatMode = ChatMode.PUBLIC;
		this.role = Role.NORMAL;
		this.title = "";
		this.autoClaimFor = null;

		if (doSpoutUpdate)
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
		return this.getColorTo(faction)+this.getNameAndTitle();
	}
	public String getNameAndTitle(FPlayer fplayer)
	{
		return this.getColorTo(fplayer)+this.getNameAndTitle();
	}
	
	/*public String getNameAndTag(Faction faction)
	{
		return this.getRelationColor(faction)+this.getNameAndTag();
	}
	public String getNameAndTag(FPlayer fplayer)
	{
		return this.getRelationColor(fplayer)+this.getNameAndTag();
	}*/
	
	// TODO: REmovded for refactoring.
	
	/*public String getNameAndRelevant(Faction faction)
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
	}*/
	
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
		if ( ! this.hasFaction())
		{
			return "";
		}
		
		return this.getColorTo(fplayer)+getChatTag();
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
	public ChatColor getColorTo(RelationParticipator rp)
	{
		return RelationUtil.getColorOfThatToMe(this, rp);
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
			this.power = this.getPowerMax();
		else if (this.power < this.getPowerMin())
			this.power = this.getPowerMin();
	}
	
	public double getPowerMax()
	{
		return Conf.powerPlayerMax + this.powerBoost;
	}
	
	public double getPowerMin()
	{
		return Conf.powerPlayerMin + this.powerBoost;
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

		Player thisPlayer = this.getPlayer();
		if (thisPlayer != null && thisPlayer.isDead()) return;  // don't let dead players regain power until they respawn

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
		Faction factionHere = Board.getFactionAt(this.getLastStoodAt());
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
		makePay = makePay && Econ.shouldBeUsed() && ! this.isAdminBypassing();

		if (myFaction == null)
		{
			resetFactionData();
			return;
		}

		boolean perm = myFaction.isPermanent();
		
		if (!perm && this.getRole() == Role.ADMIN && myFaction.getFPlayers().size() > 1)
		{
			msg("<b>You must give the admin role to someone else first.");
			return;
		}

		if (!Conf.canLeaveWithNegativePower && this.getPower() < 0)
		{
			msg("<b>You cannot leave until your power is positive.");
			return;
		}

		// if economy is enabled and they're not on the bypass list, make sure they can pay
		if (makePay && ! Econ.hasAtLeast(this, Conf.econCostLeave, "to leave your faction.")) return;

		FPlayerLeaveEvent leaveEvent = new FPlayerLeaveEvent(this,myFaction,FPlayerLeaveEvent.PlayerLeaveReason.LEAVE);
		Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
		if (leaveEvent.isCancelled()) return;

		// then make 'em pay (if applicable)
		if (makePay && ! Econ.modifyMoney(this, -Conf.econCostLeave, "to leave your faction.", "for leaving your faction.")) return;

		// Am I the last one in the faction?
		if (myFaction.getFPlayers().size() == 1)
		{
			// Transfer all money
			if (Econ.shouldBeUsed())
				Econ.transferMoney(this, myFaction, this, Econ.getBalance(myFaction.getAccountId()));
		}
		
		if (myFaction.isNormal())
		{
			for (FPlayer fplayer : myFaction.getFPlayersWhereOnline(true))
			{
				fplayer.msg("%s<i> left %s<i>.", this.describeTo(fplayer, true), myFaction.describeTo(fplayer));
			}

			if (Conf.logFactionLeave)
				P.p.log(this.getName()+" left the faction: "+myFaction.getTag());
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
			if (Conf.logFactionDisband)
				P.p.log("The faction "+myFaction.getTag()+" ("+myFaction.getId()+") was disbanded due to the last player ("+this.getName()+") leaving.");
		}
	}
	
	public boolean canClaimForFaction(Faction forFaction)
	{
		if (forFaction.isNone()) return false;

		if
		(
			   this.isAdminBypassing()
			|| (forFaction == this.getFaction() && this.getRole().isAtLeast(Role.MODERATOR))
			|| (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer()))
			|| (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer()))
		)
		{
			return true;
		}

		return false;
	}

	public boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure)
	{
		String error = null;
		FLocation flocation = new FLocation(location);
		Faction myFaction = getFaction();
		Faction currentFaction = Board.getFactionAt(flocation);
		int ownedLand = forFaction.getLandRounded();
		
		if (Conf.worldGuardChecking && Worldguard.checkForRegionsInChunk(location))
		{
			// Checks for WorldGuard regions in the chunk attempting to be claimed
			error = P.p.txt.parse("<b>This land is protected");
		}
		else if (Conf.worldsNoClaiming.contains(flocation.getWorldName()))
		{
			error = P.p.txt.parse("<b>Sorry, this world has land claiming disabled.");
		}
		else if (this.isAdminBypassing())
		{
			return true;
		}
		else if (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer()))
		{
			return true;
		}
		else if (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer()))
		{
			return true;
		}
		else if (myFaction != forFaction)
		{
			error = P.p.txt.parse("<b>You can't claim land for <h>%s<b>.", forFaction.describeTo(this));
		}
		else if (forFaction == currentFaction)
		{
			error = P.p.txt.parse("%s<i> already own this land.", forFaction.describeTo(this, true));
		}
		else if (this.getRole().value < Role.MODERATOR.value)
		{
			error = P.p.txt.parse("<b>You must be <h>%s<b> to claim land.", Role.MODERATOR.toString());
		}
		else if (forFaction.getFPlayers().size() < Conf.claimsRequireMinFactionMembers)
		{
			error = P.p.txt.parse("Factions must have at least <h>%s<b> members to claim land.", Conf.claimsRequireMinFactionMembers);
		}
		else if (currentFaction.isSafeZone())
		{
			error = P.p.txt.parse("<b>You can not claim a Safe Zone.");
		}
		else if (currentFaction.isWarZone())
		{
			error = P.p.txt.parse("<b>You can not claim a War Zone.");
		}
		else if (ownedLand >= forFaction.getPowerRounded())
		{
			error = P.p.txt.parse("<b>You can't claim more land! You need more power!");
		}
		else if (Conf.claimedLandsMax != 0 && ownedLand >= Conf.claimedLandsMax && forFaction.isNormal())
		{
			error = P.p.txt.parse("<b>Limit reached. You can't claim more land!");
		}
		else if (currentFaction.getRelationTo(forFaction) == Relation.ALLY)
		{
			error = P.p.txt.parse("<b>You can't claim the land of your allies.");
		}
		else if
		(
			Conf.claimsMustBeConnected
			&& ! this.isAdminBypassing()
			&& myFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0
			&& !Board.isConnectedLocation(flocation, myFaction)
			&& (!Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction || !currentFaction.isNormal())
		)
		{
			if (Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction)
				error = P.p.txt.parse("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!");
			else
				error = P.p.txt.parse("<b>You can only claim additional land which is connected to your first claim!");
		}
		else if (currentFaction.isNormal())
		{
			if (myFaction.isPeaceful())
			{
				error = P.p.txt.parse("%s<i> owns this land. Your faction is peaceful, so you cannot claim land from other factions.", currentFaction.getTag(this));
			}
			else if (currentFaction.isPeaceful())
			{
				error = P.p.txt.parse("%s<i> owns this land, and is a peaceful faction. You cannot claim land from them.", currentFaction.getTag(this));
			}
			else if ( ! currentFaction.hasLandInflation())
			{
				 // TODO more messages WARN current faction most importantly
				error = P.p.txt.parse("%s<i> owns this land and is strong enough to keep it.", currentFaction.getTag(this));
			}
			else if ( ! Board.isBorderLocation(flocation))
			{
				error = P.p.txt.parse("<b>You must start claiming land at the border of the territory.");
			}
		}
		
		if (notifyFailure && error != null)
		{
			msg(error);
		}
		return error == null;
	}
	
	public boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure)
	{
		// notifyFailure is false if called by auto-claim; no need to notify on every failure for it
		// return value is false on failure, true on success
		
		FLocation flocation = new FLocation(location);
		Faction currentFaction = Board.getFactionAt(flocation);
		
		int ownedLand = forFaction.getLandRounded();
		
		if ( ! this.canClaimForFactionAtLocation(forFaction, location, notifyFailure)) return false;

		// if economy is enabled and they're not on the bypass list, make sure they can pay
		boolean mustPay = Econ.shouldBeUsed() && ! this.isAdminBypassing() && ! forFaction.isSafeZone() && ! forFaction.isWarZone();
		double cost = 0.0;
		EconomyParticipator payee = null;
		if (mustPay)
		{
			cost = Econ.calculateClaimCost(ownedLand, currentFaction.isNormal());

			if (Conf.econClaimUnconnectedFee != 0.0 && forFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0 && !Board.isConnectedLocation(flocation, forFaction))
				cost += Conf.econClaimUnconnectedFee;

			if(Conf.bankEnabled && Conf.bankFactionPaysLandCosts && this.hasFaction())
				payee = this.getFaction();
			else
				payee = this;

			if ( ! Econ.hasAtLeast(payee, cost, "to claim this land")) return false;
		}

		LandClaimEvent claimEvent = new LandClaimEvent(flocation, forFaction, this);
		Bukkit.getServer().getPluginManager().callEvent(claimEvent);
		if(claimEvent.isCancelled()) return false;

		// then make 'em pay (if applicable)
		if (mustPay && ! Econ.modifyMoney(payee, -cost, "to claim this land", "for claiming this land")) return false;

		if (LWCFeatures.getEnabled() && forFaction.isNormal() && Conf.onCaptureResetLwcLocks)
		{
			LWCFeatures.clearOtherChests(flocation, this.getFaction());
		}
		
		// announce success
		Set<FPlayer> informTheseFPlayers = new HashSet<FPlayer>();
		informTheseFPlayers.add(this);
		informTheseFPlayers.addAll(forFaction.getFPlayersWhereOnline(true));
		for (FPlayer fp : informTheseFPlayers)
		{
			fp.msg("<h>%s<i> claimed land for <h>%s<i> from <h>%s<i>.", this.describeTo(fp, true), forFaction.describeTo(fp), currentFaction.describeTo(fp));
		}
		
		Board.setFactionAt(forFaction, flocation);
		SpoutFeatures.updateTerritoryDisplayLoc(flocation);

		if (Conf.logLandClaims)
			P.p.log(this.getName()+" claimed land at ("+flocation.getCoordString()+") for the faction: "+forFaction.getTag());

		return true;
	}
	
	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //
	
	@Override
	public boolean shouldBeSaved()
	{
		if (!this.hasFaction() &&
			(this.getPowerRounded() == this.getPowerMaxRounded() || this.getPowerRounded() == (int) Math.round(Conf.powerPlayerStarting))
			)
			return false;
		return ! this.deleteMe;
	}
	
	public void msg(String str, Object... args)
	{
		this.sendMessage(P.p.txt.parse(str, args));
	}
}