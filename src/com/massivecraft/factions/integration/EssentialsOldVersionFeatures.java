package com.massivecraft.factions.integration;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.P;
import com.massivecraft.factions.listeners.FactionsChatListener;

import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.chat.IEssentialsChatListener;


/*
 * This Essentials integration handler is for older 2.x.x versions of Essentials which have "IEssentialsChatListener"
 */

public class EssentialsOldVersionFeatures
{
	private static EssentialsChat essChat;

	public static void integrateChat(EssentialsChat instance)
	{
		essChat = instance;
		try
		{
			essChat.addEssentialsChatListener("Factions", new IEssentialsChatListener()
			{
				public boolean shouldHandleThisChat(AsyncPlayerChatEvent event)
				{
					return P.p.shouldLetFactionsHandleThisChat(event);
				}
				public String modifyMessage(AsyncPlayerChatEvent event, Player target, String message)
				{
					return FactionsChatListener.parseTags(message, event.getPlayer(), target);
					//return message.replace(Conf.chatTagReplaceString, P.p.getPlayerFactionTagRelation(event.getPlayer(), target)).replace("[FACTION_TITLE]", P.p.getPlayerTitle(event.getPlayer()));
				}
			});
			P.p.log("Found and will integrate chat with "+essChat.getDescription().getFullName());
		}
		catch (NoSuchMethodError ex)
		{
			essChat = null;
		}
	}

	public static void unhookChat()
	{
		if (essChat != null)
		{
			essChat.removeEssentialsChatListener("Factions");
		}
	}
}
