package com.massivecraft.factions.struct;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.P;

public enum Permission
{
	MANAGE_SAFE_ZONE("managesafezone"),
	MANAGE_WAR_ZONE("managewarzone"),
	OWNERSHIP_BYPASS("ownershipbypass"),
	ADMIN("admin"),
	AUTOCLAIM("autoclaim"),
	BALANCE("balance"),
	BALANCE_ANY("balance.any"),
	WITHDRAW("withdraw"),
	PAY("pay"),
	BYPASS("bypass"),
	CHAT("chat"),
	CLAIM("claim"),
	CONFIG("config"),
	CREATE("create"),
	DEINVITE("deinvite"),
	DEPOSIT("deposit"),
	DESCRIPTION("description"),
	DISBAND("disband"),
	DISBAND_ANY("disband.any"),
	HELP("help"),
	HOME("home"),
	INVITE("invite"),
	JOIN("join"),
	KICK("kick"),
	KICK_ANY("kick.any"),
	LEAVE("leave"),
	LIST("list"),
	LOCK("lock"),
	MAP("map"),
	MOD("mod"),
	NO_BOOM("noboom"),
	OPEN("open"),
	OWNER("owner"),
	OWNERLIST("ownerlist"),
	SET_PEACEFUL("setpeaceful"),
	SET_PERMANENT("setpermanent"),
	POWER("power"),
	POWER_ANY("power.any"),
	RELATION("relation"),
	RELOAD("reload"),
	SAVE("save"),
	SETHOME("sethome"),
	SETHOME_ANY("sethome.any"),
	SHOW("show"),
	TAG("tag"),
	TITLE("title"),
	UNCLAIM("unclaim"),
	UNCLAIM_ALL("unclaimall"),
	VERSION("version"),
	;
	
	public final String node;
	
	Permission(final String node)
	{
		this.node = "factions."+node;
    }
	
	public boolean has(CommandSender sender, boolean informSenderIfNot)
	{
		return P.p.perm.has(sender, this.node, informSenderIfNot);
	}
	
	public boolean has(CommandSender sender)
	{
		return has(sender, false);
	}
}
