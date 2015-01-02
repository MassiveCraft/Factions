package com.massivecraft.factions.cmd;

import java.util.List;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.cmd.arg.ARRank;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.event.EventFactionsRankChange;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRank extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	// The rank required to do any rank changes.
	final static Rel rankReq = Rel.OFFICER;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	// These fields are set upon perform() and unset afterwards.
	
	// Target
	private Faction targetFaction = null;
	private MPlayer target = null;
	
	// Roles
	private Rel senderRank = null;
	private Rel targetRank = null;
	private Rel rank = null;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRank()
	{
		// Aliases
		this.addAliases("rank");
	
		// Args
		this.addOptionalArg("player", "you");
		this.addOptionalArg("action", "show");
	
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.RANK.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// This sets target and much other. Returns false if not succeeded.
		if ( ! this.registerFields())
		{
			return;
		}
			
		// Sometimes we just want to show the rank.
		if ( ! this.argIsSet(1))
		{
			if ( ! Perm.RANK_SHOW.has(sender, true))
			{
				return;
			}
			this.showRank();
			return;
		}
		
		// Permission check.
		if ( ! Perm.RANK_ACTION.has(sender, true))
		{
			return;
		}
		
		// Is the player allowed or not. Method can be found later down.
		if ( ! this.isPlayerAllowed())
		{
			return;
		}
		
		// Does the change make sense.
		if ( ! this.isChangeRequired())
		{
			return;
		}
		
		EventFactionsRankChange event = new EventFactionsRankChange(sender, target, rank);
		event.run();
		if (event.isCancelled()) return;
		rank = event.getNewRank();
		
		// Change the rank.
		this.changeRank();
	}
	
	// This is always run after performing a MassiveCommand.
	@Override
	public void unsetSenderVars()
	{
		super.unsetSenderVars();
		this.unregisterFields();
	}

	// -------------------------------------------- //
	// PRIVATE
	// -------------------------------------------- //

	private boolean registerFields()
	{
		// Getting the target and faction.
		target = this.arg(0, ARMPlayer.getAny(), msender);
		if (null == target) return false;
		targetFaction = target.getFaction();
		
		// Rank if any passed.
		if (this.argIsSet(1))
		{
			rank = this.arg(1, ARRank.get(target.getRole()));
			if (null == rank) return false;
		}
		
		// Ranks
		senderRank = msender.getRole();
		targetRank = target.getRole();
		
		return true;
	}
	
	private void unregisterFields()
	{
		targetFaction = null;
		target = null;
		
		senderRank = null;
		targetRank = null;
		rank = null;
	}
	
	private void showRank()
	{
		String targetName = target.describeTo(msender, true);
		String isAre = target == msender ? "are" : "is";
		String theAan = targetRank == Rel.LEADER ? "the" : Txt.aan(targetRank.name());
		String rankName = Txt.getNicedEnum(targetRank).toLowerCase();
		String ofIn = targetRank == Rel.LEADER ? "of" : "in";
		String factionName = targetFaction.describeTo(msender, true);
		if (targetFaction == msenderFaction)
		{
			factionName = factionName.toLowerCase();
		}
		msg("%s <i>%s %s <h>%s <i>%s %s<i>.", targetName, isAre, theAan, rankName, ofIn, factionName);
	}

	private boolean isPlayerAllowed()
	{
		// People with permission don't follow the normal rules.
		if (msender.isUsingAdminMode())
		{
			return true;
		}
		
		// If somone gets the leadership of wilderness (Which has happened before).
		// We can at least try to limit their powers.
		if (targetFaction.isNone())
		{
			msg("%s <b>doesn't use ranks sorry :(", targetFaction.getName() );
			return false;
		}
		
		if (targetFaction != msenderFaction)
		{
			// Don't change ranks outside of your faction.
			msg("%s <b>is not in the same faction as you.", target.describeTo(msender));
			return false;
		}
		
		if (target == msender)
		{
			// Don't change your own rank.
			msg("<b>The target player mustn't be yourself.");
			return false;
		}
		

		if (senderRank.isLessThan(rankReq))
		{
			// You need a specific rank to change ranks.
			msg("<b>You must be %s or higher to change ranks.", Txt.getNicedEnum(rankReq).toLowerCase());
			return false;
		}
		
		// The following two if statements could be merged. 
		// But isn't for the sake of nicer error messages.
		if (senderRank == targetRank)
		{
			// You can't change someones rank if it is equal to yours.
			msg("<b>%s can't manage eachother.", Txt.getNicedEnum(rankReq)+"s");
			return false;
		}
		
		if (senderRank.isLessThan(targetRank))
		{
			// You can't change someones rank if it is higher than yours.
			msg("<b>You can't manage people of higher rank.");
			return false;
		}
		
		if (senderRank.isAtMost(rank) && senderRank != Rel.LEADER)
		{
			// You can't set ranks equal to or higer than your own. Unless you are the leader.
			msg("<b>You can't set ranks higher than or equal to your own.");
			return false;
		}
		
		// If it wasn't cancelled above, player is allowed.
		return true;
	}
	
	private boolean isChangeRequired()
	{
		// Just a nice msg. It would however be caught by an if statement below.
		if (target.getRole() == Rel.RECRUIT && arg(1).equalsIgnoreCase("demote"))
		{
			msg("%s <b>is already recruit.", target.describeTo(msender));
			return false;
		}
		
		// Just a nice msg. It would however be caught by an if statement below.
		if (target.getRole() == Rel.LEADER && arg(1).equalsIgnoreCase("promote"))
		{
			msg("%s <b>is already leader.", target.describeTo(msender));
			return false;
		}
		
		// There must be a change, else it is all waste of time.
		if (target.getRole() == rank)
		{
			msg("%s <b>already has that rank.", target.describeTo(msender));
			return false;
		}
		
		return true;
	}
	
	private void changeRank()
	{
		// In case of leadership change, we do special things not done in other rank changes.
		if (rank == Rel.LEADER)
		{
			this.changeRankLeader();
		}
		else
		{
			this.changeRankOther();
		}
	}

	private void changeRankLeader()
	{
		// If there is a current leader. Demote & inform them.
		MPlayer targetFactionCurrentLeader = targetFaction.getLeader();
		if (targetFactionCurrentLeader != null)
		{
			// Inform & demote the old leader.
			targetFactionCurrentLeader.setRole(Rel.OFFICER);
			if (targetFactionCurrentLeader != msender)
			{
				// They kinda know if they fired the command themself.
				targetFactionCurrentLeader.msg("<i>You have been demoted from the position of faction leader by %s<i>.", msender.describeTo(targetFactionCurrentLeader, true));
			}
		}
		
		// Inform & promote the new leader.
		target.setRole(Rel.LEADER);
		if (target != msender)
		{
			// They kinda know if they fired the command themself.
			target.msg("<i>You have been promoted to the position of faction leader by %s<i>.", msender.describeTo(target, true));
		}
		
		// Inform the msg sender
		msg("<i>You have promoted %s<i> to the position of faction leader.", target.describeTo(msender, true));
		
		// Inform everyone
		for (MPlayer recipient : MPlayerColl.get().getAllOnline())
		{
			String changerName = senderIsConsole ? "A server admin" : msender.describeTo(recipient);
			recipient.msg("%s<i> gave %s<i> the leadership of %s<i>.", changerName, target.describeTo(recipient), targetFaction.describeTo(recipient));
		}
	}
	
	private void changeRankOther()
	{
		// If the target is currently the leader and faction isn't permanent...
		if (targetRank == Rel.LEADER && !MConf.get().permanentFactionsDisableLeaderPromotion && targetFaction.getFlag(MFlag.ID_PERMANENT))
		{
			// ...we must promote a new one.
			targetFaction.promoteNewLeader();
		}
		// But if still no leader exists...
		if (targetFaction.getLeader() == null && ! targetFaction.getFlag(MFlag.ID_PERMANENT))
		{
			// ...we will disband it.
			// I'm kinda lazy, so I just make the console perform the command.
			Factions.get().getOuterCmdFactions().cmdFactionsDisband.execute(IdUtil.getConsole(), MUtil.list( targetFaction.getName() ));
		}
		
		List<MPlayer> recipients = targetFaction.getMPlayers();
		if ( ! recipients.contains(msender))
		{
			recipients.add(msender);
		}
		
		// Were they demoted or promoted?
		String change = (rank.isLessThan(targetRank) ? "demoted" : "promoted");
		
		// The rank will be set before the msg, so they have the appropriate prefix.
		target.setRole(rank);
		String oldRankName = Txt.getNicedEnum(targetRank).toLowerCase();
		String rankName = Txt.getNicedEnum(rank).toLowerCase();

		for(MPlayer recipient : recipients)
		{
			String targetName = target.describeTo(recipient, true);
			String wasWere = recipient == target ? "were" : "was";
			recipient.msg("%s<i> %s %s from %s to <h>%s <i>in %s<i>.", targetName, wasWere, change, oldRankName, rankName, targetFaction.describeTo(msender));
		}

	}
}
