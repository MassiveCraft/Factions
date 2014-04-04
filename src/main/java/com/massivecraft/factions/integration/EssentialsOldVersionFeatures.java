package com.massivecraft.factions.integration;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;

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
					return message.replace(Conf.chatTagReplaceString, P.p.getPlayerFactionTagRelation(event.getPlayer(), target)).replace("[FACTION_TITLE]", P.p.getPlayerTitle(event.getPlayer()));
				}
			});
			P.p.log("Found and will integrate chat with "+essChat.getDescription().getFullName());

			// As of Essentials 2.8+, curly braces are not accepted and are instead replaced with square braces, so... deal with it
			if (essChat.getDescription().getVersion().startsWith("2.8.") && Conf.chatTagReplaceString.contains("{"))
			{
				Conf.chatTagReplaceString = Conf.chatTagReplaceString.replace("{", "[").replace("}", "]");
				P.p.log("NOTE: as of Essentials 2.8+, we've had to switch the default chat replacement tag from \"{FACTION}\" to \"[FACTION]\". This has automatically been updated for you.");
			}
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
