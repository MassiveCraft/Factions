package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;

public class FCmdRoot extends FCommand
{
	public CmdAdmin cmdAdmin = new CmdAdmin();
	public CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
	public CmdAutoSafeclaim cmdAutoSafeclaim = new CmdAutoSafeclaim();
	public CmdAutoWarclaim cmdAutoWarclaim = new CmdAutoWarclaim();
	public CmdBalance cmdBalance = new CmdBalance();
	public CmdBoom cmdBoom = new CmdBoom();
	public CmdBypass cmdBypass = new CmdBypass();
	public CmdChat cmdChat = new CmdChat();
	public CmdClaim cmdClaim = new CmdClaim();
	public CmdConfig cmdConfig = new CmdConfig();
	public CmdCreate cmdCreate = new CmdCreate();
	public CmdDeinvite cmdDeinvite = new CmdDeinvite();
	public CmdDeposit cmdDeposit = new CmdDeposit();
	public CmdDescription cmdDescription = new CmdDescription();
	public CmdDisband cmdDisband = new CmdDisband();
	public CmdHelp cmdHelp = new CmdHelp();
	public CmdHome cmdHome = new CmdHome();
	public CmdInvite cmdInvite = new CmdInvite();
	public CmdJoin cmdJoin = new CmdJoin();
	public CmdKick cmdKick = new CmdKick();
	public CmdLeave cmdLeave = new CmdLeave();
	public CmdList cmdList = new CmdList();
	public CmdLock cmdLock = new CmdLock();
	public CmdMap cmdMap = new CmdMap();
	public CmdMod cmdMod = new CmdMod();
	public CmdOpen cmdOpen = new CmdOpen();
	public CmdOwner cmdOwner = new CmdOwner();
	public CmdOwnerList cmdOwnerList = new CmdOwnerList();
	public CmdPay cmdPay = new CmdPay();
	public CmdPeaceful cmdPeaceful = new CmdPeaceful();
	public CmdPermanent cmdPermanent = new CmdPermanent();
	public CmdPower cmdPower = new CmdPower();
	public CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
	public CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
	public CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
	public CmdReload cmdReload = new CmdReload();
	public CmdSafeclaim cmdSafeclaim = new CmdSafeclaim();
	public CmdSafeunclaimall cmdSafeunclaimall = new CmdSafeunclaimall();
	public CmdSaveAll cmdSaveAll = new CmdSaveAll();
	public CmdSethome cmdSethome = new CmdSethome();
	public CmdShow cmdShow = new CmdShow();
	public CmdTag cmdTag = new CmdTag();
	public CmdTitle cmdTitle = new CmdTitle();
	public CmdUnclaim cmdUnclaim = new CmdUnclaim();
	public CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
	public CmdVersion cmdVersion = new CmdVersion();
	public CmdWarclaim cmdWarclaim = new CmdWarclaim();
	public CmdWarunclaimall cmdWarunclaimall = new CmdWarunclaimall();
	public CmdWithdraw cmdWithdraw = new CmdWithdraw();
	
	public FCmdRoot()
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
		
		//this.subCommands.add(p.cmdHelp);
		
		this.subCommands.add(this.cmdAdmin);
		this.subCommands.add(this.cmdAutoClaim);
		this.subCommands.add(this.cmdAutoSafeclaim);
		this.subCommands.add(this.cmdAutoWarclaim);
		this.subCommands.add(this.cmdBalance);
		this.subCommands.add(this.cmdBoom);
		this.subCommands.add(this.cmdBypass);
		this.subCommands.add(this.cmdChat);
		this.subCommands.add(this.cmdClaim);
		this.subCommands.add(this.cmdConfig);
		this.subCommands.add(this.cmdCreate);
		this.subCommands.add(this.cmdDeinvite);
		this.subCommands.add(this.cmdDeposit);
		this.subCommands.add(this.cmdDescription);
		this.subCommands.add(this.cmdDisband);
		this.subCommands.add(this.cmdHelp);
		this.subCommands.add(this.cmdHome);
		this.subCommands.add(this.cmdInvite);
		this.subCommands.add(this.cmdJoin);
		this.subCommands.add(this.cmdKick);
		this.subCommands.add(this.cmdLeave);
		this.subCommands.add(this.cmdList);
		this.subCommands.add(this.cmdLock);
		this.subCommands.add(this.cmdMap);
		this.subCommands.add(this.cmdMod);
		this.subCommands.add(this.cmdOpen);
		this.subCommands.add(this.cmdOwner);
		this.subCommands.add(this.cmdOwnerList);
		this.subCommands.add(this.cmdPay);
		this.subCommands.add(this.cmdPeaceful);
		this.subCommands.add(this.cmdPermanent);
		this.subCommands.add(this.cmdPower);
		this.subCommands.add(this.cmdRelationAlly);
		this.subCommands.add(this.cmdRelationEnemy);
		this.subCommands.add(this.cmdRelationNeutral);
		this.subCommands.add(this.cmdReload);
		this.subCommands.add(this.cmdSafeclaim);
		this.subCommands.add(this.cmdSafeunclaimall);
		this.subCommands.add(this.cmdSaveAll);
		this.subCommands.add(this.cmdSethome);
		this.subCommands.add(this.cmdShow);
		this.subCommands.add(this.cmdTag);
		this.subCommands.add(this.cmdTitle);
		this.subCommands.add(this.cmdUnclaim);
		this.subCommands.add(this.cmdUnclaimall);
		this.subCommands.add(this.cmdVersion);
		this.subCommands.add(this.cmdWarclaim);
		this.subCommands.add(this.cmdWarunclaimall);
		this.subCommands.add(this.cmdWithdraw);
	}
	
	@Override
	public void perform()
	{
		this.commandChain.add(this);
		this.cmdHelp.execute(this.sender, this.args, this.commandChain);
	}

}
