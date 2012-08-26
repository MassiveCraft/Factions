package com.massivecraft.factions.integration.herochat;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.Rel;

public class AlliesChannel extends FactionsChannelAbstract
{
	public static final Set<Rel> targetRelations = EnumSet.of(Rel.MEMBER, Rel.ALLY);
	@Override public Set<Rel> getTargetRelations() { return targetRelations; }
	
	@Override public String getName() { return Conf.herochatAlliesName; }
	
	@Override public String getNick() { return Conf.herochatAlliesNick; }
	@Override public void setNick(String nick) { Conf.herochatAlliesNick = nick; }
	
	@Override public String getFormat() { return Conf.herochatAlliesFormat; }
	@Override public void setFormat(String format) { Conf.herochatAlliesFormat = format; }
	
	@Override public ChatColor getColor() { return Conf.herochatAlliesColor; }
	@Override public void setColor(ChatColor color) { Conf.herochatAlliesColor = color; }
	
	@Override public int getDistance() { return Conf.herochatAlliesDistance; }
	@Override public void setDistance(int distance) { Conf.herochatAlliesDistance = distance; }
	
	@Override public void addWorld(String world) { Conf.herochatAlliesWorlds.add(world); }
	@Override public Set<String> getWorlds() { return Conf.herochatAlliesWorlds; }
	@Override public void setWorlds(Set<String> worlds) { Conf.herochatAlliesWorlds = worlds; }
	
	@Override public boolean isShortcutAllowed() { return Conf.herochatAlliesIsShortcutAllowed; }
	@Override public void setShortcutAllowed(boolean shortcutAllowed) { Conf.herochatAlliesIsShortcutAllowed = shortcutAllowed; }
	
	@Override public boolean isCrossWorld() { return Conf.herochatAlliesCrossWorld; }
	@Override public void setCrossWorld(boolean crossWorld) { Conf.herochatAlliesCrossWorld = crossWorld; }
	
	@Override public boolean isMuted() { return Conf.herochatAlliesMuted; }
	@Override public void setMuted(boolean value) { Conf.herochatAlliesMuted = value; }
}
