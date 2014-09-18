package com.massivecraft.factions.cmd;

import java.util.LinkedHashMap;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Progressbar;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPlayer extends FactionsCommand
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
		this.addRequirements(ReqHasPerm.get(Perm.PLAYER.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		MPlayer mplayer = this.arg(0, ARMPlayer.getAny(), msender);
		if (mplayer == null) return;
		
		// INFO: Title
		msg(Txt.titleize("Player " + mplayer.describeTo(msender)));
		
		// INFO: Power (as progress bar)
		double progressbarQuota = mplayer.getPower() / mplayer.getPowerMax();
		int progressbarWidth = (int) Math.round(mplayer.getPowerMax() / mplayer.getPowerMaxUniversal() * 100);
		msg("<k>Power: <v>%s", Progressbar.HEALTHBAR_CLASSIC.withQuota(progressbarQuota).withWidth(progressbarWidth).render());
				
		// INFO: Power (as digits)
		msg("<k>Power: <v>%.2f / %.2f", mplayer.getPower(), mplayer.getPowerMax());
		
		// INFO: Power Boost
		if (mplayer.hasPowerBoost())
		{
			double powerBoost = mplayer.getPowerBoost();
			String powerBoostType = (powerBoost > 0 ? "bonus" : "penalty");
			msg("<k>Power Boost: <v>%f <i>(a manually granted %s)", powerBoost, powerBoostType);
		}
		
		// INFO: Power per Hour
		// If the player is not at maximum we wan't to display how much time left.
		
		String stringTillMax = "";
		double powerTillMax = mplayer.getPowerMax() - mplayer.getPower();
		if (powerTillMax > 0)
		{
			long millisTillMax = (long) (powerTillMax * TimeUnit.MILLIS_PER_HOUR / mplayer.getPowerPerHour());
			LinkedHashMap<TimeUnit, Long> unitcountsTillMax = TimeDiffUtil.unitcounts(millisTillMax, TimeUnit.getAllButMillis());
			unitcountsTillMax = TimeDiffUtil.limit(unitcountsTillMax, 2);
			String unitcountsTillMaxFormated = TimeDiffUtil.formatedVerboose(unitcountsTillMax, "<i>");
			stringTillMax = Txt.parse(" <i>(%s <i>left till max)", unitcountsTillMaxFormated);
		}
		
		msg("<k>Power per Hour: <v>%.2f%s", mplayer.getPowerPerHour(), stringTillMax);
		
		// INFO: Power per Death
		msg("<k>Power per Death: <v>%.2f", mplayer.getPowerPerDeath());
		
	}
	
}