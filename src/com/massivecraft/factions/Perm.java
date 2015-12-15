package com.massivecraft.factions;

import org.bukkit.permissions.Permissible;

import com.massivecraft.massivecore.util.PermUtil;

public enum Perm
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	ACCESS,
	ACCESS_VIEW,
	ACCESS_PLAYER,
	ACCESS_FACTION,
	ADMIN,
	CLAIM,
	CLAIM_ONE,
	CLAIM_AUTO,
	CLAIM_FILL,
	CLAIM_SQUARE,
	CLAIM_CIRCLE,
	CLAIM_ALL,
	CREATE,
	DESCRIPTION,
	DISBAND,
	EXPANSIONS,
	FACTION,
	FLAG,
	FLAG_LIST,
	FLAG_SET,
	FLAG_SHOW,
	HOME,
	INVITE,
	INVITE_LIST,
	INVITE_LIST_OTHER,
	INVITE_ADD,
	INVITE_REMOVE,
	JOIN,
	JOIN_OTHERS,
	KICK,
	LEAVE,
	LIST,
	MAP,
	MONEY,
	MONEY_BALANCE,
	MONEY_BALANCE_ANY,
	MONEY_DEPOSIT,
	MONEY_F2F,
	MONEY_F2P,
	MONEY_P2F,
	MONEY_WITHDRAW,
	MOTD,
	OPEN,
	PERM,
	PERM_LIST,
	PERM_SET,
	PERM_SHOW,
	PLAYER,
	POWERBOOST,
	RANK,
	RANK_SHOW,
	RANK_ACTION,
	RELATION,
	SEECHUNK,
	SEECHUNKOLD,
	SETHOME,
	SETPOWER,
	STATUS,
	NAME,
	TITLE,
	TITLE_COLOR,
	TERRITORYTITLES,
	UNCLAIM,
	UNCLAIM_ONE,
	UNCLAIM_AUTO,
	UNCLAIM_FILL,
	UNCLAIM_SQUARE,
	UNCLAIM_CIRCLE,
	UNCLAIM_ALL,
	UNSETHOME,
	UNSTUCK,
	VERSION,
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public final String node;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	Perm()
	{
		this.node = "factions." + this.name().toLowerCase().replace('_', '.');
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
