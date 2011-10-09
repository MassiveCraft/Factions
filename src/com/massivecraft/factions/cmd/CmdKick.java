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
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{	
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		if (fme == you)
		{
			sendMessageParsed("<b>You cannot kick yourself.");
			sendMessageParsed("<i>You might want to: %s", new CmdLeave().getUseageTemplate(false));
			return;
		}

		Faction yourFaction = you.getFaction();

		// players with admin-level "disband" permission can bypass these requirements
		if ( ! Permission.KICK_ANY.has(sender))
		{
			if (yourFaction != myFaction)
			{
				sendMessageParsed("%s<b> is not a member of %s", you.getNameAndRelevant(fme), myFaction.getTag(fme));
				return;
			}

			if (you.getRole().value >= fme.getRole().value)
			{
				// TODO add more informative messages.
				sendMessageParsed("<b>Your rank is too low to kick this player.");
				return;
			}

			if ( ! Conf.CanLeaveWithNegativePower && you.getPower() < 0)
			{
				sendMessageParsed("<b>You cannot kick that member until their power is positive.");
				return;
			}
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostKick))
		{
			return;
		}

		yourFaction.sendMessageParsed("%s<i> kicked %s<i> from the faction! :O", fme.getNameAndRelevant(yourFaction), you.getNameAndRelevant(yourFaction));
		you.sendMessageParsed("%s<i> kicked you from %s<i>! :O", fme.getNameAndRelevant(you), yourFaction.getTag(you));
		if (yourFaction != myFaction)
		{
			fme.sendMessageParsed("<i>You kicked %s<i> from the faction %s<i>!", you.getNameAndRelevant(myFaction), yourFaction.getTag(fme));
		}

		yourFaction.deinvite(you);
		you.resetFactionData();

		if (yourFaction.getFPlayers().isEmpty() && !yourFaction.isPermanent())
		{
			// Remove this faction
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				fplayer.sendMessageParsed("The faction %s<i> was disbanded.", yourFaction.getTag(fplayer));
			}
			yourFaction.detach();
		}
	}
	
}
