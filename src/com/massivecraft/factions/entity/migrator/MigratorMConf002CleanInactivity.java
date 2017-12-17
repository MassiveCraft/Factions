package com.massivecraft.factions.entity.migrator;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.store.migrator.MigratorFieldRename;
import com.massivecraft.massivecore.store.migrator.MigratorRoot;

public class MigratorMConf002CleanInactivity extends MigratorRoot
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MigratorMConf002CleanInactivity i = new MigratorMConf002CleanInactivity();
	public static MigratorMConf002CleanInactivity get() { return i; }
	private MigratorMConf002CleanInactivity()
	{
		super(MConf.class);
		this.addInnerMigrator(MigratorFieldRename.get("removePlayerMillisDefault", "playercleanToleranceMillis"));
		this.addInnerMigrator(MigratorFieldRename.get("removePlayerMillisPlayerAgeToBonus", "playercleanToleranceMillisPlayerAgeToBonus"));
		this.addInnerMigrator(MigratorFieldRename.get("removePlayerMillisFactionAgeToBonus", "playercleanToleranceMillisFactionAgeToBonus"));
	}
	
}
