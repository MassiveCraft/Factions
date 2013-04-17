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
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.store.SenderEntity;
import com.massivecraft.mcore.util.Txt;


public class FPlayer extends SenderEntity<FPlayer> implements EconomyParticipator
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static FPlayer get(Object oid)
	{
		return FPlayerColl.get().get(oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public FPlayer load(FPlayer that)
	{
		this.setFactionId(that.factionId);
		this.setRole(that.role);
		this.setTitle(that.title);
		this.setPowerBoost(that.powerBoost);
		
		this.power = that.power;
		this.lastPowerUpdateTime = that.lastPowerUpdateTime;
		this.lastLoginTime = that.lastLoginTime;
		
		return this;
	}
	
	@Override
	public boolean isDefault()
	{
		if (this.hasFaction()) return false;
		
		// Note: we do not check role or title here since they mean nothing without a faction.
		
		// TODO: This line looks obnoxious, investigate it.
		if (this.getPowerRounded() != this.getPowerMaxRounded() && this.getPowerRounded() != (int) Math.round(ConfServer.powerPlayerStarting)) return false;
		
		if (this.hasPowerBoost()) return false;
		
		return true;
	}
	
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	// In this section of the source code we place the field declarations only.
	// Each field has it's own section further down since just the getter and setter logic takes up quite some place.
	
	// This is a foreign key.
	// A players always belongs to a faction.
	// If null the player belongs to the no-faction faction called Wilderness.
	private String factionId = null;
	
	// What role does the player have in the faction?
	// The default value here is MEMBER since that one would be one of the most common ones and our goal is to save database space.
	// A note to self is that we can not change it from member to anything else just because we feel like it, that would corrupt database content.
	private Rel role = null;
	
	// What title does the player have in the faction?
	// The title is just for fun. It's completely meaningless.
	// The default case is no title since it's what you start with and also the most common case.
	// The player title is similar to the faction description.
	// 
	// Question: Can the title contain chat colors?
	// Answer: Yes but in such case the policy is that they already must be parsed using Txt.parse.
	//          If they contain markup it should not be parsed in case we coded the system correctly.
	private String title = null;
	
	// Player usually do not have a powerboost. It defaults to 0.
	// The powerBoost is a custom increase/decrease to default and maximum power.
	// Note that player powerBoost and faction powerBoost are very similar.
	private Double powerBoost = null;
	
	// TODO
	// FIELD: power
	private double power;
	
	// TODO
	// FIELD: lastPowerUpdateTime
	private long lastPowerUpdateTime;
	
	// TODO
	// FIELD: lastLoginTime
	private long lastLoginTime;
	
	// -------------------------------------------- //
	// FIELDS: RAW TRANSIENT
	// -------------------------------------------- //
	
	// Where did this player stand the last time we checked?
	private transient PS currentChunk = null; 
	public PS getCurrentChunk() { return this.currentChunk; }
	public void setCurrentChunk(PS currentChunk) { this.currentChunk = currentChunk.getChunk(true); }
	
	// FIELD: mapAutoUpdating
	private transient boolean mapAutoUpdating = false;
	public void setMapAutoUpdating(boolean mapAutoUpdating) { this.mapAutoUpdating = mapAutoUpdating; }
	public boolean isMapAutoUpdating() { return mapAutoUpdating; }
	
	// FIELD: autoClaimEnabled
	private transient Faction autoClaimFor = null;
	public Faction getAutoClaimFor() { return autoClaimFor; }
	public void setAutoClaimFor(Faction faction) { this.autoClaimFor = faction; }
		
	private transient boolean usingAdminMode = false;
	public boolean isUsingAdminMode() { return this.usingAdminMode; }
	public void setUsingAdminMode(boolean val) { this.usingAdminMode = val; }
	
	// FIELD: loginPvpDisabled
	private transient boolean loginPvpDisabled;
	
	// FIELD: account
	public String getAccountId() { return this.getId(); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	// GSON need this noarg constructor.
	public FPlayer()
	{
		this.resetFactionData(false);
		this.power = ConfServer.powerPlayerStarting;
		this.lastPowerUpdateTime = System.currentTimeMillis();
		this.lastLoginTime = System.currentTimeMillis();
		this.loginPvpDisabled = (ConfServer.noPVPDamageToOthersForXSecondsAfterLogin > 0) ? true : false;

		if ( ! ConfServer.newPlayerStartingFactionID.equals(Const.FACTIONID_NONE) && FactionColl.get().containsId(ConfServer.newPlayerStartingFactionID))
		{
			this.factionId = ConfServer.newPlayerStartingFactionID;
		}
	}
	
	public final void resetFactionData(boolean doSpoutUpdate)
	{
		// TODO: Should we not rather use ConfServer.newPlayerStartingFactionID here?
		
		// The default neutral faction
		this.setFactionId(null); 
		this.setRole(null);
		this.setTitle(null);
		
		this.autoClaimFor = null;

		if (doSpoutUpdate)
		{
			SpoutFeatures.updateTitle(this, null);
			SpoutFeatures.updateTitle(null, this);
			SpoutFeatures.updateCape(this.getPlayer(), null);
		}
	}
	
	public void resetFactionData()
	{
		this.resetFactionData(true);
	}
	
	// -------------------------------------------- //
	// FIELD: factionId
	// -------------------------------------------- //
	
	// This method never returns null
	public String getFactionId()
	{
		if (this.factionId == null) return Const.FACTIONID_NONE;
		return this.factionId;
	}
	
	// This method never returns null
	public Faction getFaction()
	{
		Faction ret = FactionColl.get().get(this.getFactionId());
		if (ret == null) ret = FactionColl.get().get(Const.FACTIONID_NONE);
		return ret;
	}
	
	public boolean hasFaction()
	{
		return !this.getFactionId().equals(Const.FACTIONID_NONE);
	}
	
	// This setter is so long because it search for default/null case and takes care of updating the faction member index 
	public void setFactionId(String factionId)
	{
		// Avoid null input
		if (factionId == null) factionId = Const.FACTIONID_NONE;
		
		// Get the old value
		String oldFactionId = this.getFactionId();
		
		// Ignore nochange
		if (factionId.equals(oldFactionId)) return;
		
		// Apply change
		if (factionId.equals(Const.FACTIONID_NONE))
		{
			this.factionId = null;
		}
		else
		{
			this.factionId = factionId;
		}
		
		// Next we must be attached and inited
		if (!this.attached()) return;
		if (!this.getColl().inited()) return;
		
		// Spout Derp
		SpoutFeatures.updateTitle(this, null);
		SpoutFeatures.updateTitle(null, this);
		
		// Update index
		Faction oldFaction = FactionColl.get().get(oldFactionId);
		Faction faction = FactionColl.get().get(factionId);
		
		oldFaction.fplayers.remove(this);
		faction.fplayers.add(this);
		
		// Mark as changed
		this.changed();
	}
	
	public void setFaction(Faction faction)
	{
		this.setFactionId(faction.getId());
	}
	
	// -------------------------------------------- //
	// FIELD: role
	// -------------------------------------------- //
	
	public Rel getRole()
	{
		if (this.role == null) return Rel.MEMBER;
		return this.role;
	}
	
	public void setRole(Rel role)
	{
		if (role == null || role == Rel.MEMBER)
		{
			this.role = null;
		}
		else
		{
			this.role = role;
		}
		SpoutFeatures.updateTitle(this, null);
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: title
	// -------------------------------------------- //
	
	public boolean hasTitle()
	{
		return this.title != null;
	}
	
	public String getTitle()
	{
		if (this.hasTitle()) return this.title;
		return Lang.PLAYER_NOTITLE;
	}
	
	public void setTitle(String title)
	{
		if (title != null)
		{
			title = title.trim();
			if (title.length() == 0)
			{
				title = null;
			}
		}
		this.title = title;
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: powerBoost
	// -------------------------------------------- //
	
	public double getPowerBoost()
	{
		Double ret = this.powerBoost;
		if (ret == null) ret = 0D;
		return ret;
	}
	
	public void setPowerBoost(Double powerBoost)
	{
		if (powerBoost == null || powerBoost == 0)
		{
			powerBoost = null;
		}
		this.powerBoost = powerBoost;
		this.changed();
	}
	
	public boolean hasPowerBoost()
	{
		return this.getPowerBoost() != 0D;
	}
	
	// -------------------------------------------- //
	// GETTERS AND SETTERS
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
		if (ConfServer.noPVPDamageToOthersForXSecondsAfterLogin > 0)
		{
			this.loginPvpDisabled = true;
		}
	}

	public boolean hasLoginPvpDisabled()
	{
		if (!loginPvpDisabled)
		{
			return false;
		}
		if (this.lastLoginTime + (ConfServer.noPVPDamageToOthersForXSecondsAfterLogin * 1000) < System.currentTimeMillis())
		{
			this.loginPvpDisabled = false;
			return false;
		}
		return true;
	}
	
	// -------------------------------------------- //
	// TITLE, NAME, FACTION TAG AND CHAT
	// -------------------------------------------- //
	
	public String getName()
	{
		return getId();
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
		if (something.length() > 0)
		{
			ret += something+" ";
		}
		ret += this.getName();
		return ret;
	}
	
	public String getNameAndTitle()
	{
		if (this.hasTitle())
		{
			return this.getNameAndSomething(this.getTitle());
		}
		else
		{
			return this.getName();
		}
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
	
	// Chat Tag: 
	// These are injected into the format of global chat messages.
	
	public String getChatTag()
	{
		if ( ! this.hasFaction()) {
			return "";
		}
		
		return String.format(ConfServer.chatTagFormat, this.role.getPrefix()+this.getTag());
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
	
	// -------------------------------------------- //
	// RELATION AND RELATION COLORS
	// -------------------------------------------- //
	
	@Override
	public String describeTo(RelationParticipator observer, boolean ucfirst)
	{
		return RelationUtil.describeThatToMe(this, observer, ucfirst);
	}
	
	@Override
	public String describeTo(RelationParticipator observer)
	{
		return RelationUtil.describeThatToMe(this, observer);
	}
	
	@Override
	public Rel getRelationTo(RelationParticipator observer)
	{
		return RelationUtil.getRelationOfThatToMe(this, observer);
	}
	
	@Override
	public Rel getRelationTo(RelationParticipator observer, boolean ignorePeaceful)
	{
		return RelationUtil.getRelationOfThatToMe(this, observer, ignorePeaceful);
	}
	
	public Rel getRelationToLocation()
	{
		// TODO: Use some built in system to get sender
		return BoardColl.get().getFactionAt(PS.valueOf(this.getPlayer())).getRelationTo(this);
	}
	
	@Override
	public ChatColor getColorTo(RelationParticipator observer)
	{
		return RelationUtil.getColorOfThatToMe(this, observer);
	}
	
	// -------------------------------------------- //
	// HEALTH
	// -------------------------------------------- //
	
	public void heal(int amnt)
	{
		Player player = this.getPlayer();
		if (player == null)
		{
			return;
		}
		player.setHealth(player.getHealth() + amnt);
	}
	
	// -------------------------------------------- //
	// POWER
	// -------------------------------------------- //
	
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
		return ConfServer.powerPlayerMax + this.powerBoost;
	}
	
	public double getPowerMin()
	{
		return ConfServer.powerPlayerMin + this.powerBoost;
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
			if (!ConfServer.powerRegenOffline)
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
		double powerPerMinute = ConfServer.powerPerMinute;
		if(ConfServer.scaleNegativePower && this.power < 0)
		{
			powerPerMinute += (Math.sqrt(Math.abs(this.power)) * Math.abs(this.power)) / ConfServer.scaleNegativeDivisor;
		}
		this.alterPower(millisPassed * powerPerMinute / millisPerMinute);
		
	}

	protected void losePowerFromBeingOffline()
	{
		if (ConfServer.powerOfflineLossPerDay > 0.0 && this.power > ConfServer.powerOfflineLossLimit)
		{
			long now = System.currentTimeMillis();
			long millisPassed = now - this.lastPowerUpdateTime;
			this.lastPowerUpdateTime = now;

			double loss = millisPassed * ConfServer.powerOfflineLossPerDay / (24*60*60*1000);
			if (this.power - loss < ConfServer.powerOfflineLossLimit)
			{
				loss = this.power;
			}
			this.alterPower(-loss);
		}
	}
	
	public void onDeath()
	{
		this.updatePower();
		this.alterPower(-ConfServer.powerPerDeath);
	}
	
	// -------------------------------------------- //
	// TERRITORY
	// -------------------------------------------- //
	
	public boolean isInOwnTerritory()
	{
		// TODO: Use Mixin to get this PS instead
		return BoardColl.get().getFactionAt(PS.valueOf(this.getPlayer())) == this.getFaction();
	}

	public boolean isInEnemyTerritory()
	{
		// TODO: Use Mixin to get this PS instead
		return BoardColl.get().getFactionAt(PS.valueOf(this.getPlayer())).getRelationTo(this) == Rel.ENEMY;
	}

	public void sendFactionHereMessage()
	{
		if (SpoutFeatures.updateTerritoryDisplay(this))
		{
			return;
		}
		Faction factionHere = BoardColl.get().getFactionAt(this.getCurrentChunk());
		String msg = Txt.parse("<i>")+" ~ "+factionHere.getTag(this);
		if (factionHere.hasDescription())
		{
			msg += " - "+factionHere.getDescription();
		}
		this.sendMessage(msg);
	}
	
	// -------------------------------------------- //
	// ACTIONS
	// -------------------------------------------- //
	
	public void leave(boolean makePay)
	{
		Faction myFaction = this.getFaction();
		makePay = makePay && Econ.shouldBeUsed() && ! this.isUsingAdminMode();

		if (myFaction == null)
		{
			resetFactionData();
			return;
		}

		boolean perm = myFaction.getFlag(FFlag.PERMANENT);
		
		if (!perm && this.getRole() == Rel.LEADER && myFaction.getFPlayers().size() > 1)
		{
			msg("<b>You must give the admin role to someone else first.");
			return;
		}

		if (!ConfServer.canLeaveWithNegativePower && this.getPower() < 0)
		{
			msg("<b>You cannot leave until your power is positive.");
			return;
		}

		// if economy is enabled and they're not on the bypass list, make sure they can pay
		if (makePay && ! Econ.hasAtLeast(this, ConfServer.econCostLeave, "to leave your faction.")) return;

		FPlayerLeaveEvent leaveEvent = new FPlayerLeaveEvent(this,myFaction,FPlayerLeaveEvent.PlayerLeaveReason.LEAVE);
		Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
		if (leaveEvent.isCancelled()) return;

		// then make 'em pay (if applicable)
		if (makePay && ! Econ.modifyMoney(this, -ConfServer.econCostLeave, "to leave your faction.", "for leaving your faction.")) return;

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

			if (ConfServer.logFactionLeave)
				Factions.get().log(this.getName()+" left the faction: "+myFaction.getTag());
		}
		
		this.resetFactionData();

		if (myFaction.isNormal() && !perm && myFaction.getFPlayers().isEmpty())
		{
			// Remove this faction
			for (FPlayer fplayer : FPlayerColl.get().getAllOnline())
			{
				fplayer.msg("<i>%s<i> was disbanded.", myFaction.describeTo(fplayer, true));
			}

			myFaction.detach();
			if (ConfServer.logFactionDisband)
				Factions.get().log("The faction "+myFaction.getTag()+" ("+myFaction.getId()+") was disbanded due to the last player ("+this.getName()+") leaving.");
		}
	}

	public boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure)
	{
		String error = null;
		
		PS ps = PS.valueOf(location);
		Faction myFaction = this.getFaction();
		Faction currentFaction = BoardColl.get().getFactionAt(ps);
		int ownedLand = forFaction.getLandCount();
		
		if (ConfServer.worldGuardChecking && Worldguard.checkForRegionsInChunk(location))
		{
			// Checks for WorldGuard regions in the chunk attempting to be claimed
			error = Txt.parse("<b>This land is protected");
		}
		else if (ConfServer.worldsNoClaiming.contains(ps.getWorld()))
		{
			error = Txt.parse("<b>Sorry, this world has land claiming disabled.");
		}
		else if (this.isUsingAdminMode())
		{
			return true;
		}
		else if (forFaction == currentFaction)
		{
			error = Txt.parse("%s<i> already own this land.", forFaction.describeTo(this, true));
		}
		else if ( ! FPerm.TERRITORY.has(this, forFaction, true))
		{
			return false;
		}
		else if (forFaction.getFPlayers().size() < ConfServer.claimsRequireMinFactionMembers)
		{
			error = Txt.parse("Factions must have at least <h>%s<b> members to claim land.", ConfServer.claimsRequireMinFactionMembers);
		}
		else if (ownedLand >= forFaction.getPowerRounded())
		{
			error = Txt.parse("<b>You can't claim more land! You need more power!");
		}
		else if (ConfServer.claimedLandsMax != 0 && ownedLand >= ConfServer.claimedLandsMax && ! forFaction.getFlag(FFlag.INFPOWER))
		{
			error = Txt.parse("<b>Limit reached. You can't claim more land!");
		}
		else if ( ! ConfServer.claimingFromOthersAllowed && currentFaction.isNormal())
		{
			error = Txt.parse("<b>You may not claim land from others.");
		}
		else if (currentFaction.getRelationTo(forFaction).isAtLeast(Rel.TRUCE) && ! currentFaction.isNone())
		{
			error = Txt.parse("<b>You can't claim this land due to your relation with the current owner.");
		}
		else if
		(
			ConfServer.claimsMustBeConnected
			&& ! this.isUsingAdminMode()
			&& myFaction.getLandCountInWorld(ps.getWorld()) > 0
			&& !BoardColl.get().isConnectedPs(ps, myFaction)
			&& (!ConfServer.claimsCanBeUnconnectedIfOwnedByOtherFaction || !currentFaction.isNormal())
		)
		{
			if (ConfServer.claimsCanBeUnconnectedIfOwnedByOtherFaction)
				error = Txt.parse("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!");
			else
				error = Txt.parse("<b>You can only claim additional land which is connected to your first claim!");
		}
		else if (currentFaction.isNormal())
		{
			if ( ! currentFaction.hasLandInflation())
			{
				 // TODO more messages WARN current faction most importantly
				error = Txt.parse("%s<i> owns this land and is strong enough to keep it.", currentFaction.getTag(this));
			}
			else if ( ! BoardColl.get().isBorderPs(ps))
			{
				error = Txt.parse("<b>You must start claiming land at the border of the territory.");
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
		
		PS flocation = PS.valueOf(location).getChunk(true);
		Faction currentFaction = BoardColl.get().getFactionAt(flocation);
		
		int ownedLand = forFaction.getLandCount();
		
		if ( ! this.canClaimForFactionAtLocation(forFaction, location, notifyFailure)) return false;
		
		// TODO: Add flag no costs??
		// if economy is enabled and they're not on the bypass list, make sure they can pay
		boolean mustPay = Econ.shouldBeUsed() && ! this.isUsingAdminMode();
		double cost = 0.0;
		EconomyParticipator payee = null;
		if (mustPay)
		{
			cost = Econ.calculateClaimCost(ownedLand, currentFaction.isNormal());

			if (ConfServer.econClaimUnconnectedFee != 0.0 && forFaction.getLandCountInWorld(flocation.getWorld()) > 0 && !BoardColl.get().isConnectedPs(flocation, forFaction))
				cost += ConfServer.econClaimUnconnectedFee;

			if(ConfServer.bankEnabled && ConfServer.bankFactionPaysLandCosts && this.hasFaction())
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

		if (LWCFeatures.getEnabled() && forFaction.isNormal() && ConfServer.onCaptureResetLwcLocks)
			LWCFeatures.clearOtherChests(flocation, this.getFaction());

		// announce success
		Set<FPlayer> informTheseFPlayers = new HashSet<FPlayer>();
		informTheseFPlayers.add(this);
		informTheseFPlayers.addAll(forFaction.getFPlayersWhereOnline(true));
		for (FPlayer fp : informTheseFPlayers)
		{
			fp.msg("<h>%s<i> claimed land for <h>%s<i> from <h>%s<i>.", this.describeTo(fp, true), forFaction.describeTo(fp), currentFaction.describeTo(fp));
		}
		
		BoardColl.get().setFactionAt(flocation, forFaction);
		SpoutFeatures.updateTerritoryDisplayLoc(flocation);

		if (ConfServer.logLandClaims)
			Factions.get().log(this.getName()+" claimed land at ("+flocation.getChunkX()+","+flocation.getChunkZ()+") for the faction: "+forFaction.getTag());

		return true;
	}
	
}
