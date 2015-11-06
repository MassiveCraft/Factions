package com.massivecraft.factions.cmd;

import java.util.List;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.command.DeprecatedCommand;
import com.massivecraft.massivecore.command.VersionCommand;

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
	public CmdFactionsRank cmdFactionsRank = new CmdFactionsRank();
	public CmdFactionsRankOld cmdFactionsRankOldLeader = new CmdFactionsRankOld("leader");
	public CmdFactionsRankOld cmdFactionsRankOldOfficer = new CmdFactionsRankOld("officer");
	public CmdFactionsRankOld cmdFactionsRankOldPromote = new CmdFactionsRankOld("promote");
	public CmdFactionsRankOld cmdFactionsRankOldDemote = new CmdFactionsRankOld("demote");
	public CmdFactionsMoney cmdFactionsMoney = new CmdFactionsMoney();
	public CmdFactionsSeeChunk cmdFactionsSeeChunk = new CmdFactionsSeeChunk();
	public CmdFactionsSeeChunkOld cmdFactionsSeeChunkOld = new CmdFactionsSeeChunkOld();
	public CmdFactionsTerritorytitles cmdFactionsTerritorytitles = new CmdFactionsTerritorytitles();
	public CmdFactionsStatus cmdFactionsStatus = new CmdFactionsStatus();
	public CmdFactionsClaim cmdFactionsClaim = new CmdFactionsClaim();
	public CmdFactionsUnclaim cmdFactionsUnclaim = new CmdFactionsUnclaim();
	public CmdFactionsAccess cmdFactionsAccess = new CmdFactionsAccess();
	public CmdFactionsRelationAlly cmdFactionsRelationAlly = new CmdFactionsRelationAlly();
	public CmdFactionsRelationTruce cmdFactionsRelationTruce = new CmdFactionsRelationTruce();
	public CmdFactionsRelationNeutral cmdFactionsRelationNeutral = new CmdFactionsRelationNeutral();
	public CmdFactionsRelationEnemy cmdFactionsRelationEnemy = new CmdFactionsRelationEnemy();
	public CmdFactionsPerm cmdFactionsPerm = new CmdFactionsPerm();
	public CmdFactionsFlag cmdFactionsFlag = new CmdFactionsFlag();
	public CmdFactionsUnstuck cmdFactionsUnstuck = new CmdFactionsUnstuck();
	public CmdFactionsExpansions cmdFactionsExpansions = new CmdFactionsExpansions();
	public CmdFactionsXPlaceholder cmdFactionsTax = new CmdFactionsXPlaceholder("FactionsTax", "tax");
	public CmdFactionsXPlaceholder cmdFactionsDynmap = new CmdFactionsXPlaceholder("FactionsDynmap", "dynmap");
	public CmdFactionsAdmin cmdFactionsAdmin = new CmdFactionsAdmin();
	public CmdFactionsDisband cmdFactionsDisband = new CmdFactionsDisband();
	public CmdFactionsPowerBoost cmdFactionsPowerBoost = new CmdFactionsPowerBoost();
	public CmdFactionsSetpower cmdFactionsSetpower = new CmdFactionsSetpower();
	public VersionCommand cmdFactionsVersion = new VersionCommand(Factions.get(), Perm.VERSION.node, "v", "version");
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactions()
	{
		// Children
		this.addChild(this.cmdFactionsList);
		this.addChild(this.cmdFactionsFaction);
		this.addChild(this.cmdFactionsPlayer);
		this.addChild(this.cmdFactionsStatus);
		this.addChild(this.cmdFactionsJoin);
		this.addChild(this.cmdFactionsLeave);
		this.addChild(this.cmdFactionsHome);
		this.addChild(this.cmdFactionsMap);
		this.addChild(this.cmdFactionsCreate);
		this.addChild(this.cmdFactionsName);
		this.addChild(this.cmdFactionsDescription);
		this.addChild(this.cmdFactionsMotd);
		this.addChild(this.cmdFactionsSethome);
		this.addChild(this.cmdFactionsUnsethome);
		this.addChild(this.cmdFactionsInvite);
		this.addChild(this.cmdFactionsKick);
		this.addChild(this.cmdFactionsTitle);
		this.addChild(this.cmdFactionsRank);
		this.addChild(this.cmdFactionsRankOldLeader);
		this.addChild(this.cmdFactionsRankOldOfficer);
		this.addChild(this.cmdFactionsRankOldPromote);
		this.addChild(this.cmdFactionsRankOldDemote);
		this.addChild(this.cmdFactionsMoney);
		this.addChild(this.cmdFactionsSeeChunk);
		this.addChild(this.cmdFactionsSeeChunkOld);
		this.addChild(this.cmdFactionsTerritorytitles);
		this.addChild(this.cmdFactionsClaim);
		this.addChild(this.cmdFactionsUnclaim);
		this.addChild(this.cmdFactionsAccess);
		this.addChild(this.cmdFactionsRelationAlly);
		this.addChild(this.cmdFactionsRelationTruce);
		this.addChild(this.cmdFactionsRelationNeutral);
		this.addChild(this.cmdFactionsRelationEnemy);
		this.addChild(this.cmdFactionsPerm);
		this.addChild(this.cmdFactionsFlag);
		this.addChild(this.cmdFactionsUnstuck);
		this.addChild(this.cmdFactionsExpansions);
		this.addChild(this.cmdFactionsTax);
		this.addChild(this.cmdFactionsDynmap);
		this.addChild(this.cmdFactionsAdmin);
		this.addChild(this.cmdFactionsDisband);
		this.addChild(this.cmdFactionsPowerBoost);
		this.addChild(this.cmdFactionsSetpower);
		this.addChild(this.cmdFactionsVersion);
		
		// Deprecated Commands
		this.addChild(new DeprecatedCommand(this.cmdFactionsClaim.cmdFactionsClaimAuto, "autoclaim"));
		this.addChild(new DeprecatedCommand(this.cmdFactionsUnclaim.cmdFactionsUnclaimAll, "unclaimall"));
		this.addChild(new DeprecatedCommand(this.cmdFactionsFlag, "open"));
		this.addChild(new DeprecatedCommand(this.cmdFactionsFaction, "show", "who"));
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
