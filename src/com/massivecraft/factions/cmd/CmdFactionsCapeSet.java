package com.massivecraft.factions.cmd;

import java.net.URL;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsCapeSet extends CmdFactionsCapeAbstract
{
	
	public CmdFactionsCapeSet()
	{
		this.addAliases("set");
		
		this.addRequiredArg("url");
		this.setErrorOnToManyArgs(false);
		
		this.addRequirements(ReqHasPerm.get(Perm.CAPE_SET.node));
	}
	
	@Override
	public void perform()
	{
		String newCape = this.argConcatFrom(0);
		
		if (isUrlValid(newCape))
		{
			capeFaction.setCape(newCape);
			SpoutFeatures.updateCape(capeFaction, null);
			msg("<h>%s <i>set the cape of <h>%s<i> to \"<h>%s<i>\".", RelationUtil.describeThatToMe(fme, fme, true), capeFaction.describeTo(fme), newCape);
			capeFaction.msg("<h>%s <i>set the cape of <h>%s<i> to \"<h>%s<i>\".", RelationUtil.describeThatToMe(fme, capeFaction, true), capeFaction.describeTo(capeFaction), newCape);
		}
		else
		{
			msg("<i>\"<h>%s<i>\" is not a valid URL.", newCape);
		}
	}
	
	public static boolean isUrlValid(String urlString)
	{
		try
		{
			new URL(urlString);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
