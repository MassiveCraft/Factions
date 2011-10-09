package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.zcore.MPlugin;

public class SaveTask implements Runnable
{
	MPlugin p;
	public SaveTask(MPlugin p)
	{
		this.p = p;
	}
	
	public void run()
	{
		p.preSaveTask();
		EM.saveAllToDisc();
		p.postSaveTask();
	}
}
