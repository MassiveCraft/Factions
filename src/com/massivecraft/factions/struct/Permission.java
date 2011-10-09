package com.massivecraft.factions.struct;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.P;

public enum Permission
{
	PARTICIPATE("participate"),
	CREATE("create"),
	VIEW_ANY_POWER("viewAnyPower"),
	VIEW_ANY_FACTION_BALANCE("viewAnyFactionBalance"),
	PEACEFUL_EXPLOTION_TOGGLE("peacefulExplosionToggle"),
	ADMIN_BYPASS("adminBypass"),
	CONFIG("config"),
	DISBAND("disband"),
	LOCK("lock"),
	MANAGE_SAFE_ZONE("manageSafeZone"),
	MANAGE_WAR_ZONE("manageWarZone"),
	OWNERSHIP_BYPASS("ownershipBypass"),
	RELOAD("reload"),
	SAVE_ALL("saveall"),
	SET_PEACEFUL("setPeaceful"),
	SET_PERMANENT("setPermanent"),
	COMMAND_ADMIN("command.admin"),
	COMMAND_AUTOCLAIM("command.autoClaim"),
	COMMAND_BALANCE("command.balance"),
	COMMAND_WITHDRAW("command.withdraw"),
	COMMAND_PAY("command.pay"),
	COMMAND_CHAT("command.chat"),
	COMMAND_CLAIM("command.claim"),
	COMMAND_CONFIG("command.config"),
	COMMAND_DEINVITE("command.deinvite"),
	COMMAND_DEPOSIT("command.deposit"),
	COMMAND_DESCRIPTION("command.description"),
	COMMAND_DISBAND("command.disband"),
	COMMAND_DISBAND_ANY("command.disband.any"),
	COMMAND_HELP("command.help"),
	COMMAND_HOME("command.home"),
	COMMAND_INVITE("command.invite"),
	COMMAND_JOIN("command.join"),
	COMMAND_KICK("command.kick"),
	COMMAND_KICK_ANY("command.kick.any"),
	COMMAND_LEAVE("command.leave"),
	COMMAND_LIST("command.list"),
	COMMAND_LOCK("command.lock"),
	COMMAND_MAP("command.map"),
	COMMAND_MOD("command.mod"),
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
	
	/*public boolean has(CommandSender sender)
	{
		//return CreativeGates.p.perm.has(sender, this.node);
	}
	
	public static boolean isCommandDisabled(CommandSender sender, String command)
	{
		return (hasPerm(sender, "factions.commandDisable."+command) && !hasPerm(sender, "factions.commandDisable.none"));
	}
	
	private static boolean hasPerm(CommandSender sender, String permNode)
	{
		if (Factions.Permissions == null || ! (sender instanceof Player))
		{
			return sender.isOp() || sender.hasPermission(permNode);
		}
		
		Player player = (Player)sender;
		return Factions.Permissions.has(player, permNode); 
	}*/
	
	
}
