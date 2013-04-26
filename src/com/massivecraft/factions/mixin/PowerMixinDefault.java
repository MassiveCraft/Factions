package com.massivecraft.factions.mixin;

import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;

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
	public double getMaxUniversal(UPlayer uplayer)
	{
		return this.getMax(uplayer);
	}
	
	@Override
	public double getMax(UPlayer uplayer)
	{
		return UConf.get(uplayer).powerMax + uplayer.getPowerBoost();
	}

	@Override
	public double getMin(UPlayer uplayer)
	{
		return UConf.get(uplayer).powerMin;
	}

	@Override
	public double getPerHour(UPlayer uplayer)
	{
		return UConf.get(uplayer).powerPerHour;
	}

	@Override
	public double getPerDeath(UPlayer uplayer)
	{
		return UConf.get(uplayer).powerPerDeath;
	}

}
