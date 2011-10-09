package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TextUtil;

public class CmdTitle extends FCommand
{
	public CmdTitle()
	{
		this.aliases.add("title");
		
		this.requiredArgs.add("player name");
		this.optionalArgs.put("title", "");
		
		this.permission = Permission.COMMAND_TITLE.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}
		
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		args.remove(0);
		String title = TextUtil.implode(args, " ");
		
		if ( ! canIAdministerYou(fme, you)) return;

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostTitle)) return;

		you.setTitle(title);
		
		// Inform
		myFaction.sendMessageParsed("%s<i> changed a title: %s", fme.getNameAndRelevant(myFaction), you.getNameAndRelevant(myFaction));

		if (Conf.spoutFactionTitlesOverNames)
		{
			SpoutFeatures.updateAppearances(me);
		}
	}
	
}
