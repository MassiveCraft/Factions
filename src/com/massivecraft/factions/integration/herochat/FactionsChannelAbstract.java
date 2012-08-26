package com.massivecraft.factions.integration.herochat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.ChannelStorage;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.MessageFormatSupplier;
import com.dthielke.herochat.MessageNotFoundException;
import com.dthielke.herochat.util.Messaging;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Rel;

public abstract class FactionsChannelAbstract implements Channel
{
	private static final Pattern msgPattern = Pattern.compile("(.*)<(.*)%1\\$s(.*)> %2\\$s");
	private final ChannelStorage storage = Herochat.getChannelManager().getStorage();
	private final MessageFormatSupplier formatSupplier = Herochat.getChannelManager();
	
	public FactionsChannelAbstract()
	{
		
	}
	
	
	
	@Override
	public boolean addMember(Chatter chatter, boolean announce, boolean flagUpdate)
	{
		if (chatter.hasChannel(this)) return false;
		chatter.addChannel(this, announce, flagUpdate);
		return true;
	}
	
	@Override
	public boolean kickMember(Chatter chatter, boolean announce)
	{
		if (!chatter.hasChannel(this)) return false;
		this.removeMember(chatter, false, true);
		
		if (announce)
		{
			try
			{
				announce(Herochat.getMessage("channel_kick").replace("$1", chatter.getPlayer().getDisplayName()));
			}
			catch (MessageNotFoundException e)
			{
				Herochat.severe("Messages.properties is missing: channel_kick");
			}
		}
		
		return true;
	}
	
	@Override
	public boolean removeMember(Chatter chatter, boolean announce, boolean flagUpdate)
	{
		if (!chatter.hasChannel(this)) return false;
		chatter.removeChannel(this, announce, flagUpdate);
	    return true;
	}
	
	
	@Override
	public Set<Chatter> getMembers()
	{
		Set<Chatter> ret = new HashSet<Chatter>();
		for (Chatter chatter : Herochat.getChatterManager().getChatters())
		{
			if(chatter.hasChannel(this)) ret.add(chatter);
		}
		return ret;
	}

	@Override
	public void announce(String message)
	{
		message = applyFormat(this.formatSupplier.getAnnounceFormat(), "").replace("%2$s", message);
		for (Chatter member : this.getMembers())
		{
			member.getPlayer().sendMessage(message);
		}
		Herochat.logChat(ChatColor.stripColor(message));
	}

	@Override
	public String applyFormat(String format, String originalFormat)
	{
		format = format.replace("{default}", this.formatSupplier.getStandardFormat());
		format = format.replace("{name}", this.getName());
		format = format.replace("{nick}", this.getNick());
		format = format.replace("{color}", this.getColor().toString());
		format = format.replace("{msg}", "%2$s");
		
		Matcher matcher = msgPattern.matcher(originalFormat);
		if ((matcher.matches()) && (matcher.groupCount() == 3))
		{
			format = format.replace("{sender}", matcher.group(1) + matcher.group(2) + "%1$s" + matcher.group(3));
		}
		else
		{
			format = format.replace("{sender}", "%1$s");
		}
		
		format = format.replaceAll("(?i)&([a-fklmno0-9])", "§$1");
		return format;
	}

	@Override
	public String applyFormat(String format, String originalFormat, Player sender)
	{
		format = applyFormat(format, originalFormat);
		format = format.replace("{plainsender}", sender.getName());
		format = format.replace("{world}", sender.getWorld().getName());
		Chat chat = Herochat.getChatService();
		if (chat != null)
		{
			try
			{
				String prefix = chat.getPlayerPrefix(sender);
				String suffix = chat.getPlayerSuffix(sender);
				String group = chat.getPrimaryGroup(sender);
				String groupPrefix = group == null ? "" : chat.getGroupPrefix(sender.getWorld(), group);
				String groupSuffix = group == null ? "" : chat.getGroupSuffix(sender.getWorld(), group);
				format = format.replace("{prefix}", prefix == null ? "" : prefix.replace("%", "%%"));
				format = format.replace("{suffix}", suffix == null ? "" : suffix.replace("%", "%%"));
				format = format.replace("{group}", group == null ? "" : group.replace("%", "%%"));
				format = format.replace("{groupprefix}", groupPrefix == null ? "" : groupPrefix.replace("%", "%%"));
				format = format.replace("{groupsuffix}", groupSuffix == null ? "" : groupSuffix.replace("%", "%%"));
			}
			catch (UnsupportedOperationException ignored) {}
		}
		else
		{
			format = format.replace("{prefix}", "");
			format = format.replace("{suffix}", "");
			format = format.replace("{group}", "");
			format = format.replace("{groupprefix}", "");
			format = format.replace("{groupsuffix}", "");
		}
		format = format.replaceAll("(?i)&([a-fklmno0-9])", "§$1");
		return format;
	}

	@Override
	public void emote(Chatter sender, String message)
	{
		message = applyFormat(this.formatSupplier.getEmoteFormat(), "").replace("%2$s", message);
		Set<Player> recipients = new HashSet<Player>();
		for (Chatter member : this.getMembers())
		{
			recipients.add(member.getPlayer());
		}
		
		trimRecipients(recipients, sender);
		
		final Player player = sender.getPlayer();
		
		if (!isMessageHeard(recipients, sender))
		{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Herochat.getPlugin(), new Runnable()
			{
				public void run()
				{
					try
					{
						Messaging.send(player, Herochat.getMessage("channel_alone"));
					}
					catch (MessageNotFoundException e)
					{
						Herochat.severe("Messages.properties is missing: channel_alone");
					}
				}
			}, 1L);
		}
		else
		{
			for (Player p : recipients)
			{
				p.sendMessage(message);
			}
		}
	}
	
	@Override
	public boolean isMuted(String name)
	{
		if (this.isMuted()) return true;
		return this.getMutes().contains(name.toLowerCase());
	}
	
	
	public abstract Set<Rel> getTargetRelations();
	
	public Set<Player> getRecipients(Player sender)
	{
		Set<Player> ret = new HashSet<Player>();
		
		FPlayer fpsender = FPlayers.i.get(sender);
		Faction faction = fpsender.getFaction();		
		ret.addAll(faction.getOnlinePlayers());
		
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			if(this.getTargetRelations().contains(faction.getRelationTo(fplayer)))
			{
				ret.add(fplayer.getPlayer());
			}
		}
		
		return ret;
	}
	
	
	@Override
	public void processChat(ChannelChatEvent event)
	{
		final Player player = event.getSender().getPlayer();

		String format = applyFormat(event.getFormat(), event.getBukkitFormat(), player);

		Chatter sender = Herochat.getChatterManager().getChatter(player);
		Set<Player> recipients = this.getRecipients(player);

		trimRecipients(recipients, sender);
		String msg = String.format(format, player.getDisplayName(), event.getMessage());
		if (!isMessageHeard(recipients, sender))
		{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Herochat.getPlugin(), new Runnable()
			{
				public void run()
				{
					try
					{
						Messaging.send(player, Herochat.getMessage("channel_alone"));
					}
					catch (MessageNotFoundException e)
					{
						Herochat.severe("Messages.properties is missing: channel_alone");
					}
				}
			}, 1L);
		}

		for (Player recipient : recipients)
		{
			recipient.sendMessage(msg);
		}

		Herochat.logChat(msg);
	}
	
	/*@Override
	public void processChat(ChannelChatEvent event)
	{
		final Player player = event.getSender().getPlayer();

		String format = applyFormat(event.getFormat(), event.getBukkitFormat(), player);

		Chatter sender = Herochat.getChatterManager().getChatter(player);
		Set<Player> recipients = new HashSet<Player>(Arrays.asList(Bukkit.getOnlinePlayers()));

		trimRecipients(recipients, sender);
		if (!isMessageHeard(recipients, sender))
		{
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Herochat.getPlugin(), new Runnable()
			{
				public void run()
				{
					try
					{
						Messaging.send(player, Herochat.getMessage("channel_alone"));
					}
					catch (MessageNotFoundException e)
					{
						Herochat.severe("Messages.properties is missing: channel_alone");
					}
				}
			}, 1L);
		}
		
		FPlayer fplayer = FPlayers.i.get(player);
		
		String formatWithoutColor = FactionsChatListener.parseTags(format, player, fplayer);
		
		//String msg = String.format(format, player.getDisplayName(), event.getMessage());
		

		for (Player recipient : recipients)
		{
			String finalFormat;
			if ( ! Conf.chatParseTags || Conf.chatTagHandledByAnotherPlugin)
			{
				finalFormat = format;
			}
			else if (! Conf.chatParseTagsColored)
			{
				finalFormat = formatWithoutColor;
			}
			else
			{
				FPlayer frecipient = FPlayers.i.get(recipient);
				finalFormat = FactionsChatListener.parseTags(format, player, fplayer, recipient, frecipient);
			}
			String msg = String.format(finalFormat, player.getDisplayName(), event.getMessage());
			recipient.sendMessage(msg);
		}

		Herochat.logChat(String.format(formatWithoutColor, player.getDisplayName(), event.getMessage()));
	}*/
	
	public boolean isMessageHeard(Set<Player> recipients, Chatter sender)
	{
		if (!isLocal()) return true;
		
		Player senderPlayer = sender.getPlayer();
		for (Player recipient : recipients)
		{
			if (recipient.equals(senderPlayer)) continue;
			if (recipient.hasPermission("herochat.admin.stealth")) continue;
			return true;
		}
		
		return false;
	}

	public void trimRecipients(Set<Player> recipients, Chatter sender)
	{
		World world = sender.getPlayer().getWorld();
		
		Set<Chatter> members = this.getMembers();
		Iterator<Player> iterator = recipients.iterator();
		while(iterator.hasNext())
		{
			Chatter recipient = Herochat.getChatterManager().getChatter(iterator.next());
			if (recipient == null) continue;
			World recipientWorld = recipient.getPlayer().getWorld();
			
			if (!members.contains(recipient))
				iterator.remove();
			else if ((isLocal()) && (!sender.isInRange(recipient, this.getDistance())))
				iterator.remove();
			else if (!hasWorld(recipientWorld))
				iterator.remove();
			else if (recipient.isIgnoring(sender))
				iterator.remove();
			else if ((!this.isCrossWorld()) && (!world.equals(recipientWorld)))
				iterator.remove();
		}
	}
	
	public boolean equals(Object other)
	{
		if (other == this) return true;
		if (other == null) return false;
		if (!(other instanceof Channel)) return false;
		Channel channel = (Channel)other;
		return (this.getName().equalsIgnoreCase(channel.getName())) || (this.getName().equalsIgnoreCase(channel.getNick()));
	}

	public int hashCode()
	{
		int prime = 31;
		int result = 1;
		result = prime * result + (this.getName() == null ? 0 : this.getName().toLowerCase().hashCode());
		result = prime * result + (this.getNick() == null ? 0 : this.getNick().toLowerCase().hashCode());
		return result;
	}
	
	@Override public boolean isTransient() { return false; }
	@Override public String getPassword() { return ""; }
	@Override public void setPassword(String password) {}
	@Override public boolean isVerbose() { return false; }
	@Override public void setVerbose(boolean verbose) {}
	@Override public boolean isHidden() { return false; }
	@Override public boolean isLocal() { return this.getDistance() != 0; }
	@Override public void attachStorage(ChannelStorage storage) { }
	@Override public boolean banMember(Chatter chatter, boolean announce) { return false; }
	@Override public Set<String> getBans() { return Collections.emptySet(); }
	@Override public Set<String> getModerators() { return Collections.emptySet(); }
	@Override public Set<String> getMutes() { return Collections.emptySet(); }
	@Override public ChannelStorage getStorage() { return this.storage; }
	@Override public boolean hasWorld(String world) { return (this.getWorlds().isEmpty()) || (this.getWorlds().contains(world)); }
	@Override public boolean hasWorld(World world) { return this.hasWorld(world.getName()); }
	@Override public boolean isBanned(String name) { return this.getBans().contains(name.toLowerCase()); }
	@Override public boolean isMember(Chatter chatter) { return this.getMembers().contains(chatter); }
	@Override public boolean isModerator(String name) { return this.getModerators().contains(name.toLowerCase()); }
	
	@Override public void onFocusGain(Chatter chatter) {}
	@Override public void onFocusLoss(Chatter chatter) {}
	
	
	
	@Override public void removeWorld(String world) { this.getWorlds().remove(world); }
	@Override public void setBanned(String name, boolean banned) {}
	@Override public void setBans(Set<String> bans) {}
	@Override public void setModerator(String name, boolean moderator) {}
	@Override public void setModerators(Set<String> moderators) { }
	@Override public void setMuted(String name, boolean muted) {}
	@Override public void setMutes(Set<String> mutes) {}
}
