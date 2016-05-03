package com.massivecraft.factions.zcore.persist.json;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.persist.MemoryFPlayer;
import com.massivecraft.factions.zcore.persist.MemoryFPlayers;
import com.massivecraft.factions.zcore.util.DiscUtil;
import com.massivecraft.factions.zcore.util.UUIDFetcher;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

public class JSONFPlayers extends MemoryFPlayers {
    // Info on how to persist
    private Gson gson;

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    private File file;

    public JSONFPlayers() {
        file = new File(P.p.getDataFolder(), "players.json");
        gson = P.p.gson;
    }

    public void convertFrom(MemoryFPlayers old) {
        this.fPlayers.putAll(Maps.transformValues(old.fPlayers, new Function<FPlayer, JSONFPlayer>() {
            @Override
            public JSONFPlayer apply(FPlayer arg0) {
                return new JSONFPlayer((MemoryFPlayer) arg0);
            }
        }));
        forceSave();
        FPlayers.instance = this;
    }

    public void forceSave() {
        forceSave(true);
    }

    public void forceSave(boolean sync) {
        final Map<String, JSONFPlayer> entitiesThatShouldBeSaved = new HashMap<String, JSONFPlayer>();
        for (FPlayer entity : this.fPlayers.values()) {
            if (((MemoryFPlayer) entity).shouldBeSaved()) {
                entitiesThatShouldBeSaved.put(entity.getId(), (JSONFPlayer) entity);
            }
        }

        saveCore(file, entitiesThatShouldBeSaved, sync);
    }

    private boolean saveCore(File target, Map<String, JSONFPlayer> data, boolean sync) {
        return DiscUtil.writeCatch(target, this.gson.toJson(data), sync);
    }

    public void load() {
        Map<String, JSONFPlayer> fplayers = this.loadCore();
        if (fplayers == null) {
            return;
        }
        this.fPlayers.clear();
        this.fPlayers.putAll(fplayers);
        P.p.log("Loaded " + fPlayers.size() + " players");
    }

    private Map<String, JSONFPlayer> loadCore() {
        if (!this.file.exists()) {
            return new HashMap<String, JSONFPlayer>();
        }

        String content = DiscUtil.readCatch(this.file);
        if (content == null) {
            return null;
        }

        Map<String, JSONFPlayer> data = this.gson.fromJson(content, new TypeToken<Map<String, JSONFPlayer>>() {
        }.getType());
        Set<String> list = new HashSet<String>();
        Set<String> invalidList = new HashSet<String>();
        for (Entry<String, JSONFPlayer> entry : data.entrySet()) {
            String key = entry.getKey();
            entry.getValue().setId(key);
            if (doesKeyNeedMigration(key)) {
                if (!isKeyInvalid(key)) {
                    list.add(key);
                } else {
                    invalidList.add(key);
                }
            }
        }

        if (list.size() > 0) {
            // We've got some converting to do!
            Bukkit.getLogger().log(Level.INFO, "Factions is now updating players.json");

            // First we'll make a backup, because god forbid anybody heed a
            // warning
            File file = new File(this.file.getParentFile(), "players.json.old");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveCore(file, (Map<String, JSONFPlayer>) data, true);
            Bukkit.getLogger().log(Level.INFO, "Backed up your old data at " + file.getAbsolutePath());

            // Start fetching those UUIDs
            Bukkit.getLogger().log(Level.INFO, "Please wait while Factions converts " + list.size() + " old player names to UUID. This may take a while.");
            UUIDFetcher fetcher = new UUIDFetcher(new ArrayList<String>(list));
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
                    // For all the valid responses, let's replace their old
                    // named entry with a UUID key
                    String id = response.get(value).toString();

                    JSONFPlayer player = data.get(value);

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
            saveCore(this.file, (Map<String, JSONFPlayer>) data, true); // Update the
            // flatfile
            Bukkit.getLogger().log(Level.INFO, "Done converting players.json to UUID.");
        }
        return (Map<String, JSONFPlayer>) data;
    }

    private boolean doesKeyNeedMigration(String key) {
        if (!key.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            // Not a valid UUID..
            if (key.matches("[a-zA-Z0-9_]{2,16}")) {
                // Valid playername, we'll mark this as one for conversion
                // to UUID
                return true;
            }
        }
        return false;
    }

    private boolean isKeyInvalid(String key) {
        return !key.matches("[a-zA-Z0-9_]{2,16}");
    }

    @Override
    public FPlayer generateFPlayer(String id) {
        FPlayer player = new JSONFPlayer(id);
        this.fPlayers.put(player.getId(), player);
        return player;
    }
}
