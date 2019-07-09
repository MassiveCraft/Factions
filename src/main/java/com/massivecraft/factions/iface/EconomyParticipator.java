package com.massivecraft.factions.iface;

import com.massivecraft.factions.zcore.util.TL;

public interface EconomyParticipator extends RelationParticipator {

    public String getAccountId();

    public void msg(String str, Object... args);

    public void msg(TL translation, Object... args);
}