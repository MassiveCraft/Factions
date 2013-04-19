package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.mixin.Mixin;

public class CmdFactionsDescription extends FCommand
{
	public CmdFactionsDescription()
	{
		this.addAliases("desc");
		
		this.addRequiredArg("desc");
		this.setErrorOnToManyArgs(false);
		
		this.addRequirements(ReqHasPerm.get(Perm.DESCRIPTION.node));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
	@Override
	public void perform()
	{
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(ConfServer.econCostDesc)) return;

		myFaction.setDescription(this.argConcatFrom(1));
		
		myFaction.msg("<i>%s <i>set your faction description to:\n%s", Mixin.getDisplayName(sender), myFaction.getDescription());
	}
	
}
