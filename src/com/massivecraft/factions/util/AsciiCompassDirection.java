package com.massivecraft.factions.util;

import org.bukkit.ChatColor;

public enum AsciiCompassDirection
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	N('N'),
	NE('/'),
	E('E'),
	SE('\\'),
	S('S'),
	SW('/'),
	W('W'),
	NW('\\'),
	NONE('+'),
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final char asciiChar;
	public char getAsciiChar() { return this.asciiChar; }
	
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final ChatColor ACTIVE = ChatColor.RED;
	public static final ChatColor INACTIVE = ChatColor.YELLOW;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	AsciiCompassDirection(final char asciiChar)
	{
		this.asciiChar = asciiChar;
	}
	
	// -------------------------------------------- //
	// VISUALIZE
	// -------------------------------------------- //
	
	public String visualize(AsciiCompassDirection directionFacing)
	{
		boolean isFacing = this.isFacing(directionFacing);
		ChatColor color = this.getColor(isFacing);
		
		return color.toString() + this.getAsciiChar();
	}
	
	private boolean isFacing(AsciiCompassDirection directionFacing)
	{
		return this == directionFacing;
	}
	
	private ChatColor getColor(boolean active)
	{
		return active ? ACTIVE : INACTIVE;
	}
	
	// -------------------------------------------- //
	// GET BY DEGREES
	// -------------------------------------------- //
	
	public static AsciiCompassDirection getByDegrees(double degrees)
	{
		// Prepare
		// The conversion from bukkit to usable degrees is (degrees - 180) % 360
		// But we reduced the 180 to 157 (-23) because it makes the math easier that follows.
		degrees = (degrees - 157) % 360;
		if (degrees < 0) degrees += 360;
		
		// Get ordinal
		int ordinal = (int) Math.floor(degrees / 45);
		
		// Return
		return AsciiCompassDirection.values()[ordinal];
	}
	
}
