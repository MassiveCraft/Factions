package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsMotdChange;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.mixin.MixinDisplayName;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsMotd extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsMotd()
	{
		// Parameters
		this.addParameter(TypeString.get(), "new", "read", true);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{	
		// Read
		if ( ! this.argIsSet(0))
		{
			message(msenderFaction.getMotdMessages());
			return;
		}
		
		// MPerm
		if ( ! MPerm.getPermMotd().has(msender, msenderFaction, true)) return;
		
		// Args
		String target = this.readArg();
		target = target.trim();
		target = Txt.parse(target);
		
		// Removal
		if (target != null && MassiveCore.NOTHING_REMOVE.contains(target))
		{
			target = null;
		}

		// Get Old
		String old = null;
		if (msenderFaction.hasMotd())
		{
			old = msenderFaction.getMotd();
		}
		
		// Target Desc
		String targetDesc = target;
		if (targetDesc == null) targetDesc = Txt.parse("<silver>nothing");
		
		// NoChange
		if (MUtil.equals(old, target))
		{
			msg("<i>The motd for %s <i>is already: <h>%s", msenderFaction.describeTo(msender, true), target);
			return;
		}

		// Event
		EventFactionsMotdChange event = new EventFactionsMotdChange(sender, msenderFaction, target);
		event.run();
		if (event.isCancelled()) return;
		target = event.getNewMotd();
		
		// Apply
		msenderFaction.setMotd(target);
		
		// Inform
		for (MPlayer follower : msenderFaction.getMPlayers())
		{
			follower.msg("<i>%s <i>set your faction motd to:\n%s", MixinDisplayName.get().getDisplayName(sender, follower), msenderFaction.getMotd());
		}
	}
	
}
