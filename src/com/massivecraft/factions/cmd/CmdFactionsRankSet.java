package com.massivecraft.factions.cmd;

import java.util.Set;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.cmd.type.TypeRank;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.event.EventFactionsRankChange;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRankSet extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTANT
	// -------------------------------------------- //
	
	private static final String MESSAGE_FORMAT = "%s <i>Rank has been set to %s<i>.";
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankSet()
	{
		// Parameters
		this.addParameter(TypeMPlayer.get(), "player");
		this.addParameter(TypeRank.get(), "rank");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		MPlayer mplayer = this.readArg();
		Rank rank = this.readArg();
		
		// Make sure sender can set this rank
		this.ensureRankSet(mplayer, rank);
		this.ensureRankUpdate(mplayer, rank);
		rank = this.ensureRankChangeEvent(mplayer, rank);
		
		// Set Rank
		this.setRank(mplayer, rank);
		
		// Prepare inform
		boolean same = mplayer == msender;
		String visualRank = TypeRank.get().getVisual(rank);
		String visualPlayer = mplayer.describeTo(msender);
		if (same) visualPlayer += "r";
		
		// Inform
		msg(MESSAGE_FORMAT, visualPlayer, visualRank);
		if (!same) mplayer.msg(MESSAGE_FORMAT, mplayer.describeTo(mplayer), visualRank);
	}
	
	// -------------------------------------------- //
	// ENSURE
	// -------------------------------------------- //
	
	private void ensureRankSet(MPlayer mplayer, Rank rank) throws MassiveException
	{
		// People with permission don't follow the normal rules.
		if (msender.isOverriding()) return;
		
		// Prepare
		MassiveException exception = new MassiveException();
		Faction faction = mplayer.getFaction();
		String describe = mplayer.describeTo(msender, true);
		Rank rankPlayer = mplayer.getRank();
		Rank rankSender = msender.getRank();
		Rank rankManager = faction.getRankManager();
		String rankManagerName = rankManager.getName();
		
		boolean isPermanent = faction.getFlag(MFlag.ID_PERMANENT);
		boolean isPermanentDemoteDisabled = MConf.get().permanentFactionsDisableLeaderPromotion;
		
		// Can't change rank in Wilderness
		if (faction.isNone()) throw exception.addMsg("%s <b>doesn't use ranks, sorry :(.", faction.getName());
		
		// Don't change your own rank.
		if (mplayer == msender) throw exception.addMsg("<b>The target player mustn't be yourself.");
		
		// Don't change ranks outside of your faction.
		if (faction != msenderFaction) throw exception.addMsg("%s <b>is not in the same faction as you.", describe);
		
		// You need to be rank manager to change ranks.
		if (msender.getRank().isLessThan(rankManager)) throw exception.addMsg("<b>You must be <h>%s <b>or higher to change ranks.", rankManagerName);
		
		// You can't change someones rank if it is equal to yours.
		if (rankPlayer == rank) throw exception.addMsg("<h>%s <b>can't manage eachother.", rankManagerName + "s");
		
		// You can't change someones rank if it is higher than yours.
		if (rankPlayer.isLessThan(rank)) throw exception.addMsg("<b>You can't manage people of higher rank.");
		
		// You can't set ranks equal to your own. Unless you are the leader.
		if (rankSender == rank && !rankSender.isLeader()) throw exception.addMsg("<b>You can't set ranks equal to your own.");
		
		// You can't promote new leaders for permanent factions if its disabled
		if (rank.isLeader() && isPermanent && isPermanentDemoteDisabled) throw exception.setMsg("<b>You can't promote a new leader for permanent factions.");
	}
	
	private void ensureRankUpdate(MPlayer mplayer, Rank rank) throws MassiveException
	{
		// Don't change their rank to something they already are.
		if (!mplayer.getRank().equals(rank)) return;
		
		// Prepare
		String describe = mplayer.describeTo(msender);
		String name = rank.getName();
		String aan = Txt.aan(name);
		
		// Inform
		throw new MassiveException().addMsg("%s <b>is already %s %s.", describe, aan, name);
	}
	
	private Rank ensureRankChangeEvent(MPlayer mplayer, Rank rank) throws MassiveException
	{
		// Create and Run
		EventFactionsRankChange event = new EventFactionsRankChange(sender, mplayer, rank);
		event.run();
		
		// Check
		if (event.isCancelled()) throw new MassiveException().setMsg("<b>Another plugin has prohibited the rank change.");
		
		// Return
		return event.getNewRank();
	}
	
	// -------------------------------------------- //
	// SET RANK
	// -------------------------------------------- //
	
	private void setRank(MPlayer mplayer, Rank rank) throws MassiveException
	{
		if (rank.isLeader())
		{
			this.setRankLeader(mplayer, rank);
		}
		else
		{
			this.setRankOther(mplayer, rank);
		}
	}
	
	private void setRankLeader(MPlayer mplayer, Rank rank) throws MassiveException
	{
		Faction faction = mplayer.getFaction();
		MPlayer leaderCurrent = faction.getLeader();
		if (leaderCurrent != null)
		{
			faction.demote(leaderCurrent);
			if (leaderCurrent != msender)
			{
				String visualSender = msender.describeTo(leaderCurrent, true);
				leaderCurrent.msg("<i>You have been demoted from the position of faction leader by %s<i>.", visualSender);
			}
		}
		
		// NOTE: Set before inform for prefix change
		mplayer.setRank(rank);
		
		// Inform everyone, this includes sender and target.
		for (MPlayer recipient : MPlayerColl.get().getAllOnline())
		{
			String changerName = senderIsConsole ? "A server admin" : msender.describeTo(recipient);
			String visualSender = mplayer.describeTo(recipient);
			String visualFaction = faction.describeTo(recipient);
			String message = Txt.parse("%s<i> gave %s<i> the leadership of %s<i>.", changerName, visualSender, visualFaction);
			
			recipient.message(message);
		}
	}
	
	private void setRankOther(MPlayer mplayer, Rank rank) throws MassiveException
	{
		Faction faction = mplayer.getFaction();
		Rank rankPlayer = mplayer.getRank();
		
		// NOTE: There has been occasions where there were more than one leader
		if (rankPlayer.isLeader() && faction.getMPlayersWhereLeader().size() == 1)
		{
			faction.promoteNewLeader();
			
			// So if the faction disbanded...
			if (faction.detached())
			{
				// ... we inform the sender.
				mplayer.resetFactionData();
				throw new MassiveException().addMsg("<i>The target was a leader and got demoted. The faction disbanded and no rank was set.");
			}
		}
		
		// Get recipients
		Set<MPlayer> recipients = new MassiveSet<>(faction.getMPlayers());
		recipients.add(msender);
		
		// Prepare inform
		String change = (mplayer.getRank().isLessThan(rank) ? "demoted" : "promoted");
		
		// NOTE: Set before inform for prefix change
		mplayer.setRank(rank);
		Mson rankNameOld = TypeRank.get().getVisualMson(rankPlayer);
		Mson rankNameNew = TypeRank.get().getVisualMson(rank);
		
		// Inform
		for (MPlayer recipient : recipients)
		{
			String describe = mplayer.describeTo(recipient, true);
			String wasWere = (recipient == mplayer) ? "were" : "was";
			recipient.msg("%s<i> %s %s from %s to <h>%s<i>.", describe, wasWere, change, rankNameOld, rankNameNew);
		}
	}
	
}
