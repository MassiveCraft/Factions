package org.mcteam.factions;

public class SaveTask implements Runnable {

	//TODO are they removed on disable?
	
	@Override
	public void run() {
		Factions.saveAll();
	}

}
