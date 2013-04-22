package com.massivecraft.factions.entity;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Const;
import com.massivecraft.factions.EconomyParticipator;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Lang;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.event.FactionsEventLandClaim;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.event.FactionsEventPowerChange;
import com.massivecraft.factions.event.FactionsEventPowerChange.PowerChangeReason;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.money.Money;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.store.SenderEntity;
import com.massivecraft.mcore.util.TimeUnit;
import com.massivecraft.mcore.util.Txt;


public class FPlayer extends SenderEntity<FPlayer> implements EconomyParticipator
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static FPlayer get(Object oid)
	{
		return FPlayerColls.get().get2(oid);
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
		
		return this;
	}
	
	@Override
	public boolean isDefault()
	{
		if (this.hasFaction()) return false;
		
		// Note: we do not check role or title here since they mean nothing without a faction.
		
		// TODO: This line looks obnoxious, investigate it.
		if (this.getPowerRounded() != this.getPowerMaxRounded() && this.getPowerRounded() != (int) Math.round(ConfServer.powerStarting)) return false;
		
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
	
	// This field contains the last calculated value of the players power.
	// The power calculation is lazy which means that the power is calculated first when you try to view the value.
	private double power;
	
	// This is the timestamp for the last calculation of the power.
	// The value is used for the lazy calculation described above.
	private long lastPowerUpdateTime;
	
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
	//private transient boolean loginPvpDisabled;
	
	// FIELD: account
	public String getAccountId() { return this.getId(); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	// GSON need this noarg constructor.
	public FPlayer()
	{
		this.resetFactionData(false);
		this.power = ConfServer.powerStarting;
		this.lastPowerUpdateTime = System.currentTimeMillis();

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
		Faction ret = FactionColls.get().get(this).get(this.getFactionId());
		if (ret == null) ret = FactionColls.get().get(this).get(Const.FACTIONID_NONE);
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
		if (!FactionColls.get().get(this).inited()) return;
		
		// Spout Derp
		SpoutFeatures.updateTitle(this, null);
		SpoutFeatures.updateTitle(null, this);
		
		// Update index
		Faction oldFaction = FactionColls.get().get(this).get(oldFactionId);
		Faction faction = FactionColls.get().get(this).get(factionId);
		
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
	// FIELD: lastPowerUpdateTime
	// -------------------------------------------- //
	
	// RAW
	
	public long getLastPowerUpdateTime()
	{
		return this.lastPowerUpdateTime;
	}
	
	public void setLastPowerUpdateTime(long lastPowerUpdateTime)
	{
		this.lastPowerUpdateTime = lastPowerUpdateTime;
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: power
	// -------------------------------------------- //
	
	// RAW
	
	public double getPower()
	{
		this.recalculatePower();
		return this.power;
	}
	
	public void setPower(double power)
	{
		this.setPower(power, System.currentTimeMillis());
	}
	
	public void setPower(double power, long now)
	{
		power = Math.min(power, this.getPowerMax());
		power = Math.max(power, this.getPowerMin());
		
		// Nochange
		if (this.power == power) return;
		
		this.power = power;
		this.setLastPowerUpdateTime(now);
		this.changed();
	}
	
	public double getPowerMax()
	{
		return ConfServer.powerMax + this.getPowerBoost();
	}
	
	public double getPowerMin()
	{
		return ConfServer.powerMin + this.getPowerBoost();
	}
	
	public void recalculatePower()
	{
		this.recalculatePower(this.isOnline());
	}
	
	private static final transient long POWER_RECALCULATION_MINIMUM_WAIT_MILLIS = 10 * TimeUnit.MILLIS_PER_SECOND;
	public void recalculatePower(boolean online)
	{
		// Is the player really on this server?
		// We use the sender ps mixin to fetch the current player location.
		// If the PS is null it's OK. We assume the player is here if we do not know.
		PS ps = Mixin.getSenderPs(this.getId());
		if (ps != null && !ps.isWorldLoadedOnThisServer()) return;
		
		// Get the now
		long now = System.currentTimeMillis();
		
		// We will only update if a certain amount of time has passed.
		if (this.getLastPowerUpdateTime() + POWER_RECALCULATION_MINIMUM_WAIT_MILLIS >= now) return;
		
		// Calculate millis passed
		long millisPassed = now - this.getLastPowerUpdateTime();
		
		// Note that we updated
		this.setLastPowerUpdateTime(now);
		
		// We consider dead players to be offline.
		if (online)
		{
			Player thisPlayer = this.getPlayer();
			if (thisPlayer != null && thisPlayer.isDead())
			{
				online = false;
			}
		}
		
		// Depending on online state pick the config values
		double powerPerHour = online ? ConfServer.powerPerHourOnline : ConfServer.powerPerHourOffline;
		double powerLimitGain = online ? ConfServer.powerLimitGainOnline : ConfServer.powerLimitGainOffline;
		double powerLimitLoss = online ? ConfServer.powerLimitLossOnline : ConfServer.powerLimitLossOffline;
		
		// Apply the negative divisor thingy
		if (ConfServer.scaleNegativePower && this.power < 0)
		{
			powerPerHour += (Math.sqrt(Math.abs(this.power)) * Math.abs(this.power)) / ConfServer.scaleNegativeDivisor;
		}
		
		// Calculate delta and target
		double powerDelta = powerPerHour * millisPassed / TimeUnit.MILLIS_PER_HOUR;
		double powerTarget = this.power + powerDelta;
		
		// Check Gain and Loss limits
		if (powerDelta >= 0)
		{
			// Gain
			if (powerTarget > powerLimitGain)
			{
				if (this.power > powerLimitGain)
				{
					// Did already cross --> Just freeze
					powerTarget = this.power;
				}
				else
				{
					// Crossing right now --> Snap to limit
					powerTarget = powerLimitGain;
				}
			}
		}
		else
		{
			// Loss
			if (powerTarget < powerLimitLoss)
			{
				if (this.power < powerLimitLoss)
				{
					// Did already cross --> Just freeze
					powerTarget = this.power;
				}
				else
				{
					// Crossing right now --> Snap to limit
					powerTarget = powerLimitLoss;
				}
			}
		}
		
		FactionsEventPowerChange event = new FactionsEventPowerChange(null, this, PowerChangeReason.TIME, powerTarget);
		event.run();
		if (event.isCancelled()) return;
		powerTarget = event.getNewPower();
		
		this.setPower(powerTarget, now);
	}
	
	// FINER
	
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
	
	// -------------------------------------------- //
	// TITLE, NAME, FACTION TAG AND CHAT
	// -------------------------------------------- //
	
	public String getName()
	{
		return this.getFixedId();
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
		return BoardColls.get().getFactionAt(PS.valueOf(this.getPlayer())).getRelationTo(this);
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
	// TERRITORY
	// -------------------------------------------- //
	
	public boolean isInOwnTerritory()
	{
		// TODO: Use Mixin to get this PS instead
		return BoardColls.get().getFactionAt(Mixin.getSenderPs(this.getId())) == this.getFaction();
	}

	public boolean isInEnemyTerritory()
	{
		// TODO: Use Mixin to get this PS instead
		return BoardColls.get().getFactionAt(Mixin.getSenderPs(this.getId())).getRelationTo(this) == Rel.ENEMY;
	}

	public void sendFactionHereMessage()
	{
		if (SpoutFeatures.updateTerritoryDisplay(this))
		{
			return;
		}
		Faction factionHere = BoardColls.get().getFactionAt(this.getCurrentChunk());
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

		boolean permanent = myFaction.getFlag(FFlag.PERMANENT);
		
		if (!permanent && this.getRole() == Rel.LEADER && myFaction.getFPlayers().size() > 1)
		{
			msg("<b>You must give the leader role to someone else first.");
			return;
		}

		if (!ConfServer.canLeaveWithNegativePower && this.getPower() < 0)
		{
			msg("<b>You cannot leave until your power is positive.");
			return;
		}

		// Event
		FactionsEventMembershipChange membershipChangeEvent = new FactionsEventMembershipChange(sender, this, myFaction, MembershipChangeReason.LEAVE);
		membershipChangeEvent.run();
		if (membershipChangeEvent.isCancelled()) return;

		// Am I the last one in the faction?
		if (myFaction.getFPlayers().size() == 1)
		{
			// Transfer all money
			if (Econ.isEnabled(this))
			{
				Econ.transferMoney(this, myFaction, this, Money.get(this));
			}
		}
		
		if (myFaction.isNormal())
		{
			for (FPlayer fplayer : myFaction.getFPlayersWhereOnline(true))
			{
				fplayer.msg("%s<i> left %s<i>.", this.describeTo(fplayer, true), myFaction.describeTo(fplayer));
			}

			if (MConf.get().logFactionLeave)
			{
				Factions.get().log(this.getName()+" left the faction: "+myFaction.getTag());
			}
		}
		
		this.resetFactionData();

		if (myFaction.isNormal() && !permanent && myFaction.getFPlayers().isEmpty())
		{
			// Remove this faction
			for (FPlayer fplayer : FPlayerColls.get().get(this).getAllOnline())
			{
				fplayer.msg("<i>%s<i> was disbanded.", myFaction.describeTo(fplayer, true));
			}

			myFaction.detach();
			if (MConf.get().logFactionDisband)
			{
				Factions.get().log("The faction "+myFaction.getTag()+" ("+myFaction.getId()+") was disbanded due to the last player ("+this.getName()+") leaving.");
			}
		}
	}

	public boolean canClaimForFactionAtLocation(Faction forFaction, PS ps, boolean notifyFailure)
	{
		String error = null;
		
		Faction myFaction = this.getFaction();
		Faction currentFaction = BoardColls.get().getFactionAt(ps);
		int ownedLand = forFaction.getLandCount();
		
		if (ConfServer.worldGuardChecking && Worldguard.checkForRegionsInChunk(ps))
		{
			// Checks for WorldGuard regions in the chunk attempting to be claimed
			error = Txt.parse("<b>This land is protected");
		}
		else if (MConf.get().worldsNoClaiming.contains(ps.getWorld()))
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
			&& !BoardColls.get().isConnectedPs(ps, myFaction)
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
			else if ( ! BoardColls.get().isBorderPs(ps))
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
	
	// notifyFailure is false if called by auto-claim; no need to notify on every failure for it
	// return value is false on failure, true on success
	public boolean attemptClaim(Faction forFaction, PS psChunk, boolean notifyFailure)
	{
		psChunk = psChunk.getChunk(true);
		Faction currentFaction = BoardColls.get().getFactionAt(psChunk);
		int ownedLand = forFaction.getLandCount();
		
		if ( ! this.canClaimForFactionAtLocation(forFaction, psChunk, notifyFailure)) return false;

		// Event
		FactionsEventLandClaim event = new FactionsEventLandClaim(sender, forFaction, psChunk);
		event.run();
		if (event.isCancelled()) return false;

		// then make 'em pay (if applicable)
		// TODO: The economy integration should cancel the event above!
		// Calculate the cost to claim the area
		double cost = Econ.calculateClaimCost(ownedLand, currentFaction.isNormal());
		if (ConfServer.econClaimUnconnectedFee != 0.0 && forFaction.getLandCountInWorld(psChunk.getWorld()) > 0 && !BoardColls.get().isConnectedPs(psChunk, forFaction))
		{
			cost += ConfServer.econClaimUnconnectedFee;
		}
		if (Econ.payForAction(cost, this, "claim this land")) return false;

		// TODO: The LWC integration should listen to Monitor for the claim event.
		if (LWCFeatures.getEnabled() && forFaction.isNormal() && ConfServer.onCaptureResetLwcLocks)
		{
			LWCFeatures.clearOtherProtections(psChunk, this.getFaction());
		}

		// announce success
		Set<FPlayer> informTheseFPlayers = new HashSet<FPlayer>();
		informTheseFPlayers.add(this);
		informTheseFPlayers.addAll(forFaction.getFPlayersWhereOnline(true));
		for (FPlayer fp : informTheseFPlayers)
		{
			fp.msg("<h>%s<i> claimed land for <h>%s<i> from <h>%s<i>.", this.describeTo(fp, true), forFaction.describeTo(fp), currentFaction.describeTo(fp));
		}
		
		BoardColls.get().setFactionAt(psChunk, forFaction);
		SpoutFeatures.updateTerritoryDisplayLoc(psChunk);

		if (MConf.get().logLandClaims)
		{
			Factions.get().log(this.getName()+" claimed land at ("+psChunk.getChunkX()+","+psChunk.getChunkZ()+") for the faction: "+forFaction.getTag());
		}

		return true;
	}
	
}
