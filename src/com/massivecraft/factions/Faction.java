package com.massivecraft.factions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.*;
import com.massivecraft.factions.zcore.persist.Entity;
import com.nijikokun.register.payment.Method.MethodAccount;
import java.util.logging.Level;


public class Faction extends Entity implements EconomyParticipator
{
	// FIELD: relationWish
	private Map<String, Relation> relationWish;
	
	// FIELD: claimOwnership
	private Map<FLocation, Set<String>> claimOwnership = new ConcurrentHashMap<FLocation, Set<String>>();
	
	// FIELD: invites
	// Where string is a lowercase player name
	private Set<String> invites; 
	public void invite(FPlayer fplayer) { this.invites.add(fplayer.getName().toLowerCase()); }
	public void deinvite(FPlayer fplayer) { this.invites.remove(fplayer.getName().toLowerCase()); }
	public boolean isInvited(FPlayer fplayer) { return this.invites.contains(fplayer.getName().toLowerCase()); }
	
	// FIELD: open
	private boolean open;
	public boolean getOpen() { return open; }
	public void setOpen(boolean isOpen) { open = isOpen; }
	
	// FIELD: peaceful
	// "peaceful" status can only be set by server admins/moderators/ops, and prevents PvP and land capture to/from the faction
	private boolean peaceful;
	public boolean isPeaceful() { return this.peaceful; }
	public void setPeaceful(boolean isPeaceful) { this.peaceful = isPeaceful; }
	
	// FIELD: peacefulExplosionsEnabled
	private boolean peacefulExplosionsEnabled;
	public void setPeacefulExplosionsEnabled(boolean val) { peacefulExplosionsEnabled = val; }
	public boolean getPeacefulExplosionsEnabled(){ return this.peacefulExplosionsEnabled; }
	
	public boolean noExplosionsInTerritory() { return this.peaceful && ! peacefulExplosionsEnabled; }

	// FIELD: permanent
	// "permanent" status can only be set by server admins/moderators/ops, and allows the faction to remain even with 0 members
	private boolean permanent;
	public boolean isPermanent() { return permanent; }
	public void setPermanent(boolean isPermanent) { permanent = isPermanent; }
	
	// FIELD: tag
	private String tag;
	public String getTag() { return this.tag; }
	public String getTag(String prefix) { return prefix+this.tag; }
	public String getTag(Faction otherFaction)
	{
		if (otherFaction == null)
		{
			return getTag();
		}
		return this.getTag(this.getColorTo(otherFaction).toString());
	}
	public String getTag(FPlayer otherFplayer) {
		if (otherFplayer == null)
		{
			return getTag();
		}
		return this.getTag(this.getColorTo(otherFplayer).toString());
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
	
	// FIELD: lastPlayerLoggedOffTime
	private transient long lastPlayerLoggedOffTime;
	
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
	
	// FIELD: permanentPower
	private Integer permanentPower;
	public Integer getPermanentPower() { return this.permanentPower; }
	public void setPermanentPower(Integer permanentPower) { this.permanentPower = permanentPower; }
	public boolean hasPermanentPower() { return this.permanentPower != null; }
	
	// -------------------------------------------- //
	// Construct
	// -------------------------------------------- //
	
	public Faction()
	{
		this.relationWish = new HashMap<String, Relation>();
		this.invites = new HashSet<String>();
		this.open = Conf.newFactionsDefaultOpen;
		this.tag = "???";
		this.description = "Default faction description :(";
		this.lastPlayerLoggedOffTime = 0;
		this.peaceful = false;
		this.peacefulExplosionsEnabled = false;
		this.permanent = false;
		this.money = 0.0;
	}
	
	// -------------------------------------------- //
	// Extra Getters And Setters
	// -------------------------------------------- //

	public boolean noPvPInTerritory() { return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisablePVP); }

	public boolean noMonstersInTerritory() { return isSafeZone() || (peaceful && Conf.peacefulTerritoryDisableMonsters); }

	

	// -------------------------------
	// Understand the types
	// -------------------------------
	
	public boolean isNormal()
	{
		return ! (this.isNone() || this.isSafeZone() || this.isWarZone());
	}
	
	public boolean isNone()
	{
		return this.getId().equals("0");
	}
	
	public boolean isSafeZone()
	{
		return this.getId().equals("-1");
	}
	
	public boolean isWarZone()
	{
		return this.getId().equals("-2");
	}
	
	public boolean isPlayerFreeType()
	{
		return this.isSafeZone() || this.isWarZone();
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
	
	@Override
	public ChatColor getColorTo(RelationParticipator rp)
	{
		return RelationUtil.getColorOfThatToMe(this, rp);
	}
	
	public Relation getRelationWish(Faction otherFaction)
	{
		if (this.relationWish.containsKey(otherFaction.getId()))
		{
			return this.relationWish.get(otherFaction.getId());
		}
		return Relation.NEUTRAL;
	}
	
	public void setRelationWish(Faction otherFaction, Relation relation)
	{
		if (this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Relation.NEUTRAL))
		{
			this.relationWish.remove(otherFaction.getId());
		}
		else
		{
			this.relationWish.put(otherFaction.getId(), relation);
		}
	}
	
	//----------------------------------------------//
	// Power
	//----------------------------------------------//
	public double getPower()
	{
		if (this.hasPermanentPower())
		{
			return this.getPermanentPower();
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
		if (this.hasPermanentPower())
		{
			return this.getPermanentPower();
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
		if (this.isPlayerFreeType()) return ret;

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
		if (this.isPlayerFreeType()) return ret;

		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction() == this && fplayer.isOnline() == online)
			{
				ret.add(fplayer);
			}
		}

		return ret;
	}
	
	public FPlayer getFPlayerAdmin()
	{
		if ( ! this.isNormal()) return null;
		
		for (FPlayer fplayer : FPlayers.i.get())
		{
			if (fplayer.getFaction() == this && fplayer.getRole() == Role.ADMIN)
			{
				return fplayer;
			}
		}
		return null;
	}
	
	public ArrayList<FPlayer> getFPlayersWhereRole(Role role)
	{
		ArrayList<FPlayer> ret = new ArrayList<FPlayer>();
		if ( ! this.isNormal()) return ret;
		
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
		if (this.isPlayerFreeType()) return ret;

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
	
	// slightly faster check than getOnlinePlayers() if you just want to see if there are any players online
	public boolean hasPlayersOnline()
	{
		// only real factions can have players online, not safe zone / war zone
		if (this.isPlayerFreeType()) return false;
		
		for (Player player: P.p.getServer().getOnlinePlayers())
		{
			FPlayer fplayer = FPlayers.i.get(player);
			if (fplayer.getFaction() == this)
			{
				return true;
			}
		}
		
		// even if all players are technically logged off, maybe someone was on recently enough to not consider them officially offline yet
		if (Conf.considerFactionsReallyOfflineAfterXMinutes > 0 && System.currentTimeMillis() < lastPlayerLoggedOffTime + (Conf.considerFactionsReallyOfflineAfterXMinutes * 60000))
		{
			return true;
		}
		return false;
	}
	
	public void memberLoggedOff()
	{
		if (this.isNormal())
		{
			lastPlayerLoggedOffTime = System.currentTimeMillis();
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
	// Ownership of specific claims
	//----------------------------------------------//

	public void clearAllClaimOwnership()
	{
		claimOwnership.clear();
	}

	public void clearClaimOwnership(FLocation loc)
	{
		claimOwnership.remove(loc);
	}

	public void clearClaimOwnership(String playerName)
	{
		if (playerName == null || playerName.isEmpty())
		{
			return;
		}

		Set<String> ownerData;
		String player = playerName.toLowerCase();

		for (Entry<FLocation, Set<String>> entry : claimOwnership.entrySet())
		{
			ownerData = entry.getValue();

			if (ownerData == null) continue;

			Iterator<String> iter = ownerData.iterator();
			while (iter.hasNext())
			{
				if (iter.next().equals(player))
				{
					iter.remove();
				}
			}

			if (ownerData.isEmpty())
			{
				claimOwnership.remove(entry.getKey());
			}
		}
	}

	public int getCountOfClaimsWithOwners()
	{
		return claimOwnership.isEmpty() ? 0 : claimOwnership.size();
	}

	public boolean doesLocationHaveOwnersSet(FLocation loc)
	{
		if (claimOwnership.isEmpty() || !claimOwnership.containsKey(loc))
		{
			return false;
		}
		
		Set<String> ownerData = claimOwnership.get(loc);
		return ownerData != null && !ownerData.isEmpty();
	}

	public boolean isPlayerInOwnerList(String playerName, FLocation loc)
	{
		if (claimOwnership.isEmpty())
		{
			return false;
		}
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null)
		{
			return false;
		}
		if (ownerData.contains(playerName.toLowerCase()))
		{
			return true;
		}
		
		return false;
	}

	public void setPlayerAsOwner(String playerName, FLocation loc)
	{
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null)
		{
			ownerData = new HashSet<String>();
		}
		ownerData.add(playerName.toLowerCase());
		claimOwnership.put(loc, ownerData);
	}

	public void removePlayerAsOwner(String playerName, FLocation loc)
	{
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null)
		{
			return;
		}
		ownerData.remove(playerName.toLowerCase());
		claimOwnership.put(loc, ownerData);
	}

	public Set<String> getOwnerList(FLocation loc)
	{
		return claimOwnership.get(loc);
	}

	public String getOwnerListString(FLocation loc)
	{
		Set<String> ownerData = claimOwnership.get(loc);
		if (ownerData == null || ownerData.isEmpty())
		{
			return "";
		}

		String ownerList = "";

		Iterator<String> iter = ownerData.iterator();
		while (iter.hasNext()) {
			if (!ownerList.isEmpty())
			{
				ownerList += ", ";
			}
			ownerList += iter.next();
		}
		return ownerList;
	}

	public boolean playerHasOwnershipRights(FPlayer fplayer, FLocation loc)
	{
		// in own faction, with sufficient role or permission to bypass ownership?
		if
		(
			fplayer.getFaction() == this
			&&
			(
				fplayer.getRole().isAtLeast(Conf.ownedAreaModeratorsBypass ? Role.MODERATOR : Role.ADMIN)
				||
				Permission.OWNERSHIP_BYPASS.has(fplayer.getPlayer())
			)
		)
		{
			return true;
		}

		// make sure claimOwnership is initialized
		if (claimOwnership.isEmpty())
			return true;

		// need to check the ownership list, then
		Set<String> ownerData = claimOwnership.get(loc);

		// if no owner list, owner list is empty, or player is in owner list, they're allowed
		if (ownerData == null || ownerData.isEmpty() || ownerData.contains(fplayer.getName().toLowerCase()))
			return true;

		return false;
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
