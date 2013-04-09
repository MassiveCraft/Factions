package com.massivecraft.factions.util;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;

public class AutoLeaveTask implements Runnable
{
	double rate;

	public AutoLeaveTask()
	{
		this.rate = ConfServer.autoLeaveRoutineRunsEveryXMinutes;
	}

	public void run()
	{
		FPlayers.i.autoLeaveOnInactivityRoutine();

		// maybe setting has been changed? if so, restart task at new rate
		if (this.rate != ConfServer.autoLeaveRoutineRunsEveryXMinutes)
			Factions.get().startAutoLeaveTask(true);
	}
}
