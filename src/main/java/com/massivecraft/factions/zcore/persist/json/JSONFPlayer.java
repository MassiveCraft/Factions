package com.massivecraft.factions.zcore.persist.json;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;

public class JSONFPlayer extends MemoryFPlayer {
    private transient boolean remove = false;

    public JSONFPlayer(MemoryFPlayer arg0) {
        super(arg0);
    }

    public JSONFPlayer() {
        remove = false;
    }

    public JSONFPlayer(String id) {
        super(id);
    }

    @Override
    public void remove() {
        remove = true;
    }

    public boolean shouldBeSaved() {
        if (!this.hasFaction() && (this.getPowerRounded() == this.getPowerMaxRounded() || this.getPowerRounded() == (int) Math.round(Conf.powerPlayerStarting))) {
            return false;
        }
        return !remove;
    }
}
