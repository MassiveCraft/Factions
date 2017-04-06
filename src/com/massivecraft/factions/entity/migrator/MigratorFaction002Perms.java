package com.massivecraft.factions.entity.migrator;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.migrator.MigratorFieldConvert;
import com.massivecraft.massivecore.store.migrator.MigratorRoot;
import com.massivecraft.massivecore.xlib.gson.JsonArray;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;

import java.util.Map.Entry;

public class MigratorFaction002Perms extends MigratorRoot
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MigratorFaction002Perms i = new MigratorFaction002Perms();
	public static MigratorFaction002Perms get() { return i; }
	private MigratorFaction002Perms()
	{
		super(Faction.class);
		this.addInnerMigrator(new MigratorFaction002PermsField());
	}
	
	public class MigratorFaction002PermsField extends MigratorFieldConvert
	{
		// -------------------------------------------- //
		// CONSTRUCT
		// -------------------------------------------- //

		private MigratorFaction002PermsField()
		{
			super("perms");
		}
		
		// -------------------------------------------- //
		// OVERRIDE
		// -------------------------------------------- //
		
		public Object migrateInner(JsonElement perms)
		{
			JsonObject ret = new JsonObject();
			
			// If non-null ...
			if (!perms.isJsonNull())
			{
				// ... and proper type ...
				if (!perms.isJsonObject()) throw new IllegalArgumentException(perms.toString());
				
				JsonArray arrayRel;
				JsonArray arrayString;
				Rel rel;
				
				// ... go through all perms ...
				for (Entry<String, JsonElement> entry: perms.getAsJsonObject().entrySet())
				{
					String id = entry.getKey();
					JsonElement value = entry.getValue();
					
					if (!value.isJsonArray()) throw new IllegalArgumentException("Inner element was no array:" + value.toString());
					arrayRel = value.getAsJsonArray();
					arrayString = new JsonArray();
					
					// ... change from Rel to string-Id ...
					for (JsonElement jsonElement : arrayRel)
					{
						if (jsonElement.isJsonNull()) continue;
						rel = MassiveCore.gson.fromJson(jsonElement, Rel.class);
						arrayString.add(rel.getId());
					}
					
					// ... and attach back to object.
					ret.add(id, arrayString);
				}
			}
			
			return ret;
		}
		
	}
	
}
