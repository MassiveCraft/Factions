package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;


public abstract class CmdFactionsSetXRadius extends CmdFactionsSetX
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetXRadius(boolean claim)
	{
		// Super
		super(claim);
		
		// Parameters
		this.addParameter(1, TypeInteger.get(), "radius");
		if (claim)
		{
			this.addParameter(TypeFaction.get(), "faction", "you");
			this.setFactionArgIndex(1);
		}
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public Integer getRadius() throws MassiveException
	{
		int radius = this.readArgAt(0);
		
		// Radius Claim Min
		if (radius < 1)
		{
			throw new MassiveException().setMsg("<b>If you specify a radius, it must be at least 1.");
		}
		
		// Radius Claim Max
		if (radius > MConf.get().setRadiusMax && ! msender.isOverriding())
		{
			throw new MassiveException().setMsg("<b>The maximum radius allowed is <h>%s<b>.", MConf.get().setRadiusMax);
		}
		
		return radius;
	}
	
	public Integer getRadiusZero() throws MassiveException
	{
		Integer ret = this.getRadius();
		return ret - 1;
	}
	
}
