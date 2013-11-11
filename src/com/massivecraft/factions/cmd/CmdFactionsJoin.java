package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsJoin extends FCommand
{
	public CmdFactionsJoin()
	{
		this.addAliases("join");
		
		this.addRequiredArg("faction");
		this.addOptionalArg("player", "you");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.JOIN.node));
	}
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get(sender));
		if (faction == null) return;

		UPlayer uplayer = this.arg(1, ARUPlayer.getStartAny(sender), usender);
		if (uplayer == null) return;
		Faction uplayerFaction = uplayer.getFaction();
		
		boolean samePlayer = uplayer == usender;
		
		// Validate
		if (!samePlayer  && ! Perm.JOIN_OTHERS.has(sender, false))
		{
			msg("<b>你没有权限移动别的玩家加入公会.");
			return;
		}

		if (faction == uplayerFaction)
		{
			msg("<i>%s <i>%s already a member of %s<i>.", uplayer.describeTo(usender, true), (samePlayer ? "are" : "is"), faction.getName(usender));
			return;
		}

		if (UConf.get(faction).factionMemberLimit > 0 && faction.getUPlayers().size() >= UConf.get(faction).factionMemberLimit)
		{
			msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getName(usender), UConf.get(faction).factionMemberLimit, uplayer.describeTo(usender, false));
			return;
		}

		if (uplayerFaction.isNormal())
		{
			msg("<b>%s must leave %s current faction first.", uplayer.describeTo(usender, true), (samePlayer ? "your" : "their"));
			return;
		}

		if (!UConf.get(faction).canLeaveWithNegativePower && uplayer.getPower() < 0)
		{
			msg("<b>%s cannot join a faction with a negative power level.", uplayer.describeTo(usender, true));
			return;
		}

		if( ! (faction.isOpen() || faction.isInvited(uplayer) || usender.isUsingAdminMode() || Perm.JOIN_ANY.has(sender, false)))
		{
			msg("<i>这个公会需要申请才能够加入.");
			if (samePlayer)
			{
				faction.msg("%s<i> 想要加入你的公会.", uplayer.describeTo(faction, true));
			}
			return;
		}

		// Event
		FactionsEventMembershipChange membershipChangeEvent = new FactionsEventMembershipChange(sender, usender, faction, MembershipChangeReason.JOIN);
		membershipChangeEvent.run();
		if (membershipChangeEvent.isCancelled()) return;
		
		// Inform
		if (!samePlayer)
		{
			uplayer.msg("<i>%s <i>你已经加入公会 %s<i>.", usender.describeTo(uplayer, true), faction.getName(uplayer));
		}
		faction.msg("<i>%s <i>加入 <lime>你的公会<i>.", uplayer.describeTo(faction, true));
		usender.msg("<i>%s <i>加入成功 %s<i>.", uplayer.describeTo(usender, true), faction.getName(usender));
		
		// Apply
		uplayer.resetFactionData();
		uplayer.setFaction(faction);
	    
		faction.setInvited(uplayer, false);

		// Derplog
		if (MConf.get().logFactionJoin)
		{
			if (samePlayer)
			{
				Factions.get().log(Txt.parse("%s 已经加入了公会 %s.", uplayer.getName(), faction.getName()));
			}
			else
			{
				Factions.get().log(Txt.parse("%s 移动玩家 %s 加入公会 %s.", usender.getName(), uplayer.getName(), faction.getName()));
			}
		}
	}
}
