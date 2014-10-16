package com.massivecraft.factions;

public class Const
{
	// Collections & Aspects
	public static final String BASENAME = "factions";
	public static final String BASENAME_ = BASENAME+"_";
	
	public static final String COLLECTION_BOARD = BASENAME_+"board";
	public static final String COLLECTION_FACTION = BASENAME_+"faction";
	public static final String COLLECTION_MFLAG = BASENAME_+"mflag";
	public static final String COLLECTION_MPERM = BASENAME_+"mperm";
	public static final String COLLECTION_MPLAYER = BASENAME_+"mplayer";
	public static final String COLLECTION_MCONF = BASENAME_+"mconf";
	
	public static final String ASPECT = BASENAME;
	
	// ASCII Map
	public static final int MAP_WIDTH = 48;
	public static final int MAP_HEIGHT = 8;
	public static final int MAP_HEIGHT_FULL = 17;
	
	public static final char[] MAP_KEY_CHARS = "\\/#?$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz".toCharArray();
	
	// SHOW
	
	public static final String SHOW_ID_FACTION_ID = BASENAME_ + "id";
	public static final String SHOW_ID_FACTION_DESCRIPTION = BASENAME_ + "description";
	public static final String SHOW_ID_FACTION_AGE = BASENAME_ + "age";
	public static final String SHOW_ID_FACTION_FLAGS = BASENAME_ + "flags";
	public static final String SHOW_ID_FACTION_POWER = BASENAME_ + "power";
	public static final String SHOW_ID_FACTION_LANDVALUES = BASENAME_ + "landvalue";
	public static final String SHOW_ID_FACTION_BANK = BASENAME_ + "bank";
	public static final String SHOW_ID_FACTION_RELATIONS = BASENAME_ + "relations";
	public static final String SHOW_ID_FACTION_FOLLOWERS = BASENAME_ + "followers";
	
	public static final int SHOW_PRIORITY_FACTION_ID = 1000;
	public static final int SHOW_PRIORITY_FACTION_DESCRIPTION = 2000;
	public static final int SHOW_PRIORITY_FACTION_AGE = 3000;
	public static final int SHOW_PRIORITY_FACTION_FLAGS = 4000;
	public static final int SHOW_PRIORITY_FACTION_POWER = 5000;
	public static final int SHOW_PRIORITY_FACTION_LANDVALUES = 6000;
	public static final int SHOW_PRIORITY_FACTION_BANK = 7000;
	public static final int SHOW_PRIORITY_FACTION_RELATIONS = 8000;
	public static final int SHOW_PRIORITY_FACTION_FOLLOWERS = 9000;
	
}
