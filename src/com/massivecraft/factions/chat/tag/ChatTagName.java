package com.massivecraft.factions.chat.tag;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

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
		// Get entities
		MPlayer usender = MPlayer.get(sender);
		
		// No "force"
		Faction faction = usender.getFaction();
		if (faction.isNone()) return "";
		
		return faction.getName();
	}

}
