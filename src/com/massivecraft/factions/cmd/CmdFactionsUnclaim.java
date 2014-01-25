package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.task.SpiralTask;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.arg.ARInteger;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;


public class CmdFactionsUnclaim extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsUnclaim()
	{
		// Aliases
		this.addAliases("unclaim");
		
		// Args
		this.addOptionalArg("radius", "1");

		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.UNCLAIM.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args+		
		Integer radius = this.arg(0, ARInteger.get(), 1);
		if (radius == null) return;

		PS chunk = PS.valueOf(me).getChunk(true);
		Faction newFaction = FactionColls.get().get(me).getNone();

		// Apply
		// single chunk
		if (radius < 2)
		{
			usender.tryClaim(FactionColls.get().get(me).getNone(), chunk, true, true);
			return;
		}

		// radius claim
		if (!Perm.UNCLAIM_RADIUS.has(sender, false))
		{
			msg("<b>You do not have permission to unclaim in a radius.");
			return;
		}

		new SpiralTask(PS.valueOf(me), radius)
		{
			private int failCount = 0;
			private final int limit = MConf.get().radiusUnClaimFailureLimit  - 1;

			@Override
			public boolean work()
			{
				boolean success = usender.tryClaim(FactionColls.get().get(me).getNone(), PS.valueOf(this.currentLocation()), true, true);
				if (success)
				{
					this.failCount = 0;
				}
				else if (this.failCount++ >= this.limit)
				{
					this.stop();
					return false;
				}
				return true;
			}
		};

	}
	
}
