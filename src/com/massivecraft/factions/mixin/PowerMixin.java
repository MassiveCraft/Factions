package com.massivecraft.factions.mixin;

import com.massivecraft.factions.entity.UPlayer;

public interface PowerMixin
{
	public double getMaxUniversal(UPlayer uplayer);
	public double getMax(UPlayer uplayer);
	public double getMin(UPlayer uplayer);
	public double getPerHour(UPlayer uplayer);
	public double getPerDeath(UPlayer uplayer);
}
