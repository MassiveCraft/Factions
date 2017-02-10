package com.massivecraft.factions.cmd;

import java.util.HashSet;
import java.util.Set;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.cmd.type.TypeRank;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.event.EventFactionsRankChange;
import com.massivecraft.massivecore.MassiveException;
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
	
	// End faction (the faction they are changed to)
	private Faction endFaction = null;
	private boolean factionChange = false;
	
	// Ranks
	private Rel senderRank = null;
	private Rel targetRank = null;
	private Rel rank = null;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRank()
	{
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeRank.get(), "action", "show");
		this.addParameter(TypeFaction.get(), "faction", "their");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// This sets target and much other.
		this.registerFields();
			
		// Sometimes we just want to show the rank.
		if ( ! this.argIsSet(1))
		{
			if ( ! Perm.RANK_SHOW.has(sender, true)) return;
			this.showRank();
			return;
		}
		
		// Permission check.
		if ( ! Perm.RANK_ACTION.has(sender, true)) return;
		
		// Is the player allowed or not. Method can be found later down.
		this.ensureAllowed();
		
		if (factionChange)
		{	
			this.changeFaction();
		}
		
		// Does the change make sense.
		this.ensureMakesSense();
		
		// Event
		EventFactionsRankChange event = new EventFactionsRankChange(sender, target, rank);
		event.run();
		if (event.isCancelled()) return;
		rank = event.getNewRank();
		
		// Change the rank.
		this.changeRank();
	}

	// This is always run after performing a MassiveCommand.
	@Override
	public void senderFields(boolean set)
	{
		super.senderFields(set);
		
		if ( ! set)
		{
			this.unregisterFields();			
		}
	}

	// -------------------------------------------- //
	// PRIVATE: REGISTER & UNREGISTER
	// -------------------------------------------- //

	private void registerFields() throws MassiveException
	{
		// Getting the target and faction.
		target = this.readArg(msender);
		targetFaction = target.getFaction();
		
		
		// Ranks
		senderRank = msender.getRole();
		targetRank = target.getRole();
		
		// Rank if any passed.
		if (this.argIsSet(1))
		{
			this.setParameterType(1, TypeRank.get(targetRank));
			rank = this.readArg();
		}
		
		// Changing peoples faction.
		endFaction = this.readArgAt(2, targetFaction);
		factionChange = (endFaction != targetFaction);

	}
	
	private void unregisterFields()
	{
		targetFaction = null;
		target = null;
		
		senderRank = null;
		targetRank = null;
		rank = null;
	}

	// -------------------------------------------- //
	// PRIVATE: ENSURE
	// -------------------------------------------- //
	
	private void ensureAllowed() throws MassiveException
	{
		// People with permission don't follow the normal rules.
		if (msender.isOverriding()) return;
		
		// If somone gets the leadership of wilderness (Which has happened before).
		// We can at least try to limit their powers.
		if (endFaction.isNone())
		{
			throw new MassiveException().addMsg("%s <b>doesn't use ranks sorry :(", targetFaction.getName() );
		}
		
		if (target == msender)
		{
			// Don't change your own rank.
			throw new MassiveException().addMsg("<b>The target player mustn't be yourself.");
		}
		
		if (targetFaction != msenderFaction)
		{
			// Don't change ranks outside of your faction.
			throw new MassiveException().addMsg("%s <b>is not in the same faction as you.", target.describeTo(msender, true));
		}
		
		if (factionChange)
		{
			// Don't change peoples faction
			throw new MassiveException().addMsg("<b>You can't change %s's <b>faction.", target.describeTo(msender));
		}

		if (senderRank.isLessThan(rankReq))
		{
			// You need a specific rank to change ranks.
			throw new MassiveException().addMsg("<b>You must be <h>%s <b>or higher to change ranks.", Txt.getNicedEnum(rankReq).toLowerCase());
		}
		
		// The following two if statements could be merged. 
		// But isn't for the sake of nicer error messages.
		if (senderRank == targetRank)
		{
			// You can't change someones rank if it is equal to yours.
			throw new MassiveException().addMsg("<h>%s <b>can't manage eachother.", Txt.getNicedEnum(rankReq)+"s");
		}
		
		if (senderRank.isLessThan(targetRank))
		{
			// You can't change someones rank if it is higher than yours.
			throw new MassiveException().addMsg("<b>You can't manage people of higher rank.");
		}
		
		// The following two if statements could be merged. 
		// But isn't for the sake of nicer error messages.
		if (senderRank == rank && senderRank != Rel.LEADER)
		{
			// You can't set ranks equal to your own. Unless you are the leader.
			throw new MassiveException().addMsg("<b>You can't set ranks equal to your own.");
		}
		
		if (senderRank.isLessThan(rank))
		{
			// You can't set ranks higher than your own.
			throw new MassiveException().addMsg("<b>You can't set ranks higher than your own.");
		}
	}
	
	private void ensureMakesSense() throws MassiveException
	{
		// Don't change their rank to something they already are.
		if (target.getRole() == rank)
		{
			throw new MassiveException().addMsg("%s <b>is already %s.", target.describeTo(msender), rank.getDescPlayerOne());
		}
	}
	
	// -------------------------------------------- //
	// PRIVATE: SHOW
	// -------------------------------------------- //
	
	private void showRank()
	{
		// Damn you grammar, causing all these checks.
		String targetName = target.describeTo(msender, true);
		String isAre = (target == msender) ? "are" : "is"; // "you are" or "he is"
		String theAan = (targetRank == Rel.LEADER) ? "the" : Txt.aan(targetRank.name()); // "a member", "an officer" or "the leader"
		String rankName = Txt.getNicedEnum(targetRank).toLowerCase();
		String ofIn = (targetRank == Rel.LEADER) ? "of" : "in"; // "member in" or "leader of"
		String factionName = targetFaction.describeTo(msender, true);
		if (targetFaction == msenderFaction)
		{
			// Having the "Y" in "Your faction" being uppercase in the middle of a sentence makes no sense.
			factionName = factionName.toLowerCase();
		}
		if (targetFaction.isNone())
		{
			// Wilderness aka none doesn't use ranks
			msg("%s <i>%s factionless", targetName, isAre);
		}
		else
		{
			// Derp	is a member in Faction
			msg("%s <i>%s %s <h>%s <i>%s %s<i>.", targetName, isAre, theAan, rankName, ofIn, factionName);
		}
	}
	
	// -------------------------------------------- //
	// PRIVATE: CHANGE FACTION
	// -------------------------------------------- //
	
	private void changeFaction() throws MassiveException
	{	
		// Don't change a leader to a new faction.
		if (targetRank == Rel.LEADER)
		{
			throw new MassiveException().addMsg("<b>You cannot remove the present leader. Demote them first.");
		}
		
		// Event
		EventFactionsMembershipChange membershipChangeEvent = new EventFactionsMembershipChange(sender, msender, endFaction, MembershipChangeReason.RANK);
		membershipChangeEvent.run();
		if (membershipChangeEvent.isCancelled()) throw new MassiveException();
		
		// Apply
		target.resetFactionData();
		target.setFaction(endFaction);
		
		// No longer invited.
		endFaction.setInvited(target, false);

		// Create recipients
		Set<MPlayer> recipients = new HashSet<MPlayer>();
		recipients.addAll(targetFaction.getMPlayersWhereOnline(true));
		recipients.addAll(endFaction.getMPlayersWhereOnline(true));
		recipients.add(msender);
		
		// Send message
		for (MPlayer recipient : recipients)
		{
			recipient.msg("%s <i>was moved from <i>%s to <i>%s<i>.", target.describeTo(recipient), targetFaction.describeTo(recipient), endFaction.describeTo(recipient));
		}
		
		// Derplog
		if (MConf.get().logFactionJoin)
		{
			Factions.get().log(Txt.parse("%s moved %s from %s to %s.", msender.getName(), target.getName(), targetFaction.getName(), endFaction.getName()));
		}
		
		// Now we don't need the old values.
		targetFaction = target.getFaction();
		targetRank = target.getRole();
		senderRank = msender.getRole(); // In case they changed their own rank
	}
	
	// -------------------------------------------- //
	// PRIVATE: CHANGE RANK
	// -------------------------------------------- //
	
	private void changeRank() throws MassiveException
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
		
		// Promote the new leader.
		target.setRole(Rel.LEADER);

		// Inform everyone, this includes sender and target.
		for (MPlayer recipient : MPlayerColl.get().getAllOnline())
		{
			String changerName = senderIsConsole ? "A server admin" : msender.describeTo(recipient);
			recipient.msg("%s<i> gave %s<i> the leadership of %s<i>.", changerName, target.describeTo(recipient), targetFaction.describeTo(recipient));
		}
	}
	
	private void changeRankOther() throws MassiveException
	{
		// If the target is currently the leader and faction isn't permanent a new leader should be promoted.
		// Sometimes a bug occurs and multiple leaders exist. Then we should be able to demote without promoting new leader
		if (targetRank == Rel.LEADER && ( ! MConf.get().permanentFactionsDisableLeaderPromotion || ! targetFaction.getFlag(MFlag.ID_PERMANENT)) && targetFaction.getMPlayersWhereRole(Rel.LEADER).size() == 1) 
			// This if statement is very long. Should I nest it for readability?
		{
			targetFaction.promoteNewLeader(); // This might disband the faction.
			
			// So if the faction disbanded...
			if (targetFaction.detached())
			{
				// ... we inform the sender.
				target.resetFactionData();
				throw new MassiveException().addMsg("<i>The target was a leader and got demoted. The faction disbanded and no rank was set.");
			}
		}

		// Create recipients
		Set<MPlayer> recipients = new HashSet<MPlayer>();
		recipients.addAll(targetFaction.getMPlayers());
		recipients.add(msender);
		
		// Were they demoted or promoted?
		String change = (rank.isLessThan(targetRank) ? "demoted" : "promoted");
		
		// The rank will be set before the msg, so they have the appropriate prefix.
		target.setRole(rank);
		String oldRankName = Txt.getNicedEnum(targetRank).toLowerCase();
		String rankName = Txt.getNicedEnum(rank).toLowerCase();

		// Send message
		for(MPlayer recipient : recipients)
		{
			String targetName = target.describeTo(recipient, true);
			String wasWere = (recipient == target) ? "were" : "was";
			recipient.msg("%s<i> %s %s from %s to <h>%s <i>in %s<i>.", targetName, wasWere, change, oldRankName, rankName, targetFaction.describeTo(msender));
		}
	}
	
}
