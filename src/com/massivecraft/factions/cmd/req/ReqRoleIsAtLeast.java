package com.massivecraft.factions.cmd.req;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.cmd.req.ReqAbstract;
import com.massivecraft.massivecore.util.Txt;

public class ReqRoleIsAtLeast extends ReqAbstract
{
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final Rel rel;
	public Rel getRel() { return this.rel; }
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	public static ReqRoleIsAtLeast get(Rel rel) { return new ReqRoleIsAtLeast(rel); }
	private ReqRoleIsAtLeast(Rel rel) { this.rel = rel; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean apply(CommandSender sender, MassiveCommand command)
	{
		UPlayer uplayer = UPlayer.get(sender);
		return uplayer.getRole().isAtLeast(this.rel);
	}
	
	@Override
	public String createErrorMessage(CommandSender sender, MassiveCommand command)
	{
		return Txt.parse("<b>You must be <h>%s <b>or higher to "+(command == null ? "do that" : command.getDesc())+".", Txt.getNicedEnum(this.rel));
	}
	
}
