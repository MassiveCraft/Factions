package com.massivecraft.factions.cmd;

import java.util.Collection;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rank;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRankList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsRankList()
	{
		// Parameters
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "their");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		int page = this.readArg();
		Faction faction = this.readArg(msenderFaction);
		
		// Are we trying to show other?
		this.tryShowOther();
		
		// Pager Prepare
		Collection<Rank> ranks = faction.getRankCollection();
		String title = Txt.parse("Ranks for %s", faction.describeTo(msender));
		List<String> args = MUtil.list(String.valueOf(page), faction.getName());
		
		// Pager Create
		final Pager<Rank> pager = new Pager<>(this, title, page, ranks, new Msonifier<Rank>()
		{
			@Override
			public Mson toMson(Rank rank, int index)
			{
				return rank.visualize();
			}
		});
		
		// Pager Args
		pager.setArgs(args);
		
		// Pager message
		pager.messageAsync();
	}
	
	private void tryShowOther() throws MassiveException
	{
		// Are we trying to show other?
		if (!this.argIsSet(0)) return;
		
		// Are we overriding?
		if (msender.isOverriding()) return;
		
		// Throw if they don't have the permission to show
		Perm.RANK_SHOW.hasThrow(sender, true);
	}
	
}
