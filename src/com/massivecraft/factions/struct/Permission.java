package com.massivecraft.factions.struct;

import org.bukkit.command.CommandSender;
import com.massivecraft.factions.P;

public enum Permission
{
	ADMIN("adminmode"),
	AUTOCLAIM("autoclaim"),
	CHAT("chat"),
	CLAIM("claim"),
	CONFIG("config"),
	CREATE("create"),
	DEINVITE("deinvite"),
	DESCRIPTION("description"),
	DISBAND("disband"),
	FLAG("flag"),
	FLAG_SET("flag.set"),
	HELP("help"),
	HOME("home"),
	INVITE("invite"),
	JOIN("join"),
	KICK("kick"),
	LEADER("leader"),
	LEAVE("leave"),
	LIST("list"),
	LOCK("lock"),
	MAP("map"),
	MONEY_BALANCE("money.balance"),
	MONEY_BALANCE_ANY("money.balance.any"),
	MONEY_DEPOSIT("money.deposit"),
	MONEY_F2F("money.f2f"),
	MONEY_F2P("money.f2p"),
	MONEY_P2F("money.p2f"),
	MONEY_WITHDRAW("money.withdraw"),
	OFFICER("officer"),
	OPEN("open"),
	PERM("perm"),
	POWER("power"),
	POWER_ANY("power.any"),
	RELATION("relation"),
	RELOAD("reload"),
	SAVE("save"),
	SETHOME("sethome"),
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
