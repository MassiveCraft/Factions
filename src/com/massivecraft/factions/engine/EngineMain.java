package com.massivecraft.factions.engine;

import com.massivecraft.massivecore.ps.PS;

public class EngineMain
{

	/**
	 * @deprecated moved to EnginePermBuild
	 */
	public static boolean canPlayerBuildAt(Object senderObject, PS ps, boolean verboose)
	{
		Boolean ret = EnginePermBuild.protect(ProtectCase.BUILD, verboose, senderObject, ps, null, null);
		return ret == null || !ret;
	}

}
