package com.massivecraft.factions.cmd;

import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.cmd.VisibilityMode;


public class CmdFactionsXDeprecated extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public MassiveCommand target;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsXDeprecated(MassiveCommand target, String... aliases)
	{
		// Fields
		this.target = target;
		
		// Aliases
		this.addAliases(aliases);
		
		// Args
		this.setErrorOnToManyArgs(false);
		
		// Visibility
		this.setVisibilityMode(VisibilityMode.INVISIBLE);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{	
		msg("<i>Use this new command instead:");
		sendMessage(target.getUseageTemplate(true));
	}
	
}
