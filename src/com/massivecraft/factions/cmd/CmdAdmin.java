package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdAdmin extends FCommand
{	
	public CmdAdmin()
	{
		super();
		this.aliases.add("admin");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.ADMIN.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer fyou = this.argAsBestFPlayerMatch(0);
		if (fyou == null) return;

		boolean permAny = Permission.ADMIN_ANY.has(sender, false);
		Faction targetFaction = fyou.getFaction();

		if (targetFaction != myFaction && !permAny)
		{
			msg("%s<i> is not a member in your faction.", fyou.describeTo(fme, true));
			return;
		}

		if (fme != null && fme.getRole() != Role.ADMIN && !permAny)
		{
			msg("<b>You are not the faction admin.");
			return;
		}

		if (fyou == fme && !permAny)
		{
			msg("<b>The target player musn't be yourself.");
			return;
		}

		FPlayer admin = targetFaction.getFPlayerAdmin();

		// if target player is currently admin, demote and replace him
		if (fyou == admin)
		{
			targetFaction.promoteNewLeader();
			msg("<i>You have demoted %s<i> from the position of faction admin.", fyou.describeTo(fme, true));
			fyou.msg("<i>You have been demoted from the position of faction admin by %s<i>.", senderIsConsole ? "a server admin" : fme.describeTo(fyou, true));
			return;
		}

		// promote target player, and demote existing admin if one exists
		if (admin != null)
			admin.setRole(Role.MODERATOR);
		fyou.setRole(Role.ADMIN);
		msg("<i>You have promoted %s<i> to the position of faction admin.", fyou.describeTo(fme, true));

		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			fplayer.msg("%s<i> gave %s<i> the leadership of %s<i>.", senderIsConsole ? "A server admin" : fme.describeTo(fplayer, true), fyou.describeTo(fplayer), targetFaction.describeTo(fplayer));
		}
	}
	
}
