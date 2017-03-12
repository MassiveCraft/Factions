package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.command.MassiveCommandDeprecated;
import com.massivecraft.massivecore.command.MassiveCommandVersion;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;

import java.util.List;

public class CmdFactions extends FactionsCommand
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static CmdFactions i = new CmdFactions();
	public static CmdFactions get() { return i; }
	
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
	public CmdFactionsRankOld cmdFactionsRankOldOwner = new CmdFactionsRankOld("owner");
	public CmdFactionsRankOld cmdFactionsRankOldOfficer = new CmdFactionsRankOld("officer");
	public CmdFactionsRankOld cmdFactionsRankOldModerator = new CmdFactionsRankOld("moderator");
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
	public CmdFactionsRelation cmdFactionsRelation = new CmdFactionsRelation();
	public CmdFactionsRelationOld cmdFactionsRelationOldAlly = new CmdFactionsRelationOld("ally");
	public CmdFactionsRelationOld cmdFactionsRelationOldTruce = new CmdFactionsRelationOld("truce");
	public CmdFactionsRelationOld cmdFactionsRelationOldNeutral = new CmdFactionsRelationOld("neutral");
	public CmdFactionsRelationOld cmdFactionsRelationOldEnemy = new CmdFactionsRelationOld("enemy");
	public CmdFactionsPerm cmdFactionsPerm = new CmdFactionsPerm();
	public CmdFactionsFlag cmdFactionsFlag = new CmdFactionsFlag();
	public CmdFactionsUnstuck cmdFactionsUnstuck = new CmdFactionsUnstuck();
	public CmdFactionsExpansions cmdFactionsExpansions = new CmdFactionsExpansions();
	public CmdFactionsXPlaceholder cmdFactionsTax = new CmdFactionsXPlaceholder("FactionsTax", "tax");
	public CmdFactionsXPlaceholder cmdFactionsDynmap = new CmdFactionsXPlaceholder("FactionsDynmap", "dynmap");
	public CmdFactionsOverride cmdFactionsOverride = new CmdFactionsOverride();
	public CmdFactionsDisband cmdFactionsDisband = new CmdFactionsDisband();
	public CmdFactionsPowerBoost cmdFactionsPowerBoost = new CmdFactionsPowerBoost();
	public CmdFactionsSetpower cmdFactionsSetpower = new CmdFactionsSetpower();
	public CmdFactionsConfig cmdFactionsConfig = new CmdFactionsConfig();
	public CmdFactionsClean cmdFactionsClean = new CmdFactionsClean();
	public MassiveCommandVersion cmdFactionsVersion = new MassiveCommandVersion(Factions.get()).setAliases("v", "version").addRequirements(RequirementHasPerm.get(Perm.VERSION));
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactions()
	{
		// Deprecated Commands
		this.addChild(new MassiveCommandDeprecated(this.cmdFactionsClaim.cmdFactionsClaimAuto, "autoclaim"));
		this.addChild(new MassiveCommandDeprecated(this.cmdFactionsUnclaim.cmdFactionsUnclaimAll, "unclaimall"));
		this.addChild(new MassiveCommandDeprecated(this.cmdFactionsFlag, "open"));
		this.addChild(new MassiveCommandDeprecated(this.cmdFactionsFaction, "show", "who"));
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
