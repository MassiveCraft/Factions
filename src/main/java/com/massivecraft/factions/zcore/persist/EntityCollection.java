package com.massivecraft.factions.zcore.persist;

import com.google.gson.Gson;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import com.massivecraft.factions.zcore.util.UUIDFetcher;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public abstract class EntityCollection<E extends Entity> {

    // -------------------------------------------- //
    // FIELDS
    // -------------------------------------------- //

    // These must be instantiated in order to allow for different configuration (orders, comparators etc)
    private Collection<E> entities;
    protected Map<String, E> id2entity;

    // If the entities are creative they will create a new instance if a non existent id was requested
    private boolean creative;

    public boolean isCreative() {
        return creative;
    }

    public void setCreative(boolean creative) {
        this.creative = creative;
    }

    // This is the auto increment for the primary key "id"
    private int nextId;

    // This ugly crap is necessary due to java type erasure
    private Class<E> entityClass;

    public abstract Type getMapType(); // This is special stuff for GSON.

    // Info on how to persist
    private Gson gson;

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    // -------------------------------------------- //
    // CONSTRUCTORS
    // -------------------------------------------- //

    public EntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson, boolean creative) {
        this.entityClass = entityClass;
        this.entities = entities;
        this.id2entity = id2entity;
        this.file = file;
        this.gson = gson;
        this.creative = creative;
        this.nextId = 1;

        EM.setEntitiesCollectionForEntityClass(this.entityClass, this);
    }

    public EntityCollection(Class<E> entityClass, Collection<E> entities, Map<String, E> id2entity, File file, Gson gson) {
        this(entityClass, entities, id2entity, file, gson, false);
    }

    // -------------------------------------------- //
    // GET
    // -------------------------------------------- //

    public Collection<E> get() {
        return entities;
    }

    public Map<String, E> getMap() {
        return this.id2entity;
    }

    public E get(String id) {
        if (this.creative) {
            return this.getCreative(id);
        }
        return id2entity.get(id);
    }

    public E getCreative(String id) {
        E e = id2entity.get(id);
        if (e != null) {
            return e;
        }
        return this.create(id);
    }

    public boolean exists(String id) {
        if (id == null) {
            return false;
        }
        return id2entity.get(id) != null;
    }

    public E getBestIdMatch(String pattern) {
        String id = TextUtil.getBestStartWithCI(this.id2entity.keySet(), pattern);
        if (id == null) {
            return null;
        }
        return this.id2entity.get(id);
    }

    // -------------------------------------------- //
    // CREATE
    // -------------------------------------------- //

    public synchronized E create() {
        return this.create(this.getNextId());
    }

    public synchronized E create(String id) {
        if (!this.isIdFree(id)) {
            return null;
        }

        E e = null;
        try {
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

    public void attach(E entity) {
        if (entity.getId() != null) {
            return;
        }
        entity.setId(this.getNextId());
        this.entities.add(entity);
        this.id2entity.put(entity.getId(), entity);
    }

    public void detach(E entity) {
        entity.preDetach();
        this.entities.remove(entity);
        this.id2entity.remove(entity.getId());
        entity.postDetach();
    }

    public void detach(String id) {
        E entity = this.id2entity.get(id);
        if (entity == null) {
            return;
        }
        this.detach(entity);
    }

    public boolean attached(E entity) {
        return this.entities.contains(entity);
    }

    public boolean detached(E entity) {
        return !this.attached(entity);
    }

    // -------------------------------------------- //
    // DISC
    // -------------------------------------------- //

    // we don't want to let saveToDisc() run multiple iterations simultaneously
    private boolean saveIsRunning = false;

    public boolean saveToDisc() {
        if (saveIsRunning) {
            return true;
        }
        saveIsRunning = true;

        Map<String, E> entitiesThatShouldBeSaved = new HashMap<String, E>();
        for (E entity : this.entities) {
            if (entity.shouldBeSaved()) {
                entitiesThatShouldBeSaved.put(entity.getId(), entity);
            }
        }

        saveIsRunning = false;
        return this.saveCore(this.file, entitiesThatShouldBeSaved);
    }

    private boolean saveCore(File target, Map<String, E> entities) {
        return DiscUtil.writeCatch(target, this.gson.toJson(entities));
    }

    public boolean loadFromDisc() {
        Map<String, E> id2entity = this.loadCore();
        if (id2entity == null) {
            return false;
        }
        this.entities.clear();
        this.entities.addAll(id2entity.values());
        this.id2entity.clear();
        this.id2entity.putAll(id2entity);
        this.fillIds();
        return true;
    }

    private Map<String, E> loadCore() {
        if (!this.file.exists()) {
            return new HashMap<String, E>();
        }

        String content = DiscUtil.readCatch(this.file);
        if (content == null) {
            return null;
        }

        Type type = this.getMapType();
        if (type.toString().contains("FPlayer")) {
            Map<String, FPlayer> data = this.gson.fromJson(content, type);
            Set<String> list = whichKeysNeedMigration(data.keySet());
            Set<String> invalidList = whichKeysAreInvalid(list);
            list.removeAll(invalidList);

            if (list.size() > 0) {
                // We've got some converting to do!
                Bukkit.getLogger().log(Level.INFO, "Factions is now updating players.json");

                // First we'll make a backup, because god forbid anybody heed a warning
                File file = new File(this.file.getParentFile(), "players.json.old");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveCore(file, (Map<String, E>) data);
                Bukkit.getLogger().log(Level.INFO, "Backed up your old data at " + file.getAbsolutePath());

                // Start fetching those UUIDs
                Bukkit.getLogger().log(Level.INFO, "Please wait while Factions converts " + list.size() + " old player names to UUID. This may take a while.");
                UUIDFetcher fetcher = new UUIDFetcher(new ArrayList(list));
                try {
                    Map<String, UUID> response = fetcher.call();
                    for (String s : list) {
                        // Are we missing any responses?
                        if (!response.containsKey(s)) {
                            // They don't have a UUID so they should just be removed
                            invalidList.add(s);
                        }
                    }
                    for (String value : response.keySet()) {
                        // For all the valid responses, let's replace their old named entry with a UUID key
                        String id = response.get(value).toString();

                        FPlayer player = data.get(value);

                        if (player == null) {
                            // The player never existed here, and shouldn't persist
                            invalidList.add(value);
                            continue;
                        }

                        player.setId(id); // Update the object so it knows

                        data.remove(value); // Out with the old...
                        data.put(id, player); // And in with the new
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (invalidList.size() > 0) {
                    for (String name : invalidList) {
                        // Remove all the invalid names we collected
                        data.remove(name);
                    }
                    Bukkit.getLogger().log(Level.INFO, "While converting we found names that either don't have a UUID or aren't players and removed them from storage.");
                    Bukkit.getLogger().log(Level.INFO, "The following names were detected as being invalid: " + StringUtils.join(invalidList, ", "));
                }
                saveCore(this.file, (Map<String, E>) data); // Update the flatfile
                Bukkit.getLogger().log(Level.INFO, "Done converting players.json to UUID.");
            }
            return (Map<String, E>) data;
        } else {
            Map<String, Faction> data = this.gson.fromJson(content, type);

            // Do we have any names that need updating in claims or invites?

            int needsUpdate = 0;
            for (String string : data.keySet()) {
                Faction f = data.get(string);
                needsUpdate += whichKeysNeedMigration(f.getInvites()).size();
                Map<FLocation, Set<String>> claims = f.getClaimOwnership();
                for (FLocation key : f.getClaimOwnership().keySet()) {
                    needsUpdate += whichKeysNeedMigration(claims.get(key)).size();
                }
            }

            if (needsUpdate > 0) {
                // We've got some converting to do!
                Bukkit.getLogger().log(Level.INFO, "Factions is now updating factions.json");

                // First we'll make a backup, because god forbid anybody heed a warning
                File file = new File(this.file.getParentFile(), "factions.json.old");
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveCore(file, (Map<String, E>) data);
                Bukkit.getLogger().log(Level.INFO, "Backed up your old data at " + file.getAbsolutePath());

                Bukkit.getLogger().log(Level.INFO, "Please wait while Factions converts " + needsUpdate + " old player names to UUID. This may take a while.");

                // Update claim ownership

                for (String string : data.keySet()) {
                    Faction f = data.get(string);
                    Map<FLocation, Set<String>> claims = f.getClaimOwnership();
                    for (FLocation key : claims.keySet()) {
                        Set<String> set = claims.get(key);

                        Set<String> list = whichKeysNeedMigration(set);

                        if (list.size() > 0) {
                            UUIDFetcher fetcher = new UUIDFetcher(new ArrayList(list));
                            try {
                                Map<String, UUID> response = fetcher.call();
                                for (String value : response.keySet()) {
                                    // Let's replace their old named entry with a UUID key
                                    String id = response.get(value).toString();
                                    set.remove(value.toLowerCase()); // Out with the old...
                                    set.add(id); // And in with the new
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            claims.put(key, set); // Update
                        }
                    }
                }

                // Update invites

                for (String string : data.keySet()) {
                    Faction f = data.get(string);
                    Set<String> invites = f.getInvites();
                    Set<String> list = whichKeysNeedMigration(invites);

                    if (list.size() > 0) {
                        UUIDFetcher fetcher = new UUIDFetcher(new ArrayList(list));
                        try {
                            Map<String, UUID> response = fetcher.call();
                            for (String value : response.keySet()) {
                                // Let's replace their old named entry with a UUID key
                                String id = response.get(value).toString();
                                invites.remove(value.toLowerCase()); // Out with the old...
                                invites.add(id); // And in with the new
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                saveCore(this.file, (Map<String, E>) data); // Update the flatfile
                Bukkit.getLogger().log(Level.INFO, "Done converting factions.json to UUID.");
            }
            return (Map<String, E>) data;
        }
    }

    private Set<String> whichKeysNeedMigration(Set<String> keys) {
        HashSet<String> list = new HashSet<String>();
        for (String value : keys) {
            if (!value.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                // Not a valid UUID..
                if (value.matches("[a-zA-Z0-9_]{2,16}")) {
                    // Valid playername, we'll mark this as one for conversion to UUID
                    list.add(value);
                }
            }
        }
        return list;
    }

    private Set<String> whichKeysAreInvalid(Set<String> keys) {
        Set<String> list = new HashSet<String>();
        for (String value : keys) {
            if (!value.matches("[a-zA-Z0-9_]{2,16}")) {
                // Not a valid player name.. go ahead and mark it for removal
                list.add(value);
            }
        }
        return list;
    }

    // -------------------------------------------- //
    // ID MANAGEMENT
    // -------------------------------------------- //

    public String getNextId() {
        while (!isIdFree(this.nextId)) {
            this.nextId += 1;
        }
        return Integer.toString(this.nextId);
    }

    public boolean isIdFree(String id) {
        return !this.id2entity.containsKey(id);
    }

    public boolean isIdFree(int id) {
        return this.isIdFree(Integer.toString(id));
    }

    protected synchronized void fillIds() {
        this.nextId = 1;
        for (Entry<String, E> entry : this.id2entity.entrySet()) {
            String id = entry.getKey();
            E entity = entry.getValue();
            entity.id = id;
            this.updateNextIdForId(id);
        }
    }

    protected synchronized void updateNextIdForId(int id) {
        if (this.nextId < id) {
            this.nextId = id + 1;
        }
    }

    protected void updateNextIdForId(String id) {
        try {
            int idAsInt = Integer.parseInt(id);
            this.updateNextIdForId(idAsInt);
        } catch (Exception ignored) {
        }
    }
}
