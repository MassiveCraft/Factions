package com.massivecraft.factions.integration.herochat;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.ChatColor;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.struct.Rel;

public class AlliesChannel extends FactionsChannelAbstract
{
	public static final Set<Rel> targetRelations = EnumSet.of(Rel.MEMBER, Rel.ALLY);
	@Override public Set<Rel> getTargetRelations() { return targetRelations; }
	
	@Override public String getName() { return ConfServer.herochatAlliesName; }
	
	@Override public String getNick() { return ConfServer.herochatAlliesNick; }
	@Override public void setNick(String nick) { ConfServer.herochatAlliesNick = nick; }
	
	@Override public String getFormat() { return ConfServer.herochatAlliesFormat; }
	@Override public void setFormat(String format) { ConfServer.herochatAlliesFormat = format; }
	
	@Override public ChatColor getColor() { return ConfServer.herochatAlliesColor; }
	@Override public void setColor(ChatColor color) { ConfServer.herochatAlliesColor = color; }
	
	@Override public int getDistance() { return ConfServer.herochatAlliesDistance; }
	@Override public void setDistance(int distance) { ConfServer.herochatAlliesDistance = distance; }
	
	@Override public void addWorld(String world) { ConfServer.herochatAlliesWorlds.add(world); }
	@Override public Set<String> getWorlds() { return ConfServer.herochatAlliesWorlds; }
	@Override public void setWorlds(Set<String> worlds) { ConfServer.herochatAlliesWorlds = worlds; }
	
	@Override public boolean isShortcutAllowed() { return ConfServer.herochatAlliesIsShortcutAllowed; }
	@Override public void setShortcutAllowed(boolean shortcutAllowed) { ConfServer.herochatAlliesIsShortcutAllowed = shortcutAllowed; }
	
	@Override public boolean isCrossWorld() { return ConfServer.herochatAlliesCrossWorld; }
	@Override public void setCrossWorld(boolean crossWorld) { ConfServer.herochatAlliesCrossWorld = crossWorld; }
	
	@Override public boolean isMuted() { return ConfServer.herochatAlliesMuted; }
	@Override public void setMuted(boolean value) { ConfServer.herochatAlliesMuted = value; }
}
