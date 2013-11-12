package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.MCommand;
import com.massivecraft.mcore.util.Txt;

public abstract class FCommand extends MCommand
{
	public MPlayer msender;
	public UPlayer usender;
	public Faction usenderFaction;
	
	@Override
	public void fixSenderVars()
	{
		this.msender = MPlayer.get(sender);
		
		this.usender = null;
		this.usenderFaction = null;			
		
		// Check disabled
		if (UConf.isDisabled(sender)) return;
		
		this.usender = UPlayer.get(this.sender);
		this.usenderFaction = this.usender.getFaction();
	}
	
	// -------------------------------------------- //
	// COMMONLY USED LOGIC
	// -------------------------------------------- //
	
	public boolean canIAdministerYou(UPlayer i, UPlayer you)
	{
		if ( ! i.getFaction().equals(you.getFaction()))
		{
			i.sendMessage(Txt.parse("%s <b>和你不在同一个公会.",you.describeTo(i, true)));
			return false;
		}
		
		if (i.getRole().isMoreThan(you.getRole()) || i.getRole().equals(Rel.LEADER) )
		{
			return true;
		}
		
		if (you.getRole().equals(Rel.LEADER))
		{
			i.sendMessage(Txt.parse("<b>只有公会会长才能这么做."));
		}
		else if (i.getRole().equals(Rel.OFFICER))
		{
			if ( i == you )
			{
				return true; //Moderators can control themselves
			}
			else
			{
				i.sendMessage(Txt.parse("<b>主持人不能控制彼此..."));
			}
		}
		else
		{
			i.sendMessage(Txt.parse("<b>你必须是工会主席才能那么做."));
		}
		
		return false;
	}
}
