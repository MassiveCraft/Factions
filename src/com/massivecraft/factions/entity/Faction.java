package com.massivecraft.factions.entity;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsIndex;
import com.massivecraft.factions.FactionsParticipator;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.Selector;
import com.massivecraft.factions.SelectorType;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.cmd.type.TypeRel;
import com.massivecraft.factions.cmd.type.TypeSelector;
import com.massivecraft.factions.predicate.PredicateCommandSenderFaction;
import com.massivecraft.factions.predicate.PredicateMPlayerRole;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.collections.MassiveMapDef;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.collections.MassiveSetDef;
import com.massivecraft.massivecore.command.type.Type;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.predicate.Predicate;
import com.massivecraft.massivecore.predicate.PredicateAnd;
import com.massivecraft.massivecore.predicate.PredicateVisibleTo;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.store.EntityInternalMap;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Faction extends Entity<Faction> implements FactionsParticipator
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final transient String NODESCRIPTION = Txt.parse("<em><silver>no description set");
	public static final transient String NOMOTD = Txt.parse("<em><silver>no message of the day set");
	
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
		this.invitations.load(that.invitations);
		this.setRelationWishes(that.relationWishes);
		this.setFlagIds(that.flags);
		this.setPermIds(that.perms);
		this.factionBans.load(that.factionBans);
		
		return this;
	}
	
	@Override
	public void preDetach(String id)
	{
		if (!this.isLive()) return;
		
		// NOTE: Existence check is required for compatibility with some plugins.
		// If they have money ...
		if (Money.exists(this))
		{
			// ... remove it.
			Money.set(this, null, 0);	
		}
	}
	
	// -------------------------------------------- //
	// VERSION
	// -------------------------------------------- //
	
	public int version = 2;
	
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
	private EntityInternalMap<Invitation> invitations = new EntityInternalMap<>(this, Invitation.class);
	
	// The keys in this map are factionIds.
	// Null means no special relation whishes.
	private MassiveMapDef<String, Rel> relationWishes = new MassiveMapDef<>();
	
	// The flag overrides are modifications to the default values.
	// Null means default.
	private MassiveMapDef<String, Boolean> flags = new MassiveMapDef<>();

	// The perm overrides are modifications to the default values.
	// Null means default.
	private MassiveMapDef<String, Set<String>> perms = new MassiveMapDef<>();
	
	// The perm blacklist of which selectors are not allowed in any way.
	private EntityInternalMap<FactionBan> factionBans = new EntityInternalMap<>(this, FactionBan.class);
	
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
	
	@Override
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
		return this.description;
	}
	
	public void setDescription(String description)
	{
		// Clean input
		String target = clean(description);
		
		// Detect Nochange
		if (MUtil.equals(this.description, target)) return;

		// Apply
		this.description = target;
		
		// Mark as changed
		this.changed();
	}
	
	// FINER
	
	public String getDescriptionDesc()
	{
		String motd = this.getDescription();
		if (motd == null) motd = NODESCRIPTION;
		return motd;
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
		return this.motd;
	}
	
	public void setMotd(String motd)
	{
		// Clean input
		String target = clean(motd);
		
		// Detect Nochange
		if (MUtil.equals(this.motd, target)) return;

		// Apply
		this.motd = target;
		
		// Mark as changed
		this.changed();
	}
	
	// FINER
	
	public String getMotdDesc()
	{
		return getMotdDesc(this.getMotd());
	}
	
	private static String getMotdDesc(String motd)
	{
		if (motd == null) motd = NOMOTD;
		return motd;
	}
	
	public List<Object> getMotdMessages()
	{
		// Create
		List<Object> ret = new MassiveList<>();
		
		// Fill
		Object title = this.getName() + " - Message of the Day";
		title = Txt.titleize(title);
		ret.add(title);
		
		String motd = Txt.parse("<i>") + this.getMotdDesc();
		ret.add(motd);
		
		ret.add("");
		
		// Return
		return ret;
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
		this.changed();
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
	@Override
	public double getPowerBoost()
	{
		Double ret = this.powerBoost;
		if (ret == null) ret = 0D;
		return ret;
	}
	
	@Override
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
	public EntityInternalMap<Invitation> getInvitations() { return this.invitations; }
	
	// FINER
	public boolean isInvited(String playerId)
	{
		return this.getInvitations().containsKey(playerId);
	}
	
	public boolean isInvited(MPlayer mplayer)
	{
		return this.isInvited(mplayer.getId());
	}
	
	public boolean uninvite(String playerId)
	{
		System.out.println(playerId);
		return this.getInvitations().detachId(playerId) != null;
	}
	
	public boolean uninvite(MPlayer mplayer)
	{
		return uninvite(mplayer.getId());
	}
	
	public void invite(String playerId, Invitation invitation)
	{
		uninvite(playerId);
		this.invitations.attach(invitation, playerId);
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
		MassiveMapDef<String, Rel> target = new MassiveMapDef<>(relationWishes);
		
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
	
	// -------------------------------------------- //
	// FIELD: flagOverrides
	// -------------------------------------------- //
	
	// RAW
	
	public Map<MFlag, Boolean> getFlags()
	{
		// We start with default values ...
		Map<MFlag, Boolean> ret = new MassiveMap<>();
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
		Map<String, Boolean> flagIds = new MassiveMap<>();
		for (Entry<MFlag, Boolean> entry : flags.entrySet())
		{
			flagIds.put(entry.getKey().getId(), entry.getValue());
		}
		setFlagIds(flagIds);
	}
	
	public void setFlagIds(Map<String, Boolean> flagIds)
	{
		// Clean input
		MassiveMapDef<String, Boolean> target = new MassiveMapDef<>();
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
		this.flags = new MassiveMapDef<>(target);
		
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
	// FIELD: perms
	// -------------------------------------------- //
	
	// RAW
	public MassiveMapDef<String, Set<String>> getPermIds()
	{
		return this.perms;
	}
	
	public void setPermIds(MassiveMapDef<String, Set<String>> perms)
	{
		// Clean input
		MassiveMapDef<String, Set<String>> target = new MassiveMapDef<String, Set<String>>();
		for (Entry<String, Set<String>> entry : perms.entrySet())
		{
			String key = entry.getKey();
			if (key == null) continue;
			key = key.toLowerCase(); // Lowercased Keys Version 2.6.0 --> 2.7.0
			
			Set<String> value = entry.getValue();
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
	
	// Finer
	public Map<MPerm, Set<String>> getPerms()
	{
		// We start with default values ...
		Map<MPerm, Set<String>> ret = new MassiveMap<>();
		
		// ... and if anything is explicitly set we use that info ...
		for (Iterator<Entry<String, Set<String>>> it = this.getPermIds().entrySet().iterator(); it.hasNext(); )
		{
			// ... for each entry ...
			Entry<String, Set<String>> entry = it.next();
			
			// ... extract id and remove null values ...
			String id = entry.getKey();
			if (id == null)
			{
				it.remove();
				continue;
			}
			
			// ... resolve object and skip unknowns ...
			MPerm mperm = MPerm.get(id);
			if (mperm == null) continue;
			
			ret.put(mperm, new MassiveSet<>(entry.getValue()));
		}
		
		for (MPerm mperm : MPerm.getAll())
		{
			// Is already configured?
			if (ret.containsKey(mperm)) continue;
			
			// Add
			ret.put(mperm, mperm.getStandardIds());
		}
		
		return ret;
	}
	
	public void setPerms(Map<MPerm, Set<String>> perms)
	{
		// Create
		MassiveMapDef<String, Set<String>> permIds = new MassiveMapDef<>();
		
		// Fill
		for (Entry<MPerm, Set<String>> entry : perms.entrySet())
		{
			permIds.put(entry.getKey().getId(), entry.getValue());
		}
		
		// Set
		this.setPermIds(permIds);
	}
	
	// -------------------------------------------- //
	// PERMITTED
	// -------------------------------------------- //
	// Being permitted for a perm can have many reasons:
	// For Players, either their Relation, Faction, Rank or themselves can have been permitted.
	// For Factions, this can be their relation or themselves being permitted.
	//
	// For each selector we need to check if any of the sub-selectors are permitted.
	
	private Set<String> getPermittedIds(MPerm perm)
	{
		String permId = perm.getId();
		Set<String> permitted = this.perms.get(permId);
		return permitted != null ? permitted : perm.getStandardIds();
	}
	
	public Set<MPerm> getPermittedFor(Selector selector)
	{
		// Create
		Set<MPerm> ret = new MassiveSet<>();
		
		// Fill
		for (MPerm perm : MPermColl.get().getAll())
		{
			if (this.isPermitted(perm, selector)) ret.add(perm);
		}
		
		// Return
		return ret;
	}
	
	public void setPermitted(MPerm perm, Selector selector, boolean add)
	{
		if (perm == null) throw new NullPointerException("perm");
		if (selector == null) throw new NullPointerException("selector");
		
		// Get Ids
		String idSelector = selector.getId();
		String idPerm = perm.getId();
		
		// Get perms
		Map<String, Set<String>> perms = this.getPermIds();
		Set<String> selectors = perms.get(idPerm);
		
		// If new, assign standard ids on first change.
		if (add && selectors == null) selectors = new MassiveSetDef<>(perm.getStandardIds());
		
		// Add || Remove
		if (add)
		{
			selectors.add(idSelector);
		}
		else if (selectors != null)
		{
			selectors.remove(idSelector);
		}
		
		perms.put(idPerm, selectors);
		
		// Changed
		this.changed();
	}
	
	public void setPermitted(MPerm perm, Selector... selectors)
	{
		for (Selector selector : selectors)
		{
			this.setPermitted(perm, selector, true);
		}
	}
	
	public boolean isPermittedAny(MPerm perm, Selector selector)
	{
		// Special
		if (selector instanceof MPlayer && this.isPermittedPlayer(perm, (MPlayer)selector)) return true;
		if (selector instanceof Faction && this.isPermittedFaction(perm, (Faction)selector)) return true;
		
		// Default
		return this.isPermitted(perm, selector);
	}
	
	private boolean isPermittedFaction(MPerm perm, Faction faction)
	{
		return this.isPermittedAny(perm, faction, faction.getRelationTo(this));
	}
	
	private boolean isPermittedPlayer(MPerm perm, MPlayer mplayer)
	{
		// TODO: Add Rank in the future
		return this.isPermittedAny(perm, mplayer, mplayer.getFaction(), mplayer.getRelationTo(this));
	}
	
	private boolean isPermittedAny(MPerm perm, Selector... selectors)
	{
		for (Selector selector : selectors)
		{
			if (this.isPermitted(perm, selector)) return true;
		}
		return false;
	}
	
	public boolean isPermitted(MPerm perm, Selector selector)
	{
		if (perm == null) throw new NullPointerException("perm");
		if (selector == null) throw new NullPointerException("selector");
		
		// Is specifically granted?
		Set<String> selectors = this.getPermIds().get(perm.getId());
		if (selectors != null) return selectors.contains(selector.getId());
		
		// Is standard?
		return selector instanceof Rel && perm.getStandard().contains(selector);
	}
	
	// -------------------------------------------- //
	// PERMITTED > VISUAL
	// -------------------------------------------- //
	
	public List<Mson> getPermittedShow(MPerm perm, RelationParticipator relationParticipator)
	{
		// Resolve Permitted
		Map<SelectorType, List<Selector>> permitted = this.resolvePermitted(perm);
		
		// Return show
		return this.getPermittedShow(perm, permitted, relationParticipator);
	}
	
	public Mson getPermittedLine(MPerm perm, RelationParticipator relationParticipator)
	{
		// Create
		Mson ret = Mson.EMPTY;
		Map<SelectorType, List<Selector>> permitted = this.resolvePermitted(perm);
		
		// Fill > Ranks
		List<Selector> ranks = permitted.get(SelectorType.RANK);
		if (ranks != null)
		{
			Mson rankMson = Mson.EMPTY;
			for (Selector selector : ranks)
			{
				Rel rank = (Rel) selector;
				// TODO: Change this to number after ranks are creatable
				Mson mson = Mson.mson(String.valueOf(rank.getName().charAt(0)));
				rankMson = rankMson.add(mson);
			}
			
			ret = ret.add(rankMson.color(ChatColor.DARK_GREEN));
		}
		
		// Fill > Relations
		List<Selector> relations = permitted.get(SelectorType.RELATION);
		if (relations != null)
		{
			Mson relMson = Mson.EMPTY;
			for (Selector selector : relations)
			{
				Rel rel = (Rel) selector;
				Mson mson = Mson.mson(String.valueOf(rel.getName().charAt(0)));
				relMson = relMson.add(mson);
			}
			
			ret = ret.add(relMson.color(ChatColor.LIGHT_PURPLE));
		}
		
		// Fill > Factions
		List<Selector> factions = permitted.get(SelectorType.FACTION);
		if (factions != null) ret = ret.add(Mson.mson("F").color(ChatColor.GREEN));
		
		// Fill > Factions
		List<Selector> players = permitted.get(SelectorType.PLAYER);
		if (players != null) ret = ret.add(Mson.mson("P").color(ChatColor.WHITE));
		
		// Fill > Name
		ret = ret.add(Mson.SPACE).add(Mson.mson(perm.getName()).uppercaseFirst().color(ChatColor.YELLOW));
		
		// Fill > Show
		ret = ret.command(CmdFactions.get().cmdFactionsPerm.cmdFactionsPermShow, perm.getId());
		List<String> showLines = Mson.toPlain(this.getPermittedShow(perm, permitted, relationParticipator), true);
		showLines.add(ret.getTooltip());
		ret = ret.tooltip(showLines);
		
		// Return
		return ret;
	}
	
	private Map<SelectorType, List<Selector>> resolvePermitted(MPerm perm)
	{
		// Create
		Map<SelectorType, List<Selector>> ret = new MassiveMap<>();
		
		// Fill
		TypeSelector type = TypeSelector.get();
		for (String id : this.getPermittedIds(perm))
		{
			Selector selector = type.readSafe(id, null);
			if (selector == null) throw new IllegalStateException("Selector id " + id + "wasn't resolvable.");
			
			SelectorType selectorType = selector.getType();
			List<Selector> selectors = ret.get(selectorType);
			if (selectors == null)
			{
				selectors = new MassiveList<>(selector);
				ret.put(selectorType, selectors);
			}
			else
			{
				selectors.add(selector);
			}
		}
		
		// Return
		return ret;
	}
	
	private List<Mson> getPermittedShow(MPerm perm, Map<SelectorType, List<Selector>> permitted, RelationParticipator relationParticipator)
	{
		String factionName = this.describeTo(relationParticipator, true);
		Mson header = Txt.titleize(factionName + " " + perm.getDesc(true, true));
		Mson ranks = getResolveSection("Ranks: ", permitted.get(SelectorType.RANK), TypeRel.get());
		Mson relations = getResolveSection("Relations: ", permitted.get(SelectorType.RELATION), TypeRel.get());
		Mson factions = getResolveSection("Factions: ", permitted.get(SelectorType.FACTION), TypeFaction.get());
		Mson players = getResolveSection("Players: ", permitted.get(SelectorType.PLAYER), TypeMPlayer.get());
		
		return new MassiveList<>(header, ranks, relations, factions, players);
	}
	
	@SuppressWarnings("unchecked")
	private static <E> Mson getResolveSection(String header, List<Selector> resolve, Type<E> type)
	{
		Mson heading = Mson.mson(header).color(ChatColor.YELLOW);
		if (resolve == null) return heading;
		
		List<Mson> ret = new MassiveList<>();
		for (Selector selector : resolve)
		{
			E element = (E) selector;
			ret.add(type.getVisualMson(element));
		}
		
		return heading.add(Mson.implode(ret, Mson.mson(", ")));
	}
	
	// -------------------------------------------- //
	// FIELD: factionBans
	// -------------------------------------------- //
	
	// Raw
	public EntityInternalMap<FactionBan> getFactionBans()
	{
		return factionBans;
	}
	
	// Finer
	public boolean isFactionBannedInherited(Selector selector)
	{
		if (selector instanceof Faction)
		{
			if (this.isFactionBanned(((Faction) selector).getRelationTo(this))) return true;
		}
		else if (selector instanceof MPlayer)
		{
			MPlayer mplayer = (MPlayer) selector;
			if (this.isFactionBanned(mplayer.getRelationTo(this)) || this.isFactionBanned(mplayer.getFaction())) return true;
		}
		
		return this.isFactionBanned(selector);
	}
	
	private boolean isFactionBanned(Selector selector)
	{
		return this.factionBans.containsKey(selector.getId());
	}
	
	// -------------------------------------------- //
	// OVERRIDE: Selector
	// -------------------------------------------- //
	
	@Override
	public SelectorType getType()
	{
		return SelectorType.FACTION;
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
		
		ret = this.limitWithPowerMax(ret);
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
		
		ret = this.limitWithPowerMax(ret);
		ret += this.getPowerBoost();
		
		return ret;
	}
	
	private double limitWithPowerMax(double power)
	{
		// NOTE: 0.0 powerMax means there is no max power
		double powerMax = MConf.get().factionPowerMax;
		
		return powerMax <= 0 || power < powerMax ? power : powerMax;
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
	// WORLDS
	// -------------------------------------------- //
	
	public Set<String> getClaimedWorlds()
	{
		return BoardColl.get().getClaimedWorlds(this);
	}
	
	// -------------------------------------------- //
	// FOREIGN KEY: MPLAYER
	// -------------------------------------------- //
	
	public List<MPlayer> getMPlayers()
	{
		return new MassiveList<>(FactionsIndex.get().getMPlayers(this));
	}
	
	public List<MPlayer> getMPlayers(Predicate<? super MPlayer> where, Comparator<? super MPlayer> orderby, Integer limit, Integer offset)
	{
		return MUtil.transform(this.getMPlayers(), where, orderby, limit, offset);
	}
	
	public List<MPlayer> getMPlayersWhere(Predicate<? super MPlayer> predicate)
	{
		return this.getMPlayers(predicate, null, null, null);
	}
	
	public List<MPlayer> getMPlayersWhereOnline(boolean online)
	{
		return this.getMPlayersWhere(online ? SenderColl.PREDICATE_ONLINE : SenderColl.PREDICATE_OFFLINE);
	}

	public List<MPlayer> getMPlayersWhereOnlineTo(Object senderObject)
	{
		return this.getMPlayersWhere(PredicateAnd.get(SenderColl.PREDICATE_ONLINE, PredicateVisibleTo.get(senderObject)));
	}
	
	public List<MPlayer> getMPlayersWhereRole(Rel role)
	{
		return this.getMPlayersWhere(PredicateMPlayerRole.get(role));
	}
	
	public MPlayer getLeader()
	{
		List<MPlayer> ret = this.getMPlayersWhereRole(Rel.LEADER);
		if (ret.size() == 0) return null;
		return ret.get(0);
	}
	
	public List<CommandSender> getOnlineCommandSenders()
	{
		// Create Ret
		List<CommandSender> ret = new MassiveList<>();
		
		// Fill Ret
		for (CommandSender sender : IdUtil.getLocalSenders())
		{
			if (MUtil.isntSender(sender)) continue;
			
			MPlayer mplayer = MPlayer.get(sender);
			if (mplayer.getFaction() != this) continue;
			
			ret.add(sender);
		}
		
		// Return Ret
		return ret;
	}
	
	public List<Player> getOnlinePlayers()
	{
		// Create Ret
		List<Player> ret = new MassiveList<>();
		
		// Fill Ret
		for (Player player : MUtil.getOnlinePlayers())
		{
			if (MUtil.isntPlayer(player)) continue;
			
			MPlayer mplayer = MPlayer.get(player);
			if (mplayer.getFaction() != this) continue;
			
			ret.add(player);
		}
		
		// Return Ret
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

		if (explosions && offlineexplosions) return true;
		if ( ! explosions && ! offlineexplosions) return false;

		boolean online = this.isFactionConsideredOnline();
		
		return (online && explosions) || (!online && offlineexplosions);
	}
	
	// -------------------------------------------- //
	// MESSAGES
	// -------------------------------------------- //
	// These methods are simply proxied in from the Mixin.
	
	// CONVENIENCE SEND MESSAGE
	
	public boolean sendMessage(Object message)
	{
		return MixinMessage.get().messagePredicate(new PredicateCommandSenderFaction(this), message);
	}
	
	public boolean sendMessage(Object... messages)
	{
		return MixinMessage.get().messagePredicate(new PredicateCommandSenderFaction(this), messages);
	}
	
	public boolean sendMessage(Collection<Object> messages)
	{
		return MixinMessage.get().messagePredicate(new PredicateCommandSenderFaction(this), messages);
	}
	
	// CONVENIENCE MSG
	
	public boolean msg(String msg)
	{
		return MixinMessage.get().msgPredicate(new PredicateCommandSenderFaction(this), msg);
	}
	
	public boolean msg(String msg, Object... args)
	{
		return MixinMessage.get().msgPredicate(new PredicateCommandSenderFaction(this), msg, args);
	}
	
	public boolean msg(Collection<String> msgs)
	{
		return MixinMessage.get().msgPredicate(new PredicateCommandSenderFaction(this), msgs);
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	// FIXME this probably needs to be moved elsewhere
	public static String clean(String message)
	{
		String target = message;
		if (target == null) return null;
		
		target = target.trim();
		if (target.isEmpty()) target = null;
		
		return target;
	}
	
}
