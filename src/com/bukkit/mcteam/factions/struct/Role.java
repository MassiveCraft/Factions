package com.bukkit.mcteam.factions.struct;

public enum Role {
	ADMIN(2, "admin"),
	MODERATOR(1, "moderator"),
	NORMAL(0, "normal player");
	
	public final int value;
	public final String nicename;
	
	private Role(final int value, final String nicename) {
        this.value = value;
        this.nicename = nicename;
    }
	
	@Override
	public String toString() {
		return this.nicename;
	}
}
