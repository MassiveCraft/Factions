package com.massivecraft.factions;

import java.util.*;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.zcore.persist.Entity;
import com.nijikokun.register.payment.Method.MethodAccount;


public class Faction extends Entity implements EconomyParticipator
{
	// FIELD: relationWish
	private Map<String, Rel> relationWish;
	
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
	private Location home;
	public void setHome(Location home) { this.home = home; }
	public Location getHome() { confirmValidHome(); return home; }
	public boolean hasHome() { return this.getHome() != null; }
	public void confirmValidHome()
	{
		if (!Conf.homesMustBeInClaimedTerritory || this.home == null || Board.getFactionAt(new FLocation(this.home)) == this)
		{
			return;
		}

		msg("<b>Your faction home has been un-set since it is no longer in your territory.");
		this.home = null;
	}
	
	// FIELD: account (fake field)
	// Bank functions
	public double money;
	public String getAccountId() { return "faction-"+this.getId(); }
	public MethodAccount getAccount()
	{
		String aid = this.getAccountId();

		// We need to override the default money given to players.
		if ( ! Econ.getMethod().hasAccount(aid))
		{
			if ( ! Econ.getMethod().createAccount(aid))
			{
				P.p.log(Level.SEVERE, "Error creating faction bank account through Register: "+aid);
//				return null;
			}
			MethodAccount acc = Econ.getMethod().getAccount(aid);
			acc.set(0); 
		}
		
		return Econ.getMethod().getAccount(aid);
	}
	
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
		Map<Rel, List<String>> ret = new HashMap<Rel, List<String>>();
		for (Rel rel : Rel.values())
		{
			ret.put(rel, new ArrayList<String>());
		}
		for (Faction faction : Factions.i.get())
		{
			Rel relation = faction.getRelationTo(this);
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
		for (FPlayer fplayer : this.getFPlayers())
		{
			ret += fplayer.getPower();
		}
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax)
		{
			ret = Conf.powerFactionMax;
		}
		return ret;
	}
	
	public double getPowerMax()
	{
		if (this.getFlag(FFlag.INFPOWER))
		{
			return 999999;
		}
		
		double ret = 0;
		for (FPlayer fplayer : this.getFPlayers())
		{
			ret += fplayer.getPowerMax();
		}
		if (Conf.powerFactionMax > 0 && ret > Conf.powerFactionMax)
		{
			ret = Conf.powerFactionMax;
		}
		return ret;
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
	
	public ArrayList<FPlayer> getFPlayers()
	{
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		//if (this.isPlayerFreeType()) return ret;

		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction() == this)
			{
				ret.add(fplayer);
			}
		}

		return ret;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereOnline(boolean online)
	{
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		//if (this.isPlayerFreeType()) return ret;

		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction() == this && fplayer.isOnline() == online)
			{
				ret.add(fplayer);
			}
		}

		return ret;
	}
	
	public FPlayer getFPlayerLeader()
	{
		//if ( ! this.isNormal()) return null;
		
		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction() == this && fplayer.getRole() == Rel.LEADER)
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
		
		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction() == this && fplayer.getRole() == role)
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
	// Persistance and entity management
	//----------------------------------------------//

	
	@Override
	public void postDetach()
	{
		if (Econ.shouldBeUsed())
		{
			Econ.getMethod().getAccount(getAccountId()).remove();
		}
		
		this.getAccountId();
		
		// Clean the board
		Board.clean();
		
		// Clean the fplayers
		FPlayers.i.clean();
	}
}
