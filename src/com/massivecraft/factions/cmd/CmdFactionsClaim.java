package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.task.SpiralTask;
import com.massivecraft.mcore.cmd.arg.ARInteger;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;


public class CmdFactionsClaim extends FCommand
{
	
	public CmdFactionsClaim()
	{
		this.addAliases("claim");
		
		this.addOptionalArg("faction", "you");
		this.addOptionalArg("radius", "1");
		
		this.addRequirements(ReqHasPerm.get(Perm.CLAIM.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform()
	{
		// Args
		final Faction forFaction = this.arg(0, ARFaction.get(me));
		if (forFaction == null) return;
		
		Integer radius = this.arg(1, ARInteger.get(), 1);
		if (radius == null) return;
		
		// FPerm
		if (!FPerm.TERRITORY.has(sender, forFaction, true)) return;
		
		// Validate
		if (radius < 1)
		{
			msg("<b>If you specify a radius, it must be at least 1.");
			return;
		}

		// Apply
		
		// single chunk
		if (radius < 2)
		{
			fme.tryClaim(forFaction, PS.valueOf(me), true, true);
			return;
		}
		
		// radius claim
		if (!Perm.CLAIM_RADIUS.has(sender, false))
		{
			msg("<b>You do not have permission to claim in a radius.");
			return;
		}

		// TODO: There must be a better way than using a spiral task.
		// TODO: Do some research to allow for claming sets of chunks in a batch with atomicity.
		// This will probably result in an alteration to the owner change event.
		// It would possibly contain a set of chunks instead of a single chunk.
		
		new SpiralTask(PS.valueOf(me), radius)
		{
			private int failCount = 0;
			private final int limit = UConf.get(me).radiusClaimFailureLimit - 1;

			@Override
			public boolean work()
			{
				boolean success = fme.tryClaim(forFaction, PS.valueOf(this.currentLocation()), true, true);
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
