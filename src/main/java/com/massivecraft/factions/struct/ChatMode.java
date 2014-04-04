package com.massivecraft.factions.struct;

public enum ChatMode {
    FACTION(2, "faction chat"),
    ALLIANCE(1, "alliance chat"),
    PUBLIC(0, "public chat");

    public final int value;
    public final String nicename;

    private ChatMode(final int value, final String nicename) {
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
        return this.nicename;
    }

    public ChatMode getNext() {
        if (this == PUBLIC) return ALLIANCE;
        if (this == ALLIANCE) return FACTION;
        return PUBLIC;
    }
}
