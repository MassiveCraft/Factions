package com.massivecraft.factions.util;

import com.google.gson.*;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PermissionsMapTypeAdapter implements JsonDeserializer<Map<Permissable, Map<PermissableAction, Access>>> {

    @Override
    public Map<Permissable, Map<PermissableAction, Access>> deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {

        try {
            JsonObject obj = json.getAsJsonObject();
            if (obj == null) {
                return null;
            }

            Map<Permissable, Map<PermissableAction, Access>> permissionsMap = new ConcurrentHashMap<>();

            // Top level is Relation
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                Permissable permissable = getPermissable(entry.getKey());

                if (permissable == null) {
                    continue;
                }

                // Second level is the map between action -> access
                Map<PermissableAction, Access> accessMap = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry2 : entry.getValue().getAsJsonObject().entrySet()) {
                    PermissableAction permissableAction = PermissableAction.fromString(entry2.getKey());
                    if (permissableAction == null) {
                        switch (entry2.getKey()) {
                            case "frostwalk":
                                permissableAction = PermissableAction.FROST_WALK;
                                break;
                            case "painbuild":
                                permissableAction = PermissableAction.PAIN_BUILD;
                                break;
                            case "items":
                                permissableAction = PermissableAction.ITEM;
                                break;
                        }
                    }
                    Access access = Access.fromString(entry2.getValue().getAsString());
                    accessMap.put(permissableAction, access);
                }
                permissionsMap.put(permissable, accessMap);
            }

            return permissionsMap;

        } catch (Exception ex) {
            P.p.log(Level.WARNING, "Error encountered while deserializing a PermissionsMap.");
            ex.printStackTrace();
            return null;
        }
    }

    private Permissable getPermissable(String name) {
        // If name is uppercase then it is (probably, no way to completely know) valid if not begin conversion
        if (name.equals(name.toUpperCase())) {
            if (Role.fromString(name.toUpperCase()) != null) {
                return Role.fromString(name.toUpperCase());
            } else if (Relation.fromString(name.toUpperCase()) != null) {
                return Relation.fromString(name.toUpperCase());
            } else {
                return null;
            }
        } else {
            if (name.equals(TL.ROLE_RECRUIT.toString())) {
                return Role.RECRUIT;
            } else if (name.equals(TL.ROLE_NORMAL.toString())) {
                return Role.NORMAL;
            } else if (name.equals(TL.ROLE_MODERATOR.toString())) {
                return Role.MODERATOR;
            } else {
                // If it is explicitly member and its old data then it refers to relation member not role, skip it
                if (name.equals("member")) {
                    return null;
                }
                return Relation.fromString(name);
            }
        }
    }

}
