package com.massivecraft.factions.mixin;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;

public class PowerMixinDefault implements PowerMixin
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static PowerMixinDefault i = new PowerMixinDefault();
	public static PowerMixinDefault get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: PowerMixin
	// -------------------------------------------- //

	@Override
	public double getMaxUniversal(MPlayer mplayer)
	{
		return this.getMax(mplayer);
	}
	
	@Override
	public double getMax(MPlayer mplayer)
	{
		return MConf.get().powerMax + mplayer.getPowerBoost();
	}

	@Override
	public double getMin(MPlayer mplayer)
	{
		return MConf.get().powerMin;
	}

	@Override
	public double getPerHour(MPlayer mplayer)
	{
		return MConf.get().powerPerHour;
	}

	@Override
	public double getPerDeath(MPlayer mplayer)
	{
		return MConf.get().powerPerDeath;
	}

}
