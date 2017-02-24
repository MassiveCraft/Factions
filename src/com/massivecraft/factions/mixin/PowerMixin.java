package com.massivecraft.factions.mixin;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.mixin.Mixin;

public class PowerMixin extends Mixin
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static PowerMixin d = new PowerMixin();
	private static PowerMixin i = d;
	public static PowerMixin get() { return i; }

	// -------------------------------------------- //
	// METHODS
	// -------------------------------------------- //

	public double getMaxUniversal(MPlayer mplayer)
	{
		return this.getMax(mplayer);
	}

	public double getMax(MPlayer mplayer)
	{
		return MConf.get().powerMax + mplayer.getPowerBoost();
	}

	public double getMin(MPlayer mplayer)
	{
		return MConf.get().powerMin;
	}

	public double getPerHour(MPlayer mplayer)
	{
		return MConf.get().powerPerHour;
	}

	public double getPerDeath(MPlayer mplayer)
	{
		return MConf.get().powerPerDeath;
	}
	
}
