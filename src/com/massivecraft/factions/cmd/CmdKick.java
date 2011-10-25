package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;

public class CmdKick extends FCommand
{
	
	public CmdKick()
	{
		super();
		this.aliases.add("kick");
		
		this.requiredArgs.add("player");
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

		if ( ! Conf.canLeaveWithNegativePower && you.getPower() < 0)
		{
			msg("<b>You cannot kick that member until their power is positive.");
			return;
		}
		
		Faction yourFaction = you.getFaction();

		if (fme != null && ! FPerm.KICK.has(fme, yourFaction)) return;
		
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

		if (Conf.logFactionKick)
			P.p.log(fme.getName()+" kicked "+you.getName()+" from the faction: "+yourFaction.getTag());

		if (yourFaction.getFPlayers().isEmpty() && !yourFaction.getFlag(FFlag.PERMANENT))
		{
			// Remove this faction
			for (FPlayer fplayer : FPlayers.i.getOnline())
			{
				fplayer.msg("The faction %s<i> was disbanded.", yourFaction.getTag(fplayer));
			}
			yourFaction.detach();

			if (Conf.logFactionDisband)
				P.p.log("The faction "+yourFaction.getTag()+" ("+yourFaction.getId()+") was disbanded since the last player was kicked by "+(senderIsConsole ? "console command" : fme.getName())+".");
		}
	}
	
}
