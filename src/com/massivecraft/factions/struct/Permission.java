package com.massivecraft.factions.struct;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.P;

public enum Permission
{
	PARTICIPATE("factions.participate"),
	CREATE("factions.create"),
	VIEW_ANY_POWER("factions.viewAnyPower"),
	PEACEFUL_EXPLOTION_TOGGLE("factions.peacefulExplosionToggle"),
	ADMIN_BYPASS("factions.adminBypass"),
	CONFIG("factions.config"),
	DISBAN("factions.disband"),
	LOCK("factions.lock"),
	MANAGE_SAFE_ZONE("factions.manageSafeZone"),
	MANAGE_WAR_ZONE("factions.manageWarZone"),
	OWNERSHIP_BYPASS("factions.ownershipBypass"),
	RELOAD("factions.reload"),
	SAVE_ALL("factions.saveall"),
	SET_PEACEFUL("factions.setPeaceful"),
	;
	
	public final String node;
	
	Permission(final String node)
	{
		this.node = node;
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
