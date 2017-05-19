package com.massivecraft.factions.entity.migrator;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.store.migrator.MigratorFieldRename;
import com.massivecraft.massivecore.store.migrator.MigratorRoot;

public class MigratorMConf002Playerclean extends MigratorRoot
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MigratorMConf002Playerclean i = new MigratorMConf002Playerclean();
	public static MigratorMConf002Playerclean get() { return i; }
	private MigratorMConf002Playerclean()
	{
		super(MConf.class);
		this.addInnerMigrator(MigratorFieldRename.get("removePlayerMillisDefault", "playercleanToleranceMillis"));
		this.addInnerMigrator(MigratorFieldRename.get("removePlayerMillisPlayerAgeToBonus", "playercleanToleranceMillisPlayerAgeToBonus"));
		this.addInnerMigrator(MigratorFieldRename.get("removePlayerMillisFactionAgeToBonus", "playercleanToleranceMillisFactionAgeToBonus"));
	}
	
}
