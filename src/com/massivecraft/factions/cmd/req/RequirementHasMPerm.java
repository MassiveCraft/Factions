package com.massivecraft.factions.cmd.req;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Triple;
import com.massivecraft.massivecore.command.MassiveCommand;
import com.massivecraft.massivecore.command.requirement.RequirementAbstract;
import org.bukkit.command.CommandSender;

public class RequirementHasMPerm extends RequirementAbstract
{
	// -------------------------------------------- //
	// SERIALIZABLE
	// -------------------------------------------- //
	
	private static final long serialVersionUID = 1L;
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	public static RequirementHasMPerm get(MPerm perm) { return new RequirementHasMPerm(perm); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	private RequirementHasMPerm(MPerm perm)
	{
		this.mpermId = perm.getId();
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final String mpermId;
	public String getMPermId() { return this.mpermId; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean apply(CommandSender sender, MassiveCommand command)
	{
		// Resolve
		Triple<MPlayer, Faction, MPerm> tripple = this.resolve(sender);
		if (tripple == null) return false;
		
		MPlayer mplayer = tripple.getFirst();
		Faction faction = tripple.getSecond();
		MPerm mperm = tripple.getThird();
		
		// Override
		if (mplayer.isOverriding()) return true;
		
		// Has
		return mperm.has(mplayer, faction, false);
	}
	
	@Override
	public String createErrorMessage(CommandSender sender, MassiveCommand command)
	{
		// Resolve
		Triple<MPlayer, Faction, MPerm> tripple = this.resolve(sender);
		if (tripple == null) return null;
		
		MPlayer mplayer = tripple.getFirst();
		Faction faction = tripple.getSecond();
		MPerm mperm = tripple.getThird();
		
		// Create Message
		return mperm.getDeniedMessage(mplayer, faction).toPlain(true);
	}
	
	private Triple<MPlayer, Faction, MPerm> resolve(CommandSender sender)
	{
		// Get the MPlayer
		MPlayer mplayer = MPlayer.get(sender);
		if (mplayer == null) return null;
		
		// Get the Faction
		Faction faction = mplayer.getFaction();
		if (faction == null) return null;
		
		// Get MPerm
		MPerm mperm = MPerm.get(this.getMPermId());
		if (mperm == null) return null;
		
		// Return
		return new Triple<>(mplayer, faction, mperm);
	}
	
}
