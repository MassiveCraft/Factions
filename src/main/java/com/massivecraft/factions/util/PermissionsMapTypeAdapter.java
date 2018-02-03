package com.massivecraft.factions.util;

import com.google.gson.*;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PermissionsMapTypeAdapter implements JsonDeserializer<Map<Relation, Map<PermissableAction, Access>>> {

    @Override
    public Map<Relation, Map<PermissableAction, Access>> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {

        try {
            JsonObject obj = json.getAsJsonObject();
            if (obj == null) {
                return null;
            }

            Map<Relation, Map<PermissableAction, Access>> permissionsMap = new ConcurrentHashMap<>();

            // Top level is Relation
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                Relation relation = Relation.fromString(entry.getKey());

                // Second level is the map between action -> access
                for (Map.Entry<String, JsonElement> entry2 : entry.getValue().getAsJsonObject().entrySet()) {
                    Map<PermissableAction, Access> accessMap = new HashMap<>();
                    PermissableAction permissableAction = PermissableAction.fromString(entry2.getKey());
                    Access access = Access.fromString(entry2.getValue().getAsString());
                    accessMap.put(permissableAction, access);

                    permissionsMap.put(relation, accessMap);
                }
            }

            return permissionsMap;

        } catch (Exception ex) {
            ex.printStackTrace();
            P.p.log(Level.WARNING, "Error encountered while deserializing a PermissionsMap.");
            return null;
        }
    }
}
