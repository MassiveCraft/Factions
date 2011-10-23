package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class CmdKick extends FCommand
{
	
	public CmdKick()
	{
		super();
		this.aliases.add("kick");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.KICK.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = true;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{	
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (fme == you)
		{
			msg("<b>You cannot kick yourself.");
			msg("<i>You might want to: %s", p.cmdBase.cmdLeave.getUseageTemplate(false));
			return;
		}

		Faction yourFaction = you.getFaction();

		// players with admin-level "disband" permission can bypass these requirements
		if ( ! Permission.KICK_ANY.has(sender))
		{
			if (yourFaction != myFaction)
			{
				msg("%s<b> is not a member of %s", you.describeTo(fme, true), myFaction.describeTo(fme));
				return;
			}

			if (you.getRole().value >= fme.getRole().value)
			{
				// TODO add more informative messages.
				msg("<b>Your rank is too low to kick this player.");
				return;
			}

			if ( ! Conf.canLeaveWithNegativePower && you.getPower() < 0)
			{
				msg("<b>You cannot kick that member until their power is positive.");
				return;
			}
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostKick, "to kick someone from the faction", "for kicking someone from the faction")) return;

		yourFaction.msg("%s<i> kicked %s<i> from the faction! :O", fme.describeTo(yourFaction, true), you.describeTo(yourFaction, true));
		you.msg("%s<i> kicked you from %s<i>! :O", fme.describeTo(you, true), yourFaction.describeTo(you));
		if (yourFaction != myFaction)
		{
			fme.msg("<i>You kicked %s<i> from the faction %s<i>!", you.describeTo(fme), yourFaction.describeTo(fme));
		}

		yourFaction.deinvite(you);
		you.resetFactionData();

		if (yourFaction.getFPlayers().isEmpty() && !yourFaction.isPermanent())
		{
			// Remove this faction
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				fplayer.msg("The faction %s<i> was disbanded.", yourFaction.getTag(fplayer));
			}
			yourFaction.detach();
		}
	}
	
}
