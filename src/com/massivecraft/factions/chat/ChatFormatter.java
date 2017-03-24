package com.massivecraft.factions.chat;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The ChatFormater is a system offered by factions for tag parsing.
 * 
 * Note that every tag and modifier id must be lowercase.
 * A tag with id "derp" is allowed but not with id "Derp". For that reason the tag {sender} will work but {Sender} wont.  
 */
public class ChatFormatter
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public final static String START = "{";
	public final static String END = "}";
	public final static String SEPARATOR = "|";
	
	public final static String ESC_START = "\\"+START;
	public final static String ESC_END = "\\"+END;
	public final static String ESC_SEPARATOR = "\\"+SEPARATOR;
	
	public final static Pattern pattern = Pattern.compile(ESC_START+"([^"+ESC_START+ESC_END+"]+)"+ESC_END);
	
	// -------------------------------------------- //
	// FORMAT
	// -------------------------------------------- //
	
	public static String format(String msg, CommandSender sender, CommandSender recipient)
	{
		// We build the return value in this string buffer
		StringBuffer ret = new StringBuffer();
		
		// A matcher to match all the tags in the msg
		Matcher matcher = pattern.matcher(msg);

		// For each tag we find
		while (matcher.find())
		{
			// The fullmatch is something like "{sender|lp|rp}"
			String fullmatch = matcher.group(0);
			
			// The submatch is something like "sender|lp|rp"
			String submatch = matcher.group(1);
			
			// The parts are something like ["sender", "lp", "rp"]
			String[] parts = submatch.split(ESC_SEPARATOR);

			// The modifier ids are something like ["lp", "rp"] and tagId something like "sender"
			List<String> modifierIds = new ArrayList<>(Arrays.asList(parts));
			String tagId = modifierIds.remove(0);
			
			// Fetch tag for the id
			ChatTag tag = ChatTag.getTag(tagId);
			
			String replacement;
			if (tag == null)
			{
				// No change if tag wasn't found
				replacement = fullmatch;
			}
			else
			{
				replacement = compute(tag, modifierIds, sender, recipient);
				if (replacement == null)
				{
					// If a tag or modifier returns null it's the same as opting out.
					replacement = fullmatch;
				}
			}
			
			matcher.appendReplacement(ret, replacement);
		}
		
		// Append the rest
		matcher.appendTail(ret);
		
		// And finally we return the string value of the buffer we built
		return ret.toString();
	}
	
	// -------------------------------------------- //
	// TAG COMPUTE
	// -------------------------------------------- //
	
	public static String compute(ChatTag tag, List<String> modifierIds, CommandSender sender, CommandSender recipient)
	{
		String ret = tag.getReplacement(sender, recipient);
		if (ret == null) return null;
		
		for (String modifierId : modifierIds)
		{
			// Find the modifier or skip
			ChatModifier modifier = ChatModifier.getModifier(modifierId);
			if (modifier == null) continue;
			
			// Modify and ignore change if null.
			// Modifier can't get or return null.
			String modified = modifier.getModified(ret, sender, recipient);
			if (modified == null) continue;
			
			ret = modified;
		}
		
		return ret;
	}
	
}
