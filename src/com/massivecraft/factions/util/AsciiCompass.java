package com.massivecraft.factions.util;

import java.util.List;

import com.massivecraft.massivecore.collections.MassiveList;

import static com.massivecraft.factions.util.AsciiCompassDirection.*;

public class AsciiCompass
{
	// -------------------------------------------- //
	// COMPASS
	// -------------------------------------------- //
	
	public static List<String> getAsciiCompass(double degrees)
	{
		return getAsciiCompass(AsciiCompassDirection.getByDegrees(degrees));
	}
	
	private static List<String> getAsciiCompass(AsciiCompassDirection directionFacing)
	{
		// Create
		List<String> ret = new MassiveList<>();
		
		// Fill
		ret.add(visualizeRow(directionFacing, NW, N, NE));
		ret.add(visualizeRow(directionFacing, W, NONE, E));
		ret.add(visualizeRow(directionFacing, SW, S, SE));
		
		// Return
		return ret;
	}
	
	// -------------------------------------------- //
	// VISUALIZE ROW
	// -------------------------------------------- //
	
	private static String visualizeRow(AsciiCompassDirection directionFacing, AsciiCompassDirection... cardinals)
	{
		// Catch
		if (cardinals == null) throw new NullPointerException("cardinals");
		
		// Create
		StringBuilder ret = new StringBuilder(cardinals.length);
		
		// Fill
		for (AsciiCompassDirection asciiCardinal : cardinals)
		{
			ret.append(asciiCardinal.visualize(directionFacing));
		}
		
		// Return
		return ret.toString();
	}
	
}
