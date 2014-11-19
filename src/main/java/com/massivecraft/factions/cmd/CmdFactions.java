package com.massivecraft.factions.cmd;

import java.util.List;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.cmd.HelpCommand;
import com.massivecraft.massivecore.cmd.VersionCommand;

public class CmdFactions extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsList cmdFactionsList = new CmdFactionsList();
	public CmdFactionsFaction cmdFactionsFaction = new CmdFactionsFaction();
	public CmdFactionsPlayer cmdFactionsPlayer = new CmdFactionsPlayer();
	public CmdFactionsJoin cmdFactionsJoin = new CmdFactionsJoin();
	public CmdFactionsLeave cmdFactionsLeave = new CmdFactionsLeave();
	public CmdFactionsHome cmdFactionsHome = new CmdFactionsHome();
	public CmdFactionsMap cmdFactionsMap = new CmdFactionsMap();
	public CmdFactionsCreate cmdFactionsCreate = new CmdFactionsCreate();
	public CmdFactionsName cmdFactionsName = new CmdFactionsName();
	public CmdFactionsDescription cmdFactionsDescription = new CmdFactionsDescription();
	public CmdFactionsMotd cmdFactionsMotd = new CmdFactionsMotd();
	public CmdFactionsSethome cmdFactionsSethome = new CmdFactionsSethome();
	public CmdFactionsUnsethome cmdFactionsUnsethome = new CmdFactionsUnsethome();
	public CmdFactionsInvite cmdFactionsInvite = new CmdFactionsInvite();
	public CmdFactionsKick cmdFactionsKick = new CmdFactionsKick();
	public CmdFactionsTitle cmdFactionsTitle = new CmdFactionsTitle();
	public CmdFactionsPromote cmdFactionsPromote = new CmdFactionsPromote();
	public CmdFactionsDemote cmdFactionsDemote = new CmdFactionsDemote();
	public CmdFactionsOfficer cmdFactionsOfficer = new CmdFactionsOfficer();
	public CmdFactionsLeader cmdFactionsLeader = new CmdFactionsLeader();
	public CmdFactionsMoney cmdFactionsMoney = new CmdFactionsMoney();
	public CmdFactionsSeeChunk cmdFactionsSeeChunk = new CmdFactionsSeeChunk();
	public CmdFactionsSeeChunkOld cmdFactionsSeeChunkOld = new CmdFactionsSeeChunkOld();
	public CmdFactionsClaim cmdFactionsClaim = new CmdFactionsClaim();
	public CmdFactionsUnclaim cmdFactionsUnclaim = new CmdFactionsUnclaim();
	public CmdFactionsAccess cmdFactionsAccess = new CmdFactionsAccess();
	public CmdFactionsRelationAlly cmdFactionsRelationAlly = new CmdFactionsRelationAlly();
	public CmdFactionsRelationTruce cmdFactionsRelationTruce = new CmdFactionsRelationTruce();
	public CmdFactionsRelationNeutral cmdFactionsRelationNeutral = new CmdFactionsRelationNeutral();
	public CmdFactionsRelationEnemy cmdFactionsRelationEnemy = new CmdFactionsRelationEnemy();
	public CmdFactionsPerm cmdFactionsPerm = new CmdFactionsPerm();
	public CmdFactionsFlag cmdFactionsFlag = new CmdFactionsFlag();
	public CmdFactionsExpansions cmdFactionsExpansions = new CmdFactionsExpansions();
	public CmdFactionsXPlaceholder cmdFactionsTax = new CmdFactionsXPlaceholder("FactionsTax", "tax");
	public CmdFactionsXPlaceholder cmdFactionsDynmap = new CmdFactionsXPlaceholder("FactionsDynmap", "dynmap");
	public CmdFactionsAdmin cmdFactionsAdmin = new CmdFactionsAdmin();
	public CmdFactionsDisband cmdFactionsDisband = new CmdFactionsDisband();
	public CmdFactionsPowerBoost cmdFactionsPowerBoost = new CmdFactionsPowerBoost();
	public VersionCommand cmdFactionsVersion = new VersionCommand(Factions.get(), Perm.VERSION.node, "v", "version");
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactions()
	{
		// SubCommands
		this.addSubCommand(HelpCommand.get());
		this.addSubCommand(this.cmdFactionsList);
		this.addSubCommand(this.cmdFactionsFaction);
		this.addSubCommand(this.cmdFactionsPlayer);
		this.addSubCommand(this.cmdFactionsJoin);
		this.addSubCommand(this.cmdFactionsLeave);
		this.addSubCommand(this.cmdFactionsHome);
		this.addSubCommand(this.cmdFactionsMap);
		this.addSubCommand(this.cmdFactionsCreate);
		this.addSubCommand(this.cmdFactionsName);
		this.addSubCommand(this.cmdFactionsDescription);
		this.addSubCommand(this.cmdFactionsMotd);
		this.addSubCommand(this.cmdFactionsSethome);
		this.addSubCommand(this.cmdFactionsUnsethome);
		this.addSubCommand(this.cmdFactionsInvite);
		this.addSubCommand(this.cmdFactionsKick);
		this.addSubCommand(this.cmdFactionsTitle);
		this.addSubCommand(this.cmdFactionsPromote);
		this.addSubCommand(this.cmdFactionsDemote);
		this.addSubCommand(this.cmdFactionsOfficer);
		this.addSubCommand(this.cmdFactionsLeader);
		this.addSubCommand(this.cmdFactionsMoney);
		this.addSubCommand(this.cmdFactionsSeeChunk);
		this.addSubCommand(this.cmdFactionsSeeChunkOld);
		this.addSubCommand(this.cmdFactionsClaim);
		this.addSubCommand(this.cmdFactionsUnclaim);
		this.addSubCommand(this.cmdFactionsAccess);
		this.addSubCommand(this.cmdFactionsRelationAlly);
		this.addSubCommand(this.cmdFactionsRelationTruce);
		this.addSubCommand(this.cmdFactionsRelationNeutral);
		this.addSubCommand(this.cmdFactionsRelationEnemy);
		this.addSubCommand(this.cmdFactionsPerm);
		this.addSubCommand(this.cmdFactionsFlag);
		this.addSubCommand(this.cmdFactionsExpansions);
		this.addSubCommand(this.cmdFactionsTax);
		this.addSubCommand(this.cmdFactionsDynmap);
		this.addSubCommand(this.cmdFactionsAdmin);
		this.addSubCommand(this.cmdFactionsDisband);
		this.addSubCommand(this.cmdFactionsPowerBoost);
		this.addSubCommand(this.cmdFactionsVersion);
		
		// Deprecated Commands
		this.addSubCommand(new CmdFactionsXDeprecated(this.cmdFactionsClaim.cmdFactionsClaimAuto, "autoclaim"));
		this.addSubCommand(new CmdFactionsXDeprecated(this.cmdFactionsUnclaim.cmdFactionsUnclaimAll, "unclaimall"));
		this.addSubCommand(new CmdFactionsXDeprecated(this.cmdFactionsFlag, "open"));
		this.addSubCommand(new CmdFactionsXDeprecated(this.cmdFactionsFaction, "show", "who"));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public List<String> getAliases()
	{
		return MConf.get().aliasesF;
	}

}
