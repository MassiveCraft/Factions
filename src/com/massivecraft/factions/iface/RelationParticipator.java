package com.massivecraft.factions.iface;

import org.bukkit.ChatColor;
import com.massivecraft.factions.struct.Rel;

public interface RelationParticipator
{
	public String describeTo(RelationParticipator that);
	public String describeTo(RelationParticipator that, boolean ucfirst);
	
	public Rel getRelationTo(RelationParticipator that);
	public Rel getRelationTo(RelationParticipator that, boolean ignorePeaceful);
	
	public ChatColor getColorTo(RelationParticipator to);
}
