package com.massivecraft.factions.entity.migrator;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.store.migrator.MigratorFieldRename;
import com.massivecraft.massivecore.store.migrator.MigratorRoot;

public class MigratorMConf003CleanInactivity extends MigratorRoot
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MigratorMConf003CleanInactivity i = new MigratorMConf003CleanInactivity();
	public static MigratorMConf003CleanInactivity get() { return i; }
	private MigratorMConf003CleanInactivity()
	{
		super(MConf.class);
		this.addInnerMigrator(MigratorFieldRename.get("playercleanToleranceMillis", "cleanInactivityToleranceMillis"));
		this.addInnerMigrator(MigratorFieldRename.get("playercleanToleranceMillisPlayerAgeToBonus", "cleanInactivityToleranceMillisPlayerAgeToBonus"));
		this.addInnerMigrator(MigratorFieldRename.get("playercleanToleranceMillisFactionAgeToBonus", "cleanInactivityToleranceMillisFactionAgeToBonus"));
	}
	
}
