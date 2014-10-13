package com.massivecraft.factions;

import org.bukkit.permissions.Permissible;

import com.massivecraft.massivecore.util.PermUtil;

public enum Perm
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	ACCESS("access"),
	ACCESS_VIEW("access.view"),
	ACCESS_PLAYER("access.player"),
	ACCESS_FACTION("access.faction"),
	ADMIN("admin"),
	CREATE("create"),
	DEMOTE("demote"),
	DESCRIPTION("description"),
	DISBAND("disband"),
	FACTION("faction"),
	FLAG("flag"),
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
	MAP("map"),
	MONEY("money"),
	MONEY_BALANCE("money.balance"),
	MONEY_BALANCE_ANY("money.balance.any"),
	MONEY_DEPOSIT("money.deposit"),
	MONEY_F2F("money.f2f"),
	MONEY_F2P("money.f2p"),
	MONEY_P2F("money.p2f"),
	MONEY_WITHDRAW("money.withdraw"),
	MOTD("motd"),
	OFFICER("officer"),
	OFFICER_ANY("officer.any"),
	OPEN("open"),
	PERM("perm"),
	PLAYER("player"),
	POWERBOOST("powerboost"),
	PROMOTE("promote"),
	RELATION("relation"),
	SEECHUNK("seechunk"),
	SEECHUNKOLD("seechunkold"),
	SET("set"),
	SET_ONE("set.one"),
	SET_AUTO("set.auto"),
	SET_FILL("set.fill"),
	SET_SQUARE("set.square"),
	SET_CIRCLE("set.circle"),
	SET_TRANSFER("set.transfer"),
	SETHOME("sethome"),
	NAME("name"),
	TITLE("title"),
	TITLE_COLOR("title.color"),
	UNSETHOME("unsethome"),
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
