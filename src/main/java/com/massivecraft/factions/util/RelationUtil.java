package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.util.Txt;

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
//		if (myFaction == null) return thatFaction.getTag(); // no relation, but can show basic name or tag

		if (that instanceof Faction)
		{
			if (me instanceof UPlayer && myFaction == thatFaction)
			{
				ret = "your faction";
			}
			else
			{
				ret = thatFaction.getName();
			}
		}
		else if (that instanceof UPlayer)
		{
			UPlayer uplayerthat = (UPlayer) that;
			if (that == me)
			{
				ret = "you";
			}
			else if (thatFaction == myFaction)
			{
				ret = uplayerthat.getNameAndTitle(myFaction);
			}
			else
			{
				ret = uplayerthat.getNameAndFactionName();
			}
		}

		if (ucfirst)
		{
			ret = Txt.upperCaseFirst(ret);
		}

		return "" + getColorOfThatToMe(that, me) + ret;
	}

	public static String describeThatToMe(RelationParticipator that, RelationParticipator me)
	{
		return describeThatToMe(that, me, false);
	}

	public static Rel getRelationOfThatToMe(RelationParticipator that, RelationParticipator me)
	{
		return getRelationOfThatToMe(that, me, false);
	}

	public static Rel getRelationOfThatToMe(RelationParticipator that, RelationParticipator me, boolean ignorePeaceful)
	{
		Rel ret = null;
		
		Faction myFaction = getFaction(me);
		if (myFaction == null) return Rel.NEUTRAL; // ERROR

		Faction thatFaction = getFaction(that);
		if (thatFaction == null) return Rel.NEUTRAL; // ERROR
		
		// The faction with the lowest wish "wins"
		if (thatFaction.getRelationWish(myFaction).isLessThan(myFaction.getRelationWish(thatFaction)))
		{
			ret = thatFaction.getRelationWish(myFaction);
		}
		else
		{
			ret = myFaction.getRelationWish(thatFaction);
		}

		if (myFaction.equals(thatFaction))
		{
			ret = Rel.MEMBER;
			// Do officer and leader check
			//P.p.log("getRelationOfThatToMe the factions are the same for "+that.getClass().getSimpleName()+" and observer "+me.getClass().getSimpleName());
			if (that instanceof UPlayer)
			{
				ret = ((UPlayer)that).getRole();
				//P.p.log("getRelationOfThatToMe it was a player and role is "+ret);
			}
		}
		else if (!ignorePeaceful && (thatFaction.getFlag(FFlag.PEACEFUL) || myFaction.getFlag(FFlag.PEACEFUL)))
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

		if (rp instanceof UPlayer)
		{
			return ((UPlayer) rp).getFaction();
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
				return MConf.get().colorFriendlyFire;
			}
			
			if (thatFaction.getFlag(FFlag.PVP) == false)
			{
				return MConf.get().colorNoPVP;
			}
		}
		return getRelationOfThatToMe(that, me).getColor();
	}
}
