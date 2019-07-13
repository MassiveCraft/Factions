package com.massivecraft.factions.iface;

import com.massivecraft.factions.struct.Relation;
import org.bukkit.ChatColor;

public interface RelationParticipator {

    String describeTo(RelationParticipator that);

    String describeTo(RelationParticipator that, boolean ucfirst);

    Relation getRelationTo(RelationParticipator that);

    Relation getRelationTo(RelationParticipator that, boolean ignorePeaceful);

    ChatColor getColorTo(RelationParticipator to);
}
