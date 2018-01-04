package com.massivecraft.factions.zcore.fperms;

public enum Access {
    ALLOW,
    DENY,
    UNDEFINED;

    /**
     * Case insensitive check for access.
     * @param check
     * @return
     */
    public static Access fromString(String check) {
        for (Access access : values()) {
            if (access.name().equalsIgnoreCase(check)) {
                return access;
            }
        }

        return null;
    }
}
