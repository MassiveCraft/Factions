package com.massivecraft.factions.integration.herochat;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.struct.Rel;

public class FactionChannel extends FactionsChannelAbstract
{
	public static final Set<Rel> targetRelations = EnumSet.of(Rel.MEMBER);
	@Override public Set<Rel> getTargetRelations() { return targetRelations; }
	
	@Override public String getName() { return ConfServer.herochatFactionName; }
	
	@Override public String getNick() { return ConfServer.herochatFactionNick; }
	@Override public void setNick(String nick) { ConfServer.herochatFactionNick = nick; }
	
	@Override public String getFormat() { return ConfServer.herochatFactionFormat; }
	@Override public void setFormat(String format) { ConfServer.herochatFactionFormat = format; }
	
	@Override public ChatColor getColor() { return ConfServer.herochatFactionColor; }
	@Override public void setColor(ChatColor color) { ConfServer.herochatFactionColor = color; }
	
	@Override public int getDistance() { return ConfServer.herochatFactionDistance; }
	@Override public void setDistance(int distance) { ConfServer.herochatFactionDistance = distance; }
	
	@Override public void addWorld(String world) { ConfServer.herochatFactionWorlds.add(world); }
	@Override public Set<String> getWorlds() { return new HashSet<String>(ConfServer.herochatFactionWorlds); }
	@Override public void setWorlds(Set<String> worlds) { ConfServer.herochatFactionWorlds = worlds; }
	
	@Override public boolean isShortcutAllowed() { return ConfServer.herochatFactionIsShortcutAllowed; }
	@Override public void setShortcutAllowed(boolean shortcutAllowed) { ConfServer.herochatFactionIsShortcutAllowed = shortcutAllowed; }
	
	@Override public boolean isCrossWorld() { return ConfServer.herochatFactionCrossWorld; }
	@Override public void setCrossWorld(boolean crossWorld) { ConfServer.herochatFactionCrossWorld = crossWorld; }
	
	@Override public boolean isMuted() { return ConfServer.herochatFactionMuted; }
	@Override public void setMuted(boolean value) { ConfServer.herochatFactionMuted = value; }
}
