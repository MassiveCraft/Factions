package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.zcore.MPlugin;

public class SaveTask implements Runnable {

    private static boolean running = false;

    MPlugin p;

    public SaveTask(MPlugin p) {
        this.p = p;
    }

    public void run() {
        if (!p.getAutoSave() || running) {
            return;
        }
        running = true;
        p.preAutoSave();
        Factions.getInstance().forceSave(false);
        FPlayers.getInstance().forceSave(false);
        Board.getInstance().forceSave(false);
        p.postAutoSave();
        running = false;
    }
}
