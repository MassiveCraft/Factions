package com.massivecraft.factions.entity.migrator;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.util.EnumerationUtil;
import com.massivecraft.massivecore.store.migrator.MigratorRoot;
import com.massivecraft.massivecore.xlib.gson.JsonArray;
import com.massivecraft.massivecore.xlib.gson.JsonElement;
import com.massivecraft.massivecore.xlib.gson.JsonObject;
import com.massivecraft.massivecore.xlib.gson.JsonPrimitive;

import java.util.Collection;
import java.util.Iterator;

public class MigratorMConf001EnumerationUtil extends MigratorRoot
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MigratorMConf001EnumerationUtil i = new MigratorMConf001EnumerationUtil();
	public static MigratorMConf001EnumerationUtil get() { return i; }
	private MigratorMConf001EnumerationUtil()
	{
		super(MConf.class);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void migrateInner(JsonObject entity)
	{
		removeFromStringsField(entity, "materialsEditOnInteract", EnumerationUtil.MATERIALS_EDIT_ON_INTERACT.getStringSet());
		removeFromStringsField(entity, "materialsEditTools", EnumerationUtil.MATERIALS_EDIT_TOOL.getStringSet());
		removeFromStringsField(entity, "materialsDoor", EnumerationUtil.MATERIALS_DOOR.getStringSet());
		removeFromStringsField(entity, "materialsContainer", EnumerationUtil.MATERIALS_CONTAINER.getStringSet());
		removeFromStringsField(entity, "entityTypesEditOnInteract", EnumerationUtil.ENTITY_TYPES_EDIT_ON_INTERACT.getStringSet());
		removeFromStringsField(entity, "entityTypesEditOnDamage", EnumerationUtil.ENTITY_TYPES_EDIT_ON_DAMAGE.getStringSet());
		removeFromStringsField(entity, "entityTypesContainer", EnumerationUtil.ENTITY_TYPES_CONTAINER.getStringSet());
		removeFromStringsField(entity, "entityTypesMonsters", EnumerationUtil.ENTITY_TYPES_MONSTER.getStringSet());
		removeFromStringsField(entity, "entityTypesAnimals", EnumerationUtil.ENTITY_TYPES_ANIMAL.getStringSet());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	private void removeFromStringsField(JsonObject entity, String fieldName, Collection<String> removals)
	{
		JsonElement stringsElement = entity.get(fieldName);
		if (!(stringsElement instanceof JsonArray)) return;
		JsonArray strings = (JsonArray)stringsElement;
		
		for (Iterator<JsonElement> iterator = strings.iterator(); iterator.hasNext();)
		{
			JsonElement stringElement = iterator.next();
			if (!(stringElement instanceof JsonPrimitive)) continue;
			JsonPrimitive string = (JsonPrimitive)stringElement;
			
			if (!removals.contains(string.getAsString())) continue;
			
			iterator.remove();
		}
	}
	
}
