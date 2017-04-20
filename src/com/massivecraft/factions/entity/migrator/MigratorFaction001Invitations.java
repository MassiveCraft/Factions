package com.massivecraft.factions.entity.migrator;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.store.migrator.MigratorFieldConvert;
import com.massivecraft.massivecore.store.migrator.MigratorFieldRename;
import com.massivecraft.massivecore.store.migrator.MigratorRoot;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;

public class MigratorFaction001Invitations extends MigratorRoot
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static com.massivecraft.factions.entity.migrator.MigratorFaction001Invitations i = new com.massivecraft.factions.entity.migrator.MigratorFaction001Invitations();
	public static com.massivecraft.factions.entity.migrator.MigratorFaction001Invitations get() { return i; }
	private MigratorFaction001Invitations()
	{
		super(Faction.class);
		this.addInnerMigrator(MigratorFieldRename.get("invitedPlayerIds", "invitations"));
		this.addInnerMigrator(new MigratorFaction001InvitationsField());
	}
	
	public class MigratorFaction001InvitationsField extends MigratorFieldConvert
	{
		// -------------------------------------------- //
		// CONSTRUCT
		// -------------------------------------------- //

		private MigratorFaction001InvitationsField()
		{
			super("invitations");
		}
		
		// -------------------------------------------- //
		// OVERRIDE
		// -------------------------------------------- //
		
		public Object migrateInner(JsonElement idList)
		{
			JsonObject ret = new JsonObject();
			//EntityInternalMap<Invitation> ret = new EntityInternalMap<>(null, Invitation.class);
			
			// If non-null
			if (!idList.isJsonNull())
			{
				// ... and proper type ...
				if (!idList.isJsonArray()) throw new IllegalArgumentException(idList.toString());
				
				// ... fill!
				for (JsonElement playerId : idList.getAsJsonArray())
				{
					String id = playerId.getAsString();
					
					// Create invitation
					JsonObject invitation = new JsonObject();
					
					// Attach
					ret.add(id, invitation);
				}
			}
			
			return ret;
		}
		
	}
	
}
