package com.massivecraft.factions.cmd;

import java.util.Collections;

import com.massivecraft.factions.Conf;

public class FCmdRoot extends FCommand
{
	public CmdLeader cmdLeader = new CmdLeader();
	public CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
	public CmdAdmin cmdBypass = new CmdAdmin();
	public CmdChat cmdChat = new CmdChat();
	public CmdChatSpy cmdChatSpy = new CmdChatSpy();
	public CmdClaim cmdClaim = new CmdClaim();
	public CmdConfig cmdConfig = new CmdConfig();
	public CmdCreate cmdCreate = new CmdCreate();
	public CmdDeinvite cmdDeinvite = new CmdDeinvite();
	public CmdDescription cmdDescription = new CmdDescription();
	public CmdDisband cmdDisband = new CmdDisband();
	public CmdFlag cmdFlag = new CmdFlag();
	public CmdHelp cmdHelp = new CmdHelp();
	public CmdHome cmdHome = new CmdHome();
	public CmdInvite cmdInvite = new CmdInvite();
	public CmdJoin cmdJoin = new CmdJoin();
	public CmdKick cmdKick = new CmdKick();
	public CmdLeave cmdLeave = new CmdLeave();
	public CmdList cmdList = new CmdList();
	public CmdLock cmdLock = new CmdLock();
	public CmdMap cmdMap = new CmdMap();
	public CmdOfficer cmdOfficer = new CmdOfficer();
	public CmdMoney cmdMoney = new CmdMoney();
	public CmdOpen cmdOpen = new CmdOpen();
	public CmdPerm cmdPerm = new CmdPerm();
	public CmdPower cmdPower = new CmdPower();
	public CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
	public CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
	public CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
	public CmdRelationTruce cmdRelationTruce = new CmdRelationTruce();
	public CmdReload cmdReload = new CmdReload();
	public CmdSaveAll cmdSaveAll = new CmdSaveAll();
	public CmdSeeChunk cmdSeeChunks = new CmdSeeChunk();
	public CmdSethome cmdSethome = new CmdSethome();
	public CmdShow cmdShow = new CmdShow();
	public CmdTag cmdTag = new CmdTag();
	public CmdTitle cmdTitle = new CmdTitle();
	public CmdUnclaim cmdUnclaim = new CmdUnclaim();
	public CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
	public CmdVersion cmdVersion = new CmdVersion();
	
	public FCmdRoot()
	{
		super();
		this.aliases.addAll(Conf.baseCommandAliases);
		this.aliases.removeAll(Collections.singletonList(null));  // remove any nulls from extra commas
		this.allowNoSlashAccess = Conf.allowNoSlashCommand;
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("","")
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
		
		this.disableOnLock = false;
		
		this.setHelpShort("The faction base command");
		this.helpLong.add(p.txt.parseTags("<i>This command contains all faction stuff."));
		
		//this.subCommands.add(p.cmdHelp);
		
		this.addSubCommand(this.cmdLeader);
		this.addSubCommand(this.cmdAutoClaim);
		this.addSubCommand(this.cmdBypass);
		this.addSubCommand(this.cmdChat);
		this.addSubCommand(this.cmdChatSpy);
		this.addSubCommand(this.cmdClaim);
		this.addSubCommand(this.cmdConfig);
		this.addSubCommand(this.cmdCreate);
		this.addSubCommand(this.cmdDeinvite);
		this.addSubCommand(this.cmdDescription);
		this.addSubCommand(this.cmdDisband);
		this.addSubCommand(this.cmdFlag);
		this.addSubCommand(this.cmdHelp);
		this.addSubCommand(this.cmdHome);
		this.addSubCommand(this.cmdInvite);
		this.addSubCommand(this.cmdJoin);
		this.addSubCommand(this.cmdKick);
		this.addSubCommand(this.cmdLeave);
		this.addSubCommand(this.cmdList);
		this.addSubCommand(this.cmdLock);
		this.addSubCommand(this.cmdMap);
		this.addSubCommand(this.cmdOfficer);
		this.addSubCommand(this.cmdMoney);
		this.addSubCommand(this.cmdOpen);
		this.addSubCommand(this.cmdPerm);
		this.addSubCommand(this.cmdPower);
		this.addSubCommand(this.cmdRelationAlly);
		this.addSubCommand(this.cmdRelationEnemy);
		this.addSubCommand(this.cmdRelationNeutral);
		this.addSubCommand(this.cmdRelationTruce);
		this.addSubCommand(this.cmdReload);
		this.addSubCommand(this.cmdSaveAll);
		this.addSubCommand(this.cmdSeeChunks);
		this.addSubCommand(this.cmdSethome);
		this.addSubCommand(this.cmdShow);
		this.addSubCommand(this.cmdTag);
		this.addSubCommand(this.cmdTitle);
		this.addSubCommand(this.cmdUnclaim);
		this.addSubCommand(this.cmdUnclaimall);
		this.addSubCommand(this.cmdVersion);
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		this.cmdHelp.execute(this.sender, this.args, this.commandChain);
	}

}
