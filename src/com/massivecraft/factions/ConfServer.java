package com.massivecraft.factions;

import java.util.*;

import com.massivecraft.mcore.SimpleConfig;
import com.massivecraft.mcore.util.MUtil;

public class ConfServer extends SimpleConfig
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static transient ConfServer i = new ConfServer();
	public static ConfServer get() { return i; }
	public ConfServer() { super(Factions.get()); }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public static List<String> baseCommandAliases = MUtil.list("f");
	public static String dburi = "default";
	
}
