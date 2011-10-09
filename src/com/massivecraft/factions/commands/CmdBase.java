package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;

public class CmdBase extends FCommand
{
	//public CmdAccept cmdAccept = new CmdAccept();
	
	public CmdBase()
	{
		super();
		this.aliases.addAll(Conf.baseCommandAliases);
		this.allowNoSlashAccess = Conf.allowNoSlashCommand;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		
		this.setHelpShort("The faction base command");
		this.helpLong.add(p.txt.tags("<i>This command contains all faction stuff."));
		
		/*this.subCommands.add(p.cmdHelp);
		this.subCommands.add(new CmdIntend());
		this.subCommands.add(new CmdInfect());
		this.subCommands.add(cmdAccept);
		this.subCommands.add(new CmdList());
		this.subCommands.add(new CmdSetfood());
		this.subCommands.add(new CmdSetinfection());
		this.subCommands.add(new CmdTurn());
		this.subCommands.add(new CmdCure());
		this.subCommands.add(new CmdVersion());*/
	}
	
	@Override
	public void perform()
	{
		//this.commandChain.add(this);
		//p.cmdHelp.execute(this.sender, this.args, this.commandChain);
	}

}
