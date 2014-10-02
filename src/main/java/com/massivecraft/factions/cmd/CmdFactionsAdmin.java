package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsAdmin extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAdmin()
	{
		// Aliases
		this.addAliases("admin");

		// Args
		this.addOptionalArg("on/off", "flip");
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.ADMIN.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Boolean target = this.arg(0, ARBoolean.get(), !msender.isUsingAdminMode());
		if (target == null) return;
		
		// Apply
		msender.setUsingAdminMode(target);		
		
		// Inform
		String desc = Txt.parse(msender.isUsingAdminMode() ? "<g>ENABLED" : "<b>DISABLED");
		
		String messageYou = Txt.parse("<i>%s %s <i>admin bypass mode.", msender.getDisplayName(msender), desc);
		String messageLog = Txt.parse("<i>%s %s <i>admin bypass mode.", msender.getDisplayName(IdUtil.getConsole()), desc);
		
		msender.sendMessage(messageYou);
		Factions.get().log(messageLog);
	}
	
}
