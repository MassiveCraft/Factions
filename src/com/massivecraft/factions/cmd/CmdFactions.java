package com.massivecraft.factions.cmd;

import java.util.Collections;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.HelpCommand;
import com.massivecraft.mcore.cmd.VersionCommand;

public class CmdFactions extends FCommand
{
	public CmdFactionsAccess cmdFactionsAccess = new CmdFactionsAccess();
	public CmdFactionsLeader cmdFactionsLeader = new CmdFactionsLeader();
	public CmdFactionsAutoClaim cmdFactionsAutoClaim = new CmdFactionsAutoClaim();
	public CmdFactionsAdmin cmdFactionsAdmin = new CmdFactionsAdmin();
	public CmdFactionsClaim cmdFactionsClaim = new CmdFactionsClaim();
	public CmdFactionsCreate cmdFactionsCreate = new CmdFactionsCreate();
	public CmdFactionsDemote cmdFactionsDemote = new CmdFactionsDemote();
	public CmdFactionsDescription cmdFactionsDescription = new CmdFactionsDescription();
	public CmdFactionsDisband cmdFactionsDisband = new CmdFactionsDisband();
	public CmdFactionsFaction cmdFactionsFaction = new CmdFactionsFaction();
	public CmdFactionsFlag cmdFactionsFlag = new CmdFactionsFlag();
	public CmdFactionsHome cmdFactionsHome = new CmdFactionsHome();
	public CmdFactionsInvite cmdFactionsInvite = new CmdFactionsInvite();
	public CmdFactionsJoin cmdFactionsJoin = new CmdFactionsJoin();
	public CmdFactionsKick cmdFactionsKick = new CmdFactionsKick();
	public CmdFactionsLeave cmdFactionsLeave = new CmdFactionsLeave();
	public CmdFactionsList cmdFactionsList = new CmdFactionsList();
	public CmdFactionsMap cmdFactionsMap = new CmdFactionsMap();
	public CmdFactionsOfficer cmdFactionsOfficer = new CmdFactionsOfficer();
	public CmdFactionsMoney cmdFactionsMoney = new CmdFactionsMoney();
	public CmdFactionsOpen cmdFactionsOpen = new CmdFactionsOpen();
	public CmdFactionsPerm cmdFactionsPerm = new CmdFactionsPerm();
	public CmdFactionsPlayer cmdFactionsPlayer = new CmdFactionsPlayer();
	public CmdFactionsPowerBoost cmdFactionsPowerBoost = new CmdFactionsPowerBoost();
	public CmdFactionsPromote cmdFactionsPromote = new CmdFactionsPromote();
	public CmdFactionsRelationAlly cmdFactionsRelationAlly = new CmdFactionsRelationAlly();
	public CmdFactionsRelationEnemy cmdFactionsRelationEnemy = new CmdFactionsRelationEnemy();
	public CmdFactionsRelationNeutral cmdFactionsRelationNeutral = new CmdFactionsRelationNeutral();
	public CmdFactionsRelationTruce cmdFactionsRelationTruce = new CmdFactionsRelationTruce();
	public CmdFactionsSeeChunk cmdFactionsSeeChunk = new CmdFactionsSeeChunk();
	public CmdFactionsSethome cmdFactionsSethome = new CmdFactionsSethome();
	public CmdFactionsName cmdFactionsName = new CmdFactionsName();
	public CmdFactionsTitle cmdFactionsTitle = new CmdFactionsTitle();
	public CmdFactionsUnclaim cmdFactionsUnclaim = new CmdFactionsUnclaim();
	public CmdFactionsUnclaimall cmdFactionsUnclaimall = new CmdFactionsUnclaimall();
	public VersionCommand cmdFactionsVersion = new VersionCommand(Factions.get(), Perm.VERSION.node, "v", "version");
	
	public CmdFactions()
	{
		this.aliases.addAll(ConfServer.baseCommandAliases);
		
		// remove any nulls from extra commas
		// TODO: When is this required? Should this be added to MCore?
		this.aliases.removeAll(Collections.singletonList(null));
		
		this.setDesc("The faction base command");
		this.setHelp("This command contains all faction stuff.");
		
		this.addSubCommand(HelpCommand.get());
		this.addSubCommand(this.cmdFactionsList);
		this.addSubCommand(this.cmdFactionsFaction);
		this.addSubCommand(this.cmdFactionsPlayer);
		this.addSubCommand(this.cmdFactionsJoin);
		this.addSubCommand(this.cmdFactionsLeave);
		this.addSubCommand(this.cmdFactionsHome);
		this.addSubCommand(this.cmdFactionsCreate);
		this.addSubCommand(this.cmdFactionsSethome);
		this.addSubCommand(this.cmdFactionsName);
		this.addSubCommand(this.cmdFactionsDemote);
		this.addSubCommand(this.cmdFactionsDescription);
		this.addSubCommand(this.cmdFactionsPerm);
		this.addSubCommand(this.cmdFactionsFlag);
		this.addSubCommand(this.cmdFactionsInvite);
		this.addSubCommand(this.cmdFactionsOpen);
		this.addSubCommand(this.cmdFactionsMoney);
		this.addSubCommand(this.cmdFactionsClaim);
		this.addSubCommand(this.cmdFactionsAutoClaim);
		this.addSubCommand(this.cmdFactionsUnclaim);
		this.addSubCommand(this.cmdFactionsUnclaimall);
		this.addSubCommand(this.cmdFactionsAccess);
		this.addSubCommand(this.cmdFactionsKick);
		this.addSubCommand(this.cmdFactionsOfficer);
		this.addSubCommand(this.cmdFactionsLeader);
		this.addSubCommand(this.cmdFactionsTitle);
		this.addSubCommand(this.cmdFactionsMap);
		this.addSubCommand(this.cmdFactionsSeeChunk);
		this.addSubCommand(this.cmdFactionsDisband);
		this.addSubCommand(this.cmdFactionsRelationAlly);
		this.addSubCommand(this.cmdFactionsRelationEnemy);
		this.addSubCommand(this.cmdFactionsRelationNeutral);
		this.addSubCommand(this.cmdFactionsRelationTruce);
		this.addSubCommand(this.cmdFactionsAdmin);
		this.addSubCommand(this.cmdFactionsPowerBoost);
		this.addSubCommand(this.cmdFactionsPromote);
		this.addSubCommand(this.cmdFactionsVersion);
	}
	
	@Override
	public void perform()
	{
		this.getCommandChain().add(this);
		HelpCommand.getInstance().execute(this.sender, this.args, this.commandChain);
	}

}
