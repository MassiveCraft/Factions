package com.massivecraft.factions.chat.tag;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;

public class ChatTagNameforce extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagNameforce() { super("factions_nameforce"); }
	private static ChatTagNameforce i = new ChatTagNameforce();
	public static ChatTagNameforce get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(CommandSender sender, CommandSender recipient)
	{
		// Get entities
		MPlayer usender = MPlayer.get(sender);
		
		Faction faction = usender.getFaction();
		return faction.getName();
	}

}
