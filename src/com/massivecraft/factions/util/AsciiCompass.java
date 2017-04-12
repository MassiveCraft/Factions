package com.massivecraft.factions.util;

import com.massivecraft.massivecore.collections.MassiveList;

import java.util.List;

import static com.massivecraft.factions.util.AsciiCompassDirection.E;
import static com.massivecraft.factions.util.AsciiCompassDirection.N;
import static com.massivecraft.factions.util.AsciiCompassDirection.NE;
import static com.massivecraft.factions.util.AsciiCompassDirection.NONE;
import static com.massivecraft.factions.util.AsciiCompassDirection.NW;
import static com.massivecraft.factions.util.AsciiCompassDirection.S;
import static com.massivecraft.factions.util.AsciiCompassDirection.SE;
import static com.massivecraft.factions.util.AsciiCompassDirection.SW;
import static com.massivecraft.factions.util.AsciiCompassDirection.W;

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
