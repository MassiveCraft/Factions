package com.massivecraft.factions.integration.herochat;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.MConf;
import org.bukkit.ChatColor;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class ChannelFactionsFaction extends ChannelFactionsAbstract
{
	public static final Set<Rel> targetRelations = EnumSet.of(Rel.MEMBER, Rel.RECRUIT);
	@Override public Set<Rel> getTargetRelations() { return targetRelations; }
	
	@Override public String getName() { return MConf.get().herochatFactionName; }
	
	@Override public String getNick() { return MConf.get().herochatFactionNick; }
	@Override public void setNick(String nick) { MConf.get().herochatFactionNick = nick; }
	
	@Override public String getFormat() { return MConf.get().herochatFactionFormat; }
	@Override public void setFormat(String format) { MConf.get().herochatFactionFormat = format; }
	
	@Override public ChatColor getColor() { return MConf.get().herochatFactionColor; }
	@Override public void setColor(ChatColor color) { MConf.get().herochatFactionColor = color; }
	
	@Override public int getDistance() { return MConf.get().herochatFactionDistance; }
	@Override public void setDistance(int distance) { MConf.get().herochatFactionDistance = distance; }
	
	@Override public void addWorld(String world) { MConf.get().herochatFactionWorlds.add(world); }
	@Override public Set<String> getWorlds() { return new HashSet<>(MConf.get().herochatFactionWorlds); }
	@Override public void setWorlds(Set<String> worlds) { MConf.get().herochatFactionWorlds = worlds; }
	
	@Override public boolean isShortcutAllowed() { return MConf.get().herochatFactionIsShortcutAllowed; }
	@Override public void setShortcutAllowed(boolean shortcutAllowed) { MConf.get().herochatFactionIsShortcutAllowed = shortcutAllowed; }
	
	@Override public boolean isCrossWorld() { return MConf.get().herochatFactionCrossWorld; }
	@Override public void setCrossWorld(boolean crossWorld) { MConf.get().herochatFactionCrossWorld = crossWorld; }
	
	@Override public boolean isMuted() { return MConf.get().herochatFactionMuted; }
	@Override public void setMuted(boolean value) { MConf.get().herochatFactionMuted = value; }
}
