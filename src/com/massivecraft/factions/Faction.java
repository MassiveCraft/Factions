package com.massivecraft.factions;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.zcore.persist.Entity;


public class Faction extends Entity implements EconomyParticipator
{
	// FIELD: relationWish
	private Map<String, Rel> relationWish;

	// FIELD: fplayers
	// speedy lookup of players in faction
	private transient Set<FPlayer> fplayers = new HashSet<FPlayer>();

	// FIELD: invites
	// Where string is a lowercase player name
	private Set<String> invites; 
	public void invite(FPlayer fplayer) { this.invites.add(fplayer.getId().toLowerCase()); }
	public void deinvite(FPlayer fplayer) { this.invites.remove(fplayer.getId().toLowerCase()); }
	public boolean isInvited(FPlayer fplayer) { return this.invites.contains(fplayer.getId().toLowerCase()); }
	
	// FIELD: open
	private boolean open;
	public boolean getOpen() { return open; }
	public void setOpen(boolean isOpen) { open = isOpen; }
	
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
		if (Conf.factionTagForceUpperCase)
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
		if (!Conf.homesMustBeInClaimedTerritory || this.home == null || (this.home.getLocation() != null && Board.getFactionAt(new FLocation(this.home.getLocation())) == this))
			return;

		msg("<b>Your faction home has been un-set since it is no longer in your territory.");
		this.home = null;
	}
	
	// FIELD: account (fake field)
	// Bank functions
	public double money;
	public String getAccountId()
	{
		String aid = "faction-"+this.getId();

		// We need to override the default money given to players.
		if ( ! Econ.hasAccount(aid))
			Econ.setBalance(aid, 0);

		return aid;
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
		if (Conf.factionFlagDefaults.get(flag).equals(value))
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
		this.invites = new HashSet<String>();
		this.open = Conf.newFactionsDefaultOpen;
		this.tag = "???";
		this.description = "Default faction description :(";
		this.money = 0.0;
		this.powerBoost = 0.0;
		this.flagOverrides = new LinkedHashMap<FFlag, Boolean>();
		this.permOverrides = new LinkedHashMap<FPerm, Set<Rel>>();
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
		for (Faction faction : Factions.i.get())
		{
			Rel relation = faction.getRelationTo(this);
			if (onlyNonNeutral && relation == Rel.NEUTRAL) continue;
			ret.get(relation).add(faction.getTag(rp));
		}
		return ret;
	}
	
	// TODO: Implement a has enough feature.
	//----------------------------------------------//
	// Power
	//----------------------------------------------//
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
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax)
		{
			ret = Conf.powerFactionMax;
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
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax)
		{
			ret = Conf.powerFactionMax;
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
	
	public int getLandRounded() {
		return Board.getFactionCoordCount(this);
	}
	
	public int getLandRoundedInWorld(String worldName)
	{
		return Board.getFactionCoordCountInWorld(this, worldName);
	}
	
	public boolean hasLandInflation()
	{
		return this.getLandRounded() > this.getPowerRounded();
	}
	
	// -------------------------------
	// FPlayers
	// -------------------------------

	// maintain the reference list of FPlayers in this faction
	public void refreshFPlayers()
	{
		fplayers.clear();
		if (this.isNone()) return;

		for (FPlayer fplayer : FPlayers.i.get())
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

		for (Player player: P.p.getServer().getOnlinePlayers())
		{
			FPlayer fplayer = FPlayers.i.get(player);
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
		if (this.getFlag(FFlag.PERMANENT) && Conf.permanentFactionsDisableLeaderPromotion) return;

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
			if (Conf.logFactionDisband)
				P.p.log("The faction "+this.getTag()+" ("+this.getId()+") has been disbanded since it has no members left.");

			for (FPlayer fplayer : FPlayers.i.getOnline())
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
			P.p.log("Faction "+this.getTag()+" ("+this.getId()+") leader was removed. Replacement leader: "+replacements.get(0).getName());
		}
	}

	//----------------------------------------------//
	// Messages
	//----------------------------------------------//
	public void msg(String message, Object... args)
	{
		message = P.p.txt.parse(message, args);
		
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
	
	//----------------------------------------------//
	// Deprecated
	//----------------------------------------------//
	/**
	 * @deprecated  As of release 1.7, replaced by {@link #getFPlayerLeader()}
	 */
	public FPlayer getFPlayerAdmin()
	{
		return getFPlayerLeader();
	}
	
	/**
	 * @deprecated  As of release 1.7, replaced by {@link #getFlag()}
	 */
	public boolean isPeaceful()
	{
		return this.getFlag(FFlag.PEACEFUL);
	}
	
	/**
	 * @deprecated  As of release 1.7, replaced by {@link #getFlag()}
	 */
	public boolean getPeacefulExplosionsEnabled()
	{
		return this.getFlag(FFlag.EXPLOSIONS);
	}
	
	/**
	 * @deprecated  As of release 1.7, replaced by {@link #getFlag()}
	 */
	public boolean noExplosionsInTerritory()
	{
		return ! this.getFlag(FFlag.EXPLOSIONS);
	}
	
	/**
	 * @deprecated  As of release 1.7, replaced by {@link #getFlag()}
	 */
	public boolean isSafeZone()
	{
		return ! this.getFlag(FFlag.EXPLOSIONS);
	}
	
	//----------------------------------------------//
	// Persistance and entity management
	//----------------------------------------------//

	
	@Override
	public void postDetach()
	{
		if (Econ.shouldBeUsed())
		{
			Econ.setBalance(getAccountId(), 0);
		}
		
		// Clean the board
		Board.clean();
		
		// Clean the fplayers
		FPlayers.i.clean();
	}
}
