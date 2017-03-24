package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;

import java.util.List;

public class CmdFactionsFlagShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlagShow()
	{
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(Parameter.getPage());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameters
		final Faction faction = this.readArg(msenderFaction);
		int page = this.readArg();
		
		// Pager create
		String title = "Flags for " + faction.describeTo(msender);
		Pager<MFlag> pager = new Pager<>(this, title, page, MFlag.getAll(), new Stringifier<MFlag>()
		{
			@Override
			public String toString(MFlag mflag, int index)
			{
				return mflag.getStateDesc(faction.getFlag(mflag), true, true, true, true, true);
			}
		});
		
		// Pager args
		List<String> pagerArgs = new MassiveList<>(
			faction.getId(),
			String.valueOf(page)
		);
		pager.setArgs(pagerArgs);
		
		// Pager message
		pager.messageAsync();
	}
	
}
