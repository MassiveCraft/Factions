package com.massivecraft.factions.entity;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.EconomyParticipator;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FactionEqualsPredictate;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Lang;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.util.*;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.money.Money;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.store.Entity;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.util.SenderUtil;
import com.massivecraft.mcore.util.Txt;


public class Faction extends Entity<Faction> implements EconomyParticipator
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static Faction get(Object oid)
	{
		return FactionColls.get().get2(oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public Faction load(Faction that)
	{
		this.setName(that.name);
		this.setDescription(that.description);
		this.setCreatedAtMillis(that.createdAtMillis);
		this.setHome(that.home);
		this.setPowerBoost(that.powerBoost);
		this.setOpen(that.open);
		this.setInvitedPlayerIds(that.invitedPlayerIds);
		this.setRelationWishes(that.relationWishes);
		this.setFlags(that.flags);
		this.setPerms(that.perms);
		
		return this;
	}
	
	@Override
	public void preDetach(String id)
	{
		Money.set(this, 0);
		
		String universe = this.getUniverse();
		
		// Clean the board
		BoardColls.get().getForUniverse(universe).clean();
		
		// Clean the uplayers
		UPlayerColls.get().getForUniverse(universe).clean();
	}
	
	// -------------------------------------------- //
	// FIELDS: RAW
	// -------------------------------------------- //
	// In this section of the source code we place the field declarations only.
	// Each field has it's own section further down since just the getter and setter logic takes up quite some place.
	
	// The actual faction id looks something like "54947df8-0e9e-4471-a2f9-9af509fb5889" and that is not too easy to remember for humans.
	// Thus we make use of a name. Since the id is used in all foreign key situations changing the name is fine.
	// Null should never happen. The name must not be null.
	private String name = null;
	
	// Factions can optionally set a description for themselves.
	// This description can for example be seen in territorial alerts.
	// Null means the faction has no description.
	private String description = null;
	
	// We store the creation date for the faction.
	// It can be displayed on info pages etc.
	private long createdAtMillis = System.currentTimeMillis();
	
	// Factions can optionally set a home location.
	// If they do their members can teleport there using /f home
	// Null means the faction has no home.
	private PS home = null;
	
	// Factions usually do not have a powerboost. It defaults to 0.
	// The powerBoost is a custom increase/decrease to default and maximum power.
	// Null means the faction has powerBoost (0).
	private Double powerBoost = null;
	
	// Can anyone join the Faction?
	// If the faction is open they can.
	// If the faction is closed an invite is required.
	// Null means default for the universe.
	private Boolean open = null;
	
	// This is the ids of the invited players.
	// They are actually "senderIds" since you can invite "@console" to your faction.
	// Null means no one is invited
	private Set<String> invitedPlayerIds = null;
	
	// The keys in this map are factionIds.
	// Null means no special relation whishes.
	private Map<String, Rel> relationWishes = null;
	
	// The flag overrides are modifications to the default values.
	// Null means default for the universe.
	private Map<FFlag, Boolean> flags = null;

	// The perm overrides are modifications to the default values.
	// Null means default for the universe.
	private Map<FPerm, Set<Rel>> perms = null;
	
	// -------------------------------------------- //
	// FIELD: id
	// -------------------------------------------- //
	
	// FINER
	
	public boolean isNone()
	{
		return this.getId().equals(UConf.get(this).factionIdNone);
	}
	
	public boolean isNormal()
	{
		return ! this.isNone();
	}
	
	// -------------------------------------------- //
	// FIELD: name
	// -------------------------------------------- //
	
	// RAW
	
	public String getName()
	{
		String ret = this.name;
		
		UConf uconf = UConf.get(this);
		if (uconf != null && UConf.get(this).factionNameForceUpperCase)
		{
			ret = ret.toUpperCase();
		}
		
		return ret;
	}
	
	public void setName(String name)
	{
		// Clean input
		String target = name;
		
		// Detect Nochange
		if (MUtil.equals(this.name, target)) return;

		// Apply
		this.name = target;
		
		// Mark as changed
		this.changed();
	}
	
	// FINER
	
	public String getComparisonName()
	{
		return MiscUtil.getComparisonString(this.getName());
	}
	
	public String getName(String prefix)
	{
		return prefix + this.getName();
	}
	
	public String getName(RelationParticipator observer)
	{
		if (observer == null) return getName();
		return this.getName(this.getColorTo(observer).toString());
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
		// Clean input
		String target = description;
		if (target != null)
		{
			target = target.trim();
			// This code should be kept for a while to clean out the previous default text that was actually stored in the database.
			if (target.length() == 0 || target.equals("Default faction description :("))
			{
				target = null;
			}
		}
		
		// Detect Nochange
		if (MUtil.equals(this.description, target)) return;

		// Apply
		this.description = target;
		
		// Mark as changed
		this.changed();
	}
	
	// -------------------------------------------- //
	// FIELD: createdAtMillis
	// -------------------------------------------- //
	
	public long getCreatedAtMillis()
	{
		return this.createdAtMillis;
	}
	
	public void setCreatedAtMillis(long createdAtMillis)
	{
		// Clean input
		long target = createdAtMillis;
		
		// Detect Nochange
		if (MUtil.equals(this.createdAtMillis, createdAtMillis)) return;

		// Apply
		this.createdAtMillis = target;
		
		// Mark as changed
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
		if (!UConf.get(this).homesMustBeInClaimedTerritory) return true;
		if (BoardColls.get().getFactionAt(ps) == this) return true;
		return false;
	}
	
	public boolean hasHome()
	{
		return this.getHome() != null;
	}
	
	public void setHome(PS home)
	{
		// Clean input
		PS target = home;
		
		// Detect Nochange
		if (MUtil.equals(this.home, target)) return;
		
		// Apply
		this.home = target;
		
		// Mark as changed
		this.changed();
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
	
	// -------------------------------------------- //
	// FIELD: open
	// -------------------------------------------- //
	
	public boolean isDefaultOpen()
	{
		UConf uconf = UConf.get(this);
		if (uconf == null) return false;
		return uconf.defaultFactionOpen;
	}
	
	public boolean isOpen()
	{
		Boolean ret = this.open;
		if (ret == null) ret = this.isDefaultOpen();
		return ret;
	}
	
	public void setOpen(Boolean open)
	{
		// Clean input
		Boolean target = open;
		
		// Detect Nochange
		if (MUtil.equals(this.open, target)) return;
		
		// Apply
		this.open = target;
		
		// Mark as changed
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
		// Clean input
		TreeSet<String> target;
		if (invitedPlayerIds == null || invitedPlayerIds.isEmpty())
		{
			target = null;
		}
		else
		{
			target = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
			for (String invitedPlayerId : invitedPlayerIds)
			{
				target.add(invitedPlayerId.toLowerCase());
			}
		}
		
		// Detect Nochange
		if (MUtil.equals(this.invitedPlayerIds, target)) return;
		
		// Apply
		this.invitedPlayerIds = target;
		
		// Mark as changed
		this.changed();
	}
	
	// FINER
	
	public boolean isInvited(String playerId)
	{
		return this.getInvitedPlayerIds().contains(playerId);
	}
	
	public boolean isInvited(UPlayer uplayer)
	{
		return this.isInvited(uplayer.getId());
	}
	
	public boolean setInvited(String playerId, boolean invited)
	{
		TreeSet<String> invitedPlayerIds = this.getInvitedPlayerIds();
		boolean ret;
		if (invited)
		{
			ret = invitedPlayerIds.add(playerId.toLowerCase());
		}
		else
		{
			ret = invitedPlayerIds.remove(playerId.toLowerCase());
		}
		this.setInvitedPlayerIds(invitedPlayerIds);
		return ret;
		
	}
	
	public void setInvited(UPlayer uplayer, boolean invited)
	{
		this.setInvited(uplayer.getId(), invited);
	}
	
	// -------------------------------------------- //
	// FIELD: relationWish
	// -------------------------------------------- //
	
	// RAW
	
	public Map<String, Rel> getRelationWishes()
	{
		Map<String, Rel> ret = new LinkedHashMap<String, Rel>();
		if (this.relationWishes != null) ret.putAll(this.relationWishes);
		return ret;
	}
	
	public void setRelationWishes(Map<String, Rel> relationWishes)
	{
		// Clean input
		Map<String, Rel> target;
		if (relationWishes == null || relationWishes.isEmpty())
		{
			target = null;
		}
		else
		{
			target = new LinkedHashMap<String, Rel>(relationWishes);
		}
		
		// Detect Nochange
		if (MUtil.equals(this.relationWishes, target)) return;
		
		// Apply
		this.relationWishes = target;
		
		// Mark as changed
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
	
	public Map<Rel, List<String>> getFactionNamesPerRelation(RelationParticipator rp)
	{
		return getFactionNamesPerRelation(rp, false);
	}

	// onlyNonNeutral option provides substantial performance boost on large servers for listing only non-neutral factions
	public Map<Rel, List<String>> getFactionNamesPerRelation(RelationParticipator rp, boolean onlyNonNeutral)
	{
		Map<Rel, List<String>> ret = new HashMap<Rel, List<String>>();
		for (Rel rel : Rel.values())
		{
			ret.put(rel, new ArrayList<String>());
		}
		for (Faction faction : FactionColls.get().get(this).getAll())
		{
			Rel relation = faction.getRelationTo(this);
			if (onlyNonNeutral && relation == Rel.NEUTRAL) continue;
			ret.get(relation).add(faction.getName(rp));
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
			ret.put(fflag, fflag.getDefault(this));
		}
		
		if (this.flags != null)
		{
			for (Entry<FFlag, Boolean> entry : this.flags.entrySet())
			{
				ret.put(entry.getKey(), entry.getValue());
			}
		}
		
		return ret;
	}
	
	public void setFlags(Map<FFlag, Boolean> flags)
	{
		// Clean input
		Map<FFlag, Boolean> target;
		if (flags == null)
		{
			target = null;
		}
		else
		{
			target = new LinkedHashMap<FFlag, Boolean>(flags);
			
			if (this.attached() && Factions.get().isDatabaseInitialized())
			{
				Iterator<Entry<FFlag, Boolean>> iter = target.entrySet().iterator();
				while (iter.hasNext())
				{
					Entry<FFlag, Boolean> entry = iter.next();
					if (entry.getKey().getDefault(this) == entry.getValue())
					{
						iter.remove();
					}
				}
				
				if (target.isEmpty()) target = null;
			}
		}

		// Detect Nochange
		if (MUtil.equals(this.flags, target)) return;
		
		// Apply
		this.flags = target;
		
		// Mark as changed
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
			ret.put(fperm, fperm.getDefault(this));
		}
		
		if (this.perms != null)
		{
			for (Entry<FPerm, Set<Rel>> entry : this.perms.entrySet())
			{
				ret.put(entry.getKey(), new LinkedHashSet<Rel>(entry.getValue()));
			}
		}
		
		return ret;
	}
	
	public void setPerms(Map<FPerm, Set<Rel>> perms)
	{
		// Clean input
		Map<FPerm, Set<Rel>> target;
		if (perms == null)
		{
			target = null;
		}
		else
		{
			target = new LinkedHashMap<FPerm, Set<Rel>>();
			for (Entry<FPerm, Set<Rel>> entry : perms.entrySet())
			{
				target.put(entry.getKey(), new LinkedHashSet<Rel>(entry.getValue()));
			}
			
			if (this.attached() && Factions.get().isDatabaseInitialized())
			{
				Iterator<Entry<FPerm, Set<Rel>>> iter = target.entrySet().iterator();
				while (iter.hasNext())
				{
					Entry<FPerm, Set<Rel>> entry = iter.next();
					
					FPerm key = entry.getKey();					
					if (key == null)
					{
						// TODO: I have no idea why this key is null at times... Why?
						System.out.println("key was null :/");
						iter.remove();
						continue;
					}
					
					Set<Rel> keyDefault = key.getDefault(this);
					Set<Rel> value = entry.getValue();
					
					if (keyDefault.equals(value))
					{
						iter.remove();
					}
				}
				
				if (target.isEmpty()) target = null;
			}
		}
		
		// Detect Nochange
		if (MUtil.equals(this.perms, target)) return;
		
		// Apply
		this.perms = target;
		
		// Mark as changed
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
		
		//System.out.println("setRelationPermitted before:");
		//System.out.println(Factions.get().gson.toJson(perms, new TypeToken<Map<FPerm, Set<Rel>>>(){}.getType()));
		
		Set<Rel> rels = perms.get(perm);

		if (permitted)
		{
			rels.add(rel);
		}
		else
		{
			rels.remove(rel);
		}
		
		//System.out.println("setRelationPermitted after:");
		//System.out.println(Factions.get().gson.toJson(perms, new TypeToken<Map<FPerm, Set<Rel>>>(){}.getType()));
		
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
		if (this.getFlag(FFlag.INFPOWER)) return 999999;
		
		double ret = 0;
		for (UPlayer uplayer : this.getUPlayers())
		{
			ret += uplayer.getPower();
		}
		
		double factionPowerMax = UConf.get(this).factionPowerMax;
		if (factionPowerMax > 0 && ret > factionPowerMax)
		{
			ret = factionPowerMax;
		}
		
		ret += this.getPowerBoost();
		
		return ret;
	}
	
	public double getPowerMax()
	{
		if (this.getFlag(FFlag.INFPOWER)) return 999999;
	
		double ret = 0;
		for (UPlayer uplayer : this.getUPlayers())
		{
			ret += uplayer.getPowerMax();
		}
		
		double factionPowerMax = UConf.get(this).factionPowerMax;
		if (factionPowerMax > 0 && ret > factionPowerMax)
		{
			ret = factionPowerMax;
		}
		
		ret += this.getPowerBoost();
		
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
	
	public int getLandCount()
	{
		return BoardColls.get().get(this).getCount(this);
	}
	public int getLandCountInWorld(String worldName)
	{
		return Board.get(worldName).getCount(this);
	}
	
	public boolean hasLandInflation()
	{
		return this.getLandCount() > this.getPowerRounded();
	}
	
	// -------------------------------------------- //
	// FOREIGN KEY: UPLAYER
	// -------------------------------------------- //
	
	protected transient List<UPlayer> uplayers = new ArrayList<UPlayer>();
	public void reindexUPlayers()
	{
		this.uplayers.clear();
		
		String factionId = this.getId();
		if (factionId == null) return;
		
		for (UPlayer uplayer : UPlayerColls.get().get(this).getAll())
		{
			if (!MUtil.equals(factionId, uplayer.getFactionId())) continue;
			this.uplayers.add(uplayer);
		}
	}
	
	// TODO: Even though this check method removeds the invalid entries it's not a true solution.
	// TODO: Find the bug causing non-attached UPlayers to be present in the index.
	private void checkUPlayerIndex()
	{
		Iterator<UPlayer> iter = this.uplayers.iterator();
		while (iter.hasNext())
		{
			UPlayer uplayer = iter.next();
			if (!uplayer.attached())
			{
				String msg = Txt.parse("<rose>WARN: <i>Faction <h>%s <i>aka <h>%s <i>had unattached uplayer in index:", this.getName(), this.getId());
				Factions.get().log(msg);
				Factions.get().log(Factions.get().gson.toJson(uplayer));
				iter.remove();
			}
		}
	}
	
	public List<UPlayer> getUPlayers()
	{
		this.checkUPlayerIndex();
		return new ArrayList<UPlayer>(this.uplayers);
	}
	
	public List<UPlayer> getUPlayersWhereOnline(boolean online)
	{
		List<UPlayer> ret = this.getUPlayers();
		Iterator<UPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			UPlayer uplayer = iter.next();
			if (uplayer.isOnline() != online)
			{
				iter.remove();
			}
		}
		return ret;
	}	
	
	public List<UPlayer> getUPlayersWhereRole(Rel role)
	{
		List<UPlayer> ret = this.getUPlayers();
		Iterator<UPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			UPlayer uplayer = iter.next();
			if (uplayer.getRole() != role)
			{
				iter.remove();
			}
		}
		return ret;
	}
	
	public UPlayer getLeader()
	{
		List<UPlayer> ret = this.getUPlayers();
		Iterator<UPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			UPlayer uplayer = iter.next();
			if (uplayer.getRole() == Rel.LEADER)
			{
				return uplayer;
			}
		}
		return null;
	}
	
	public List<CommandSender> getOnlineCommandSenders()
	{
		List<CommandSender> ret = new ArrayList<CommandSender>();
		for (CommandSender player : SenderUtil.getOnlineSenders())
		{
			UPlayer uplayer = UPlayer.get(player);
			if (!MUtil.equals(uplayer.getUniverse(), this.getUniverse())) continue;
			if (uplayer.getFaction() != this) continue;
			ret.add(player);
		}
		return ret;
	}
	
	public List<Player> getOnlinePlayers()
	{
		List<Player> ret = new ArrayList<Player>();
		for (Player player : Bukkit.getOnlinePlayers())
		{
			UPlayer uplayer = UPlayer.get(player);
			if (!MUtil.equals(uplayer.getUniverse(), this.getUniverse())) continue;
			if (uplayer.getFaction() != this) continue;
			ret.add(player);
		}
		return ret;
	}

	// used when current leader is about to be removed from the faction; promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader()
	{
		if ( ! this.isNormal()) return;
		if (this.getFlag(FFlag.PERMANENT) && UConf.get(this).permanentFactionsDisableLeaderPromotion) return;

		UPlayer oldLeader = this.getLeader();

		// get list of officers, or list of normal members if there are no officers
		List<UPlayer> replacements = this.getUPlayersWhereRole(Rel.OFFICER);
		if (replacements == null || replacements.isEmpty())
		{
			replacements = this.getUPlayersWhereRole(Rel.MEMBER);
		}

		if (replacements == null || replacements.isEmpty())
		{
			// faction leader is the only member; one-man faction
			if (this.getFlag(FFlag.PERMANENT))
			{
				if (oldLeader != null)
				{
					// TODO: Where is the logic in this? Why MEMBER? Why not LEADER again? And why not OFFICER or RECRUIT?
					oldLeader.setRole(Rel.MEMBER);
				}
				return;
			}

			// no members left and faction isn't permanent, so disband it
			if (MConf.get().logFactionDisband)
			{
				Factions.get().log("The faction "+this.getName()+" ("+this.getId()+") has been disbanded since it has no members left.");
			}

			for (UPlayer uplayer : UPlayerColls.get().get(this).getAllOnline())
			{
				uplayer.msg("The faction %s<i> was disbanded.", this.getName(uplayer));
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
			Factions.get().log("Faction "+this.getName()+" ("+this.getId()+") leader was removed. Replacement leader: "+replacements.get(0).getName());
		}
	}
	
	// -------------------------------------------- //
	// FACTION ONLINE STATE
	// -------------------------------------------- //

	public boolean isAllUPlayersOffline()
	{
		return this.getUPlayersWhereOnline(true).size() == 0;
	}
	
	public boolean isAnyUPlayersOnline()
	{
		return !this.isAllUPlayersOffline();
	}
	
	public boolean isFactionConsideredOffline()
	{
		return this.isAllUPlayersOffline();
	}
	
	public boolean isFactionConsideredOnline()
	{
		return !this.isFactionConsideredOffline();
	}
	
	public boolean isExplosionsAllowed()
	{
		boolean explosions = this.getFlag(FFlag.EXPLOSIONS);
		boolean offlineexplosions = this.getFlag(FFlag.OFFLINE_EXPLOSIONS);
		boolean online = this.isFactionConsideredOnline();
		
		return (online && explosions) || (!online && offlineexplosions);
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
