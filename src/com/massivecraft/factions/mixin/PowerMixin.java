package com.massivecraft.factions.mixin;

import com.massivecraft.factions.entity.MPlayer;

public interface PowerMixin
{
	public double getMaxUniversal(MPlayer mplayer);
	public double getMax(MPlayer mplayer);
	public double getMin(MPlayer mplayer);
	public double getPerHour(MPlayer mplayer);
	public double getPerDeath(MPlayer mplayer);
}
