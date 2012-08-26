package com.massivecraft.factions.integration.herochat;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Rel;

public class FactionChannel extends FactionsChannelAbstract
{
	public static final Set<Rel> targetRelations = EnumSet.of(Rel.MEMBER);
	@Override public Set<Rel> getTargetRelations() { return targetRelations; }
	
	@Override public String getName() { return Conf.herochatFactionName; }
	
	@Override public String getNick() { return Conf.herochatFactionNick; }
	@Override public void setNick(String nick) { Conf.herochatFactionNick = nick; }
	
	@Override public String getFormat() { return Conf.herochatFactionFormat; }
	@Override public void setFormat(String format) { Conf.herochatFactionFormat = format; }
	
	@Override public ChatColor getColor() { return Conf.herochatFactionColor; }
	@Override public void setColor(ChatColor color) { Conf.herochatFactionColor = color; }
	
	@Override public int getDistance() { return Conf.herochatFactionDistance; }
	@Override public void setDistance(int distance) { Conf.herochatFactionDistance = distance; }
	
	@Override public void addWorld(String world) { Conf.herochatFactionWorlds.add(world); }
	@Override public Set<String> getWorlds() { return new HashSet<String>(Conf.herochatFactionWorlds); }
	@Override public void setWorlds(Set<String> worlds) { Conf.herochatFactionWorlds = worlds; }
	
	@Override public boolean isShortcutAllowed() { return Conf.herochatFactionIsShortcutAllowed; }
	@Override public void setShortcutAllowed(boolean shortcutAllowed) { Conf.herochatFactionIsShortcutAllowed = shortcutAllowed; }
	
	@Override public boolean isCrossWorld() { return Conf.herochatFactionCrossWorld; }
	@Override public void setCrossWorld(boolean crossWorld) { Conf.herochatFactionCrossWorld = crossWorld; }
	
	@Override public boolean isMuted() { return Conf.herochatFactionMuted; }
	@Override public void setMuted(boolean value) { Conf.herochatFactionMuted = value; }
}
