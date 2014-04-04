package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.zcore.MPlugin;

public class SaveTask implements Runnable
{
	static private boolean running = false;

	MPlugin p;
	public SaveTask(MPlugin p)
	{
		this.p = p;
	}
	
	public void run()
	{
		if ( ! p.getAutoSave() || running) return;
		running = true;
		p.preAutoSave();
		EM.saveAllToDisc();
		p.postAutoSave();
		running = false;
	}
}
