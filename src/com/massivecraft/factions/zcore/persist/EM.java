package com.massivecraft.factions.zcore.persist;

import java.util.*;

import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.factions.zcore.persist.EntityCollection;

public class EM
{
	public static Map<Class<? extends Entity>, EntityCollection<? extends Entity>> class2Entities = new LinkedHashMap<Class<? extends Entity>, EntityCollection<? extends Entity>>();
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> EntityCollection<T> getEntitiesCollectionForEntityClass(Class<T> entityClass)
	{
		return (EntityCollection<T>) class2Entities.get(entityClass);
	}
	
	public static void setEntitiesCollectionForEntityClass(Class<? extends Entity> entityClass, EntityCollection<? extends Entity> entities)
	{
		class2Entities.put(entityClass, entities);
	}
	
	// -------------------------------------------- //
	// ATTACH AND DETACH
	// -------------------------------------------- //
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> void attach(T entity)
	{
		EntityCollection<T> ec = (EntityCollection<T>) getEntitiesCollectionForEntityClass(entity.getClass());
		ec.attach(entity);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> void detach(T entity)
	{
		EntityCollection<T> ec = (EntityCollection<T>) getEntitiesCollectionForEntityClass(entity.getClass());
		ec.detach(entity);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> boolean attached(T entity)
	{
		EntityCollection<T> ec = (EntityCollection<T>) getEntitiesCollectionForEntityClass(entity.getClass());
		return ec.attached(entity);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Entity> boolean detached(T entity)
	{
		EntityCollection<T> ec = (EntityCollection<T>) getEntitiesCollectionForEntityClass(entity.getClass());
		return ec.detached(entity);
	}
	
	// -------------------------------------------- //
	// DISC
	// -------------------------------------------- //
	
	public static void saveAllToDisc()
	{
		for (EntityCollection<? extends Entity> ec : class2Entities.values())
		{
			ec.saveToDisc();
		}
	}
	
	public static void loadAllFromDisc()
	{
		for (EntityCollection<? extends Entity> ec : class2Entities.values())
		{
			ec.loadFromDisc();
		}
	}
}
