package com.massivecraft.factions;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.adapters.FFlagTypeAdapter;
import com.massivecraft.factions.adapters.FLocToStringSetTypeAdapter;
import com.massivecraft.factions.adapters.FPermTypeAdapter;
import com.massivecraft.factions.adapters.LocationTypeAdapter;
import com.massivecraft.factions.adapters.RelTypeAdapter;
import com.massivecraft.factions.cmd.*;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.LWCFeatures;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsChatEarlyListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.listeners.FactionsPlayerListener;
import com.massivecraft.factions.listeners.FactionsServerListener;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.MPlugin;

import com.griefcraft.lwc.LWCPlugin;
import com.nijiko.permissions.PermissionHandler;
import com.earth2me.essentials.chat.EssentialsChat;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.integration.EssentialsFeatures;
import com.massivecraft.factions.integration.capi.CapiFeatures;

public class P extends MPlugin
{
	// Our single plugin instance
	public static P p;
	
	// Listeners
	public final FactionsPlayerListener playerListener;
	public final FactionsChatEarlyListener chatEarlyListener;
	public final FactionsEntityListener entityListener;
	public final FactionsBlockListener blockListener;
	public final FactionsServerListener serverListener;
	
	// Persistance related
	private boolean locked = false;
	public boolean getLocked() {return this.locked;}
	public void setLocked(boolean val) {this.locked = val; this.setAutoSave(val);}
	
	// Commands
	public FCmdRoot cmdBase;
	public CmdAutoHelp cmdAutoHelp;
	
	public P()
	{
		p = this;
		this.playerListener = new FactionsPlayerListener(this);
		this.chatEarlyListener = new FactionsChatEarlyListener(this);
		this.entityListener = new FactionsEntityListener(this);
		this.blockListener = new FactionsBlockListener(this);
		this.serverListener = new FactionsServerListener(this);
	}
	
	
	
	public static PermissionHandler Permissions;
	private static EssentialsChat essChat;
	
	
	@Override
	public void onEnable()
	{
		if ( ! preEnable()) return;
		
		// Load Conf from disk
		Conf.load();
		FPlayers.i.loadFromDisc();
		Factions.i.loadFromDisc();
		Board.load();
		
		// Add Base Commands
		this.cmdBase = new FCmdRoot();
		this.cmdAutoHelp = new CmdAutoHelp();
		this.getBaseCommands().add(cmdBase);
		
		//setupPermissions();
		integrateEssentialsChat();
		setupSpout(this);
		Econ.doSetup();
		Econ.oldMoneyDoTransfer();
		CapiFeatures.setup();
		setupLWC();
		
		if(Conf.worldGuardChecking)
		{
			Worldguard.init(this);			
		}
		
		// Player Events
		this.registerEvent(Event.Type.PLAYER_CHAT, this.playerListener, Event.Priority.Highest);
		this.registerEvent(Event.Type.PLAYER_CHAT, this.chatEarlyListener, Event.Priority.Lowest);
		this.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, this.playerListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PLAYER_MOVE, this.playerListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PLAYER_RESPAWN, this.playerListener, Event.Priority.High);
		this.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this.playerListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PLAYER_BUCKET_FILL, this.playerListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PLAYER_KICK, this.playerListener, Event.Priority.Normal);
		
		// Entity Events
		this.registerEvent(Event.Type.ENDERMAN_PICKUP, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.ENDERMAN_PLACE, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.ENTITY_DEATH, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.CREATURE_SPAWN, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.ENTITY_TARGET, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PAINTING_BREAK, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.PAINTING_PLACE, this.entityListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.EXPLOSION_PRIME, this.entityListener, Event.Priority.Normal);
		
		// Block Events
		this.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, this.blockListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.BLOCK_PISTON_RETRACT, this.blockListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.BLOCK_SPREAD, this.blockListener, Event.Priority.Normal);
		this.registerEvent(Event.Type.BLOCK_BURN, this.blockListener, Event.Priority.Normal);
		
		// Server Events
		this.registerEvent(Event.Type.PLUGIN_ENABLE, this.serverListener, Event.Priority.Monitor);
		this.registerEvent(Event.Type.PLUGIN_DISABLE, this.serverListener, Event.Priority.Monitor);
		
		postEnable();
	}
	
	@Override
	public GsonBuilder getGsonBuilder()
	{
		Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>(){}.getType();

		return new GsonBuilder()
		.setPrettyPrinting()
		.disableHtmlEscaping()
		.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
		.registerTypeAdapter(Location.class, new LocationTypeAdapter())
		.registerTypeAdapter(mapFLocToStringSetType, new FLocToStringSetTypeAdapter())
		.registerTypeAdapter(Rel.class, new RelTypeAdapter())
		.registerTypeAdapter(FPerm.class, new FPermTypeAdapter())
		.registerTypeAdapter(FFlag.class, new FFlagTypeAdapter());
	}

	@Override
	public void onDisable()
	{
		Board.save();
		Conf.save();
		unhookEssentialsChat();
		super.onDisable();
	}
	
	@Override
	public void postAutoSave()
	{
		Board.save();
		Conf.save();
	}

	// -------------------------------------------- //
	// Integration with other plugins
	// -------------------------------------------- //

	private void setupSpout(P factions)
	{
		Plugin test = factions.getServer().getPluginManager().getPlugin("Spout");

		if (test != null && test.isEnabled())
		{
			SpoutFeatures.setAvailable(true, test.getDescription().getFullName());
		}
	}

	private void integrateEssentialsChat()
	{
		if (essChat != null) return;

		Plugin test = this.getServer().getPluginManager().getPlugin("EssentialsChat");

		if (test != null && test.isEnabled())
		{
			essChat = (EssentialsChat)test;
			EssentialsFeatures.integrateChat(essChat);
		}
	}
	
	private void unhookEssentialsChat()
	{
		if (essChat != null)
		{
			EssentialsFeatures.unhookChat();
		}
	}

	private void setupLWC()
	{
		Plugin test = this.getServer().getPluginManager().getPlugin("LWC");

		if(test != null && test.isEnabled())
			LWCFeatures.integrateLWC((LWCPlugin)test);
	}

	// -------------------------------------------- //
	// Functions for other plugins to hook into
	// -------------------------------------------- //

	// This value will be updated whenever new hooks are added
	public int hookSupportVersion()
	{
		return 3;
	}

	// If another plugin is handling insertion of chat tags, this should be used to notify Factions
	public void handleFactionTagExternally(boolean notByFactions)
	{
		Conf.chatTagHandledByAnotherPlugin = notByFactions;
	}

	// Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
	// enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()
	
	
	public boolean shouldLetFactionsHandleThisChat(PlayerChatEvent event)
	{
		if (event == null) return false;
		return (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage()));
	}

	// Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
	// local chat, or anything else which targets individual recipients, so Faction Chat can be done
	public boolean isPlayerFactionChatting(Player player)
	{
		if (player == null) return false;
		FPlayer me = FPlayers.i.get(player);
		
		if (me == null)return false;
		return me.getChatMode().isAtLeast(ChatMode.ALLIANCE);
	}

	// Is this chat message actually a Factions command, and thus should be left alone by other plugins?
	public boolean isFactionsCommand(String check)
	{
		if (check == null || check.isEmpty()) return false;
		return this.handleCommand(null, check, true);
	}

	// Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
	public String getPlayerFactionTag(Player player)
	{
		return getPlayerFactionTagRelation(player, null);
	}

	// Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
	public String getPlayerFactionTagRelation(Player speaker, Player listener)
	{
		String tag = "~";

		if (speaker == null)
			return tag;

		FPlayer me = FPlayers.i.get(speaker);
		if (me == null)
			return tag;

		// if listener isn't set, or config option is disabled, give back uncolored tag
		if (listener == null || !Conf.chatTagRelationColored) {
			tag = me.getChatTag().trim();
		} else {
			FPlayer you = FPlayers.i.get(listener);
			if (you == null)
				tag = me.getChatTag().trim();
			else  // everything checks out, give the colored tag
				tag = me.getChatTag(you).trim();
		}
		if (tag.isEmpty())
			tag = "~";

		return tag;
	}

	// Get a player's title within their faction, mainly for usage by chat plugins for local/channel chat
	public String getPlayerTitle(Player player)
	{
		if (player == null)
			return "";

		FPlayer me = FPlayers.i.get(player);
		if (me == null)
			return "";

		return me.getTitle().trim();
	}

	// Get a list of all faction tags (names)
	public Set<String> getFactionTags()
	{
		Set<String> tags = new HashSet<String>();
		for (Faction faction : Factions.i.get())
		{
			tags.add(faction.getTag());
		}
		return tags;
	}

	// Get a list of all players in the specified faction
	public Set<String> getPlayersInFaction(String factionTag)
	{
		Set<String> players = new HashSet<String>();
		Faction faction = Factions.i.getByTag(factionTag);
		if (faction != null)
		{
			for (FPlayer fplayer : faction.getFPlayers())
			{
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// Get a list of all online players in the specified faction
	public Set<String> getOnlinePlayersInFaction(String factionTag)
	{
		Set<String> players = new HashSet<String>();
		Faction faction = Factions.i.getByTag(factionTag);
		if (faction != null)
		{
			for (FPlayer fplayer : faction.getFPlayersWhereOnline(true))
			{
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// check if player is allowed to build/destroy in a particular location
	public boolean isPlayerAllowedToBuildHere(Player player, Location location)
	{
		return FactionsBlockListener.playerCanBuildDestroyBlock(player, location.getBlock(), "", true);
	}

	// check if player is allowed to interact with the specified block (doors/chests/whatever)
	public boolean isPlayerAllowedToInteractWith(Player player, Block block)
	{
		return FactionsPlayerListener.canPlayerUseBlock(player, block, true);
	}

	// check if player is allowed to use a specified item (flint&steel, buckets, etc) in a particular location
	public boolean isPlayerAllowedToUseThisHere(Player player, Location location, Material material)
	{
		return FactionsPlayerListener.playerCanUseItemHere(player, location, material, true);
	}
}
