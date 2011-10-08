package com.massivecraft.factions.integration;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;

import com.earth2me.essentials.chat.EssentialsChat;
import com.earth2me.essentials.chat.IEssentialsChatListener;


public class EssentialsFeatures {
	private static EssentialsChat essChat;

	public static void integrateChat(EssentialsChat instance) {
		essChat = instance;
		try {
			essChat.addEssentialsChatListener("Factions", new IEssentialsChatListener() {
				public boolean shouldHandleThisChat(PlayerChatEvent event)
				{
					return Factions.instance.shouldLetFactionsHandleThisChat(event);
				}
				public String modifyMessage(PlayerChatEvent event, Player target, String message)
				{
					return message.replace("{FACTION}", Factions.instance.getPlayerFactionTagRelation(event.getPlayer(), target)).replace("{FACTION_TITLE}", Factions.instance.getPlayerTitle(event.getPlayer()));
				}
			});
			Factions.log("Found and will integrate chat with "+essChat.getDescription().getFullName());
		}
		catch (NoSuchMethodError ex) {
			essChat = null;
		}
	}

	public static void unhookChat() {
		if (essChat != null) {
			essChat.removeEssentialsChatListener("Factions");
		}
	}
}
