package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARMPerm;
import com.massivecraft.factions.cmd.arg.ARRel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPermSet extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermSet()
	{
		// Aliases
		this.addAliases("set");
		
		// Args
		this.addRequiredArg("perm");
		this.addRequiredArg("relation");
		this.addRequiredArg("yes/no");
		this.addOptionalArg("faction", "you");
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.PERM_SET.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		MPerm perm = this.arg(0, ARMPerm.get());
		Rel rel = this.arg(1, ARRel.get());
		Boolean value = this.arg(2, ARBoolean.get());
		Faction faction = this.arg(3, ARFaction.get(), msenderFaction);
		
		// Do the sender have the right to change perms for this faction?
		if ( ! MPerm.getPermPerms().has(msender, faction, true)) return;
		
		// Is this perm editable?
		if ( ! msender.isUsingAdminMode() && ! perm.isEditable())
		{
			msg("<b>The perm <h>%s <b>is not editable.", perm.getName());
			return;
		}
		
		// No change
		if (faction.getPermitted(perm).contains(rel) == value)
		{
			msg("%s <i>already has %s <i>set to %s <i>for %s<i>.", faction.describeTo(msender), perm.getDesc(true, false), Txt.parse(value ? "<g>YES" : "<b>NOO"), rel.getColor() + rel.getDescPlayerMany());
			return;
		}
		
		// Apply
		faction.setRelationPermitted(perm, rel, value);
		
		// The following is to make sure the leader always has the right to change perms if that is our goal.
		if (perm == MPerm.getPermPerms() && MPerm.getPermPerms().getStandard().contains(Rel.LEADER))
		{
			faction.setRelationPermitted(MPerm.getPermPerms(), Rel.LEADER, true);
		}
		
		// Create messages
		List<String> messages = new ArrayList<String>();
		
		// Inform sender
		messages.add(Txt.titleize("Perm for " + faction.describeTo(msender, true)));
		messages.add(MPerm.getStateHeaders());
		messages.add(Txt.parse(perm.getStateInfo(faction.getPermitted(perm), true)));
		sendMessage(messages);
		
		// Inform faction (their message is slighly different)
		List<MPlayer> recipients = faction.getMPlayers();
		recipients.remove(msender);
		
		for (MPlayer recipient : recipients)
		{
			messages.add(0, Txt.parse("<h>%s <i>set a perm for <h>%s<i>.", msender.describeTo(recipient, true), faction.describeTo(recipient, true)));
			recipient.sendMessage(messages);
		}
	}
	
}
