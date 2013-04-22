package com.massivecraft.factions.entity;

import org.bukkit.ChatColor;

import com.massivecraft.mcore.MCore;
import com.massivecraft.mcore.store.Entity;

public class MConf extends Entity<MConf>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static MConf get()
	{
		return MConfColl.get().get(MCore.INSTANCE);
	}
	
	// -------------------------------------------- //
	// COLORS
	// -------------------------------------------- //
	
	public ChatColor colorMember = ChatColor.GREEN;
	public ChatColor colorAlly = ChatColor.DARK_PURPLE;
	public ChatColor colorTruce = ChatColor.LIGHT_PURPLE;
	public ChatColor colorNeutral = ChatColor.WHITE;
	public ChatColor colorEnemy = ChatColor.RED;
	
	public ChatColor colorNoPVP = ChatColor.GOLD;
	public ChatColor colorFriendlyFire = ChatColor.DARK_RED;
	//public ChatColor colorWilderness = ChatColor.DARK_GREEN;
	

}