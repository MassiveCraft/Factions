package com.massivecraft.factions.cmd.req;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.command.requirement.RequirementAbstract;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.command.CommandSender;

public class ReqRoleIsAtLeast extends RequirementAbstract
{
	// -------------------------------------------- //
	// SERIALIZABLE
	// -------------------------------------------- //
	
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
		if (MUtil.isntSender(sender)) return false;
		
		MPlayer mplayer = MPlayer.get(sender);
		return mplayer.getRole().isAtLeast(this.getRel());
	}
	
	@Override
	public String createErrorMessage(CommandSender sender, MassiveCommand command)
	{
		return Txt.parse("<b>You must be <h>%s <b>or higher to %s.", Txt.getNicedEnum(this.getRel()), getDesc(command));
	}
	
}
