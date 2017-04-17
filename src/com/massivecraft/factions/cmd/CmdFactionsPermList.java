package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPermColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.predicate.Predicate;
import org.bukkit.Bukkit;

import java.util.List;

public class CmdFactionsPermList extends FactionsCommand
{
	// -------------------------------------------- //
	// REUSABLE PREDICATE
	// -------------------------------------------- //
	
	private static final Predicate<MPerm> PREDICATE_MPERM_VISIBLE = new Predicate<MPerm>()
	{
		@Override
		public boolean apply(MPerm mperm)
		{
			return mperm.isVisible();
		}
	};
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermList()
	{
		// Parameters
		this.addParameter(Parameter.getPage());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameter
		int page = this.readArg();
		
		// Pager create
		String title = String.format("Perms for %s", msenderFaction.describeTo(msender));
		final Pager<MPerm> pager = new Pager<>(this, title, page, new Stringifier<MPerm>()
		{
			@Override
			public String toString(MPerm mperm, int index)
			{
				return mperm.getDesc(true, true);
			}
		});
		
		final Predicate<MPerm> predicate = msender.isOverriding() ? null : PREDICATE_MPERM_VISIBLE;
		
		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				// Get items
				List<MPerm> items = MPermColl.get().getAll(predicate);
				
				// Pager items
				pager.setItems(items);
				
				// Pager message
				pager.message();
			}
		});
	}

}
