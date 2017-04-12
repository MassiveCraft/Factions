package com.massivecraft.factions.integration.herochat;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.ChannelStorage;
import com.dthielke.herochat.ChatCompleteEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import com.dthielke.herochat.MessageFormatSupplier;
import com.dthielke.herochat.MessageNotFoundException;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.util.MUtil;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ChannelFactionsAbstract implements Channel
{
	private static final Pattern msgPattern = Pattern.compile("(.*)<(.*)%1\\$s(.*)> %2\\$s");
	private final ChannelStorage storage = Herochat.getChannelManager().getStorage();
	private final MessageFormatSupplier formatSupplier = Herochat.getChannelManager();
	
	@Override
	public boolean addMember(Chatter chatter, boolean announce, boolean flagUpdate)
	{
		if (chatter.hasChannel(this)) return false;
		
		if ((announce) && (this.isVerbose()))
		{
			try
			{
				this.announce(Herochat.getMessage("channel_join").replace("$1", chatter.getPlayer().getDisplayName()));
			}
			catch (MessageNotFoundException e)
			{
				Herochat.severe("Messages.properties is missing: channel_join");
			}
		}
		
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
				
		if (announce && this.isVerbose())
		{
			try
			{
				this.announce(Herochat.getMessage("channel_leave").replace("$1", chatter.getPlayer().getDisplayName()));
			}
			catch (MessageNotFoundException e)
			{
				Herochat.severe("Messages.properties is missing: channel_leave");
			}
		}
		
		return true;
	}
	
	@Override
	public Set<Chatter> getMembers()
	{
		Set<Chatter> ret = new HashSet<>();
		for (Chatter chatter : Herochat.getChatterManager().getChatters())
		{
			if(chatter.hasChannel(this)) ret.add(chatter);
		}
		return ret;
	}

	@Override
	public void announce(String message)
	{
		String colorized = message.replaceAll("(?i)&([a-fklmno0-9])", "§$1");
		message = applyFormat(this.getFormatSupplier().getAnnounceFormat(), "").replace("%2$s", colorized);
		for (Chatter member : this.getMembers())
		{
			member.getPlayer().sendMessage(message);
		}
		Herochat.logChat(ChatColor.stripColor(message));
	}

	@Override
	public String applyFormat(String format, String originalFormat)
	{
		format = format.replace("{default}", this.getFormatSupplier().getStandardFormat());
		format = format.replace("{name}", this.getName());
		format = format.replace("{nick}", this.getNick());
		format = format.replace("{color}", this.getColor().toString());
		format = format.replace("{msg}", "%2$s");
		
		Matcher matcher = msgPattern.matcher(originalFormat);
		if (matcher.matches() && matcher.groupCount() == 3)
		{
			format = format.replace("{sender}", matcher.group(1) + matcher.group(2) + "%1$s" + matcher.group(3));
		}
		else
		{
			format = format.replace("{sender}", "%1$s");
		}
		
		format = format.replaceAll("(?i)&([a-fklmnor0-9])", "§$1");
		return format;
	}

	@Override
	public String applyFormat(String format, String originalFormat, Player sender)
	{
		format = this.applyFormat(format, originalFormat);
		format = format.replace("{plainsender}", sender.getName());
		format = format.replace("{world}", sender.getWorld().getName());
		Chat chat = Herochat.getChatService();
		if (chat != null)
		{
			try
			{
				String prefix = chat.getPlayerPrefix(sender);
				if (prefix == null || prefix == "")
				{
					prefix = chat.getPlayerPrefix((String)null, sender.getName());
				}
				String suffix = chat.getPlayerSuffix(sender);
				if (suffix == null || suffix == "")
				{
					suffix = chat.getPlayerSuffix((String)null, sender.getName());
				}
				String group = chat.getPrimaryGroup(sender);
				String groupPrefix = group == null ? "" : chat.getGroupPrefix(sender.getWorld(), group);
				if (group != null && (groupPrefix == null || groupPrefix == ""))
				{
					groupPrefix = chat.getGroupPrefix((String)null, group);
				}
				String groupSuffix = group == null ? "" : chat.getGroupSuffix(sender.getWorld(), group);
				if (group != null && (groupSuffix == null || groupSuffix == ""))
				{
					groupSuffix = chat.getGroupSuffix((String)null, group);
				}
				format = format.replace("{prefix}", prefix == null ? "" : prefix.replace("%", "%%"));
				format = format.replace("{suffix}", suffix == null ? "" : suffix.replace("%", "%%"));
				format = format.replace("{group}", group == null ? "" : group.replace("%", "%%"));
				format = format.replace("{groupprefix}", groupPrefix == null ? "" : groupPrefix.replace("%", "%%"));
				format = format.replace("{groupsuffix}", groupSuffix == null ? "" : groupSuffix.replace("%", "%%"));
			} 
			catch (UnsupportedOperationException ignored)
			{
	
			}
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
		message = this.applyFormat(this.getFormatSupplier().getEmoteFormat(), "").replace("%2$s", message);
		
		Set<Player> recipients = new HashSet<>();
		for (Chatter member : this.getMembers())
		{
			recipients.add(member.getPlayer());
		}
		
		this.trimRecipients(recipients, sender);
		
		for (Player recipient : recipients)
		{
			recipient.sendMessage(message);
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
		Set<Player> ret = new HashSet<>();
		
		MPlayer fsender = MPlayer.get(sender);
		Faction faction = fsender.getFaction();
		
		for (Player player : MUtil.getOnlinePlayers())
		{
			MPlayer frecipient = MPlayer.get(player);
			if (!this.getTargetRelations().contains(faction.getRelationTo(frecipient))) continue;
			ret.add(player);
		}
		
		return ret;
	}
	
	@Override
	public void processChat(ChannelChatEvent event)
	{
		Player player = event.getSender().getPlayer();
		
		String format = this.applyFormat(event.getFormat(), event.getBukkitFormat(), player);
		
		Chatter sender = Herochat.getChatterManager().getChatter(player);
		
		// NOTE: This line is not standard deobfuscation. It's altered to achieve the recipient limitations.
		Set<Player> recipients = this.getRecipients(player);
		
		this.trimRecipients(recipients, sender);
		String msg = String.format(format, new Object[] { player.getDisplayName(), event.getMessage() });
		for (Player recipient : recipients)
		{
			recipient.sendMessage(msg);
		}
		
		Bukkit.getPluginManager().callEvent(new ChatCompleteEvent(sender, this, msg));
		Herochat.logChat(msg);
	}
	
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
	
	@Override
	public MessageFormatSupplier getFormatSupplier()
	{
		return this.formatSupplier;
	}
	
	@Override
	public void sendRawMessage(String rawMessage)
	{
		for (Chatter member : this.getMembers())
		{
			member.getPlayer().sendMessage(rawMessage);
		}
	}
}
