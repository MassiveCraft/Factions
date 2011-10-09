package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;

public class CmdChat extends FCommand
{
	
	public CmdChat()
	{
		super();
		this.aliases.add("c");
		this.aliases.add("chat");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("mode", "next");
		
		this.permission = Permission.COMMAND_CHAT.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = true;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! Conf.factionOnlyChat )
		{
			sendMessageParsed("<b>Faction-only chat is disabled on this server.");
			return;
		}
		
		String modeString = this.argAsString(0).toLowerCase();
		ChatMode modeTarget = fme.getChatMode().getNext();
		
		if (modeString != null)
		{
			if(modeString.startsWith("p"))
			{
				modeTarget = ChatMode.PUBLIC;
			}
			else if (modeString.startsWith("a"))
			{
				modeTarget = ChatMode.ALLIANCE;
			}
			else if(modeString.startsWith("f"))
			{
				modeTarget = ChatMode.FACTION;
			}
			sendMessageParsed("<b>Unrecognised chat mode. <i>Please enter either 'a','f' or 'p'");
			return;
		}
		
		fme.setChatMode(modeTarget);
		
		if(fme.getChatMode() == ChatMode.PUBLIC)
		{
			sendMessageParsed("<i>Public chat mode.");
		}
		else if (fme.getChatMode() == ChatMode.ALLIANCE )
		{
			sendMessageParsed("<i>Alliance only chat mode.");
		}
		else
		{
			sendMessageParsed("<i>Faction only chat mode.");
		}
	}
}
