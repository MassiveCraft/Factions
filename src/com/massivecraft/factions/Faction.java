package com.massivecraft.factions;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.util.*;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.store.Entity;
import com.massivecraft.mcore.util.MUtil;
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
		this.home = that.home;
		this.setPowerBoost(that.powerBoost);
		this.cape = that.cape;
		this.open = that.open;
		this.setInvitedPlayerIds(that.invitedPlayerIds);
		this.setRelationWishes(that.relationWish);
		this.setFlags(that.flagOverrides);
		this.setPerms(that.permOverrides);
		
		return this;
	}
	
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	// In this section of the source code we place the field declarations only.
	// Each field has it's own section further down since even the getter and setter logic takes up quite some place.
	
	// TODO: The faction "tag" could/should also have been called "name".
	// The actual faction id looks something like "54947df8-0e9e-4471-a2f9-9af509fb5889" and that is not too easy to remember for humans.
	// Thus we make use of a name. Since the id is used in all foreign key situations changing the name is fine.
	private String tag = null;
	
	// Factions can optionally set a description for themselves.
	// This description can for example be seen in territorial alerts.
	private String description = null;
	
	// Factions can optionally set a home location.
	// If they do their members can teleport there using /f home
	private PS home = null;
	
	// Factions usually do not have a powerboost. It defaults to 0.
	// The powerBoost is a custom increase/decrease to default and maximum power.
	private Double powerBoost = null;
	
	// The cape field is used by the Spout integration features.
	// It's the URL to the faction cape. 
	private String cape = null;
	
	// Can anyone join the Faction?
	// If the faction is open they can.
	// If the faction is closed an invite is required.
	private Boolean open = null;
	
	// This is the ids of the invited players.
	// They are actually "senderIds" since you can invite "@console" to your faction.
	@SerializedName("invites")
	private Set<String> invitedPlayerIds = null;
	
	// The keys in this map are factionIds.
	private Map<String, Rel> relationWish = null;
	
	// The flag overrides are modifications to the default values.
	private Map<FFlag, Boolean> flagOverrides = null;

	// The perm overrides are modifications to the default values.
	private Map<FPerm, Set<Rel>> permOverrides = null;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public Faction()
	{
		
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
	// FIELD: home
	// -------------------------------------------- //
	
	public PS getHome()
	{
		this.verifyHomeIsValid();
		return this.home;
	}
	
	public void verifyHomeIsValid()
	{
		if (this.isValidHome(this.home)) return;
		this.home = null;
		msg("<b>Your faction home has been un-set since it is no longer in your territory.");
	}
	
	public boolean isValidHome(PS ps)
	{
		if (ps == null) return true;
		if (!ConfServer.homesMustBeInClaimedTerritory) return true;
		if (BoardColl.get().getFactionAt(ps) == this) return true;
		return false;
	}
	
	public boolean hasHome()
	{
		return this.getHome() != null;
	}
	
	public void setHome(PS home)
	{
		this.home = home;
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
	// FIELD: cape
	// -------------------------------------------- //
	
	public String getCape()
	{
		return cape;
	}
	
	public void setCape(String cape)
	{
		this.cape = cape;
		SpoutFeatures.updateCape(this, null);
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
	// FIELD: permOverrides
	// -------------------------------------------- //
	
	// RAW
	
	public Map<FPerm, Set<Rel>> getPerms()
	{
		Map<FPerm, Set<Rel>> ret = new LinkedHashMap<FPerm, Set<Rel>>();
		
		for (FPerm fperm : FPerm.values())
		{
			ret.put(fperm, fperm.getDefault());
		}
		
		if (this.permOverrides != null)
		{
			for (Entry<FPerm, Set<Rel>> entry : this.permOverrides.entrySet())
			{
				ret.put(entry.getKey(), new LinkedHashSet<Rel>(entry.getValue()));
			}
		}
		
		return ret;
	}
	
	public void setPerms(Map<FPerm, Set<Rel>> perms)
	{
		Map<FPerm, Set<Rel>> target = new LinkedHashMap<FPerm, Set<Rel>>();
		
		if (perms != null)
		{
			for (Entry<FPerm, Set<Rel>> entry : perms.entrySet())
			{
				target.put(entry.getKey(), new LinkedHashSet<Rel>(entry.getValue()));
			}
		}
		
		Iterator<Entry<FPerm, Set<Rel>>> iter = target.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<FPerm, Set<Rel>> entry = iter.next();
			if (entry.getKey().getDefault().equals(entry.getValue()))
			{
				iter.remove();
			}
		}
		
		if (target == null || target.isEmpty())
		{
			this.permOverrides = null;
		}
		else
		{
			this.permOverrides = target;
		}
		this.changed();
	}
	
	// FINER
	
	public Set<Rel> getPermittedRelations(FPerm perm)
	{
		return this.getPerms().get(perm);
	}
	
	public void setPermittedRelations(FPerm perm, Set<Rel> rels)
	{
		Map<FPerm, Set<Rel>> perms = this.getPerms();
		perms.put(perm, rels);
		this.setPerms(perms);
	}
	
	public void setPermittedRelations(FPerm perm, Rel... rels)
	{
		Set<Rel> temp = new HashSet<Rel>();
		temp.addAll(Arrays.asList(rels));
		this.setPermittedRelations(perm, temp);
	}
	
	public void setRelationPermitted(FPerm perm, Rel rel, boolean permitted)
	{
		Map<FPerm, Set<Rel>> perms = this.getPerms();
		Set<Rel> rels = perms.get(perm);

		if (permitted)
		{
			rels.add(rel);
		}
		else
		{
			rels.remove(rel);
		}
		
		this.setPerms(perms);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: RelationParticipator
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
	// POWER
	// -------------------------------------------- //
	// TODO: Implement a has enough feature.
	
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
		return ret + this.getPowerBoost();
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
		return ret + this.getPowerBoost();
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
	// FOREIGN KEYS: FPLAYERS
	// -------------------------------------------- //

	// TODO: With this approach null must be used as default always.
	// TODO: Take a moment and reflect upon the consequenses eeeeeeh...
	// TODO: This one may be to slow after all :/ Thus I must maintain an index.
	
	protected transient List<FPlayer> fplayers = null;
	public void reindexFPlayers()
	{
		this.fplayers = new ArrayList<FPlayer>();
		
		String factionId = this.getId();
		if (factionId == null) return;
		
		for (FPlayer fplayer : FPlayerColl.get().getAll())
		{
			if (!MUtil.equals(factionId, fplayer.getFactionId())) continue;
			this.fplayers.add(fplayer);
		}
	}
	
	public List<FPlayer> getFPlayers()
	{
		return new ArrayList<FPlayer>(this.fplayers);
	}
	
	public List<FPlayer> getFPlayersWhereOnline(boolean online)
	{
		List<FPlayer> ret = this.getFPlayers();
		Iterator<FPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			FPlayer fplayer = iter.next();
			if (fplayer.isOnline() != online)
			{
				iter.remove();
			}
		}
		return ret;
	}
	
	public List<FPlayer> getFPlayersWhereRole(Rel role)
	{
		List<FPlayer> ret = this.getFPlayers();
		Iterator<FPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			FPlayer fplayer = iter.next();
			if (fplayer.getRole() != role)
			{
				iter.remove();
			}
		}
		return ret;
	}
	
	public FPlayer getLeader()
	{
		List<FPlayer> ret = this.getFPlayers();
		Iterator<FPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			FPlayer fplayer = iter.next();
			if (fplayer.getRole() == Rel.LEADER)
			{
				return fplayer;
			}
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
	// These methods are simply proxied in from the Mixin.
	
	// CONVENIENCE SEND MESSAGE
	
	public boolean sendMessage(String message)
	{
		return Mixin.message(new FactionEqualsPredictate(this), message);
	}
	
	public boolean sendMessage(String... messages)
	{
		return Mixin.message(new FactionEqualsPredictate(this), messages);
	}
	
	public boolean sendMessage(Collection<String> messages)
	{
		return Mixin.message(new FactionEqualsPredictate(this), messages);
	}
	
	// CONVENIENCE MSG
	
	public boolean msg(String msg)
	{
		return Mixin.msg(new FactionEqualsPredictate(this), msg);
	}
	
	public boolean msg(String msg, Object... args)
	{
		return Mixin.msg(new FactionEqualsPredictate(this), msg, args);
	}
	
	public boolean msg(Collection<String> msgs)
	{
		return Mixin.msg(new FactionEqualsPredictate(this), msgs);
	}
	
}
