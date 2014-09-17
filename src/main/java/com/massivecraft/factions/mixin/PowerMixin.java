package com.massivecraft.factions.mixin;

import com.massivecraft.factions.entity.MPlayer;

public interface PowerMixin
{
	public double getMaxUniversal(MPlayer uplayer);
	public double getMax(MPlayer uplayer);
	public double getMin(MPlayer uplayer);
	public double getPerHour(MPlayer uplayer);
	public double getPerDeath(MPlayer uplayer);
}
