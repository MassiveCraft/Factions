package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.zcore.util.TextUtil;

public class RelationUtil
{
	public static String describeThatToMe(RelationParticipator that, RelationParticipator me, boolean ucfirst)
	{
		String ret = "";

		if (that == null)
		{
			return "A server admin";
		}
		
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
				ret = thatFaction.getTag();
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

		return "" + getColorOfThatToMe(that, me) + ret;
	}

	public static String describeThatToMe(RelationParticipator that, RelationParticipator me)
	{
		return describeThatToMe(that, me, false);
	}

	public static Rel getRelationTo(RelationParticipator me, RelationParticipator that)
	{
		return getRelationTo(that, me, false);
	}

	public static Rel getRelationTo(RelationParticipator me, RelationParticipator that, boolean ignorePeaceful)
	{
		Faction fthat = getFaction(that);
		if (fthat == null) return Rel.NEUTRAL; // ERROR

		Faction fme = getFaction(me);
		if (fme == null) return Rel.NEUTRAL; // ERROR

		if (!fthat.isNormal() || !fme.isNormal())
		{
			return Rel.NEUTRAL;
		}

		if (fthat.equals(fme))
		{
			return Rel.MEMBER;
		}

		if (!ignorePeaceful && (fme.getFlag(FFlag.PEACEFUL) || fthat.getFlag(FFlag.PEACEFUL)))
		{
			return Rel.TRUCE;
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

	public static ChatColor getColorOfThatToMe(RelationParticipator that, RelationParticipator me)
	{
		//Faction thatFaction = getFaction(that);
		// TODO: Add special colors to zone as a feature to replace this one
		/*if (thatFaction != null)
		{
			if (thatFaction.isPeaceful() && thatFaction != getFaction(me))
			{
				return Conf.colorPeaceful;
			}
			
			if (thatFaction.isSafeZone() && thatFaction != getFaction(me))
			{
				return Conf.colorPeaceful;
			}
			
			if (thatFaction.isWarZone() && thatFaction != getFaction(me))
			{
				return Conf.colorWar;
			}
		}*/
		
		return getRelationTo(that, me).getColor();
	}
}
