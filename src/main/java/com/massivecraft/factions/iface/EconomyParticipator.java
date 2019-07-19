package com.massivecraft.factions.iface;

import com.massivecraft.factions.zcore.util.TL;

public interface EconomyParticipator extends RelationParticipator {

    String getAccountId();

    void msg(String str, Object... args);

    void msg(TL translation, Object... args);
}
