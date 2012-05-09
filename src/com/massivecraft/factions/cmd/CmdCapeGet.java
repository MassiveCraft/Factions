package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;

public class CmdCapeGet extends CapeCommand
{
	public CmdCapeGet()
	{
		this.aliases.add("get");
		this.permission = Permission.CAPE_GET.node;
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
