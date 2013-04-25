package com.massivecraft.factions.cmd.req;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.cmd.MCommand;
import com.massivecraft.mcore.cmd.req.ReqAbstract;
import com.massivecraft.mcore.util.Txt;

public class ReqHasntFaction extends ReqAbstract
{
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ReqHasntFaction i = new ReqHasntFaction();
	public static ReqHasntFaction get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean apply(CommandSender sender, MCommand command)
	{
		return !UPlayer.get(sender).hasFaction();
	}
	
	@Override
	public String createErrorMessage(CommandSender sender, MCommand command)
	{
		return Txt.parse("<b>You must leave your current faction before you "+(command == null ? "do that" : command.getDesc())+".");
	}
	
}
