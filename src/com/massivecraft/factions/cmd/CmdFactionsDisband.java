package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.event.FactionsEventDisband;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsDisband extends FCommand
{
	public CmdFactionsDisband()
	{
		this.addAliases("disband");
		
		this.addOptionalArg("faction", "you");

		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.DISBAND.node));
	}
	
	@Override
	public void perform()
	{	
		// Args
		Faction faction = this.arg(0, ARFaction.get(usender), usenderFaction);
		if (faction == null) return;
		
		// FPerm
		if ( ! FPerm.DISBAND.has(usender, faction, true)) return;

		// Verify
		if (faction.getFlag(FFlag.PERMANENT))
		{
			msg("<i>这是一个永久公会, 你不能解散它.");
			return;
		}

		// Event
		FactionsEventDisband event = new FactionsEventDisband(me, faction);
		event.run();
		if (event.isCancelled()) return;

		// Merged Apply and Inform
		
		// Run event for each player in the faction
		for (UPlayer uplayer : faction.getUPlayers())
		{
			FactionsEventMembershipChange membershipChangeEvent = new FactionsEventMembershipChange(sender, uplayer, FactionColls.get().get(faction).getNone(), MembershipChangeReason.DISBAND);
			membershipChangeEvent.run();
		}

		// Inform all players
		for (UPlayer uplayer : UPlayerColls.get().get(usender).getAllOnline())
		{
			String who = usender.describeTo(uplayer);
			if (uplayer.getFaction() == faction)
			{
				uplayer.msg("<h>%s<i> 解散你的公会.", who);
			}
			else
			{
				uplayer.msg("<h>%s<i> 解散工会 %s.", who, faction.getName(uplayer));
			}
		}
		
		if (MConf.get().logFactionDisband)
		{
			Factions.get().log(Txt.parse("<i>公会 <h>%s <i>(<h>%s<i>) 被 <h>%s<i> 解散.", faction.getName(), faction.getId(), usender.getDisplayName()));
		}		
		
		faction.detach();
	}
}
