package com.massivecraft.factions;

import org.bukkit.ChatColor;


public interface RelationParticipator
{
	String describeTo(RelationParticipator observer);
	String describeTo(RelationParticipator observer, boolean ucfirst);
	
	Rel getRelationTo(RelationParticipator observer);
	Rel getRelationTo(RelationParticipator observer, boolean ignorePeaceful);
	
	ChatColor getColorTo(RelationParticipator observer);
}
