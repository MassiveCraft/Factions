package com.massivecraft.factions;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.util.*;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.store.Entity;
import com.massivecraft.mcore.util.SenderUtil;
import com.massivecraft.mcore.xlib.gson.annotations.SerializedName;


public class Faction extends Entity<Faction> implements EconomyParticipator
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static Faction get(Object oid)
	{
		return FactionColl.get().get(oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public Faction load(Faction that)
	{
		this.tag = that.tag;
		this.setDescription(that.description);
		this.open = that.open;
		this.setInvitedPlayerIds(that.invitedPlayerIds);
		this.setRelationWishes(that.relationWish);
		this.home = that.home;
		this.cape = that.cape;
		this.setPowerBoost(that.powerBoost);
		this.setFlags(that.flagOverrides);
		this.permOverrides = that.permOverrides;
		
		return this;
	}
	
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	// TODO
	// In this section of the source code we place the field declarations only.
	// Each field has it's own section further down since even the getter and setter logic takes up quite some place.
	
	// TODO: The faction "tag" could/should also have been called "name".
	// The actual faction id looks something like "54947df8-0e9e-4471-a2f9-9af509fb5889" and that is not too easy to remember for humans.
	// Thus we make use of a name. Since the id is used in all foreign key situations changing the name is fine.
	private String tag = null;
	
	private String description = null;
	
	private Boolean open = null;
	
	@SerializedName("invites")
	private Set<String> invitedPlayerIds = null;
	
	private Map<String, Rel> relationWish = null;
	
	// FIELD: home
	// TODO: Use a PS instead!
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
	
	// FIELD: cape
	private String cape;
	public String getCape() { return cape; }
	public void setCape(String val) { this.cape = val; SpoutFeatures.updateCape(this, null); }

	// The powerBoost is a custom increase/decrease to default and max power for this faction
	private Double powerBoost = null;

	// The flag overrides are the modifications to the default values
	private Map<FFlag, Boolean> flagOverrides;
	

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
	// CONSTRUCT
	// -------------------------------------------- //
	
	public Faction()
	{
		this.flagOverrides = new LinkedHashMap<FFlag, Boolean>();
		this.permOverrides = new LinkedHashMap<FPerm, Set<Rel>>();
	}
	
	// -------------------------------------------- //
	// FIELD: id
	// -------------------------------------------- //
	
	// FINER
	
	public boolean isNone()
	{
		return this.getId().equals(Const.FACTIONID_NONE);
	}
	
	public boolean isNormal()
	{
		return ! this.isNone();
	}
	
	// This is the bank account id used by external money-plugins
	@Override
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
	
	// -------------------------------------------- //
	// FIELD: tag
	// -------------------------------------------- //
	// TODO: Rename tag --> name ?
	
	// RAW
	
	public String getTag()
	{
		String ret = this.tag;
		if (ConfServer.factionTagForceUpperCase)
		{
			ret = ret.toUpperCase();
		}
		return ret;
	}
	
	public void setTag(String str)
	{
		if (ConfServer.factionTagForceUpperCase)
		{
			str = str.toUpperCase();
		}
		this.tag = str;
		this.changed();
	}
	
	// FINER
	
	public String getComparisonTag()
	{
		return MiscUtil.getComparisonString(this.getTag());
	}
	
	public String getTag(String prefix)
	{
		return prefix + this.getTag();
	}
	
	public String getTag(RelationParticipator observer)
	{
		if (observer == null) return getTag();
		return this.getTag(this.getColorTo(observer).toString());
	}
	
	// -------------------------------------------- //
	// FIELD: description
	// -------------------------------------------- //
	
	// RAW
	
	public boolean hasDescription()
	{
		return this.description != null;
	}
	
	public String getDescription()
	{
		if (this.hasDescription()) return this.description;
		return Lang.FACTION_NODESCRIPTION;
	}
	
	public void setDescription(String description)
	{
		if (description != null)
		{
			description = description.trim();
			// This code should be kept for a while to clean out the previous default text that was actually stored in the database.
			if (description.length() == 0 || description.equalsIgnoreCase("Default faction description :("))
			{
				description = null;
			}
		}
		this.description = description;
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: open
	// -------------------------------------------- //
	
	public boolean isOpen()
	{
		Boolean ret = this.open;
		if (ret == null) ret = ConfServer.newFactionsDefaultOpen;
		return ret;
	}
	
	public void setOpen(Boolean open)
	{
		this.open = open;
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: relationWish
	// -------------------------------------------- //
	
	// RAW
	
	public Map<String, Rel> getRelationWishes()
	{
		Map<String, Rel> ret = new LinkedHashMap<String, Rel>();
		if (this.relationWish != null) ret.putAll(this.relationWish);
		return ret;
	}
	
	public void setRelationWishes(Map<String, Rel> relationWishes)
	{
		if (relationWishes == null || relationWishes.isEmpty())
		{
			this.relationWish = null;
		}
		else
		{
			this.relationWish = relationWishes;
		}
		this.changed();
	}
	
	// FINER
	
	public Rel getRelationWish(String factionId)
	{
		Rel ret = this.getRelationWishes().get(factionId);
		if (ret == null) ret = Rel.NEUTRAL;
		return ret;
	}
	
	public Rel getRelationWish(Faction faction)
	{
		return this.getRelationWish(faction.getId());
	}
	
	public void setRelationWish(String factionId, Rel rel)
	{
		Map<String, Rel> relationWishes = this.getRelationWishes();
		if (rel == null || rel == Rel.NEUTRAL)
		{
			relationWishes.remove(factionId);
		}
		else
		{
			relationWishes.put(factionId, rel);
		}
		this.setRelationWishes(relationWishes);
	}
	
	public void setRelationWish(Faction faction, Rel rel)
	{
		this.setRelationWish(faction.getId(), rel);
	}
	
	// TODO: What is this and where is it used?
	
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
		for (Faction faction : FactionColl.get().getAll())
		{
			Rel relation = faction.getRelationTo(this);
			if (onlyNonNeutral && relation == Rel.NEUTRAL) continue;
			ret.get(relation).add(faction.getTag(rp));
		}
		return ret;
	}
	
	// -------------------------------------------- //
	// FIELD: invitedPlayerIds
	// -------------------------------------------- //
	
	// RAW
	
	public TreeSet<String> getInvitedPlayerIds()
	{
		TreeSet<String> ret = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		if (this.invitedPlayerIds != null) ret.addAll(this.invitedPlayerIds);
		return ret;
	}
	
	public void setInvitedPlayerIds(Collection<String> invitedPlayerIds)
	{
		if (invitedPlayerIds == null || invitedPlayerIds.isEmpty())
		{
			this.invitedPlayerIds = null;
		}
		else
		{
			TreeSet<String> target = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for (String invitedPlayerId : invitedPlayerIds)
			{
				target.add(invitedPlayerId.toLowerCase());
			}
			this.invitedPlayerIds = target;
		}
		this.changed();
	}
	
	// FINER
	
	public boolean isInvited(String playerId)
	{
		return this.getInvitedPlayerIds().contains(playerId);
	}
	
	public boolean isInvited(FPlayer fplayer)
	{
		return this.isInvited(fplayer.getId());
	}
	
	public boolean invite(String playerId)
	{
		TreeSet<String> invitedPlayerIds = this.getInvitedPlayerIds();
		if (invitedPlayerIds.add(playerId.toLowerCase()))
		{
			this.setInvitedPlayerIds(invitedPlayerIds);
			return true;
		}
		return false;
	}
	
	public void invite(FPlayer fplayer)
	{
		this.invite(fplayer.getId());
	}
	
	public boolean deinvite(String playerId)
	{
		TreeSet<String> invitedPlayerIds = this.getInvitedPlayerIds();
		if (invitedPlayerIds.remove(playerId.toLowerCase()))
		{
			this.setInvitedPlayerIds(invitedPlayerIds);
			return true;
		}
		return false;
	}
	
	public void deinvite(FPlayer fplayer)
	{
		this.deinvite(fplayer.getId());
	}
	
	// -------------------------------------------- //
	// FIELD: powerBoost
	// -------------------------------------------- //
	
	// RAW
	
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
	
	// -------------------------------------------- //
	// FIELD: flagOverrides
	// -------------------------------------------- //
	
	// RAW
	
	public Map<FFlag, Boolean> getFlags()
	{
		Map<FFlag, Boolean> ret = new LinkedHashMap<FFlag, Boolean>();
		
		for (FFlag fflag : FFlag.values())
		{
			ret.put(fflag, fflag.getDefault());
		}
		
		if (this.flagOverrides != null)
		{
			for (Entry<FFlag, Boolean> entry : this.flagOverrides.entrySet())
			{
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		
		return ret;
	}
	
	public void setFlags(Map<FFlag, Boolean> flags)
	{
		Map<FFlag, Boolean> target = new LinkedHashMap<FFlag, Boolean>();
		
		if (flags != null)
		{
			target.putAll(flags);
		}
		
		Iterator<Entry<FFlag, Boolean>> iter = target.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<FFlag, Boolean> entry = iter.next();
			if (entry.getKey().getDefault() == entry.getValue())
			{
				iter.remove();
			}
		}
		
		if (target == null || target.isEmpty())
		{
			this.flagOverrides = null;
		}
		else
		{
			this.flagOverrides = target;
		}
		this.changed();
	}
	
	// FINER
	
	public boolean getFlag(FFlag flag)
	{
		return this.getFlags().get(flag);
	}
	public void setFlag(FFlag flag, boolean value)
	{
		Map<FFlag, Boolean> flags = this.getFlags();
		flags.put(flag, value);
		this.setFlags(flags);
	}
	
	// -------------------------------------------- //
	// RELATION AND COLORS
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
	
	
	
	// TODO: Implement a has enough feature.
	// -------------------------------------------- //
	// POWER
	// -------------------------------------------- //
	
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
		for (FPlayer fplayer : this.getFPlayers())
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
	
	public int getLandCount()
	{
		return BoardColl.get().getCount(this);
	}
	public int getLandCountInWorld(String worldName)
	{
		return BoardColl.get().get(worldName).getCount(this);
	}
	
	public boolean hasLandInflation()
	{
		return this.getLandCount() > this.getPowerRounded();
	}
	
	// -------------------------------------------- //
	// FPLAYERS
	// -------------------------------------------- //

	public List<FPlayer> getFPlayers()
	{
		List<FPlayer> ret = new ArrayList<FPlayer>();
		for (FPlayer fplayer : FPlayerColl.get().getAll())
		{
			if (fplayer.getFaction() != this) continue;
			ret.add(fplayer);
		}
		return ret;
	}
	
	public List<FPlayer> getFPlayersWhereOnline(boolean online)
	{
		List<FPlayer> ret = new ArrayList<FPlayer>();
		for (FPlayer fplayer : FPlayerColl.get().getAll())
		{
			if (fplayer.getFaction() != this) continue;
			if (fplayer.isOnline() != online) continue;
			ret.add(fplayer);
		}
		return ret;
	}
	
	public List<FPlayer> getFPlayersWhereRole(Rel role)
	{
		List<FPlayer> ret = new ArrayList<FPlayer>();
		for (FPlayer fplayer : FPlayerColl.get().getAll())
		{
			if (fplayer.getFaction() != this) continue;
			if (fplayer.getRole() != role) continue;
			ret.add(fplayer);
		}
		return ret;
	}
	
	public FPlayer getLeader()
	{
		for (FPlayer fplayer : FPlayerColl.get().getAll())
		{
			if (fplayer.getFaction() != this) continue;
			if (fplayer.getRole() != Rel.LEADER) continue;
			return fplayer;
		}
		return null;
	}
	
	public List<CommandSender> getOnlineCommandSenders()
	{
		List<CommandSender> ret = new ArrayList<CommandSender>();
		for (CommandSender player : SenderUtil.getOnlineSenders())
		{
			FPlayer fplayer = FPlayerColl.get().get(player);
			if (fplayer.getFaction() != this) continue;
			ret.add(player);
		}
		return ret;
	}
	
	public List<Player> getOnlinePlayers()
	{
		List<Player> ret = new ArrayList<Player>();
		for (Player player : Bukkit.getOnlinePlayers())
		{
			FPlayer fplayer = FPlayerColl.get().get(player);
			if (fplayer.getFaction() != this) continue;
			ret.add(player);
		}
		return ret;
	}

	// used when current leader is about to be removed from the faction; promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader()
	{
		if ( ! this.isNormal()) return;
		if (this.getFlag(FFlag.PERMANENT) && ConfServer.permanentFactionsDisableLeaderPromotion) return;

		FPlayer oldLeader = this.getLeader();

		// get list of officers, or list of normal members if there are no officers
		List<FPlayer> replacements = this.getFPlayersWhereRole(Rel.OFFICER);
		if (replacements == null || replacements.isEmpty())
		{
			replacements = this.getFPlayersWhereRole(Rel.MEMBER);
		}

		if (replacements == null || replacements.isEmpty())
		{	// faction leader is the only member; one-man faction
			if (this.getFlag(FFlag.PERMANENT))
			{
				if (oldLeader != null)
				{
					oldLeader.setRole(Rel.MEMBER);
				}
				return;
			}

			// no members left and faction isn't permanent, so disband it
			if (ConfServer.logFactionDisband)
			{
				Factions.get().log("The faction "+this.getTag()+" ("+this.getId()+") has been disbanded since it has no members left.");
			}

			for (FPlayer fplayer : FPlayerColl.get().getAllOnline())
			{
				fplayer.msg("The faction %s<i> was disbanded.", this.getTag(fplayer));
			}

			this.detach();
		}
		else
		{	// promote new faction leader
			if (oldLeader != null)
			{
				oldLeader.setRole(Rel.MEMBER);
			}
				
			replacements.get(0).setRole(Rel.LEADER);
			this.msg("<i>Faction leader <h>%s<i> has been removed. %s<i> has been promoted as the new faction leader.", oldLeader == null ? "" : oldLeader.getName(), replacements.get(0).getName());
			Factions.get().log("Faction "+this.getTag()+" ("+this.getId()+") leader was removed. Replacement leader: "+replacements.get(0).getName());
		}
	}

	// -------------------------------------------- //
	// MESSAGES
	// -------------------------------------------- //
	// These methods are simply proxied in from the SenderEntity class using a for loop.
	
	// CONVENIENCE SEND MESSAGE
	
	public boolean sendMessage(String message)
	{
		for (FPlayer fplayer : this.getFPlayers())
		{
			fplayer.sendMessage(message);
		}
		return true;
	}
	
	public boolean sendMessage(String... messages)
	{
		for (FPlayer fplayer : this.getFPlayers())
		{
			fplayer.sendMessage(messages);
		}
		return true;
	}
	
	public boolean sendMessage(Collection<String> messages)
	{
		for (FPlayer fplayer : this.getFPlayers())
		{
			fplayer.sendMessage(messages);
		}
		return true;
	}
	
	// CONVENIENCE MSG
	
	public boolean msg(String msg)
	{
		for (FPlayer fplayer : this.getFPlayers())
		{
			fplayer.msg(msg);
		}
		return true;
	}
	
	public boolean msg(String msg, Object... args)
	{
		for (FPlayer fplayer : this.getFPlayers())
		{
			fplayer.msg(msg, args);
		}
		return true;
	}
	
	public boolean msg(Collection<String> msgs)
	{
		for (FPlayer fplayer : this.getFPlayers())
		{
			fplayer.msg(msgs);
		}
		return true;
	}
	
}
