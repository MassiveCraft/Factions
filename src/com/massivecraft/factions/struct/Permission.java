package com.massivecraft.factions.struct;

import org.bukkit.command.CommandSender;
import com.massivecraft.factions.P;

public enum Permission
{
	ACCESS("access"),
	ACCESS_ANY("access.any"),
	ACCESS_VIEW("access.view"),
	ADMIN("adminmode"),
	AUTOCLAIM("autoclaim"),
	CAPE("cape"),
	CAPE_GET("cape.get"),
	CAPE_SET("cape.set"),
	CAPE_REMOVE("cape.remove"),
	CLAIM("claim"),
	CLAIM_RADIUS("claim.radius"),
	CONFIG("config"),
	CREATE("create"),
	DEINVITE("deinvite"),
	DEMOTE("demote"),
	DESCRIPTION("description"),
	DISBAND("disband"),
	FLAG("flag"),
	FLAG_SET("flag.set"),
	HELP("help"),
	HOME("home"),
	INVITE("invite"),
	JOIN("join"),
	JOIN_ANY("join.any"),
	JOIN_OTHERS("join.others"),
	KICK("kick"),
	LEADER("leader"),
	LEADER_ANY("leader.any"),
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
	OFFICER_ANY("officer.any"),
	OPEN("open"),
	PERM("perm"),
	POWER("power"),
	POWER_ANY("power.any"),
	POWERBOOST("powerboost"),
	PROMOTE("promote"),
	RELATION("relation"),
	RELOAD("reload"),
	SAVE("save"),
	SEE_CHUNK("seechunk"),
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
