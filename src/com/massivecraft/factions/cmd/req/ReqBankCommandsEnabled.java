package com.massivecraft.factions.cmd.req;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.UConf;
import com.massivecraft.mcore.cmd.MCommand;
import com.massivecraft.mcore.cmd.req.ReqAbstract;
import com.massivecraft.mcore.util.Txt;

public class ReqBankCommandsEnabled extends ReqAbstract
{
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ReqBankCommandsEnabled i = new ReqBankCommandsEnabled();
	public static ReqBankCommandsEnabled get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean apply(CommandSender sender, MCommand command)
	{
		return UConf.get(sender).econEnabled && UConf.get(sender).bankEnabled;
	}
	
	@Override
	public String createErrorMessage(CommandSender sender, MCommand command)
	{
		if (!UConf.get(sender).bankEnabled)
		{
			return Txt.parse("<b>The Factions bank system is disabled on this server.");
		}
		return Txt.parse("<b>The Factions economy features are disabled on this server.");
	}
	
}
