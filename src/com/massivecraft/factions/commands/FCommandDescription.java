package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TextUtil;

public class FCommandDescription extends FCommand
{
	public FCommandDescription()
	{
		super();
		this.aliases.add("desc");
		
		this.requiredArgs.add("desc");
		//this.optionalArgs
		
		this.permission = Permission.COMMAND_DESCRIPTION.node;
		
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

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostDesc))
		{
			return;
		}

		myFaction.setDescription(TextUtil.implode(args, " "));
		
		// Broadcast the description to everyone
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			fplayer.sendMessageParsed("The faction "+fplayer.getRelationColor(fme)+myFaction.getTag()+"<i> changed their description to:");
			fplayer.sendMessageParsed("<i>"+myFaction.getDescription());
		}
	}
	
}
