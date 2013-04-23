package com.massivecraft.factions;

import org.bukkit.permissions.Permissible;

import com.massivecraft.mcore.util.PermUtil;

public enum Perm
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
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
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public final String node;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	Perm(final String node)
	{
		this.node = "factions."+node;
	}
	
	// -------------------------------------------- //
	// HAS
	// -------------------------------------------- //
	
	public boolean has(Permissible permissible, boolean informSenderIfNot)
	{
		return PermUtil.has(permissible, this.node, informSenderIfNot);
	}
	
	public boolean has(Permissible permissible)
	{
		return has(permissible, false);
	}
	
}
