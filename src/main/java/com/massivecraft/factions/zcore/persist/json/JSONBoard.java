package com.massivecraft.factions.zcore.persist.json;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.persist.MemoryBoard;
import com.massivecraft.factions.zcore.util.DiscUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class JSONBoard extends MemoryBoard {
    private static transient File file = new File(P.p.getDataFolder(), "board.json");

    // -------------------------------------------- //
    // Persistance
    // -------------------------------------------- //

    public Map<String, Map<String, String>> dumpAsSaveFormat() {
        Map<String, Map<String, String>> worldCoordIds = new HashMap<String, Map<String, String>>();

        String worldName, coords;
        String id;

        for (Entry<FLocation, String> entry : flocationIds.entrySet()) {
            worldName = entry.getKey().getWorldName();
            coords = entry.getKey().getCoordString();
            id = entry.getValue();
            if (!worldCoordIds.containsKey(worldName)) {
                worldCoordIds.put(worldName, new TreeMap<String, String>());
            }

            worldCoordIds.get(worldName).put(coords, id);
        }

        return worldCoordIds;
    }

    public void loadFromSaveFormat(Map<String, Map<String, String>> worldCoordIds) {
        flocationIds.clear();

        String worldName;
        String[] coords;
        int x, z;
        String factionId;

        for (Entry<String, Map<String, String>> entry : worldCoordIds.entrySet()) {
            worldName = entry.getKey();
            for (Entry<String, String> entry2 : entry.getValue().entrySet()) {
                coords = entry2.getKey().trim().split("[,\\s]+");
                x = Integer.parseInt(coords[0]);
                z = Integer.parseInt(coords[1]);
                factionId = entry2.getValue();
                flocationIds.put(new FLocation(worldName, x, z), factionId);
            }
        }
    }

    public boolean forceSave() {
        return forceSave(true);
    }

    public boolean forceSave(boolean sync) {
        if (sync) {
            try {
                DiscUtil.write(file, P.p.gson.toJson(dumpAsSaveFormat()));
            } catch (Exception e) {
                e.printStackTrace();
                P.p.log("Failed to save the board to disk.");
                return false;
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(P.p, new Runnable() {
                @Override
                public void run() {
                    try {
                        DiscUtil.write(file, P.p.gson.toJson(dumpAsSaveFormat()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        P.p.log("Failed to save the board to disk.");
                    }
                }
            });
        }

        return true;
    }

    public boolean load() {
        P.p.log("Loading board from disk");

        if (!file.exists()) {
            P.p.log("No board to load from disk. Creating new file.");
            forceSave();
            return true;
        }

        try {
            Type type = new TypeToken<Map<String, Map<String, String>>>() {
            }.getType();
            Map<String, Map<String, String>> worldCoordIds = P.p.gson.fromJson(DiscUtil.read(file), type);
            loadFromSaveFormat(worldCoordIds);
            P.p.log("Loaded " + flocationIds.size() + " board locations");
        } catch (Exception e) {
            e.printStackTrace();
            P.p.log("Failed to load the board from disk.");
            return false;
        }

        return true;
    }

    @Override
    public void convertFrom(MemoryBoard old) {
        this.flocationIds = old.flocationIds;
        forceSave();
        Board.instance = this;
    }
}
