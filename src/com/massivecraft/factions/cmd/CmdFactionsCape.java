package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;

public class CmdFactionsCape extends FCommand
{
	public CmdFactionsCapeGet cmdCapeGet = new CmdFactionsCapeGet();
	public CmdFactionsCapeSet cmdCapeSet = new CmdFactionsCapeSet();
	public CmdFactionsCapeRemove cmdCapeRemove = new CmdFactionsCapeRemove();
	
	public CmdFactionsCape()
	{
		super();
		this.aliases.add("cape");
		
		this.permission = Perm.CAPE.node;
		
		this.addSubCommand(this.cmdCapeGet);
		this.addSubCommand(this.cmdCapeSet);
		this.addSubCommand(this.cmdCapeRemove);
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		Factions.get().cmdAutoHelp.execute(this.sender, this.args, this.commandChain);
	}
	
}
