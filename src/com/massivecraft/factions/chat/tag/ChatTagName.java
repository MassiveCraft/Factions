package com.massivecraft.factions.chat.tag;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;

public class ChatTagName extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagName() { super("factions_name"); }
	private static ChatTagName i = new ChatTagName();
	public static ChatTagName get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(CommandSender sender, CommandSender recipient)
	{
		// Check disabled
		if (UConf.isDisabled(sender)) return "";

		// Get entities
		UPlayer usender = UPlayer.get(sender);
		
		// No "force"
		Faction faction = usender.getFaction();
		if (faction.isNone()) return "";
		
		return faction.getName();
	}

}
