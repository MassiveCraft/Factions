package com.massivecraft.factions.entity;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.ps.PSFormatHumanSpace;
import com.massivecraft.mcore.store.SenderEntity;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.util.SenderUtil;
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
		this.setPowerBoost(that.powerBoost);
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
	
	@Override
	public void postAttach(String id)
	{
		// If inited ...
		if (!Factions.get().isDatabaseInitialized()) return;
		
		// ... update the index.
		Faction faction = this.getFaction();
		faction.uplayers.add(this);
		
		Factions.get().log(Txt.parse("<g>postAttach added <h>%s <i>aka <h>%s <i>to <h>%s <i>aka <h>%s<i>.", id, Mixin.getDisplayName(id), faction.getId(), faction.getName()));
	}
	
	@Override
	public void preDetach(String id)
	{
		// If inited ...
		if (!Factions.get().isDatabaseInitialized()) return;
		
		// ... update the index.
		Faction faction = this.getFaction();
		faction.uplayers.remove(this);
		
		Factions.get().log(Txt.parse("<b>preDetach removed <h>%s <i>aka <h>%s <i>to <h>%s <i>aka <h>%s<i>.", id, Mixin.getDisplayName(id), faction.getId(), faction.getName()));
	}
	
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	// In this section of the source code we place the field declarations only.
	// Each field has it's own section further down since just the getter and setter logic takes up quite some place.
	
	// This is a foreign key.
	// Each player belong to a faction.
	// Null means default for the universe.
	private String factionId = null;
	
	// What role does the player have in the faction?
	// Null means default for the universe.
	private Rel role = null;
	
	// What title does the player have in the faction?
	// The title is just for fun. It's not connected to any game mechanic.
	// The player title is similar to the faction description.
	// 
	// Question: Can the title contain chat colors?
	// Answer: Yes but in such case the policy is that they already must be parsed using Txt.parse.
	//         If the title contains raw markup, such as "<white>" instead of "§f" it will not be parsed and "<white>" will be displayed.
	//
	// Null means the player has no title.
	private String title = null;
	
	// Player usually do not have a powerboost. It defaults to 0.
	// The powerBoost is a custom increase/decrease to default and maximum power.
	// Note that player powerBoost and faction powerBoost are very similar.
	private Double powerBoost = null; 
	
	// Each player has an individual power level.
	// The power level for online players is occasionally updated by a recurring task and the power should stay the same for offline players.
	// For that reason the value is to be considered correct when you pick it. Do not call the power update method.
	// Null means default for the universe.
	private Double power = null;
	
	// The id for the faction this uplayer is currently autoclaiming for.
	// NOTE: This field will not be saved to the database ever.
	// Null means the player isn't auto claiming.
	private transient Faction autoClaimFaction = null;
	public Faction getAutoClaimFaction() { return this.autoClaimFaction; }
	public void setAutoClaimFaction(Faction autoClaimFaction) { this.autoClaimFaction = autoClaimFaction; }
	
	// -------------------------------------------- //
	// FIELDS: MULTIVERSE PROXY
	// -------------------------------------------- //
	
	public boolean isMapAutoUpdating() { return MPlayer.get(this).isMapAutoUpdating(); }
	public void setMapAutoUpdating(boolean mapAutoUpdating) { MPlayer.get(this).setMapAutoUpdating(mapAutoUpdating); }
	
	public boolean isUsingAdminMode() { return MPlayer.get(this).isUsingAdminMode(); }
	public void setUsingAdminMode(boolean usingAdminMode) { MPlayer.get(this).setUsingAdminMode(usingAdminMode); }
	
	// -------------------------------------------- //
	// CORE UTILITIES
	// -------------------------------------------- //
	
	public void resetFactionData()
	{
		// The default neutral faction
		this.setFactionId(null); 
		this.setRole(null);
		this.setTitle(null);
		this.setAutoClaimFaction(null);
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
		return !this.getFactionId().equals(UConf.get(this).factionIdNone);
	}
	
	// This setter is so long because it search for default/null case and takes care of updating the faction member index 
	public void setFactionId(String factionId)
	{
		// Clean input
		String target = factionId;

		// Detect Nochange
		if (MUtil.equals(this.factionId, target)) return;
		
		// Get the raw old value
		String oldFactionId = this.factionId;
		
		// Apply
		this.factionId = target;
		
		// Must be attached and initialized 
		if (!this.attached()) return;
		if (!Factions.get().isDatabaseInitialized()) return;
		
		if (oldFactionId == null) oldFactionId = this.getDefaultFactionId();
		
		// Update index
		Faction oldFaction = FactionColls.get().get(this).get(oldFactionId);
		Faction faction = this.getFaction();
		
		if (oldFaction != null) oldFaction.uplayers.remove(this);
		if (faction != null) faction.uplayers.add(this);
		
		String oldFactionIdDesc = "NULL";
		String oldFactionNameDesc = "NULL";
		if (oldFaction != null)
		{
			oldFactionIdDesc = oldFaction.getId();
			oldFactionNameDesc = oldFaction.getName();
		}
		String factionIdDesc = "NULL";
		String factionNameDesc = "NULL";
		if (faction != null)
		{
			factionIdDesc = faction.getId();
			factionNameDesc = faction.getName();
		}
		
		Factions.get().log(Txt.parse("<i>setFactionId moved <h>%s <i>aka <h>%s <i>from <h>%s <i>aka <h>%s <i>to <h>%s <i>aka <h>%s<i>.", this.getId(), Mixin.getDisplayName(this.getId()), oldFactionIdDesc, oldFactionNameDesc, factionIdDesc, factionNameDesc));
		
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
		// Clean input
		Rel target = role;
		
		// Detect Nochange
		if (MUtil.equals(this.role, target)) return;
		
		// Apply
		this.role = target;
		
		// Mark as changed
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
		// Clean input
		String target = title;
		if (target != null)
		{
			target = target.trim();
			if (target.length() == 0)
			{
				target = null;
			}
		}
		
		// NOTE: That we parse the title here is considered part of the 1.8 --> 2.0 migration.
		// This should be removed once the migration phase is considered to be over.
		if (target != null)
		{
			target = Txt.parse(target);
		}
		
		// Detect Nochange
		if (MUtil.equals(this.title, target)) return;
		
		// Apply
		this.title = target;
		
		// Mark as changed
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
		// Clean input
		Double target = powerBoost;
		if (target == null || target == 0) target = null;
		
		// Detect Nochange
		if (MUtil.equals(this.powerBoost, target)) return;
		
		// Apply
		this.powerBoost = target;
		
		// Mark as changed
		this.changed();
	}
	
	public boolean hasPowerBoost()
	{
		return this.getPowerBoost() != 0D;
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
	
	public int getPowerMaxRounded()
	{
		return (int) Math.round(this.getPowerMax());
	}
	
	public int getPowerMinRounded()
	{
		return (int) Math.round(this.getPowerMin());
	}
	
	public int getPowerMaxUniversalRounded()
	{
		return (int) Math.round(this.getPowerMaxUniversal());
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
		// Clean input
		Double target = power;
		
		// Detect Nochange
		if (MUtil.equals(this.power, target)) return;
		
		// Apply
		this.power = target;
		
		// Mark as changed
		this.changed();
	}
	
	// FINER
	
	public int getPowerRounded()
	{
		return (int) Math.round(this.getPower());
	}
	
	// -------------------------------------------- //
	// TITLE, NAME, FACTION NAME AND CHAT
	// -------------------------------------------- //
	
	public String getName()
	{
		return this.getFixedId();
	}
	
	public String getFactionName()
	{
		Faction faction = this.getFaction();
		if (faction.isNone()) return "";
		return faction.getName();
	}
	
	// Base concatenations:
	
	public String getNameAndSomething(String color, String something)
	{
		String ret = "";
		ret += color;
		ret += this.getRole().getPrefix();
		if (something != null && something.length() > 0)
		{
			ret += something;
			ret += " ";
			ret += color;
		}
		ret += this.getName();
		return ret;
	}
	
	public String getNameAndFactionName()
	{
		return this.getNameAndSomething("", this.getFactionName());
	}
	
	public String getNameAndTitle(String color)
	{
		if (this.hasTitle())
		{
			return this.getNameAndSomething(color, this.getTitle());
		}
		else
		{
			return this.getNameAndSomething(color, null);
		}
	}
	
	// Colored concatenations:
	// These are used in information messages
	
	public String getNameAndTitle(Faction faction)
	{
		return this.getNameAndTitle(this.getColorTo(faction).toString());
	}
	public String getNameAndTitle(UPlayer uplayer)
	{
		return this.getNameAndTitle(this.getColorTo(uplayer).toString());
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
		PS ps = Mixin.getSenderPs(this.getId());
		if (ps == null) return false;
		return BoardColls.get().getFactionAt(ps) == this.getFaction();
	}

	public boolean isInEnemyTerritory()
	{
		PS ps = Mixin.getSenderPs(this.getId());
		if (ps == null) return false;
		return BoardColls.get().getFactionAt(ps).getRelationTo(this) == Rel.ENEMY;
	}
	
	// -------------------------------------------- //
	// ACTIONS
	// -------------------------------------------- //
	
	public void leave()
	{
		Faction myFaction = this.getFaction();

		boolean permanent = myFaction.getFlag(FFlag.PERMANENT);
		
		if (myFaction.getUPlayers().size() > 1)
		{
			if (!permanent && this.getRole() == Rel.LEADER)
			{
				msg("<b>You must give the leader role to someone else first.");
				return;
			}
			
			if (!UConf.get(myFaction).canLeaveWithNegativePower && this.getPower() < 0)
			{
				msg("<b>You cannot leave until your power is positive.");
				return;
			}
		}

		// Event
		FactionsEventMembershipChange membershipChangeEvent = new FactionsEventMembershipChange(this.getSender(), this, myFaction, MembershipChangeReason.LEAVE);
		membershipChangeEvent.run();
		if (membershipChangeEvent.isCancelled()) return;
		
		if (myFaction.isNormal())
		{
			for (UPlayer uplayer : myFaction.getUPlayersWhereOnline(true))
			{
				uplayer.msg("%s<i> left %s<i>.", this.describeTo(uplayer, true), myFaction.describeTo(uplayer));
			}

			if (MConf.get().logFactionLeave)
			{
				Factions.get().log(this.getName()+" left the faction: "+myFaction.getName());
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

			if (MConf.get().logFactionDisband)
			{
				Factions.get().log("The faction "+myFaction.getName()+" ("+myFaction.getId()+") was disbanded due to the last player ("+this.getName()+") leaving.");
			}
			myFaction.detach();
		}
	}
	
	public boolean tryClaim(Faction newFaction, PS ps, boolean verbooseChange, boolean verbooseSame)
	{
		PS chunk = ps.getChunk(true);
		Faction oldFaction = BoardColls.get().getFactionAt(chunk);
		
		UConf uconf = UConf.get(newFaction);
		MConf mconf = MConf.get();
		
		// Validate
		if (newFaction == oldFaction)
		{
			msg("%s<i> already owns this land.", newFaction.describeTo(this, true));
			return true;
		}
		
		if (!this.isUsingAdminMode())
		{
			if (newFaction.isNormal())
			{
				if (mconf.getWorldsNoClaiming().contains(ps.getWorld()))
				{
					msg("<b>Sorry, this world has land claiming disabled.");
					return false;
				}
				
				if (!FPerm.TERRITORY.has(this, newFaction, true))
				{
					return false;
				}
				
				if (newFaction.getUPlayers().size() < uconf.claimsRequireMinFactionMembers)
				{
					msg("Factions must have at least <h>%s<b> members to claim land.", uconf.claimsRequireMinFactionMembers);
					return false;
				}
				
				int ownedLand = newFaction.getLandCount();
				
				if (uconf.claimedLandsMax != 0 && ownedLand >= uconf.claimedLandsMax && ! newFaction.getFlag(FFlag.INFPOWER))
				{
					msg("<b>Limit reached. You can't claim more land.");
					return false;
				}
				
				if (ownedLand >= newFaction.getPowerRounded())
				{
					msg("<b>You can't claim more land. You need more power.");
					return false;
				}
				
				if
				(
					uconf.claimsMustBeConnected
					&&
					newFaction.getLandCountInWorld(ps.getWorld()) > 0
					&&
					!BoardColls.get().isConnectedPs(chunk, newFaction)
					&&
					(!uconf.claimsCanBeUnconnectedIfOwnedByOtherFaction || oldFaction.isNone())
				)
				{
					if (uconf.claimsCanBeUnconnectedIfOwnedByOtherFaction)
					{
						msg("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!");
					}
					else
					{
						msg("<b>You can only claim additional land which is connected to your first claim!");
					}
					return false;
				}
			}
			
			if (oldFaction.isNormal())
			{
				if (!FPerm.TERRITORY.has(this, oldFaction, false))
				{
					if (!uconf.claimingFromOthersAllowed)
					{
						msg("<b>You may not claim land from others.");
						return false;
					}
					
					if (oldFaction.getRelationTo(newFaction).isAtLeast(Rel.TRUCE))
					{
						msg("<b>You can't claim this land due to your relation with the current owner.");
						return false;
					}
					
					if (!oldFaction.hasLandInflation())
					{
						msg("%s<i> owns this land and is strong enough to keep it.", oldFaction.getName(this));
						return false;
					}
					
					if ( ! BoardColls.get().isBorderPs(chunk))
					{
						msg("<b>You must start claiming land at the border of the territory.");
						return false;
					}
				}
			}
		}
		
		// Event
		FactionsEventChunkChange event = new FactionsEventChunkChange(sender, chunk, newFaction);
		event.run();
		if (event.isCancelled()) return false;

		// Apply
		BoardColls.get().setFactionAt(chunk, newFaction);
		
		// Inform
		Set<UPlayer> informees = new HashSet<UPlayer>();
		informees.add(this);
		if (newFaction.isNormal())
		{
			informees.addAll(newFaction.getUPlayers());
		}
		if (oldFaction.isNormal())
		{
			informees.addAll(oldFaction.getUPlayers());
		}
		if (MConf.get().logLandClaims)
		{
			informees.add(UPlayer.get(SenderUtil.getConsole()));
		}
		
		String chunkString = chunk.toString(PSFormatHumanSpace.get());
		String typeString = event.getType().toString().toLowerCase();
		for (UPlayer informee : informees)
		{
			informee.msg("<h>%s<i> did %s %s <i>for <h>%s<i> from <h>%s<i>.", this.describeTo(informee, true), typeString, chunkString, newFaction.describeTo(informee), oldFaction.describeTo(informee));
		}

		return true;
	}
	
}
