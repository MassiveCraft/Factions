package com.massivecraft.factions.cmd.req;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.cmd.req.ReqAbstract;
import com.massivecraft.massivecore.util.Txt;

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
	public boolean apply(CommandSender sender, MassiveCommand command)
	{
		return MConf.get().bankEnabled && Econ.isEnabled();
	}
	
	@Override
	public String createErrorMessage(CommandSender sender, MassiveCommand command)
	{
		if ( ! MConf.get().bankEnabled)
		{
			return Txt.parse("<b>Faction banks are disabled.");
		}
		return Txt.parse("<b>Faction economy features are disabled.");
	}
	
}
