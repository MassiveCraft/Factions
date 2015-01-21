package com.massivecraft.factions.entity;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.EconomyParticipator;
import com.massivecraft.factions.FactionEqualsPredictate;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Lang;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.util.*;
import com.massivecraft.massivecore.CaseInsensitiveComparator;
import com.massivecraft.massivecore.collections.MassiveMapDef;
import com.massivecraft.massivecore.collections.MassiveTreeSetDef;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

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
		this.setName(that.name);
		this.setDescription(that.description);
		this.setMotd(that.motd);
		this.setCreatedAtMillis(that.createdAtMillis);
		this.setHome(that.home);
		this.setPowerBoost(that.powerBoost);
		this.setInvitedPlayerIds(that.invitedPlayerIds);
		this.setRelationWishes(that.relationWishes);
		this.setFlagIds(that.flags);
		this.setPermIds(that.perms);
		
		return this;
	}
	
	@Override
	public void preDetach(String id)
	{
		// The database must be fully inited.
		// We may move factions around during upgrades.
		if (!Factions.get().isDatabaseInitialized()) return;
		
		// Zero balance
		Money.set(this, null, 0);
		
		// Clean the board
		BoardColl.get().clean();
		
		// Clean the mplayers
		MPlayerColl.get().clean();
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
	
	// Factions can optionally set a message of the day.
	// This message will be shown when logging on to the server.
	// Null means the faction has no motd
	private String motd = null;
	
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
	// Null means default.
	// private Boolean open = null;
	
	// This is the ids of the invited players.
	// They are actually "senderIds" since you can invite "@console" to your faction.
	// Null means no one is invited
	private MassiveTreeSetDef<String, CaseInsensitiveComparator> invitedPlayerIds = new MassiveTreeSetDef<String, CaseInsensitiveComparator>(CaseInsensitiveComparator.get());
	
	// The keys in this map are factionIds.
	// Null means no special relation whishes.
	private MassiveMapDef<String, Rel> relationWishes = new MassiveMapDef<String, Rel>();
	
	// The flag overrides are modifications to the default values.
	// Null means default.
	private MassiveMapDef<String, Boolean> flags = new MassiveMapDef<String, Boolean>();

	// The perm overrides are modifications to the default values.
	// Null means default.
	private MassiveMapDef<String, Set<Rel>> perms = new MassiveMapDef<String, Set<Rel>>();
	
	// -------------------------------------------- //
	// FIELD: id
	// -------------------------------------------- //
	
	// FINER
	
	public boolean isNone()
	{
		return this.getId().equals(Factions.ID_NONE);
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
		
		if (MConf.get().factionNameForceUpperCase)
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
	// FIELD: motd
	// -------------------------------------------- //
	
	// RAW
	
	public boolean hasMotd()
	{
		return this.motd != null;
	}
	
	public String getMotd()
	{
		if (this.hasMotd()) return Txt.parse(this.motd);
		return Lang.FACTION_NOMOTD;
	}
	
	public void setMotd(String description)
	{
		// Clean input
		String target = description;
		if (target != null)
		{
			target = target.trim();
			if (target.length() == 0)
			{
				target = null;
			}
		}
		
		// Detect Nochange
		if (MUtil.equals(this.motd, target)) return;

		// Apply
		this.motd = target;
		
		// Mark as changed
		this.changed();
	}
	
	// FINER
	
	public List<String> getMotdMessages()
	{
		final String title = Txt.titleize(this.getName() + " - Message of the Day");
		final String motd = "<i>" + this.getMotd();
		final List<String> messages = Txt.parse(MUtil.list(title, motd, ""));
		return messages;
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
		if (!MConf.get().homesMustBeInClaimedTerritory) return true;
		if (BoardColl.get().getFactionAt(ps) == this) return true;
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
	
	// Nowadays this is a flag!
	
	@Deprecated
	public boolean isDefaultOpen()
	{
		return MFlag.getFlagOpen().isStandard();
	}
	
	@Deprecated
	public boolean isOpen()
	{
		return this.getFlag(MFlag.getFlagOpen());
	}
	
	@Deprecated
	public void setOpen(Boolean open)
	{
		MFlag flag = MFlag.getFlagOpen();
		if (open == null) open = flag.isStandard();
		this.setFlag(flag, open);
	}
	
	// -------------------------------------------- //
	// FIELD: invitedPlayerIds
	// -------------------------------------------- //
	
	// RAW
	
	public TreeSet<String> getInvitedPlayerIds()
	{
		return this.invitedPlayerIds;
	}
	
	public void setInvitedPlayerIds(Collection<String> invitedPlayerIds)
	{
		// Clean input
		MassiveTreeSetDef<String, CaseInsensitiveComparator> target = new MassiveTreeSetDef<String, CaseInsensitiveComparator>(CaseInsensitiveComparator.get());
		if (invitedPlayerIds != null)
		{
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
	
	public boolean isInvited(MPlayer mplayer)
	{
		return this.isInvited(mplayer.getId());
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
	
	public void setInvited(MPlayer mplayer, boolean invited)
	{
		this.setInvited(mplayer.getId(), invited);
	}
	
	public List<MPlayer> getInvitedMPlayers()
	{
		List<MPlayer> mplayers = new ArrayList<MPlayer>();
		
		for (String id : this.getInvitedPlayerIds())
		{	
			MPlayer mplayer = MPlayer.get(id);
			mplayers.add(mplayer);
		}
		return mplayers;
	}
	
	// -------------------------------------------- //
	// FIELD: relationWish
	// -------------------------------------------- //
	
	// RAW
	
	public Map<String, Rel> getRelationWishes()
	{
		return this.relationWishes;
	}
	
	public void setRelationWishes(Map<String, Rel> relationWishes)
	{
		// Clean input
		MassiveMapDef<String, Rel> target = new MassiveMapDef<String, Rel>(relationWishes);
		
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
	
	public Map<Rel, List<String>> getRelationNames(RelationParticipator rp, Set<Rel> rels, boolean skipPeaceful)
	{
		// Create Ret
		Map<Rel, List<String>> ret = new LinkedHashMap<Rel, List<String>>();
		for (Rel rel : rels)
		{
			ret.put(rel, new ArrayList<String>());
		}
		
		for (Faction faction : FactionColl.get().getAll())
		{
			if (skipPeaceful && faction.getFlag(MFlag.getFlagPeaceful())) continue;
			
			Rel rel = faction.getRelationTo(this);
			
			List<String> names = ret.get(rel);
			if (names == null) continue;
			
			String name = faction.getName(rp);
			names.add(name);
		}
		
		// Return Ret
		return ret;
	}
	
	// -------------------------------------------- //
	// FIELD: flagOverrides
	// -------------------------------------------- //
	
	// RAW
	
	public Map<MFlag, Boolean> getFlags()
	{
		// We start with default values ...
		Map<MFlag, Boolean> ret = new LinkedHashMap<MFlag, Boolean>();
		for (MFlag mflag : MFlag.getAll())
		{
			ret.put(mflag, mflag.isStandard());
		}
		
		// ... and if anything is explicitly set we use that info ...
		Iterator<Entry<String, Boolean>> iter = this.flags.entrySet().iterator();
		while (iter.hasNext())
		{
			// ... for each entry ...
			Entry<String, Boolean> entry = iter.next();
			
			// ... extract id and remove null values ...
			String id = entry.getKey();					
			if (id == null)
			{
				iter.remove();
				this.changed();
				continue;
			}
			
			// ... resolve object and skip unknowns ...
			MFlag mflag = MFlag.get(id);
			if (mflag == null) continue;
			
			ret.put(mflag, entry.getValue());
		}
		
		return ret;
	}
	
	public void setFlags(Map<MFlag, Boolean> flags)
	{
		Map<String, Boolean> flagIds = new LinkedHashMap<String, Boolean>();
		for (Entry<MFlag, Boolean> entry : flags.entrySet())
		{
			flagIds.put(entry.getKey().getId(), entry.getValue());
		}
		setFlagIds(flagIds);
	}
	
	public void setFlagIds(Map<String, Boolean> flagIds)
	{
		// Clean input
		MassiveMapDef<String, Boolean> target = new MassiveMapDef<String, Boolean>();
		for (Entry<String, Boolean> entry : flagIds.entrySet())
		{
			String key = entry.getKey();
			if (key == null) continue;
			key = key.toLowerCase(); // Lowercased Keys Version 2.6.0 --> 2.7.0
			
			Boolean value = entry.getValue();
			if (value == null) continue;
			
			target.put(key, value);
		}

		// Detect Nochange
		if (MUtil.equals(this.flags, target)) return;
		
		// Apply
		this.flags = new MassiveMapDef<String, Boolean>(target);
		
		// Mark as changed
		this.changed();
	}
	
	// FINER
	
	public boolean getFlag(String flagId)
	{
		if (flagId == null) throw new NullPointerException("flagId");
		
		Boolean ret = this.flags.get(flagId);
		if (ret != null) return ret;
		
		MFlag flag = MFlag.get(flagId);
		if (flag == null) throw new NullPointerException("flag");
		
		return flag.isStandard();
	}
	
	public boolean getFlag(MFlag flag)
	{
		if (flag == null) throw new NullPointerException("flag");
		
		String flagId = flag.getId();
		if (flagId == null) throw new NullPointerException("flagId");
		
		Boolean ret = this.flags.get(flagId);
		if (ret != null) return ret;
		
		return flag.isStandard();
	}
	
	public Boolean setFlag(String flagId, boolean value)
	{
		if (flagId == null) throw new NullPointerException("flagId");
		
		Boolean ret = this.flags.put(flagId, value);
		if (ret == null || ret != value) this.changed();
		return ret;
	}
	
	public Boolean setFlag(MFlag flag, boolean value)
	{
		if (flag == null) throw new NullPointerException("flag");
		
		String flagId = flag.getId();
		if (flagId == null) throw new NullPointerException("flagId");
		
		Boolean ret = this.flags.put(flagId, value);
		if (ret == null || ret != value) this.changed();
		return ret;
	}
	
	// -------------------------------------------- //
	// FIELD: permOverrides
	// -------------------------------------------- //
	
	// RAW
	
	public Map<MPerm, Set<Rel>> getPerms()
	{
		// We start with default values ...
		Map<MPerm, Set<Rel>> ret = new LinkedHashMap<MPerm, Set<Rel>>();
		for (MPerm mperm : MPerm.getAll())
		{
			ret.put(mperm, new LinkedHashSet<Rel>(mperm.getStandard()));
		}
		
		// ... and if anything is explicitly set we use that info ...
		Iterator<Entry<String, Set<Rel>>> iter = this.perms.entrySet().iterator();
		while (iter.hasNext())
		{
			// ... for each entry ...
			Entry<String, Set<Rel>> entry = iter.next();
			
			// ... extract id and remove null values ...
			String id = entry.getKey();					
			if (id == null)
			{
				iter.remove();
				continue;
			}
			
			// ... resolve object and skip unknowns ...
			MPerm mperm = MPerm.get(id);
			if (mperm == null) continue;
			
			ret.put(mperm, new LinkedHashSet<Rel>(entry.getValue()));
		}
		
		return ret;
	}
	
	public void setPerms(Map<MPerm, Set<Rel>> perms)
	{
		Map<String, Set<Rel>> permIds = new LinkedHashMap<String, Set<Rel>>();
		for (Entry<MPerm, Set<Rel>> entry : perms.entrySet())
		{
			permIds.put(entry.getKey().getId(), entry.getValue());
		}
		setPermIds(permIds);
	}
	
	public void setPermIds(Map<String, Set<Rel>> perms)
	{
		// Clean input
		MassiveMapDef<String, Set<Rel>> target = new MassiveMapDef<String, Set<Rel>>();
		for (Entry<String, Set<Rel>> entry : perms.entrySet())
		{
			String key = entry.getKey();
			if (key == null) continue;
			key = key.toLowerCase(); // Lowercased Keys Version 2.6.0 --> 2.7.0
			
			Set<Rel> value = entry.getValue();
			if (value == null) continue;
			
			target.put(key, value);
		}
		
		// Detect Nochange
		if (MUtil.equals(this.perms, target)) return;
		
		// Apply
		this.perms = target;
		
		// Mark as changed
		this.changed();
	}
	
	// FINER
	
	public boolean isPermitted(String permId, Rel rel)
	{
		if (permId == null) throw new NullPointerException("permId");
		
		Set<Rel> rels = this.perms.get(permId);
		if (rels != null) return rels.contains(rel);
		
		MPerm perm = MPerm.get(permId);
		if (perm == null) throw new NullPointerException("perm");
		
		return perm.getStandard().contains(rel);
	}
	
	public boolean isPermitted(MPerm perm, Rel rel)
	{
		if (perm == null) throw new NullPointerException("perm");
		
		String permId = perm.getId();
		if (permId == null) throw new NullPointerException("permId");
		
		Set<Rel> rels = this.perms.get(permId);
		if (rels != null) return rels.contains(rel);
		
		return perm.getStandard().contains(rel);
	}
	
	// ---
	
	public Set<Rel> getPermitted(MPerm perm)
	{
		if (perm == null) throw new NullPointerException("perm");
		
		String permId = perm.getId();
		if (permId == null) throw new NullPointerException("permId");
		
		Set<Rel> rels = this.perms.get(permId);
		if (rels != null) return rels;
		
		return perm.getStandard();
	}
	
	public Set<Rel> getPermitted(String permId)
	{
		if (permId == null) throw new NullPointerException("permId");
		
		Set<Rel> rels = this.perms.get(permId);
		if (rels != null) return rels;
		
		MPerm perm = MPerm.get(permId);
		if (perm == null) throw new NullPointerException("perm");
		
		return perm.getStandard();
	}
	
	@Deprecated
	// Use getPermitted instead. It's much quicker although not immutable.
	public Set<Rel> getPermittedRelations(MPerm perm)
	{
		return this.getPerms().get(perm);
	}
	
	// ---
	// TODO: Fix these below. They are reworking the whole map.
	
	public void setPermittedRelations(MPerm perm, Set<Rel> rels)
	{
		Map<MPerm, Set<Rel>> perms = this.getPerms();
		perms.put(perm, rels);
		this.setPerms(perms);
	}
	
	public void setPermittedRelations(MPerm perm, Rel... rels)
	{
		Set<Rel> temp = new HashSet<Rel>();
		temp.addAll(Arrays.asList(rels));
		this.setPermittedRelations(perm, temp);
	}
	
	public void setRelationPermitted(MPerm perm, Rel rel, boolean permitted)
	{
		Map<MPerm, Set<Rel>> perms = this.getPerms();
		
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
		if (this.getFlag(MFlag.getFlagInfpower())) return 999999;
		
		double ret = 0;
		for (MPlayer mplayer : this.getMPlayers())
		{
			ret += mplayer.getPower();
		}
		
		double factionPowerMax = MConf.get().factionPowerMax;
		if (factionPowerMax > 0 && ret > factionPowerMax)
		{
			ret = factionPowerMax;
		}
		
		ret += this.getPowerBoost();
		
		return ret;
	}
	
	public double getPowerMax()
	{
		if (this.getFlag(MFlag.getFlagInfpower())) return 999999;
	
		double ret = 0;
		for (MPlayer mplayer : this.getMPlayers())
		{
			ret += mplayer.getPowerMax();
		}
		
		double factionPowerMax = MConf.get().factionPowerMax;
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
		return BoardColl.get().getCount(this);
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
	// FOREIGN KEY: MPLAYER
	// -------------------------------------------- //
	
	protected transient List<MPlayer> mplayers = new ArrayList<MPlayer>();
	public void reindexMPlayers()
	{
		this.mplayers.clear();
		
		String factionId = this.getId();
		if (factionId == null) return;
		
		for (MPlayer mplayer : MPlayerColl.get().getAll())
		{
			if (!MUtil.equals(factionId, mplayer.getFactionId())) continue;
			this.mplayers.add(mplayer);
		}
	}
	
	// TODO: Even though this check method removeds the invalid entries it's not a true solution.
	// TODO: Find the bug causing non-attached MPlayers to be present in the index.
	private void checkMPlayerIndex()
	{
		Iterator<MPlayer> iter = this.mplayers.iterator();
		while (iter.hasNext())
		{
			MPlayer mplayer = iter.next();
			if (!mplayer.attached())
			{
				String msg = Txt.parse("<rose>WARN: <i>Faction <h>%s <i>aka <h>%s <i>had unattached mplayer in index:", this.getName(), this.getId());
				Factions.get().log(msg);
				Factions.get().log(Factions.get().gson.toJson(mplayer));
				iter.remove();
			}
		}
	}
	
	public List<MPlayer> getMPlayers()
	{
		this.checkMPlayerIndex();
		return new ArrayList<MPlayer>(this.mplayers);
	}
	
	public List<MPlayer> getMPlayersWhereOnline(boolean online)
	{
		List<MPlayer> ret = this.getMPlayers();
		Iterator<MPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			MPlayer mplayer = iter.next();
			if (mplayer.isOnline() != online)
			{
				iter.remove();
			}
		}
		return ret;
	}	
	
	public List<MPlayer> getMPlayersWhereRole(Rel role)
	{
		List<MPlayer> ret = this.getMPlayers();
		Iterator<MPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			MPlayer mplayer = iter.next();
			if (mplayer.getRole() != role)
			{
				iter.remove();
			}
		}
		return ret;
	}
	
	public MPlayer getLeader()
	{
		List<MPlayer> ret = this.getMPlayers();
		Iterator<MPlayer> iter = ret.iterator();
		while (iter.hasNext())
		{
			MPlayer mplayer = iter.next();
			if (mplayer.getRole() == Rel.LEADER)
			{
				return mplayer;
			}
		}
		return null;
	}
	
	public List<CommandSender> getOnlineCommandSenders()
	{
		List<CommandSender> ret = new ArrayList<CommandSender>();
		for (CommandSender player : IdUtil.getOnlineSenders())
		{
			MPlayer mplayer = MPlayer.get(player);
			if (mplayer.getFaction() != this) continue;
			ret.add(player);
		}
		return ret;
	}
	
	public List<Player> getOnlinePlayers()
	{
		List<Player> ret = new ArrayList<Player>();
		for (Player player : MUtil.getOnlinePlayers())
		{
			MPlayer mplayer = MPlayer.get(player);
			if (mplayer.getFaction() != this) continue;
			ret.add(player);
		}
		return ret;
	}

	// used when current leader is about to be removed from the faction; promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader()
	{
		if ( ! this.isNormal()) return;
		if (this.getFlag(MFlag.getFlagPermanent()) && MConf.get().permanentFactionsDisableLeaderPromotion) return;

		MPlayer oldLeader = this.getLeader();

		// get list of officers, or list of normal members if there are no officers
		List<MPlayer> replacements = this.getMPlayersWhereRole(Rel.OFFICER);
		if (replacements == null || replacements.isEmpty())
		{
			replacements = this.getMPlayersWhereRole(Rel.MEMBER);
		}

		if (replacements == null || replacements.isEmpty())
		{
			// faction leader is the only member; one-man faction
			if (this.getFlag(MFlag.getFlagPermanent()))
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

			for (MPlayer mplayer : MPlayerColl.get().getAllOnline())
			{
				mplayer.msg("<i>The faction %s<i> was disbanded.", this.getName(mplayer));
			}

			this.detach();
		}
		else
		{
			// promote new faction leader
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

	public boolean isAllMPlayersOffline()
	{
		return this.getMPlayersWhereOnline(true).size() == 0;
	}
	
	public boolean isAnyMPlayersOnline()
	{
		return !this.isAllMPlayersOffline();
	}
	
	public boolean isFactionConsideredOffline()
	{
		return this.isAllMPlayersOffline();
	}
	
	public boolean isFactionConsideredOnline()
	{
		return !this.isFactionConsideredOffline();
	}
	
	public boolean isExplosionsAllowed()
	{
		boolean explosions = this.getFlag(MFlag.getFlagExplosions());
		boolean offlineexplosions = this.getFlag(MFlag.getFlagOfflineexplosions());
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
		return Mixin.messagePredictate(new FactionEqualsPredictate(this), message);
	}
	
	public boolean sendMessage(String... messages)
	{
		return Mixin.messagePredictate(new FactionEqualsPredictate(this), messages);
	}
	
	public boolean sendMessage(Collection<String> messages)
	{
		return Mixin.messagePredictate(new FactionEqualsPredictate(this), messages);
	}
	
	// CONVENIENCE MSG
	
	public boolean msg(String msg)
	{
		return Mixin.msgPredictate(new FactionEqualsPredictate(this), msg);
	}
	
	public boolean msg(String msg, Object... args)
	{
		return Mixin.msgPredictate(new FactionEqualsPredictate(this), msg, args);
	}
	
	public boolean msg(Collection<String> msgs)
	{
		return Mixin.msgPredictate(new FactionEqualsPredictate(this), msgs);
	}
	
}
