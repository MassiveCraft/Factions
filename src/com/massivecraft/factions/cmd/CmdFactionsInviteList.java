package com.massivecraft.factions.cmd;

import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.cmd.MassiveCommandException;
import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.pager.PagerSimple;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsInviteList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsInviteList()
	{
		// Aliases
		this.addAliases("l", "list");

		// Args
		this.addOptionalArg("page", "1");
		this.addOptionalArg("faction", "you");
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.INVITE_LIST.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //	
	
	@Override
	public void perform() throws MassiveCommandException
	{		
		// Args	
		Integer pageHumanBased = this.arg(0, ARInteger.get(), 1);
		
		Faction faction = this.arg(1, ARFaction.get(), msenderFaction);
		
		if ( faction != msenderFaction && ! Perm.INVITE_LIST_OTHER.has(sender, true)) return;
		
		// MPerm
		if ( ! MPerm.getPermInvite().has(msender, msenderFaction, true)) return;
		
		// Create Pager
		final List<MPlayer> mplayers = faction.getInvitedMPlayers();
		final PagerSimple<MPlayer> pager = new PagerSimple<MPlayer>(mplayers, sender);
		
		// Use Pager
		List<String> messages = pager.getPageTxt(pageHumanBased, "Invited Players List", new Stringifier<MPlayer>(){
			
			public String toString(MPlayer target)
			{
				// TODO: Madus would like to implement this in MPlayer
				String targetName = target.getDisplayName(msender);
				String isAre = target == msender ? "are" : "is";
				Rel targetRank = target.getRole();
				Faction targetFaction = target.getFaction();
				String theAan = targetRank == Rel.LEADER ? "the" : Txt.aan(targetRank.name());
				String rankName = Txt.getNicedEnum(targetRank).toLowerCase();
				String ofIn = targetRank == Rel.LEADER ? "of" : "in";
				String factionName = targetFaction.describeTo(msender, true);
				if (targetFaction == msenderFaction)
				{
					factionName = factionName.toLowerCase();
				}
				return Txt.parse("%s <i>%s %s <h>%s <i>%s %s<i>.", targetName, isAre, theAan, rankName, ofIn, factionName);
			}
		});
		
		// Send message
		sendMessage(messages);
	}
	
}
