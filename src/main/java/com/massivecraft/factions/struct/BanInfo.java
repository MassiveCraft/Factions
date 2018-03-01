package com.massivecraft.factions.struct;

public class BanInfo {

    // FPlayer IDs
    private final String banner;
    private final String banned;
    private final long time;

    public BanInfo(String banner, String banned, long time) {
        this.banner = banner;
        this.banned = banned;
        this.time = time;
    }

    /**
     * Get the FPlayer ID of the player who issued the ban.
     *
     * @return FPlayer ID.
     */
    public String getBanner() {
        return this.banner;
    }

    /**
     * Get the FPlayer ID of the player who got banned.
     *
     * @return FPlayer ID.
     */
    public String getBanned() {
        return banned;
    }

    /**
     * Get the server time when the ban was issued.
     *
     * @return system timestamp.
     */
    public long getTime() {
        return time;
    }
}
