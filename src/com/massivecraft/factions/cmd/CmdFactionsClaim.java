package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.util.SpiralTask;
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
		Faction forFaction = this.arg(0, ARFaction.get());
		if (forFaction == null) return;
		
		Integer radius = this.arg(1, ARInteger.get(), 1);
		if (radius == null) return;
		

		if (radius < 1)
		{
			msg("<b>If you specify a radius, it must be at least 1.");
			return;
		}

		if (radius < 2)
		{
			// single chunk
			fme.attemptClaim(forFaction, me.getLocation(), true);
			return;
		}
		
		// radius claim
		if (! Perm.CLAIM_RADIUS.has(sender, false))
		{
			msg("<b>You do not have permission to claim in a radius.");
			return;
		}

		// TODO: I do not beleive in the spiral-task. Get rid of this. The failcount can be precalculated.
		new SpiralTask(PS.valueOf(me), radius)
		{
			private int failCount = 0;
			private final int limit = ConfServer.radiusClaimFailureLimit - 1;

			@Override
			public boolean work()
			{
				boolean success = fme.attemptClaim(forFaction, this.currentLocation(), true);
				if (success)
					failCount = 0;
				else if ( ! success && failCount++ >= limit)
				{
					this.stop();
					return false;
				}

				return true;
			}
		};
		
	}
}
