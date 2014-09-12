package com.massivecraft.factions.cmd;

import java.util.LinkedHashMap;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.Progressbar;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPlayer extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPlayer()
	{
		// Aliases
		this.addAliases("p", "player");

		// Args
		this.addOptionalArg("player", "you");

		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.PLAYER.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		UPlayer uplayer = this.arg(0, ARUPlayer.getAny(sender), usender);
		if (uplayer == null) return;
		
		// INFO: Title
		msg(Txt.titleize(Txt.upperCaseFirst(uplayer.getUniverse()) + " Player " + uplayer.describeTo(usender)));
		
		// INFO: Power (as progress bar)
		double progressbarQuota = uplayer.getPower() / uplayer.getPowerMax();
		int progressbarWidth = (int) Math.round(uplayer.getPowerMax() / uplayer.getPowerMaxUniversal() * 100);
		msg("<k>Power: <v>%s", Progressbar.HEALTHBAR_CLASSIC.withQuota(progressbarQuota).withWidth(progressbarWidth).render());
				
		// INFO: Power (as digits)
		msg("<k>Power: <v>%.2f / %.2f", uplayer.getPower(), uplayer.getPowerMax());
		
		// INFO: Power Boost
		if (uplayer.hasPowerBoost())
		{
			double powerBoost = uplayer.getPowerBoost();
			String powerBoostType = (powerBoost > 0 ? "bonus" : "penalty");
			msg("<k>Power Boost: <v>%f <i>(a manually granted %s)", powerBoost, powerBoostType);
		}
		
		// INFO: Power per Hour
		// If the player is not at maximum we wan't to display how much time left.
		
		String stringTillMax = "";
		double powerTillMax = uplayer.getPowerMax() - uplayer.getPower();
		if (powerTillMax > 0)
		{
			long millisTillMax = (long) (powerTillMax * TimeUnit.MILLIS_PER_HOUR / uplayer.getPowerPerHour());
			LinkedHashMap<TimeUnit, Long> unitcountsTillMax = TimeDiffUtil.unitcounts(millisTillMax, TimeUnit.getAllButMillis());
			unitcountsTillMax = TimeDiffUtil.limit(unitcountsTillMax, 2);
			String unitcountsTillMaxFormated = TimeDiffUtil.formatedVerboose(unitcountsTillMax, "<i>");
			stringTillMax = Txt.parse(" <i>(%s <i>left till max)", unitcountsTillMaxFormated);
		}
		
		msg("<k>Power per Hour: <v>%.2f%s", uplayer.getPowerPerHour(), stringTillMax);
		
		// INFO: Power per Death
		msg("<k>Power per Death: <v>%.2f", uplayer.getPowerPerDeath());
		
	}
	
}