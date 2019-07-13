package com.massivecraft.factions.struct;

import com.massivecraft.factions.zcore.util.TL;

public enum ChatMode {
    MOD(4, TL.CHAT_MOD),
    FACTION(3, TL.CHAT_FACTION),
    ALLIANCE(2, TL.CHAT_ALLIANCE),
    TRUCE(1, TL.CHAT_TRUCE),
    PUBLIC(0, TL.CHAT_PUBLIC);

    public final int value;
    public final TL nicename;

    ChatMode(final int value, final TL nicename) {
        this.value = value;
        this.nicename = nicename;
    }

    public boolean isAtLeast(ChatMode role) {
        return this.value >= role.value;
    }

    public boolean isAtMost(ChatMode role) {
        return this.value <= role.value;
    }

    @Override
    public String toString() {
        return this.nicename.toString();
    }

    public ChatMode getNext() {
        switch (this) {
            case PUBLIC:
                return TRUCE;
            case TRUCE:
                return ALLIANCE;
            case ALLIANCE:
                return FACTION;
            case FACTION:
                return MOD;
            default:
                return PUBLIC;
        }
    }
}
