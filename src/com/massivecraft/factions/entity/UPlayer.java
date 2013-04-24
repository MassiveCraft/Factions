package com.massivecraft.factions.entity;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.EconomyParticipator;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Lang;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.event.FactionsEventChunkChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.money.Money;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.store.SenderEntity;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.util.Txt;


public class UPlayer extends SenderEntity<UPlayer> implements EconomyParticipator
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static UPlayer get(Object oid)
	{
		return UPlayerColls.get().get2(oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public UPlayer load(UPlayer that)
	{
		this.setFactionId(that.factionId);
		this.setRole(that.role);
		this.setTitle(that.title);
		this.setPower(that.power);
		
		return this;
	}
	
	@Override
	public boolean isDefault()
	{
		if (this.hasFaction()) return false;
		// Role means nothing without a faction.
		// Title means nothing without a faction.
		if (this.getPowerRounded() != (int) Math.round(UConf.get(this).defaultPlayerPower)) return false;
		
		return true;
	}
	
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	// In this section of the source code we place the field declarations only.
	// Each field has it's own section further down since just the getter and setter logic takes up quite some place.
	
	// This is a foreign key.
	// Each player belong to a faction.
	// Null means default which is the no-faction faction called Wilderness.
	private String factionId = null;
	
	// What role does the player have in the faction?
	// Null means default which is the default value for the universe.
	private Rel role = null;
	
	// What title does the player have in the faction?
	// The title is just for fun. It's not connected to any game mechanic.
	// The default case is no title since it's what you start with and also the most common case.
	// The player title is similar to the faction description.
	// 
	// Question: Can the title contain chat colors?
	// Answer: Yes but in such case the policy is that they already must be parsed using Txt.parse.
	//         If the title contains raw markup, such as "<white>" instead of "§f" it will not be parsed and "<white>" will be displayed.
	private String title = null;
	
	// Each player has an individual power level.
	// The power level for online players is occasionally updated by a recurring task and the power should stay the same for offline players.
	// For that reason the value is to be considered correct when you pick it. Do not call the power update method.
	// Null means default which is the default value for the universe.
	private Double power = null;
	
	// -------------------------------------------- //
	// FIELDS: RAW TRANSIENT
	// -------------------------------------------- //
	
	// FIELD: mapAutoUpdating
	// TODO: Move this to the MPlayer
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
	// CORE UTILITIES
	// -------------------------------------------- //
	
	public void resetFactionData()
	{
		// The default neutral faction
		this.setFactionId(null); 
		this.setRole(null);
		this.setTitle(null);
		
		this.autoClaimFor = null;
	}
	
	/*
	public boolean isPresent(boolean requireFetchable)
	{
		if (!this.isOnline()) return false;
		
		if (requireFetchable)
		{
			
		}
		else
		{
			
		}
		
		PS ps = Mixin.getSenderPs(this.getId());
		if (ps == null) return false;
		
		String psUniverse = Factions.get().getMultiverse().getUniverseForWorldName(ps.getWorld());
		if (!psUniverse.equals(this.getUniverse())) return false;
		
		if (!requireFetchable) return true;
		
		Player player = this.getPlayer();
		if (player == null) return false;
		
		if (player.isDead()) return false;
		
		return true;
	}
	*/
	
	// -------------------------------------------- //
	// FIELD: factionId
	// -------------------------------------------- //
	
	public String getDefaultFactionId()
	{
		return UConf.get(this).defaultPlayerFactionId;
	}
	
	// This method never returns null
	public String getFactionId()
	{
		if (this.factionId == null) return this.getDefaultFactionId();
		return this.factionId;
	}
	
	// This method never returns null
	public Faction getFaction()
	{
		Faction ret = FactionColls.get().get(this).get(this.getFactionId());
		if (ret == null) ret = FactionColls.get().get(this).get(UConf.get(this).defaultPlayerFactionId);
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
		if (factionId == null) factionId = this.getDefaultFactionId();
		
		// Get the old value
		String oldFactionId = this.getFactionId();
		
		// Ignore nochange
		if (factionId.equals(oldFactionId)) return;
		
		// Apply change
		if (factionId.equals(this.getDefaultFactionId())) factionId = null;
		this.factionId = factionId;
		
		// Next we must be attached and inited
		if (!this.attached()) return;
		if (!this.getColl().inited()) return;
		if (!Factions.get().isDatabaseInitialized()) return;
		
		// Update index
		Faction oldFaction = FactionColls.get().get(this).get(oldFactionId);
		Faction faction = FactionColls.get().get(this).get(factionId);
		
		oldFaction.uplayers.remove(this);
		faction.uplayers.add(this);
		
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
	
	public Rel getDefaultRole()
	{
		return UConf.get(this).defaultPlayerRole;
	}
	
	public Rel getRole()
	{
		if (this.role == null) return this.getDefaultRole();
		return this.role;
	}
	
	public void setRole(Rel role)
	{
		if (role == null || MUtil.equals(role, this.getDefaultRole())) role = null;
		this.role = role;
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
	// FIELD: power
	// -------------------------------------------- //
	
	// MIXIN: RAW
	
	public double getPowerMaxUniversal()
	{
		return Factions.get().getPowerMixin().getMaxUniversal(this);
	}
	
	public double getPowerMax()
	{
		return Factions.get().getPowerMixin().getMax(this);
	}
	
	public double getPowerMin()
	{
		return Factions.get().getPowerMixin().getMin(this);
	}
	
	public double getPowerPerHour()
	{
		return Factions.get().getPowerMixin().getPerHour(this);
	}
	
	public double getPowerPerDeath()
	{
		return Factions.get().getPowerMixin().getPerDeath(this);
	}
	
	// MIXIN: FINER
	
	public double getLimitedPower(double power)
	{
		power = Math.max(power, this.getPowerMin());
		power = Math.min(power, this.getPowerMax());
		return power;
	}
	
	// RAW
	
	public double getDefaultPower()
	{
		return UConf.get(this).defaultPlayerPower;
	}
	
	public double getPower()
	{
		Double ret = this.power;
		if (ret == null) ret = this.getDefaultPower();
		ret = this.getLimitedPower(ret);
		return ret;
	}
	
	public void setPower(Double power)
	{
		if (power == null || MUtil.equals(power, this.getDefaultPower())) power = null;
		power = this.getLimitedPower(power);
		this.power = power;
		this.changed();
	}
	
	// FINER
	
	public int getPowerRounded()
	{
		return (int) Math.round(this.getPower());
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
		Faction faction = this.getFaction();
		if (faction.isNone()) return "";
		return faction.getTag();
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
	public String getNameAndTitle(UPlayer uplayer)
	{
		return this.getColorTo(uplayer)+this.getNameAndTitle();
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
	
	// -------------------------------------------- //
	// ACTIONS
	// -------------------------------------------- //
	
	public void leave()
	{
		Faction myFaction = this.getFaction();

		boolean permanent = myFaction.getFlag(FFlag.PERMANENT);
		
		if (!permanent && this.getRole() == Rel.LEADER && myFaction.getUPlayers().size() > 1)
		{
			msg("<b>You must give the leader role to someone else first.");
			return;
		}

		if (!UConf.get(myFaction).canLeaveWithNegativePower && this.getPower() < 0)
		{
			msg("<b>You cannot leave until your power is positive.");
			return;
		}

		// Event
		FactionsEventMembershipChange membershipChangeEvent = new FactionsEventMembershipChange(sender, this, myFaction, MembershipChangeReason.LEAVE);
		membershipChangeEvent.run();
		if (membershipChangeEvent.isCancelled()) return;

		// Am I the last one in the faction?
		if (myFaction.getUPlayers().size() == 1)
		{
			// Transfer all money
			if (Econ.isEnabled(this))
			{
				Econ.transferMoney(this, myFaction, this, Money.get(this));
			}
		}
		
		if (myFaction.isNormal())
		{
			for (UPlayer uplayer : myFaction.getUPlayersWhereOnline(true))
			{
				uplayer.msg("%s<i> left %s<i>.", this.describeTo(uplayer, true), myFaction.describeTo(uplayer));
			}

			if (MConf.get().logFactionLeave)
			{
				Factions.get().log(this.getName()+" left the faction: "+myFaction.getTag());
			}
		}
		
		this.resetFactionData();

		if (myFaction.isNormal() && !permanent && myFaction.getUPlayers().isEmpty())
		{
			// Remove this faction
			for (UPlayer uplayer : UPlayerColls.get().get(this).getAllOnline())
			{
				uplayer.msg("<i>%s<i> was disbanded.", myFaction.describeTo(uplayer, true));
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
		
		UConf uconf = UConf.get(ps);
		
		if (uconf.worldGuardChecking && Worldguard.checkForRegionsInChunk(ps))
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
		else if (forFaction.getUPlayers().size() < uconf.claimsRequireMinFactionMembers)
		{
			error = Txt.parse("Factions must have at least <h>%s<b> members to claim land.", uconf.claimsRequireMinFactionMembers);
		}
		else if (ownedLand >= forFaction.getPowerRounded())
		{
			error = Txt.parse("<b>You can't claim more land! You need more power!");
		}
		else if (uconf.claimedLandsMax != 0 && ownedLand >= uconf.claimedLandsMax && ! forFaction.getFlag(FFlag.INFPOWER))
		{
			error = Txt.parse("<b>Limit reached. You can't claim more land!");
		}
		else if ( ! uconf.claimingFromOthersAllowed && currentFaction.isNormal())
		{
			error = Txt.parse("<b>You may not claim land from others.");
		}
		else if (currentFaction.getRelationTo(forFaction).isAtLeast(Rel.TRUCE) && ! currentFaction.isNone())
		{
			error = Txt.parse("<b>You can't claim this land due to your relation with the current owner.");
		}
		else if
		(
			uconf.claimsMustBeConnected
			&& ! this.isUsingAdminMode()
			&& myFaction.getLandCountInWorld(ps.getWorld()) > 0
			&& !BoardColls.get().isConnectedPs(ps, myFaction)
			&& (!uconf.claimsCanBeUnconnectedIfOwnedByOtherFaction || !currentFaction.isNormal())
		)
		{
			if (uconf.claimsCanBeUnconnectedIfOwnedByOtherFaction)
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
		
		if ( ! this.canClaimForFactionAtLocation(forFaction, psChunk, notifyFailure)) return false;

		// Event
		FactionsEventChunkChange event = new FactionsEventChunkChange(sender, psChunk, forFaction);
		event.run();
		if (event.isCancelled()) return false;

		// TODO: The LWC integration should listen to Monitor for the claim event.
		if (LWCFeatures.getEnabled() && forFaction.isNormal() && UConf.get(forFaction).lwcRemoveOnCapture)
		{
			LWCFeatures.clearOtherProtections(psChunk, this.getFaction());
		}

		// announce success
		Set<UPlayer> informTheseUPlayers = new HashSet<UPlayer>();
		informTheseUPlayers.add(this);
		informTheseUPlayers.addAll(forFaction.getUPlayersWhereOnline(true));
		for (UPlayer fp : informTheseUPlayers)
		{
			fp.msg("<h>%s<i> claimed land for <h>%s<i> from <h>%s<i>.", this.describeTo(fp, true), forFaction.describeTo(fp), currentFaction.describeTo(fp));
		}
		
		BoardColls.get().setFactionAt(psChunk, forFaction);

		if (MConf.get().logLandClaims)
		{
			Factions.get().log(this.getName()+" claimed land at ("+psChunk.getChunkX()+","+psChunk.getChunkZ()+") for the faction: "+forFaction.getTag());
		}

		return true;
	}
	
}
