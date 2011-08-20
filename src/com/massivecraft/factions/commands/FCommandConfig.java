package com.massivecraft.factions.commands;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.SpoutFeatures;

public class FCommandConfig extends FBaseCommand {

	private static HashMap<String, String> properFieldNames = new HashMap<String, String>();

	public FCommandConfig() {
		aliases.add("config");

		senderMustBePlayer = false;

		requiredParameters.add("setting");
		requiredParameters.add("value");

		helpDescription = "change a conf.json setting";
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermConfigure(sender);
	}

	@Override
	public void perform() {

		if( isLocked() ) {
			sendLockMessage();
			return;
		}

		// store a lookup map of lowercase field names paired with proper capitalization field names
		// that way, if the person using this command messes up the capitalization, we can fix that
		if (properFieldNames.isEmpty()) {
			Field[] fields = Conf.class.getDeclaredFields();
			for(int i = 0; i < fields.length; i++) {
				properFieldNames.put(fields[i].getName().toLowerCase(), fields[i].getName());
			}
		}

		String field = parameters.get(0).toLowerCase();
		if (field.startsWith("\"") && field.endsWith("\"")) {
			field = field.substring(1, field.length() - 1);
		}
		String fieldName = properFieldNames.get(field);

		if (fieldName == null || fieldName.isEmpty()) {
			sendMessage("No configuration setting \""+parameters.get(0)+"\" exists.");
			return;
		}

		String success = "";

		String value = parameters.get(1);
		for(int i = 2; i < parameters.size(); i++) {
			value += ' ' + parameters.get(i);
		}

		try {
			Field target = Conf.class.getField(fieldName);

			// boolean
			if (target.getType() == boolean.class) {
				if (aliasTrue.contains(value.toLowerCase())) {
					target.setBoolean(null, true);
					success = "\""+fieldName+"\" option set to true (enabled).";
				}
				else if (aliasFalse.contains(value.toLowerCase())) {
					target.setBoolean(null, false);
					success = "\""+fieldName+"\" option set to false (disabled).";
				}
				else {
					sendMessage("Cannot set \""+fieldName+"\": boolean value required (true or false).");
					return;
				}
			}

			// int 
			else if (target.getType() == int.class) {
				try {
					int intVal = Integer.parseInt(value);
					target.setInt(null, intVal);
					success = "\""+fieldName+"\" option set to "+intVal+".";
				}
				catch(NumberFormatException ex) {
					sendMessage("Cannot set \""+fieldName+"\": integer (whole number) value required.");
					return;
				}
			}

			// double
			else if (target.getType() == double.class) {
				try {
					double doubleVal = Double.parseDouble(value);
					target.setDouble(null, doubleVal);
					success = "\""+fieldName+"\" option set to "+doubleVal+".";
				}
				catch(NumberFormatException ex) {
					sendMessage("Cannot set \""+fieldName+"\": double (numeric) value required.");
					return;
				}
			}

			// String
			else if (target.getType() == String.class) {
				target.set(null, value);
				success = "\""+fieldName+"\" option set to \""+value+"\".";
			}

			// ChatColor
			else if (target.getType() == ChatColor.class) {
				ChatColor newColor = null;
				try {
					newColor = ChatColor.valueOf(value.toUpperCase());
				}
				catch (IllegalArgumentException ex) {
				}
				if (newColor == null) {
					sendMessage("Cannot set \""+fieldName+"\": \""+value.toUpperCase()+"\" is not a valid color.");
					return;
				}
				target.set(null, newColor);
				success = "\""+fieldName+"\" color option set to \""+value.toUpperCase()+"\".";
			}

			// Set<?> or other parameterized collection
			else if (target.getGenericType() instanceof ParameterizedType) {
				ParameterizedType targSet = (ParameterizedType)target.getGenericType();
				Type innerType = targSet.getActualTypeArguments()[0];

				// not a Set, somehow, and that should be the only collection we're using in Conf.java
				if (targSet.getRawType() != Set.class) {
					sendMessage("\""+fieldName+"\" is not a data collection type which can be modified with this command.");
					return;
				}

				// Set<Material>
				else if (innerType == Material.class) {
					Material newMat = null;
					try {
						newMat = Material.valueOf(value.toUpperCase());
					}
					catch (IllegalArgumentException ex) {
					}
					if (newMat == null) {
						sendMessage("Cannot change \""+fieldName+"\" set: \""+value.toUpperCase()+"\" is not a valid material.");
						return;
					}

					@SuppressWarnings("unchecked")
					Set<Material> matSet = (Set<Material>)target.get(null);

					// Material already present, so remove it
					if (matSet.contains(newMat)) {
						matSet.remove(newMat);
						target.set(null, matSet);
						success = "\""+fieldName+"\" set: Material \""+value.toUpperCase()+"\" removed.";
					}
					// Material not present yet, add it
					else {
						matSet.add(newMat);
						target.set(null, matSet);
						success = "\""+fieldName+"\" set: Material \""+value.toUpperCase()+"\" added.";
					}
				}

				// Set<String>
				else if (innerType == String.class) {
					@SuppressWarnings("unchecked")
					Set<String> stringSet = (Set<String>)target.get(null);

					// String already present, so remove it
					if (stringSet.contains(value)) {
						stringSet.remove(value);
						target.set(null, stringSet);
						success = "\""+fieldName+"\" set: \""+value+"\" removed.";
					}
					// String not present yet, add it
					else {
						stringSet.add(value);
						target.set(null, stringSet);
						success = "\""+fieldName+"\" set: \""+value+"\" added.";
					}
				}

				// Set of unknown type
				else {
					sendMessage("\""+fieldName+"\" is not a data type set which can be modified with this command.");
					return;
				}
			}

			// unknown type
			else {
				sendMessage("\""+fieldName+"\" is not a data type which can be modified with this command.");
				return;
			}
		}
		catch (NoSuchFieldException ex) {
			sendMessage("Configuration setting \""+fieldName+"\" couldn't be matched, though it should be... please report this error.");
			return;
		}
		catch (IllegalAccessException ex) {
			sendMessage("Error setting configuration setting \""+fieldName+"\" to \""+value+"\".");
			return;
		}

		if (!success.isEmpty()) {
			sendMessage(success);
			if (sender instanceof Player) {
				Factions.log(success + " Command was run by "+player.getName()+".");
			}
		}
		// save change to disk
		Conf.save();

		// in case some Spout related setting was changed
		SpoutFeatures.updateAppearances();
	}
	
}
