package com.massivecraft.factions.zcore.persist;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonSyntaxException;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.TextUtil;

public abstract class EntityCollection<E extends Entity>
{	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// These must be instantiated in order to allow for different configuration (orders, comparators etc)
	private Collection<E> entities;
	protected Map<String, E> id2entity;
	
	// If the entities are creative they will create a new instance if a non existent id was requested
	private boolean creative;
	public boolean isCreative() { return creative; }
	public void setCreative(boolean creative) { this.creative = creative; }

	// This is the auto increment for the primary key "id"
	private int nextId;
	
	// This ugly crap is necessary due to java type erasure
	private Class<E> entityClass;
	public abstract Type getMapType(); // This is special stuff for GSON.
	
	// Info on how to persist
	private Gson gson;
	public Gson getGson() { return gson; }
	public void setGson(Gson gson) { this.gson = gson; }

	private File file;
	public File getFile() { return file; }
	public void setFile(File file) { this.file = file; }
	
	// -------------------------------------------- //
	// CONSTRUCTORS
	// -------------------------------------------- //

	public EntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson, boolean creative)
	{
		this.entityClass = entityClass;
		this.entities = entities;
		this.id2entity = id2entity;
		this.file = file;
		this.gson = gson;
		this.creative = creative;
		this.nextId = 1;
		
		EM.setEntitiesCollectionForEntityClass(this.entityClass, this);
	}
	
	public EntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson)
	{
		this(entityClass, entities, id2entity, file, gson, false);
	}
	
	// -------------------------------------------- //
	// GET
	// -------------------------------------------- //

	public Collection<E> get()
	{
		return entities;
	}
	
	public Map<String, E> getMap()
	{
		return this.id2entity;
	}
	
	public E get(String id)
	{
		if (this.creative) return this.getCreative(id);
		return id2entity.get(id);
	}
	
	public E getCreative(String id)
	{
		E e = id2entity.get(id);
		if (e != null) return e;
		return this.create(id);
	}
	
	public boolean exists(String id)
	{
		if (id == null) return false;
		return id2entity.get(id) != null;
	}
	
	public E getBestIdMatch(String pattern)
	{
		String id = TextUtil.getBestStartWithCI(this.id2entity.keySet(), pattern);
		if (id == null) return null;
		return this.id2entity.get(id);
	}
	
	// -------------------------------------------- //
	// CREATE
	// -------------------------------------------- //
	
	public synchronized E create()
	{
		return this.create(this.getNextId());
	}
	
	public synchronized E create(String id)
	{
		if ( ! this.isIdFree(id)) return null;
		
		E e = null;
		try
		{
			e = this.entityClass.newInstance();
		} catch (Exception ignored) {
			ignored.printStackTrace();
		}
		
		e.setId(id);
		this.entities.add(e);
		this.id2entity.put(e.getId(), e);
		this.updateNextIdForId(id);
		return e;
	}
	
	// -------------------------------------------- //
	// ATTACH AND DETACH
	// -------------------------------------------- //
	
	public void attach(E entity)
	{
		if (entity.getId() != null) return;
		entity.setId(this.getNextId());
		this.entities.add(entity);
		this.id2entity.put(entity.getId(), entity);
	}
	
	public void detach(E entity)
	{
		entity.preDetach();
		this.entities.remove(entity);
		this.id2entity.remove(entity.getId());
		entity.postDetach();
	}
	
	public void detach(String id)
	{
		E entity = this.id2entity.get(id);
		if (entity == null) return;
		this.detach(entity);
	}
	
	public boolean attached(E entity)
	{
		return this.entities.contains(entity);
	}
	
	public boolean detached(E entity)
	{
		return ! this.attached(entity);
	}
	
	// -------------------------------------------- //
	// DISC
	// -------------------------------------------- //
	
	public boolean saveToDisc()
	{
		Map<String, E> entitiesThatShouldBeSaved = new HashMap<String, E>();
		for (E entity : this.entities)
		{
			if (entity.shouldBeSaved())
			{
				entitiesThatShouldBeSaved.put(entity.getId(), entity);
			}
		}
		
		return this.saveCore(entitiesThatShouldBeSaved);
	}
	
	private boolean saveCore(Map<String, E> entities)
	{
		return DiscUtil.writeCatch(this.file, this.gson.toJson(entities));
	}
	
	public boolean loadFromDisc()
	{
		Map<String, E> id2entity = this.loadCore();
		if (id2entity == null) return false;
		this.entities.clear();
		this.entities.addAll(id2entity.values());
		this.id2entity.clear();
		this.id2entity.putAll(id2entity);
		this.fillIds();
		return true;
	}
	
	private Map<String, E> loadCore()
	{
		if ( ! this.file.exists())
		{
			return new HashMap<String, E>();
		}
		
		String content = DiscUtil.readCatch(this.file);
		if (content == null)
		{
			return null;
		}
		
		Type type = this.getMapType();
		try
		{
			return this.gson.fromJson(content, type);
		}
		catch(JsonSyntaxException ex)
		{
			Bukkit.getLogger().log(Level.WARNING, "JSON error encountered loading \"" + file + "\": " + ex.getLocalizedMessage());

			// backup bad file, so user can attempt to recover something from it
			File backup = new File(file.getPath()+"_bad");
			if (backup.exists()) backup.delete();
			Bukkit.getLogger().log(Level.WARNING, "Backing up copy of bad file to: "+backup);
			file.renameTo(backup);

			return null;
		}
	}
	
	// -------------------------------------------- //
	// ID MANAGEMENT
	// -------------------------------------------- //
	
	public String getNextId()
	{
		while ( ! isIdFree(this.nextId) )
		{
			this.nextId += 1;
		}
		return Integer.toString(this.nextId);
	}
	
	public boolean isIdFree(String id)
	{
		return ! this.id2entity.containsKey(id);
	}
	public boolean isIdFree(int id)
	{
		return this.isIdFree(Integer.toString(id));
	}
	
	protected synchronized void fillIds()
	{
		this.nextId = 1;
		for(Entry<String, E> entry : this.id2entity.entrySet())
		{
			String id = entry.getKey();
			E entity = entry.getValue();
			entity.id = id;
			this.updateNextIdForId(id);
		}
	}
	
	protected synchronized void updateNextIdForId(int id)
	{
		if (this.nextId < id)
		{
			this.nextId = id + 1;
		}
	}
	
	protected void updateNextIdForId(String id)
	{
		try
		{
			int idAsInt = Integer.parseInt(id);
			this.updateNextIdForId(idAsInt);
		}
		catch (Exception ignored) { }
	}
}
