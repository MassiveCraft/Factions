package com.massivecraft.factions.task;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Factions;

public class EconLandRewardTask implements Runnable
{
	double rate;
	
	public EconLandRewardTask()
	{
		this.rate = ConfServer.econLandRewardTaskRunsEveryXMinutes;
	}

	@Override
	public void run()
	{
		FactionColl.get().econLandRewardRoutine();
		
		// TODO: This technique is TPS dependent and wrong.
		// Instead of restarting a TPS dependent task the task should poll every once in a while for the system millis.
		// With such a setup the need for restarts are gone.
		
		// maybe setting has been changed? if so, restart task at new rate
		if (this.rate != ConfServer.econLandRewardTaskRunsEveryXMinutes)
		{
			Factions.get().startEconLandRewardTask(true);
		}
	}
}
