package com.massivecraft.factions.zcore.persist;

public class SaveTask implements Runnable
{
	public void run()
	{
		EM.saveAllToDisc();
	}
}
