package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsCapeGet extends CmdFactionsCapeAbstract
{
	public CmdFactionsCapeGet()
	{
		this.addAliases("get");
		
		this.addRequirements(ReqHasPerm.get(Perm.CAPE_GET.node));
	}
	
	@Override
	public void perform()
	{
		if (currentCape == null)
		{
			msg("<h>%s <i>has no cape set.", capeFaction.describeTo(fme, true));
		}
		else
		{
			msg("<i>The cape of <h>%s <i>is \"<h>%s<i>\".", capeFaction.describeTo(fme, true), currentCape);
		}
	}
}
