package com.massivecraft.factions.entity;

import com.massivecraft.factions.AccessStatus;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.Selector;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.event.EventFactionsCreatePerms;
import com.massivecraft.massivecore.Named;
import com.massivecraft.massivecore.Prioritized;
import com.massivecraft.massivecore.Registerable;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.comparator.ComparatorSmart;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.predicate.PredicateIsRegistered;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.massivecraft.factions.Rel.TRUCE;

public class MPerm extends Entity<MPerm> implements Prioritized, Registerable, Named
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public final static transient String ID_BUILD = "build";
	public final static transient String ID_PAINBUILD = "painbuild";
	public final static transient String ID_DOOR = "door";
	public final static transient String ID_BUTTON = "button";
	public final static transient String ID_LEVER = "lever";
	public final static transient String ID_CONTAINER = "container";
	
	public final static transient String ID_NAME = "name";
	public final static transient String ID_DESC = "desc";
	public final static transient String ID_MOTD = "motd";
	public final static transient String ID_INVITE = "invite";
	public final static transient String ID_KICK = "kick";
	public final static transient String ID_TITLE = "title";
	public final static transient String ID_HOME = "home";
	public final static transient String ID_SETHOME = "sethome";
	public final static transient String ID_DEPOSIT = "deposit";
	public final static transient String ID_WITHDRAW = "withdraw";
	public final static transient String ID_TERRITORY = "territory";
	public final static transient String ID_ACCESS = "access";
	public final static transient String ID_CLAIMNEAR = "claimnear";
	public final static transient String ID_REL = "rel";
	public final static transient String ID_DISBAND = "disband";
	public final static transient String ID_FLAGS = "flags";
	public final static transient String ID_PERMS = "perms";
	public final static transient String ID_STATUS = "status";
	
	public final static transient int PRIORITY_BUILD = 1000;
	public final static transient int PRIORITY_PAINBUILD = 2000;
	public final static transient int PRIORITY_DOOR = 3000;
	public final static transient int PRIORITY_BUTTON = 4000;
	public final static transient int PRIORITY_LEVER = 5000;
	public final static transient int PRIORITY_CONTAINER = 6000;
	
	public final static transient int PRIORITY_NAME = 7000;
	public final static transient int PRIORITY_DESC = 8000;
	public final static transient int PRIORITY_MOTD = 9000;
	public final static transient int PRIORITY_INVITE = 10000;
	public final static transient int PRIORITY_KICK = 11000;
	public final static transient int PRIORITY_TITLE = 12000;
	public final static transient int PRIORITY_HOME = 13000;
	public final static transient int PRIORITY_SETHOME = 14000;
	public final static transient int PRIORITY_DEPOSIT = 15000;
	public final static transient int PRIORITY_WITHDRAW = 16000;
	public final static transient int PRIORITY_TERRITORY = 17000;
	public final static transient int PRIORITY_ACCESS = 18000;
	public final static transient int PRIORITY_CLAIMNEAR = 19000;
	public final static transient int PRIORITY_REL = 20000;
	public final static transient int PRIORITY_DISBAND = 21000;
	public final static transient int PRIORITY_FLAGS = 22000;
	public final static transient int PRIORITY_PERMS = 23000;
	public final static transient int PRIORITY_STATUS = 24000;
	
	public final static transient Set<Rel> STANDARD_BUILD = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
	public final static transient Set<Rel> STANDARD_PAINBUILD = new MassiveSet<>();
	public final static transient Set<Rel> STANDARD_DOOR = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY);
	public final static transient Set<Rel> STANDARD_BUTTON = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY);
	public final static transient Set<Rel> STANDARD_LEVER = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY);
	public final static transient Set<Rel> STANDARD_CONTAINER = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER);
	
	public final static transient Set<Rel> STANDARD_NAME = new MassiveSet<>(Rel.LEADER);
	public final static transient Set<Rel> STANDARD_DESC = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_MOTD = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_INVITE = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_KICK = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_TITLE = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_HOME = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT);
	public final static transient Set<Rel> STANDARD_SETHOME = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_DEPOSIT = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY, TRUCE, Rel.NEUTRAL, Rel.ENEMY);
	public final static transient Set<Rel> STANDARD_WITHDRAW = new MassiveSet<>(Rel.LEADER);
	public final static transient Set<Rel> STANDARD_TERRITORY = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_ACCESS = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_CLAIMNEAR = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT, Rel.ALLY);
	public final static transient Set<Rel> STANDARD_REL = new MassiveSet<>(Rel.LEADER, Rel.OFFICER);
	public final static transient Set<Rel> STANDARD_DISBAND = new MassiveSet<>(Rel.LEADER);
	public final static transient Set<Rel> STANDARD_FLAGS = new MassiveSet<>(Rel.LEADER);
	public final static transient Set<Rel> STANDARD_PERMS = new MassiveSet<>(Rel.LEADER);
	public final static transient Set<Rel> STANDARD_STATUS = new MassiveSet<>(Rel.LEADER, Rel.OFFICER, Rel.MEMBER, Rel.RECRUIT);
	
	// -------------------------------------------- //
	// META: CORE
	// -------------------------------------------- //
	
	public static MPerm get(Object oid)
	{
		return MPermColl.get().get(oid);
	}
	
	public static List<MPerm> getAll()
	{
		return getAll(false);
	}
	
	public static List<MPerm> getAll(boolean isAsync)
	{
		setupStandardPerms();
		new EventFactionsCreatePerms().run();
		
		return MPermColl.get().getAll(PredicateIsRegistered.get(), ComparatorSmart.get());
	}
	
	public static void setupStandardPerms()
	{
		getPermBuild();
		getPermPainbuild();
		getPermDoor();
		getPermButton();
		getPermLever();
		getPermContainer();
		
		getPermName();
		getPermDesc();
		getPermMotd();
		getPermInvite();
		getPermKick();
		getPermTitle();
		getPermHome();
		getPermStatus();
		getPermSethome();
		getPermDeposit();
		getPermWithdraw();
		getPermTerritory();
		getPermAccess();
		getPermClaimnear();
		getPermRel();
		getPermDisband();
		getPermFlags();
		getPermPerms();
	}
	
	public static MPerm getPermBuild() { return getCreative(PRIORITY_BUILD, ID_BUILD, ID_BUILD, "edit the terrain", STANDARD_BUILD, true, true, true); }
	public static MPerm getPermPainbuild() { return getCreative(PRIORITY_PAINBUILD, ID_PAINBUILD, ID_PAINBUILD, "edit, take damage", STANDARD_PAINBUILD, true, true, true); }
	public static MPerm getPermDoor() { return getCreative(PRIORITY_DOOR, ID_DOOR, ID_DOOR, "use doors", STANDARD_DOOR, true, true, true); }
	public static MPerm getPermButton() { return getCreative(PRIORITY_BUTTON, ID_BUTTON, ID_BUTTON, "use stone buttons", STANDARD_BUTTON, true, true, true); }
	public static MPerm getPermLever() { return getCreative(PRIORITY_LEVER, ID_LEVER, ID_LEVER, "use levers", STANDARD_LEVER, true, true, true); }
	public static MPerm getPermContainer() { return getCreative(PRIORITY_CONTAINER, ID_CONTAINER, ID_CONTAINER, "use containers", STANDARD_CONTAINER, true, true, true); }
	
	public static MPerm getPermName() { return getCreative(PRIORITY_NAME, ID_NAME, ID_NAME, "set name", STANDARD_NAME, false, true, true); }
	public static MPerm getPermDesc() { return getCreative(PRIORITY_DESC, ID_DESC, ID_DESC, "set description", STANDARD_DESC, false, true, true); }
	public static MPerm getPermMotd() { return getCreative(PRIORITY_MOTD, ID_MOTD, ID_MOTD, "set motd", STANDARD_MOTD, false, true, true); }
	public static MPerm getPermInvite() { return getCreative(PRIORITY_INVITE, ID_INVITE, ID_INVITE, "invite players", STANDARD_INVITE, false, true, true); }
	public static MPerm getPermStatus() { return getCreative(PRIORITY_STATUS, ID_STATUS, ID_STATUS, "show status", STANDARD_STATUS, false, true, true); }
	public static MPerm getPermKick() { return getCreative(PRIORITY_KICK, ID_KICK, ID_KICK, "kick members", STANDARD_KICK, false, true, true); }
	public static MPerm getPermTitle() { return getCreative(PRIORITY_TITLE, ID_TITLE, ID_TITLE, "set titles", STANDARD_TITLE, false, true, true); }
	public static MPerm getPermHome() { return getCreative(PRIORITY_HOME, ID_HOME, ID_HOME, "teleport home", STANDARD_HOME, false, true, true); }
	public static MPerm getPermSethome() { return getCreative(PRIORITY_SETHOME, ID_SETHOME, ID_SETHOME, "set the home", STANDARD_SETHOME, false, true, true); }
	public static MPerm getPermDeposit() { return getCreative(PRIORITY_DEPOSIT, ID_DEPOSIT, ID_DEPOSIT, "deposit money", STANDARD_DEPOSIT, false, false, false); } // non editable, non visible.
	public static MPerm getPermWithdraw() { return getCreative(PRIORITY_WITHDRAW, ID_WITHDRAW, ID_WITHDRAW, "withdraw money", STANDARD_WITHDRAW, false, true, true); }
	public static MPerm getPermTerritory() { return getCreative(PRIORITY_TERRITORY, ID_TERRITORY, ID_TERRITORY, "claim or unclaim", STANDARD_TERRITORY, false, true, true); }
	public static MPerm getPermAccess() { return getCreative(PRIORITY_ACCESS, ID_ACCESS, ID_ACCESS, "grant territory", STANDARD_ACCESS, false, true, true); }
	public static MPerm getPermClaimnear() { return getCreative(PRIORITY_CLAIMNEAR, ID_CLAIMNEAR, ID_CLAIMNEAR, "claim nearby", STANDARD_CLAIMNEAR, false, false, false); } // non editable, non visible.
	public static MPerm getPermRel() { return getCreative(PRIORITY_REL, ID_REL, ID_REL, "change relations", STANDARD_REL, false, true, true); }
	public static MPerm getPermDisband() { return getCreative(PRIORITY_DISBAND, ID_DISBAND, ID_DISBAND, "disband the faction", STANDARD_DISBAND, false, true, true); }
	public static MPerm getPermFlags() { return getCreative(PRIORITY_FLAGS, ID_FLAGS, ID_FLAGS, "manage flags", STANDARD_FLAGS, false, true, true); }
	public static MPerm getPermPerms() { return getCreative(PRIORITY_PERMS, ID_PERMS, ID_PERMS, "manage permissions", STANDARD_PERMS, false, true, true); }
	
	public static MPerm getCreative(int priority, String id, String name, String desc, Set<Rel> standard, boolean territory, boolean editable, boolean visible)
	{
		MPerm ret = MPermColl.get().get(id, false);
		if (ret != null)
		{
			ret.setRegistered(true);
			return ret;
		}
		
		ret = new MPerm(priority, name, desc, standard, territory, editable, visible);
		MPermColl.get().attach(ret, id);
		ret.setRegistered(true);
		ret.sync();
		
		return ret;
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public MPerm load(MPerm that)
	{
		this.priority = that.priority;
		this.name = that.name;
		this.desc = that.desc;
		this.standard = that.standard;
		this.territory = that.territory;
		this.editable = that.editable;
		this.visible = that.visible;
		
		return this;
	}
	
	// -------------------------------------------- //
	// TRANSIENT FIELDS (Registered)
	// -------------------------------------------- //
	
	private transient boolean registered = false;
	public boolean isRegistered() { return this.registered; }
	public void setRegistered(boolean registered) { this.registered = registered; }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// The sort priority. Low values appear first in sorted lists.
	// 1 is high up, 99999 is far down.
	// Standard Faction perms use "thousand values" like 1000, 2000, 3000 etc to allow adding new perms inbetween.
	// So 1000 might sound like a lot but it's actually the priority for the first perm.
	private int priority = 0;
	@Override public int getPriority() { return this.priority; }
	public MPerm setPriority(int priority) { this.priority = priority; this.changed(); return this; }
	
	// The name of the perm. According to standard it should be fully lowercase just like the perm id.
	// In fact the name and the id of all standard perms are the same.
	// I just added the name in case anyone feel like renaming their perms for some reason.
	// Example: "build"
	private String name = "defaultName";
	@Override public String getName() { return this.name; }
	public MPerm setName(String name) { this.name = name; this.changed(); return this; }
	
	// The perm function described as an "order".
	// The desc should match the format:
	// "You are not allowed to X."
	// "You are not allowed to edit the terrain."
	// Example: "edit the terrain"
	private String desc = "defaultDesc";
	public String getDesc() { return this.desc; }
	public MPerm setDesc(String desc) { this.desc = desc; this.changed(); return this; }
	
	// What is the standard (aka default) perm value?
	// This value will be set for factions from the beginning.
	// Example: ... set of relations ...
	private Set<Rel> standard = new MassiveSet<>();
	public Set<Rel> getStandard() { return this.standard; }
	public MPerm setStandard(Set<Rel> standard) { this.standard = standard; this.changed(); return this; }
	
	// Is this a territory perm meaning it has to do with territory construction, modification or interaction?
	// True Examples: build, container, door, lever etc.
	// False Examples: name, invite, home, sethome, deposit, withdraw etc.
	private boolean territory = false;
	public boolean isTerritory() { return this.territory; }
	public MPerm setTerritory(boolean territory) { this.territory = territory; this.changed(); return this; }
	
	// Is this perm editable by players?
	// With this we mean standard non administrator players.
	// All perms can be changed using /f override.
	// Example: true (all perms are editable by default)
	private boolean editable = false;
	public boolean isEditable() { return this.editable; }
	public MPerm setEditable(boolean editable) { this.editable = editable; this.changed(); return this; }
	
	// Is this perm visible to players?
	// With this we mean standard non administrator players.
	// All perms can be seen using /f override.
	// Some perms can be rendered meaningless by settings in Factions or external plugins.
	// Say we set "editable" to false.
	// In such case we might want to hide the perm by setting "visible" false.
	// If it can't be changed, why bother showing it?
	// Example: true (yeah we need to see this permission)
	private boolean visible = true;
	public boolean isVisible() { return this.visible; }
	public MPerm setVisible(boolean visible) { this.visible = visible; this.changed(); return this; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public MPerm()
	{
		// No argument constructor for GSON
	}
	
	public MPerm(int priority, String name, String desc, Set<Rel> standard, boolean territory, boolean editable, boolean visible)
	{
		this.priority = priority;
		this.name = name;
		this.desc = desc;
		this.standard = standard;
		this.territory = territory;
		this.editable = editable;
		this.visible = visible;
	}
	
	// -------------------------------------------- //
	// MESSAGES
	// -------------------------------------------- //
	
	public Mson getDeniedMessage(MPlayer mplayer, Faction faction)
	{
		if (mplayer == null) throw new NullPointerException("mplayer");
		if (faction == null) throw new NullPointerException("faction");
		
		CommandSender sender = mplayer.getSender();
		
		// Create not allowed message
		Mson message = Mson.mson(
			Mson.fromParsedMessage(faction.describeTo(mplayer, true)),
			Mson.mson(" does not allow you to "),
			this.getDesc(),
			"."
		).color(ChatColor.RED);
		
		// Add bypass notice
		if (sender != null && Perm.OVERRIDE.has(sender))
		{
			message = message.add(
				"\nYou can bypass by using ",
				CmdFactions.get().cmdFactionsOverride.getTemplate()
			).color(ChatColor.YELLOW);
		}
		
		// Return
		return message;
	}
	
	public void sendDeniedMessage(Selector selector, Faction faction)
	{
		if (selector == null) throw new NullPointerException("selector");
		if (faction == null) throw new NullPointerException("faction");
		if (!(selector instanceof MPlayer)) return;
		
		MPlayer mplayer = (MPlayer) selector;
		mplayer.message(this.getDeniedMessage(mplayer, faction));
	}
	
	public String getDesc(boolean withName, boolean withDesc)
	{
		List<String> parts = new ArrayList<>();
		
		if (withName)
		{
			String nameFormat;
			if ( ! this.isVisible())
			{
				nameFormat = "<silver>%s";
			}
			else if (this.isEditable())
			{
				nameFormat = "<pink>%s";
			}
			else
			{
				nameFormat = "<aqua>%s";
			}
			String name = this.getName();
			String nameDesc = Txt.parse(nameFormat, name);
			parts.add(nameDesc);
		}
		
		if (withDesc)
		{
			String desc = this.getDesc();
			
			String descDesc = Txt.parse("<i>%s", desc);
			parts.add(descDesc);
		}
		
		return Txt.implode(parts, " ");
	}
	
	// -------------------------------------------- //
	// HAS
	// -------------------------------------------- //
	
	public boolean has(Selector selector, Faction faction)
	{
		return this.has(selector, faction, false);
	}
	
	public boolean has(Selector selector, Faction faction, boolean verboose)
	{
		if (selector == null) throw new NullPointerException("selector");
		if (faction == null) throw new NullPointerException("faction");
		
		// Is overriding?
		if (selector instanceof MPlayer && ((MPlayer) selector).isOverriding()) return true;
		
		// Is this participator blacklisted?
		if (!faction.isFactionBannedInherited(selector))
		{
			// Is he permitted?
			if (faction.isPermittedAny(this, selector)) return true;
		}
		
		// Inform
		if (verboose) this.sendDeniedMessage(selector, faction);
		
		// Otherwise, he doesn't have this perm.
		return false;
	}
	
	public boolean has(MPlayer mplayer, PS ps, boolean verboose)
	{
		// Null Check
		if (mplayer == null) throw new NullPointerException("mplayer");
		if (ps == null) throw new NullPointerException("ps");
		
		if (mplayer.isOverriding()) return true;
		
		TerritoryAccess ta = BoardColl.get().getTerritoryAccessAt(ps);
		Faction hostFaction = ta.getHostFaction();
		
		if (this.isTerritory())
		{
			AccessStatus accessStatus = ta.getTerritoryAccess(mplayer);
			if (accessStatus != AccessStatus.STANDARD)
			{
				if (verboose && accessStatus == AccessStatus.DECREASED)
				{
					this.sendDeniedMessage(mplayer, hostFaction);
				}
				
				return accessStatus.hasAccess();
			}
		}
		
		return this.has(mplayer, hostFaction, verboose);
	}
	
	// -------------------------------------------- //
	// CONVENIENCE
	// -------------------------------------------- //
	
	// TODO: REl to id conversion
	public Set<String> getStandardIds()
	{
		// Create
		Set<String> ret = new MassiveSet<>();
		
		// Fill
		for (Rel rel : this.getStandard())
		{
			ret.add(rel.getId());
		}
		
		// Return
		return ret;
	}
	
}
