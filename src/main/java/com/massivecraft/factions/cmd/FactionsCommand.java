package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.util.Txt;

public class FactionsCommand extends MassiveCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public MPlayer msender;
	public Faction msenderFaction;
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void fixSenderVars()
	{
		this.msender = MPlayer.get(sender);
		this.msenderFaction = this.msender.getFaction();
	}
	
	
	@Override
	public void unsetSenderVars()
	{
		this.msender = null;
		this.msenderFaction = null;
	}
	
	// -------------------------------------------- //
	// COMMONLY USED LOGIC
	// -------------------------------------------- //
	
	public boolean canIAdministerYou(MPlayer i, MPlayer you)
	{
		if ( ! i.getFaction().equals(you.getFaction()))
		{
			i.sendMessage(Txt.parse("%s <b>is not in the same faction as you.",you.describeTo(i, true)));
			return false;
		}
		
		if (i.getRole().isMoreThan(you.getRole()) || i.getRole().equals(Rel.LEADER) )
		{
			return true;
		}
		
		if (you.getRole().equals(Rel.LEADER))
		{
			i.sendMessage(Txt.parse("<b>Only the faction leader can do that."));
		}
		else if (i.getRole().equals(Rel.OFFICER))
		{
			if ( i == you )
			{
				return true; //Moderators can control themselves
			}
			else
			{
				i.sendMessage(Txt.parse("<b>Officers can't control each other..."));
			}
		}
		else
		{
			i.sendMessage(Txt.parse("<b>You must be a faction officer to do that."));
		}
		
		return false;
	}
	
}
