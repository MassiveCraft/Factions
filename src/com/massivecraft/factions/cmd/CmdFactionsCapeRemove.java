package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsCapeRemove extends CmdFactionsCapeAbstract
{
	public CmdFactionsCapeRemove()
	{
		this.addAliases("rm", "rem", "remove", "del", "delete");
		this.addRequirements(ReqHasPerm.get(Perm.CAPE_REMOVE.node));
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
			capeFaction.setCape(null);
			SpoutFeatures.updateCape(capeFaction, null);
			msg("<h>%s <i>removed the cape from <h>%s<i>.", RelationUtil.describeThatToMe(fme, fme, true), capeFaction.describeTo(fme));
			capeFaction.msg("<h>%s <i>removed the cape from <h>%s<i>.", RelationUtil.describeThatToMe(fme, capeFaction, true), capeFaction.describeTo(capeFaction));
		}
	}
}
