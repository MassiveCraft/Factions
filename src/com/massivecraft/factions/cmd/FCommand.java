package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.MCommand;
import com.massivecraft.mcore.util.Txt;

public abstract class FCommand extends MCommand
{
	public UPlayer fme;
	public Faction myFaction;

	@Override
	public void fixSenderVars()
	{
		this.fme = UPlayer.get(this.sender);
		this.myFaction = this.fme.getFaction();
	}

	// -------------------------------------------- //
	// COMMONLY USED LOGIC
	// -------------------------------------------- //

	public boolean canIAdministerYou(UPlayer i, UPlayer you)
	{
		if ( ! i.getFaction().equals(you.getFaction()))
		{
			i.sendMessage(Txt.parse("%s <b>is not in the same faction as you.",you.describeTo(i, true)));
			return false;
		}

		if (i.getRole().isMoreThan(you.getRole()) || i.getRole().equals(Rel.LEADER) )
		{
			return true;
		}

		if (you.getRole().equals(Rel.LEADER))
		{
			i.sendMessage(Txt.parse("<b>Only the faction leader can do that."));
		}
		else if (i.getRole().equals(Rel.OFFICER))
		{
			if ( i == you )
			{
				return true; //Moderators can control themselves
			}
			else
			{
				i.sendMessage(Txt.parse("<b>Moderators can't control each other..."));
			}
		}
		else
		{
			i.sendMessage(Txt.parse("<b>You must be a faction moderator to do that."));
		}

		return false;
	}
}
