package com.massivecraft.factions.cmd;

import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsInviteList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsInviteList()
	{
		// Parameters
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //	
	
	@Override
	public void perform() throws MassiveException
	{		
		// Args	
		int page = this.readArg();
		
		Faction faction = this.readArg(msenderFaction);
		
		if ( faction != msenderFaction && ! Perm.INVITE_LIST_OTHER.has(sender, true)) return;
		
		// MPerm
		if ( ! MPerm.getPermInvite().has(msender, msenderFaction, true)) return;
		
		// Pager Create
		final List<MPlayer> mplayers = faction.getInvitedMPlayers();
		final Pager<MPlayer> pager = new Pager<MPlayer>(this, "Invited Players List", page, mplayers, new Stringifier<MPlayer>(){
			public String toString(MPlayer target, int index)
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
		
		// Pager Message
		pager.message();
	}
	
}
