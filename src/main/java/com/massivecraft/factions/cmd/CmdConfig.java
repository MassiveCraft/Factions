package com.massivecraft.factions.cmd;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;

public class CmdConfig extends FCommand
{
	private static HashMap<String, String> properFieldNames = new HashMap<String, String>();

	public CmdConfig()
	{
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
	public void perform()
	{
		// store a lookup map of lowercase field names paired with proper capitalization field names
		// that way, if the person using this command messes up the capitalization, we can fix that
		if (properFieldNames.isEmpty())
		{
			Field[] fields = Conf.class.getDeclaredFields();
			for(int i = 0; i < fields.length; i++)
			{
				properFieldNames.put(fields[i].getName().toLowerCase(), fields[i].getName());
			}
		}

		String field = this.argAsString(0).toLowerCase();
		if (field.startsWith("\"") && field.endsWith("\""))
		{
			field = field.substring(1, field.length() - 1);
		}
		String fieldName = properFieldNames.get(field);

		if (fieldName == null || fieldName.isEmpty())
		{
			msg("<b>No configuration setting \"<h>%s<b>\" exists.", field);
			return;
		}

		String success = "";

		String value = args.get(1);
		for(int i = 2; i < args.size(); i++)
		{
			value += ' ' + args.get(i);
		}

		try
		{
			Field target = Conf.class.getField(fieldName);

			// boolean
			if (target.getType() == boolean.class)
			{
				boolean targetValue = this.strAsBool(value);
				target.setBoolean(null, targetValue);
				
				if (targetValue)
				{
					success = "\""+fieldName+"\" option set to true (enabled).";
				}
				else
				{
					success = "\""+fieldName+"\" option set to false (disabled).";
				}
			}

			// int 
			else if (target.getType() == int.class)
			{
				try
				{
					int intVal = Integer.parseInt(value);
					target.setInt(null, intVal);
					success = "\""+fieldName+"\" option set to "+intVal+".";
				}
				catch(NumberFormatException ex)
				{
					sendMessage("Cannot set \""+fieldName+"\": integer (whole number) value required.");
					return;
				}
			}

			// long 
			else if (target.getType() == long.class)
			{
				try
				{
					long longVal = Long.parseLong(value);
					target.setLong(null, longVal);
					success = "\""+fieldName+"\" option set to "+longVal+".";
				}
				catch(NumberFormatException ex)
				{
					sendMessage("Cannot set \""+fieldName+"\": long integer (whole number) value required.");
					return;
				}
			}

			// double
			else if (target.getType() == double.class)
			{
				try
				{
					double doubleVal = Double.parseDouble(value);
					target.setDouble(null, doubleVal);
					success = "\""+fieldName+"\" option set to "+doubleVal+".";
				}
				catch(NumberFormatException ex)
				{
					sendMessage("Cannot set \""+fieldName+"\": double (numeric) value required.");
					return;
				}
			}

			// float
			else if (target.getType() == float.class)
			{
				try
				{
					float floatVal = Float.parseFloat(value);
					target.setFloat(null, floatVal);
					success = "\""+fieldName+"\" option set to "+floatVal+".";
				}
				catch(NumberFormatException ex)
				{
					sendMessage("Cannot set \""+fieldName+"\": float (numeric) value required.");
					return;
				}
			}

			// String
			else if (target.getType() == String.class)
			{
				target.set(null, value);
				success = "\""+fieldName+"\" option set to \""+value+"\".";
			}

			// ChatColor
			else if (target.getType() == ChatColor.class)
			{
				ChatColor newColor = null;
				try
				{
					newColor = ChatColor.valueOf(value.toUpperCase());
				}
				catch (IllegalArgumentException ex)
				{
					
				}
				if (newColor == null)
				{
					sendMessage("Cannot set \""+fieldName+"\": \""+value.toUpperCase()+"\" is not a valid color.");
					return;
				}
				target.set(null, newColor);
				success = "\""+fieldName+"\" color option set to \""+value.toUpperCase()+"\".";
			}

			// Set<?> or other parameterized collection
			else if (target.getGenericType() instanceof ParameterizedType)
			{
				ParameterizedType targSet = (ParameterizedType)target.getGenericType();
				Type innerType = targSet.getActualTypeArguments()[0];

				// not a Set, somehow, and that should be the only collection we're using in Conf.java
				if (targSet.getRawType() != Set.class)
				{
					sendMessage("\""+fieldName+"\" is not a data collection type which can be modified with this command.");
					return;
				}

				// Set<Material>
				else if (innerType == Material.class)
				{
					Material newMat = null;
					try
					{
						newMat = Material.valueOf(value.toUpperCase());
					}
					catch (IllegalArgumentException ex)
					{
						
					}
					if (newMat == null)
					{
						sendMessage("Cannot change \""+fieldName+"\" set: \""+value.toUpperCase()+"\" is not a valid material.");
						return;
					}

					@SuppressWarnings("unchecked")
					Set<Material> matSet = (Set<Material>)target.get(null);

					// Material already present, so remove it
					if (matSet.contains(newMat))
					{
						matSet.remove(newMat);
						target.set(null, matSet);
						success = "\""+fieldName+"\" set: Material \""+value.toUpperCase()+"\" removed.";
					}
					// Material not present yet, add it
					else
					{
						matSet.add(newMat);
						target.set(null, matSet);
						success = "\""+fieldName+"\" set: Material \""+value.toUpperCase()+"\" added.";
					}
				}

				// Set<String>
				else if (innerType == String.class)
				{
					@SuppressWarnings("unchecked")
					Set<String> stringSet = (Set<String>)target.get(null);

					// String already present, so remove it
					if (stringSet.contains(value))
					{
						stringSet.remove(value);
						target.set(null, stringSet);
						success = "\""+fieldName+"\" set: \""+value+"\" removed.";
					}
					// String not present yet, add it
					else 
					{
						stringSet.add(value);
						target.set(null, stringSet);
						success = "\""+fieldName+"\" set: \""+value+"\" added.";
					}
				}

				// Set of unknown type
				else
				{
					sendMessage("\""+fieldName+"\" is not a data type set which can be modified with this command.");
					return;
				}
			}

			// unknown type
			else
			{
				sendMessage("\""+fieldName+"\" is not a data type which can be modified with this command.");
				return;
			}
		}
		catch (NoSuchFieldException ex)
		{
			sendMessage("Configuration setting \""+fieldName+"\" couldn't be matched, though it should be... please report this error.");
			return;
		}
		catch (IllegalAccessException ex)
		{
			sendMessage("Error setting configuration setting \""+fieldName+"\" to \""+value+"\".");
			return;
		}

		if (!success.isEmpty())
		{
			if (sender instanceof Player)
			{
				sendMessage(success);
				P.p.log(success + " Command was run by "+fme.getName()+".");
			}
			else  // using P.p.log() instead of sendMessage if run from server console so that "[Factions v#.#.#]" is prepended in server log
				P.p.log(success);
		}
		// save change to disk
		Conf.save();

		// in case some Spout related setting was changed
		SpoutFeatures.updateAppearances();
	}
	
}
