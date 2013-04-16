package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.HelpCommand;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsCape extends FCommand
{
	public CmdFactionsCapeGet cmdCapeGet = new CmdFactionsCapeGet();
	public CmdFactionsCapeSet cmdCapeSet = new CmdFactionsCapeSet();
	public CmdFactionsCapeRemove cmdCapeRemove = new CmdFactionsCapeRemove();
	
	public CmdFactionsCape()
	{
		this.addAliases("cape");
		
		this.addRequirements(ReqHasPerm.get(Perm.CAPE.node));
		
		this.addSubCommand(this.cmdCapeGet);
		this.addSubCommand(this.cmdCapeSet);
		this.addSubCommand(this.cmdCapeRemove);
	}
	
	@Override
	public void perform()
	{
		this.getCommandChain().add(this);
		HelpCommand.getInstance().execute(this.sender, this.args, this.commandChain);
	}
	
}
