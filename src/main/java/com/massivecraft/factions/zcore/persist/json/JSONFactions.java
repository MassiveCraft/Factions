package com.massivecraft.factions.zcore.persist.json;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.persist.MemoryFaction;
import com.massivecraft.factions.zcore.persist.MemoryFactions;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.UUIDFetcher;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class JSONFactions extends MemoryFactions {
    // Info on how to persist
    private final Gson gson;

    public Gson getGson() {
        return gson;
    }

    private final File file;

    public File getFile() {
        return file;
    }

    // -------------------------------------------- //
    // CONSTRUCTORS
    // -------------------------------------------- //

    public JSONFactions() {
        this.file = new File(P.p.getDataFolder(), "factions.json");
        this.gson = P.p.gson;
        this.nextId = 1;
    }

    public void forceSave() {
        forceSave(true);
    }

    public void forceSave(boolean sync) {
        final Map<String, JSONFaction> entitiesThatShouldBeSaved = new HashMap<String, JSONFaction>();
        for (Faction entity : this.factions.values()) {
            entitiesThatShouldBeSaved.put(entity.getId(), (JSONFaction) entity);
        }

        saveCore(file, entitiesThatShouldBeSaved, sync);
    }

    private boolean saveCore(File target, Map<String, JSONFaction> entities, boolean sync) {
        return DiscUtil.writeCatch(target, this.gson.toJson(entities), sync);
    }

    public void load() {
        Map<String, JSONFaction> factions = this.loadCore();
        if (factions == null) {
            return;
        }
        this.factions.putAll(factions);

        super.load();
        P.p.log("Loaded " + factions.size() + " Factions");
    }

    private Map<String, JSONFaction> loadCore() {
        if (!this.file.exists()) {
            return new HashMap<String, JSONFaction>();
        }

        String content = DiscUtil.readCatch(this.file);
        if (content == null) {
            return null;
        }

        Map<String, JSONFaction> data = this.gson.fromJson(content, new TypeToken<Map<String, JSONFaction>>() {
        }.getType());

        this.nextId = 1;
        // Do we have any names that need updating in claims or invites?

        int needsUpdate = 0;
        for (Entry<String, JSONFaction> entry : data.entrySet()) {
            String id = entry.getKey();
            Faction f = entry.getValue();
            f.setId(id);
            this.updateNextIdForId(id);
            needsUpdate += whichKeysNeedMigration(f.getInvites()).size();
            for (Set<String> keys : f.getClaimOwnership().values()) {
                needsUpdate += whichKeysNeedMigration(keys).size();
            }
        }

        if (needsUpdate > 0) {
            // We've got some converting to do!
            Bukkit.getLogger().log(Level.INFO, "Factions is now updating factions.json");

            // First we'll make a backup, because god forbid anybody heed a
            // warning
            File file = new File(this.file.getParentFile(), "factions.json.old");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveCore(file, (Map<String, JSONFaction>) data, true);
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
                        UUIDFetcher fetcher = new UUIDFetcher(new ArrayList<String>(list));
                        try {
                            Map<String, UUID> response = fetcher.call();
                            for (String value : response.keySet()) {
                                // Let's replace their old named entry with a
                                // UUID key
                                String id = response.get(value).toString();
                                set.remove(value.toLowerCase()); // Out with the
                                // old...
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
                    UUIDFetcher fetcher = new UUIDFetcher(new ArrayList<String>(list));
                    try {
                        Map<String, UUID> response = fetcher.call();
                        for (String value : response.keySet()) {
                            // Let's replace their old named entry with a UUID
                            // key
                            String id = response.get(value).toString();
                            invites.remove(value.toLowerCase()); // Out with the
                            // old...
                            invites.add(id); // And in with the new
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            saveCore(this.file, (Map<String, JSONFaction>) data, true); // Update the flatfile
            Bukkit.getLogger().log(Level.INFO, "Done converting factions.json to UUID.");
        }
        return data;
    }

    private Set<String> whichKeysNeedMigration(Set<String> keys) {
        HashSet<String> list = new HashSet<String>();
        for (String value : keys) {
            if (!value.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                // Not a valid UUID..
                if (value.matches("[a-zA-Z0-9_]{2,16}")) {
                    // Valid playername, we'll mark this as one for conversion
                    // to UUID
                    list.add(value);
                }
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
        return !this.factions.containsKey(id);
    }

    public boolean isIdFree(int id) {
        return this.isIdFree(Integer.toString(id));
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

    @Override
    public Faction generateFactionObject() {
        String id = getNextId();
        Faction faction = new JSONFaction(id);
        updateNextIdForId(id);
        return faction;
    }

    @Override
    public Faction generateFactionObject(String id) {
        Faction faction = new JSONFaction(id);
        return faction;
    }

    @Override
    public void convertFrom(MemoryFactions old) {
        this.factions.putAll(Maps.transformValues(old.factions, new Function<Faction, JSONFaction>() {
            @Override
            public JSONFaction apply(Faction arg0) {
                return new JSONFaction((MemoryFaction) arg0);
            }
        }));
        this.nextId = old.nextId;
        forceSave();
        Factions.instance = this;
    }
}
