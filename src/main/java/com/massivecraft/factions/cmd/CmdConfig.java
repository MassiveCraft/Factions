package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Set;

public class CmdConfig extends FCommand {

    private static HashMap<String, String> properFieldNames = new HashMap<>();

    public CmdConfig() {
        super();
        this.aliases.add("config");

        this.requiredArgs.add("setting");
        this.requiredArgs.add("value");
        this.errorOnToManyArgs = false;

        this.permission = Permission.CONFIG.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        // store a lookup map of lowercase field names paired with proper capitalization field names
        // that way, if the person using this command messes up the capitalization, we can fix that
        if (properFieldNames.isEmpty()) {
            Field[] fields = Conf.class.getDeclaredFields();
            for (Field field : fields) {
                properFieldNames.put(field.getName().toLowerCase(), field.getName());
            }
        }

        String field = this.argAsString(0).toLowerCase();
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
        }
        String fieldName = properFieldNames.get(field);

        if (fieldName == null || fieldName.isEmpty()) {
            msg(TL.COMMAND_CONFIG_NOEXIST, field);
            return;
        }

        String success;

        StringBuilder value = new StringBuilder(args.get(1));
        for (int i = 2; i < args.size(); i++) {
            value.append(' ').append(args.get(i));
        }

        try {
            Field target = Conf.class.getField(fieldName);

            // boolean
            if (target.getType() == boolean.class) {
                boolean targetValue = this.strAsBool(value.toString());
                target.setBoolean(null, targetValue);

                if (targetValue) {
                    success = "\"" + fieldName + TL.COMMAND_CONFIG_SET_TRUE.toString();
                } else {
                    success = "\"" + fieldName + TL.COMMAND_CONFIG_SET_FALSE.toString();
                }
            }

            // int
            else if (target.getType() == int.class) {
                try {
                    int intVal = Integer.parseInt(value.toString());
                    target.setInt(null, intVal);
                    success = "\"" + fieldName + TL.COMMAND_CONFIG_OPTIONSET.toString() + intVal + ".";
                } catch (NumberFormatException ex) {
                    sendMessage(TL.COMMAND_CONFIG_INTREQUIRED.format(fieldName));
                    return;
                }
            }

            // long
            else if (target.getType() == long.class) {
                try {
                    long longVal = Long.parseLong(value.toString());
                    target.setLong(null, longVal);
                    success = "\"" + fieldName + TL.COMMAND_CONFIG_OPTIONSET.toString() + longVal + ".";
                } catch (NumberFormatException ex) {
                    sendMessage(TL.COMMAND_CONFIG_LONGREQUIRED.format(fieldName));
                    return;
                }
            }

            // double
            else if (target.getType() == double.class) {
                try {
                    double doubleVal = Double.parseDouble(value.toString());
                    target.setDouble(null, doubleVal);
                    success = "\"" + fieldName + TL.COMMAND_CONFIG_OPTIONSET.toString() + doubleVal + ".";
                } catch (NumberFormatException ex) {
                    sendMessage(TL.COMMAND_CONFIG_DOUBLEREQUIRED.format(fieldName));
                    return;
                }
            }

            // float
            else if (target.getType() == float.class) {
                try {
                    float floatVal = Float.parseFloat(value.toString());
                    target.setFloat(null, floatVal);
                    success = "\"" + fieldName + TL.COMMAND_CONFIG_OPTIONSET.toString() + floatVal + ".";
                } catch (NumberFormatException ex) {
                    sendMessage(TL.COMMAND_CONFIG_FLOATREQUIRED.format(fieldName));
                    return;
                }
            }

            // String
            else if (target.getType() == String.class) {
                target.set(null, value.toString());
                success = "\"" + fieldName + TL.COMMAND_CONFIG_OPTIONSET.toString() + value + "\".";
            }

            // ChatColor
            else if (target.getType() == ChatColor.class) {
                ChatColor newColor = null;
                try {
                    newColor = ChatColor.valueOf(value.toString().toUpperCase());
                } catch (IllegalArgumentException ex) {

                }
                if (newColor == null) {
                    sendMessage(TL.COMMAND_CONFIG_INVALID_COLOUR.format(fieldName, value.toString().toUpperCase()));
                    return;
                }
                target.set(null, newColor);
                success = "\"" + fieldName + TL.COMMAND_CONFIG_COLOURSET.toString() + value.toString().toUpperCase() + "\".";
            }

            // Set<?> or other parameterized collection
            else if (target.getGenericType() instanceof ParameterizedType) {
                ParameterizedType targSet = (ParameterizedType) target.getGenericType();
                Type innerType = targSet.getActualTypeArguments()[0];

                // not a Set, somehow, and that should be the only collection we're using in Conf.java
                if (targSet.getRawType() != Set.class) {
                    sendMessage(TL.COMMAND_CONFIG_INVALID_COLLECTION.format(fieldName));
                    return;
                }

                // Set<Material>
                else if (innerType == Material.class) {
                    Material newMat = null;
                    try {
                        newMat = Material.valueOf(value.toString().toUpperCase());
                    } catch (IllegalArgumentException ex) {

                    }
                    if (newMat == null) {
                        sendMessage(TL.COMMAND_CONFIG_INVALID_MATERIAL.format(fieldName, value.toString().toUpperCase()));
                        return;
                    }

                    @SuppressWarnings("unchecked") Set<Material> matSet = (Set<Material>) target.get(null);

                    // Material already present, so remove it
                    if (matSet.contains(newMat)) {
                        matSet.remove(newMat);
                        target.set(null, matSet);
                        success = TL.COMMAND_CONFIG_MATERIAL_REMOVED.format(fieldName, value.toString().toUpperCase());
                    }
                    // Material not present yet, add it
                    else {
                        matSet.add(newMat);
                        target.set(null, matSet);
                        success = TL.COMMAND_CONFIG_MATERIAL_ADDED.format(fieldName, value.toString().toUpperCase());
                    }
                }

                // Set<String>
                else if (innerType == String.class) {
                    @SuppressWarnings("unchecked") Set<String> stringSet = (Set<String>) target.get(null);

                    // String already present, so remove it
                    if (stringSet.contains(value.toString())) {
                        stringSet.remove(value.toString());
                        target.set(null, stringSet);
                        success = TL.COMMAND_CONFIG_SET_REMOVED.format(fieldName, value.toString());
                    }
                    // String not present yet, add it
                    else {
                        stringSet.add(value.toString());
                        target.set(null, stringSet);
                        success = TL.COMMAND_CONFIG_SET_ADDED.format(fieldName, value.toString());
                    }
                }

                // Set of unknown type
                else {
                    sendMessage(TL.COMMAND_CONFIG_INVALID_TYPESET.format(fieldName));
                    return;
                }
            }

            // unknown type
            else {
                sendMessage(TL.COMMAND_CONFIG_ERROR_TYPE.format(fieldName, target.getClass().getName()));
                return;
            }
        } catch (NoSuchFieldException ex) {
            sendMessage(TL.COMMAND_CONFIG_ERROR_MATCHING.format(fieldName));
            return;
        } catch (IllegalAccessException ex) {
            sendMessage(TL.COMMAND_CONFIG_ERROR_SETTING.format(fieldName, value.toString()));
            return;
        }

        if (!success.isEmpty()) {
            if (sender instanceof Player) {
                sendMessage(success);
                P.p.log(success + TL.COMMAND_CONFIG_LOG.format((Player) sender));
            } else  // using P.p.log() instead of sendMessage if run from server console so that "[Factions v#.#.#]" is prepended in server log
            {
                P.p.log(success);
            }
        }
        // save change to disk
        Conf.save();
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CONFIG_DESCRIPTION;
    }

}
