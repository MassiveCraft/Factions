package com.massivecraft.factions.integration.herochat;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.MConf;
import org.bukkit.ChatColor;

import java.util.EnumSet;
import java.util.Set;

public class ChannelFactionsAllies extends ChannelFactionsAbstract
{
	public static final Set<Rel> targetRelations = EnumSet.of(Rel.MEMBER, Rel.RECRUIT, Rel.ALLY);
	@Override public Set<Rel> getTargetRelations() { return targetRelations; }
	
	@Override public String getName() { return MConf.get().herochatAlliesName; }
	
	@Override public String getNick() { return MConf.get().herochatAlliesNick; }
	@Override public void setNick(String nick) { MConf.get().herochatAlliesNick = nick; }
	
	@Override public String getFormat() { return MConf.get().herochatAlliesFormat; }
	@Override public void setFormat(String format) { MConf.get().herochatAlliesFormat = format; }
	
	@Override public ChatColor getColor() { return MConf.get().herochatAlliesColor; }
	@Override public void setColor(ChatColor color) { MConf.get().herochatAlliesColor = color; }
	
	@Override public int getDistance() { return MConf.get().herochatAlliesDistance; }
	@Override public void setDistance(int distance) { MConf.get().herochatAlliesDistance = distance; }
	
	@Override public void addWorld(String world) { MConf.get().herochatAlliesWorlds.add(world); }
	@Override public Set<String> getWorlds() { return MConf.get().herochatAlliesWorlds; }
	@Override public void setWorlds(Set<String> worlds) { MConf.get().herochatAlliesWorlds = worlds; }
	
	@Override public boolean isShortcutAllowed() { return MConf.get().herochatAlliesIsShortcutAllowed; }
	@Override public void setShortcutAllowed(boolean shortcutAllowed) { MConf.get().herochatAlliesIsShortcutAllowed = shortcutAllowed; }
	
	@Override public boolean isCrossWorld() { return MConf.get().herochatAlliesCrossWorld; }
	@Override public void setCrossWorld(boolean crossWorld) { MConf.get().herochatAlliesCrossWorld = crossWorld; }
	
	@Override public boolean isMuted() { return MConf.get().herochatAlliesMuted; }
	@Override public void setMuted(boolean value) { MConf.get().herochatAlliesMuted = value; }
}
