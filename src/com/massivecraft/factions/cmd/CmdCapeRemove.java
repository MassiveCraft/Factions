package com.massivecraft.factions.cmd;

import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.RelationUtil;

public class CmdCapeRemove extends CapeCommand
{
	
	public CmdCapeRemove()
	{
		this.aliases.add("rm");
		this.aliases.add("rem");
		this.aliases.add("remove");
		this.aliases.add("del");
		this.aliases.add("delete");
		this.permission = Permission.CAPE_REMOVE.node;
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
