package com.massivecraft.factions.cmd;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FFlag;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;

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
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
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

				// Set<?>
				if (targSet.getRawType() == Set.class)
				{
					// Set<Material>
					if (innerType == Material.class)
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
							success = "\""+fieldName+"\" set: \""+value+"\" removed.";
						}
						// String not present yet, add it
						else 
						{
							stringSet.add(value);
							success = "\""+fieldName+"\" set: \""+value+"\" added.";
						}
						target.set(null, stringSet);
					}

					// Set of unknown type
					else
					{
						sendMessage("\""+fieldName+"\" is not a data type set which can be modified with this command.");
						return;
					}
				}

				// Map<?, ?>
				else if (targSet.getRawType() == Map.class)
				{
					if (args.size() < 3)
					{
						sendMessage("Cannot change \""+fieldName+"\" map: not enough arguments passed.");
						return;
					}
					Type innerType2 = targSet.getActualTypeArguments()[1];
					String value1 = args.get(1);
					String value2 = value.substring(value1.length() + 1);

					// Map<FFlag, Boolean>
					if (innerType == FFlag.class && innerType2 == Boolean.class)
					{
						value1 = value1.toUpperCase();
						FFlag newFlag = null;
						try
						{
							newFlag = FFlag.valueOf(value1);
						}
						catch (IllegalArgumentException ex) {}

						if (newFlag == null)
						{
							sendMessage("Cannot change \""+fieldName+"\" map: \""+value1+"\" is not a valid FFlag.");
							return;
						}

						@SuppressWarnings("unchecked")
						Map<FFlag, Boolean> map = (Map<FFlag, Boolean>)target.get(null);

						Boolean targetValue = this.strAsBool(value2);

						map.put(newFlag, targetValue);
						target.set(null, map);

						if (targetValue)
							success = "\""+fieldName+"\" flag \""+value1+"\" set to true (enabled).";
						else
							success = "\""+fieldName+"\" flag \""+value1+"\" set to false (disabled).";
					}

					// Map<FPerm, Set<Rel>>
					else if (innerType == FPerm.class && innerType2 instanceof ParameterizedType)
					{
						if (((ParameterizedType)innerType2).getRawType() != Set.class)
						{
							sendMessage("\""+fieldName+"\" is not a data type map which can be modified with this command, due to the inner collection type.");
							return;
						}

						value1 = value1.toUpperCase();
						value2 = value2.toUpperCase();

						FPerm newPerm = null;
						Rel newRel = null;
						try
						{
							newPerm = FPerm.valueOf(value1);
							newRel = Rel.valueOf(value2);
						}
						catch (IllegalArgumentException ex) {}

						if (newPerm == null)
						{
							sendMessage("Cannot change \""+fieldName+"\" map: \""+value1+"\" is not a valid FPerm.");
							return;
						}
						if (newRel == null)
						{
							sendMessage("Cannot change \""+fieldName+"\" map: \""+value2+"\" is not a valid Rel.");
							return;
						}

						@SuppressWarnings("unchecked")
						Map<FPerm, Set<Rel>> map = (Map<FPerm, Set<Rel>>)target.get(null);

						Set<Rel> relSet = map.get(newPerm);
						if (relSet == null)
							relSet = new HashSet<Rel>();

						// Rel already present, so remove it
						if (relSet.contains(newRel))
						{
							relSet.remove(newRel);
							success = "\""+fieldName+"\" permission \""+value1+"\": relation \""+value2+"\" removed.";
						}
						// Rel not present yet, add it
						else 
						{
							relSet.add(newRel);
							success = "\""+fieldName+"\" permission \""+value1+"\": relation \""+value2+"\" added.";
						}

						map.put(newPerm, relSet);
						target.set(null, map);
					}

					// Map of unknown type
					else
					{
						sendMessage("\""+fieldName+"\" is not a data type map which can be modified with this command.");
						return;
					}
				}

				// not a Set or Map?
				else
				{
					sendMessage("\""+fieldName+"\" is not a data collection type which can be modified with this command.");
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
		SpoutFeatures.updateTitle(null, null);
		//SpoutFeatures.updateCape(null);
	}
	
}
