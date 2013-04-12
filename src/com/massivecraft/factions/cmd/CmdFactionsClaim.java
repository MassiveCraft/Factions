package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.mcore.ps.PS;


public class CmdFactionsClaim extends FCommand
{
	
	public CmdFactionsClaim()
	{
		super();
		this.aliases.add("claim");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		this.optionalArgs.put("radius", "1");
		
		this.permission = Perm.CLAIM.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		// Read and validate input
		final Faction forFaction = this.argAsFaction(0, myFaction);
		int radius = this.argAsInt(1, 1);

		if (radius < 1)
		{
			msg("<b>If you specify a radius, it must be at least 1.");
			return;
		}

		if (radius < 2)
		{
			// single chunk
			fme.attemptClaim(forFaction, me.getLocation(), true);
		}
		else
		{
			// radius claim
			if (! Perm.CLAIM_RADIUS.has(sender, false))
			{
				msg("<b>You do not have permission to claim in a radius.");
				return;
			}

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
}
