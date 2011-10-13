package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TextUtil;

public class RelationUtil
{
	public static String describeThatToMe(RelationParticipator that, RelationParticipator me, boolean ucfirst)
	{
		String ret = "";

		Faction thatFaction = getFaction(that);
		if (thatFaction == null) return "ERROR"; // ERROR

		Faction myFaction = getFaction(me);
		if (myFaction == null) return "ERROR"; // ERROR

		if (that instanceof Faction)
		{
			if (me instanceof FPlayer && myFaction == thatFaction)
			{
				ret = "your faction";
			}
			else
			{
				ret = "the faction " + thatFaction.getTag();
			}
		}
		else if (that instanceof FPlayer)
		{
			FPlayer fplayerthat = (FPlayer) that;
			if (that == me)
			{
				ret = "you";
			}
			else if (thatFaction == myFaction)
			{
				ret = fplayerthat.getNameAndTitle();
			}
			else
			{
				ret = fplayerthat.getNameAndTag();
			}
		}

		if (ucfirst)
		{
			ret = TextUtil.upperCaseFirst(ret);
		}

		return "" + getRelationColor(me, that) + ret;
	}

	public static String describeThatToMe(RelationParticipator that, RelationParticipator me)
	{
		return describeThatToMe(that, me, false);
	}

	public static Relation getRelationTo(RelationParticipator me, RelationParticipator that)
	{
		return getRelationTo(that, me, false);
	}

	public static Relation getRelationTo(RelationParticipator me, RelationParticipator that, boolean ignorePeaceful)
	{
		Faction fthat = getFaction(that);
		if (fthat == null) return Relation.NEUTRAL; // ERROR

		Faction fme = getFaction(me);
		if (fme == null) return Relation.NEUTRAL; // ERROR

		if (!fthat.isNormal() || !fme.isNormal())
		{
			return Relation.NEUTRAL;
		}

		if (fthat.equals(fme))
		{
			return Relation.MEMBER;
		}

		if (!ignorePeaceful && (fme.isPeaceful() || fthat.isPeaceful()))
		{
			return Relation.NEUTRAL;
		}

		if (fme.getRelationWish(fthat).value >= fthat.getRelationWish(fme).value)
		{
			return fthat.getRelationWish(fme);
		}

		return fme.getRelationWish(fthat);
	}

	public static Faction getFaction(RelationParticipator rp)
	{
		if (rp instanceof Faction)
		{
			return (Faction) rp;
		}

		if (rp instanceof FPlayer)
		{
			return ((FPlayer) rp).getFaction();
		}

		// ERROR
		return null;
	}

	public static ChatColor getRelationColor(RelationParticipator me,
			RelationParticipator that)
	{
		return getRelationTo(that, me).getColor();
	}
}
