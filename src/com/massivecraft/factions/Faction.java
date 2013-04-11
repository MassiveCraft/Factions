package com.massivecraft.factions;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.util.Txt;
import com.massivecraft.mcore.xlib.gson.annotations.SerializedName;


public class Faction extends Entity implements EconomyParticipator
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	/*public static Faction get(Object oid)
	{
		return FactionColl.get().get(oid);
	}*/
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	/*@Override
	public Faction load(Faction that)
	{
		//this.item = that.item;
		// TODO
		
		return this;
	}*/
	
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	
	// speedy lookup of players in faction
	private transient Set<FPlayer> fplayers = new HashSet<FPlayer>();
	// TODO
	
	private Map<String, Rel> relationWish;
	// TODO
	
	@SerializedName("invites")
	private Set<String> invitedPlayerIds = null;
	public TreeSet<String> getInvitedPlayerIds()
	{
		TreeSet<String> ret = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		if (this.invitedPlayerIds != null) ret.addAll(this.invitedPlayerIds);
		return ret;
	}
	public void setInvitedPlayerIds(Collection<String> invitedPlayerIds)
	{
		TreeSet<String> target = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		
		if (invitedPlayerIds != null)
		{
			for (String invitedPlayerId : invitedPlayerIds)
			{
				target.add(invitedPlayerId.toLowerCase());
			}
		}
		
		this.invitedPlayerIds = target;
		// TODO: Add when we use a true mcore entity.
		// this.changed();
	}
	
	private boolean open;
	public boolean isOpen() { return this.open; }
	public void setOpen(boolean open) { this.open = open; }
	
	// FIELD: tag
	private String tag;
	public String getTag() { return this.tag; }
	public String getTag(String prefix) { return prefix+this.tag; }
	public String getTag(RelationParticipator observer)
	{
		if (observer == null)
		{
			return getTag();
		}
		return this.getTag(this.getColorTo(observer).toString());
	}
	public void setTag(String str)
	{
		if (ConfServer.factionTagForceUpperCase)
		{
			str = str.toUpperCase();
		}
		this.tag = str;
	}
	public String getComparisonTag() { return MiscUtil.getComparisonString(this.tag); }
	
	// FIELD: description
	private String description;
	public String getDescription() { return this.description; }
	public void setDescription(String value) { this.description = value; }
	
	// FIELD: home
	private LazyLocation home;
	public void setHome(Location home) { this.home = new LazyLocation(home); }
	public boolean hasHome() { return this.getHome() != null; }
	public Location getHome()
	{
		confirmValidHome();
		return (this.home != null) ? this.home.getLocation() : null;
	}
	public void confirmValidHome()
	{
		if (!ConfServer.homesMustBeInClaimedTerritory || this.home == null || (this.home.getLocation() != null && BoardColl.get().getFactionAt(PS.valueOf(this.home.getLocation())) == this))
			return;

		msg("<b>Your faction home has been un-set since it is no longer in your territory.");
		this.home = null;
	}
	
	// FIELD: account (fake field)
	// Bank functions
	public String getAccountId()
	{
		String accountId = "faction-"+this.getId();

		// We need to override the default money given to players.
		if ( ! Econ.hasAccount(accountId))
		{
			Econ.setBalance(accountId, 0);
		}

		return accountId;
	}
	
	// FIELD: cape
	private String cape;
	public String getCape() { return cape; }
	public void setCape(String val) { this.cape = val; SpoutFeatures.updateCape(this, null); }

	// FIELD: powerBoost
	// special increase/decrease to default and max power for this faction
	private double powerBoost;
	public double getPowerBoost() { return this.powerBoost; }
	public void setPowerBoost(double powerBoost) { this.powerBoost = powerBoost; }

	// FIELDS: Flag management
	// TODO: This will save... defaults if they where changed to...
	private Map<FFlag, Boolean> flagOverrides; // Contains the modifications to the default values
	public boolean getFlag(FFlag flag)
	{
		Boolean ret = this.flagOverrides.get(flag);
		if (ret == null) ret = flag.getDefault();
		return ret;
	}
	public void setFlag(FFlag flag, boolean value)
	{
		if (ConfServer.factionFlagDefaults.get(flag).equals(value))
		{
			this.flagOverrides.remove(flag);
			return;
		}
		this.flagOverrides.put(flag, value);
	}

	// FIELDS: Permission <-> Groups management
	private Map<FPerm, Set<Rel>> permOverrides; // Contains the modifications to the default values
	public Set<Rel> getPermittedRelations(FPerm perm)
	{
		Set<Rel> ret = this.permOverrides.get(perm);
		if (ret == null) ret = perm.getDefault();
		return ret;
	}
	
	/*
	public void addPermittedRelation(FPerm perm, Rel rel)
	{
		Set<Rel> newPermittedRelations = EnumSet.noneOf(Rel.class);
		newPermittedRelations.addAll(this.getPermittedRelations(perm));
		newPermittedRelations.add(rel);
		this.setPermittedRelations(perm, newPermittedRelations);
	}
	
	public void removePermittedRelation(FPerm perm, Rel rel)
	{
		Set<Rel> newPermittedRelations = EnumSet.noneOf(Rel.class);
		newPermittedRelations.addAll(this.getPermittedRelations(perm));
		newPermittedRelations.remove(rel);
		this.setPermittedRelations(perm, newPermittedRelations);
	}*/
	
	public void setRelationPermitted(FPerm perm, Rel rel, boolean permitted)
	{
		Set<Rel> newPermittedRelations = EnumSet.noneOf(Rel.class);
		newPermittedRelations.addAll(this.getPermittedRelations(perm));
		if (permitted)
		{
			newPermittedRelations.add(rel);
		}
		else
		{
			newPermittedRelations.remove(rel);
		}
		this.setPermittedRelations(perm, newPermittedRelations);
	}
	
	public void setPermittedRelations(FPerm perm, Set<Rel> rels)
	{
		if (perm.getDefault().equals(rels))
		{
			this.permOverrides.remove(perm);
			return;
		}
		this.permOverrides.put(perm, rels);
	}
	
	public void setPermittedRelations(FPerm perm, Rel... rels)
	{
		Set<Rel> temp = new HashSet<Rel>();
		temp.addAll(Arrays.asList(rels));
		this.setPermittedRelations(perm, temp);
	}
	
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	public Faction()
	{
		this.relationWish = new HashMap<String, Rel>();
		this.open = ConfServer.newFactionsDefaultOpen;
		this.tag = "???";
		this.description = "Default faction description :(";
		this.powerBoost = 0.0;
		this.flagOverrides = new LinkedHashMap<FFlag, Boolean>();
		this.permOverrides = new LinkedHashMap<FPerm, Set<Rel>>();
	}
	
	// -------------------------------------------- //
	// FIELDS: EXTRA
	// -------------------------------------------- //
	
	// TODO: Make use of a player name extractor?
	
	public boolean addInvitedPlayerId(String playerId)
	{
		TreeSet<String> invitedPlayerIds = this.getInvitedPlayerIds();
		if (invitedPlayerIds.add(playerId.toLowerCase()))
		{
			this.setInvitedPlayerIds(invitedPlayerIds);
			return true;
		}
		return false;
	}
	
	public boolean removeInvitedPlayerId(String playerId)
	{
		TreeSet<String> invitedPlayerIds = this.getInvitedPlayerIds();
		if (invitedPlayerIds.remove(playerId.toLowerCase()))
		{
			this.setInvitedPlayerIds(invitedPlayerIds);
			return true;
		}
		return false;
	}
	
	public boolean isInvited(FPlayer fplayer)
	{
		return this.getInvitedPlayerIds().contains(fplayer.getId());
	}
	
	// -------------------------------------------- //
	// ACTIONS
	// -------------------------------------------- //
	
	public void invite(FPlayer fplayer)
	{
		this.addInvitedPlayerId(fplayer.getId());
	}
	
	public void deinvite(FPlayer fplayer)
	{
		this.removeInvitedPlayerId(fplayer.getId());
	}

	// -------------------------------
	// Understand the types
	// -------------------------------
	
	// TODO: These should be gone after the refactoring...
	
	public boolean isNormal()
	{
		//return ! (this.isNone() || this.isSafeZone() || this.isWarZone());
		return ! this.isNone();
	}
	
	public boolean isNone()
	{
		return this.getId().equals("0");
	}
	
	// -------------------------------
	// Relation and relation colors
	// -------------------------------
	
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
	
	public Rel getRelationWish(Faction otherFaction)
	{
		if (this.relationWish.containsKey(otherFaction.getId()))
		{
			return this.relationWish.get(otherFaction.getId());
		}
		return Rel.NEUTRAL;
	}
	
	public void setRelationWish(Faction otherFaction, Rel relation)
	{
		if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Rel.NEUTRAL))
		{
			this.relationWish.remove(otherFaction.getId());
		}
		else
		{
			this.relationWish.put(otherFaction.getId(), relation);
		}
	}
	
	public Map<Rel, List<String>> getFactionTagsPerRelation(RelationParticipator rp)
	{
		return getFactionTagsPerRelation(rp, false);
	}

	// onlyNonNeutral option provides substantial performance boost on large servers for listing only non-neutral factions
	public Map<Rel, List<String>> getFactionTagsPerRelation(RelationParticipator rp, boolean onlyNonNeutral)
	{
		Map<Rel, List<String>> ret = new HashMap<Rel, List<String>>();
		for (Rel rel : Rel.values())
		{
			ret.put(rel, new ArrayList<String>());
		}
		for (Faction faction : FactionColl.i.get())
		{
			Rel relation = faction.getRelationTo(this);
			if (onlyNonNeutral && relation == Rel.NEUTRAL) continue;
			ret.get(relation).add(faction.getTag(rp));
		}
		return ret;
	}
	
	// TODO: Implement a has enough feature.
	// -------------------------------------------- //
	// Power
	// -------------------------------------------- //
	public double getPower()
	{
		if (this.getFlag(FFlag.INFPOWER))
		{
			return 999999;
		}
		
		double ret = 0;
		for (FPlayer fplayer : fplayers)
		{
			ret += fplayer.getPower();
		}
		if (ConfServer.powerFactionMax > 0 && ret > ConfServer.powerFactionMax)
		{
			ret = ConfServer.powerFactionMax;
		}
		return ret + this.powerBoost;
	}
	
	public double getPowerMax()
	{
		if (this.getFlag(FFlag.INFPOWER))
		{
			return 999999;
		}
		
		double ret = 0;
		for (FPlayer fplayer : fplayers)
		{
			ret += fplayer.getPowerMax();
		}
		if (ConfServer.powerFactionMax > 0 && ret > ConfServer.powerFactionMax)
		{
			ret = ConfServer.powerFactionMax;
		}
		return ret + this.powerBoost;
	}
	
	public int getPowerRounded()
	{
		return (int) Math.round(this.getPower());
	}
	
	public int getPowerMaxRounded()
	{
		return (int) Math.round(this.getPowerMax());
	}
	
	//  TODO: Why "rounded"? Rename to getLandCount? or getChunkCount?
	public int getLandRounded()
	{
		return BoardColl.get().getCount(this);
	}
	public int getLandRoundedInWorld(String worldName)
	{
		return BoardColl.get().get(worldName).getCount(this);
	}
	
	public boolean hasLandInflation()
	{
		return this.getLandRounded() > this.getPowerRounded();
	}
	
	// -------------------------------------------- //
	// FPlayers
	// -------------------------------------------- //

	// maintain the reference list of FPlayers in this faction
	public void refreshFPlayers()
	{
		fplayers.clear();
		if (this.isNone()) return;

		for (FPlayer fplayer : FPlayerColl.i.get())
		{
			if (fplayer.getFaction() == this)
			{
				fplayers.add(fplayer);
			}
		}
	}
	protected boolean addFPlayer(FPlayer fplayer)
	{
		if (this.isNone()) return false;

		return fplayers.add(fplayer);
	}
	protected boolean removeFPlayer(FPlayer fplayer)
	{
		if (this.isNone()) return false;

		return fplayers.remove(fplayer);
	}

	public Set<FPlayer> getFPlayers()
	{
		// return a shallow copy of the FPlayer list, to prevent tampering and concurrency issues
		Set<FPlayer> ret = new HashSet<FPlayer>(fplayers);
		return ret;
	}
	
	public Set<FPlayer> getFPlayersWhereOnline(boolean online)
	{
		Set<FPlayer> ret = new HashSet<FPlayer>();

		for (FPlayer fplayer : fplayers)
		{
			if (fplayer.isOnline() == online)
			{
				ret.add(fplayer);
			}
		}

		return ret;
	}
	
	public FPlayer getFPlayerLeader()
	{
		//if ( ! this.isNormal()) return null;
		
		for (FPlayer fplayer : fplayers)
		{
			if (fplayer.getRole() == Rel.LEADER)
			{
				return fplayer;
			}
		}
		return null;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereRole(Rel role)
	{
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		//if ( ! this.isNormal()) return ret;
		
		for (FPlayer fplayer : fplayers)
		{
			if (fplayer.getRole() == role)
			{
				ret.add(fplayer);
			}
		}
		
		return ret;
	}
	
	public ArrayList<Player> getOnlinePlayers()
	{
		ArrayList<Player> ret = new ArrayList<Player>();
		//if (this.isPlayerFreeType()) return ret;

		for (Player player: Factions.get().getServer().getOnlinePlayers())
		{
			FPlayer fplayer = FPlayerColl.i.get(player);
			if (fplayer.getFaction() == this)
			{
				ret.add(player);
			}
		}

		return ret;
	}

	// used when current leader is about to be removed from the faction; promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader()
	{
		if (! this.isNormal()) return;
		if (this.getFlag(FFlag.PERMANENT) && ConfServer.permanentFactionsDisableLeaderPromotion) return;

		FPlayer oldLeader = this.getFPlayerLeader();

		// get list of officers, or list of normal members if there are no officers
		ArrayList<FPlayer> replacements = this.getFPlayersWhereRole(Rel.OFFICER);
		if (replacements == null || replacements.isEmpty())
			replacements = this.getFPlayersWhereRole(Rel.MEMBER);

		if (replacements == null || replacements.isEmpty())
		{	// faction leader is the only member; one-man faction
			if (this.getFlag(FFlag.PERMANENT))
			{
				if (oldLeader != null)
					oldLeader.setRole(Rel.MEMBER);
				return;
			}

			// no members left and faction isn't permanent, so disband it
			if (ConfServer.logFactionDisband)
				Factions.get().log("The faction "+this.getTag()+" ("+this.getId()+") has been disbanded since it has no members left.");

			for (FPlayer fplayer : FPlayerColl.i.getOnline())
			{
				fplayer.msg("The faction %s<i> was disbanded.", this.getTag(fplayer));
			}

			this.detach();
		}
		else
		{	// promote new faction leader
			if (oldLeader != null)
				oldLeader.setRole(Rel.MEMBER);
			replacements.get(0).setRole(Rel.LEADER);
			this.msg("<i>Faction leader <h>%s<i> has been removed. %s<i> has been promoted as the new faction leader.", oldLeader == null ? "" : oldLeader.getName(), replacements.get(0).getName());
			Factions.get().log("Faction "+this.getTag()+" ("+this.getId()+") leader was removed. Replacement leader: "+replacements.get(0).getName());
		}
	}

	// -------------------------------------------- //
	// Messages
	// -------------------------------------------- //
	
	public void msg(String message, Object... args)
	{
		message = Txt.parse(message, args);
		
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true))
		{
			fplayer.sendMessage(message);
		}
	}
	
	public void sendMessage(String message)
	{
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true))
		{
			fplayer.sendMessage(message);
		}
	}
	
	public void sendMessage(List<String> messages)
	{
		for (FPlayer fplayer : this.getFPlayersWhereOnline(true))
		{
			fplayer.sendMessage(messages);
		}
	}
	
	// -------------------------------------------- //
	// Persistance and entity management
	// -------------------------------------------- //
	
	@Override
	public void postDetach()
	{
		if (Econ.shouldBeUsed())
		{
			Econ.setBalance(getAccountId(), 0);
		}
		
		// Clean the board
		// TODO: Use events for this instead
		BoardColl.get().clean();
		
		// Clean the fplayers
		FPlayerColl.i.clean();
	}
}
