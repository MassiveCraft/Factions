package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

import com.massivecraft.factions.Conf;
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

	public static Rel getRelationOfThatToMe(RelationParticipator that, RelationParticipator me)
	{
		return getRelationOfThatToMe(me, that, false);
	}

	public static Rel getRelationOfThatToMe(RelationParticipator that, RelationParticipator me, boolean ignorePeaceful)
	{
		Rel ret = null;
		
		Faction fthat = getFaction(me);
		if (fthat == null) return Rel.NEUTRAL; // ERROR

		Faction fme = getFaction(that);
		if (fme == null) return Rel.NEUTRAL; // ERROR
		
		// The faction with the lowest wish "wins"
		if (fme.getRelationWish(fthat).isLessThan(fthat.getRelationWish(fme)))
		{
			ret = fme.getRelationWish(fthat);
		}
		else
		{
			ret = fthat.getRelationWish(fme);
		}

		if (fthat.equals(fme))
		{
			ret = Rel.MEMBER;
			// Do officer and leader check
			if (that instanceof FPlayer)
			{
				ret = ((FPlayer)that).getRole();
			}
		}
		else if (!ignorePeaceful && (fme.getFlag(FFlag.PEACEFUL) || fthat.getFlag(FFlag.PEACEFUL)))
		{
			ret = Rel.TRUCE;
		}

		return ret;
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
		Faction thatFaction = getFaction(that);
		if (thatFaction != null && thatFaction != getFaction(me))
		{
			if (thatFaction.getFlag(FFlag.FRIENDLYFIRE) == true)
			{
				return Conf.colorFriendlyFire;
			}
			
			if (thatFaction.getFlag(FFlag.PVP) == false)
			{
				return Conf.colorNoPVP;
			}
		}
		return getRelationOfThatToMe(that, me).getColor();
	}
}
