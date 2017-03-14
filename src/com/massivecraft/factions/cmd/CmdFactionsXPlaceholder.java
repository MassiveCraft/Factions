package com.massivecraft.factions.cmd;

public class CmdFactionsXPlaceholder extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public String extensionName;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsXPlaceholder(String extensionName, String... aliases)
	{
		// Fields
		this.extensionName = extensionName;
		this.setSetupEnabled(false);
		
		// Aliases
		this.addAliases(aliases);
		
		// Desc
		this.setDesc("Use " + extensionName);
		
		// Parameters
		this.setOverflowSensitive(false);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		msg("<b>The extension <h>%s <b>isn't installed.", this.extensionName);
		msg("<g>Learn more and download the extension here:");
		msg("<aqua>https://www.massivecraft.com/%s", this.extensionName.toLowerCase());
	}
	
}
