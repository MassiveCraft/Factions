package com.bukkit.mcteam.util;

import java.util.*;

import org.bukkit.entity.Player;

/**
 * The purpose of this tool is twofold:
 * 1: Avoid client crashes due to bad color formating.
 * 2: Make color continue on word wrapping
 * 
 * In minecraft the degree sign is used as a prefix to another char to create a color.
 * For example the code for white is "\u00A7f". 
 * The "\u00A7" is the unicode notation for the degree sign and the "f" means white.
 * 
 * When does minecraft wrap the text? After how many chars?
 * Answer: 
 * Because the font isn't monospace this differs depending on what you write.
 * However we can fit 53 "M" without wrapping and the 54th char would then wrap (be at the beginning of the next line instead)
 * As there is no broader char than "M" we can know for sure the minimum line length is 53.
 * Note that this means the number of DISPLAYED chars per row is 53.
 * A degree sign and the char after will NOT count, as they will not be displayed as chars.
 * 
 * Good to know: Numbers have the same font width as an M.
 * 
 * When does the client crash?
 * Answer: 
 * When a row ends with a degree char and optionally another sign after.
 * Another way to say the same: When a line ends with either a broken or valid color notation.
 * AND
 * The client will ALWAYS crash if the sign after the last displayed char in a row is a degree char.
 * A goofy way to explatin it:
 * For a line with only "M" and numbers, the fiftyfourth "displayed char" musn't be a degree sign.
 * 
 * WARNING:
 * Above is a hypothesis I have created based on what my experiments have shown.
 * I am fairly sure it is correct but please help me test it further.
 */
public class ChatFixUtil {
	public final static char deg = '\u00A7';
	public final static int lineLength = 53;
	
	/**
	 * This method wraps the msg for you at row lengths of 53,
	 * avoids client crash scenarios and makes the previous color continue on
	 * the next line.
	 * 
	 * The upsides with filtering your messages through this method are: 
	 * - No client crashes.
	 * - Line wrapping with preserved color.
	 * 
	 * The downsides are:
	 * - The width of the chat window will not be used to it's fullest.
	 *   For example you can fit more that 53 commas (,) in a chatwindow row
	 *   but the line would break after 53 displayed chars.
	 * 
	 * Suggested usage:
	 * NO NEED TO USE the fix method for static help pages in your plugin.
	 * As the text is static you can make sure there is no client crash yourself
	 * and be able to use the full line length.
	 * 
	 * DO USE in cases like where you output colored messages with playernames in your
	 * plugin. As the player names have different length there is potential for client crash.
	 */
	public static ArrayList<String> fix(String msg) {
		// Make sure the end of msg is good
		msg = cleanMsgEnding(msg); 
		
		ArrayList<String> ret = new ArrayList<String>();
		int displen = 0; // The number of displayed chars in row so far.
		String row = "";
		String latestColor = null;

		for (int i = 0; i < msg.length(); i++) {
			if (displen == lineLength) {
				// it is time to start on the next row!
				ret.add(row);
				displen = 0;
				row = "";
				if (latestColor != null) {
					row += deg+latestColor;
				}
		    }
			char c = msg.charAt(i);
			
			if (c == deg) {
				latestColor = String.valueOf(msg.charAt(i+1));
				row += deg+latestColor;
				i++;
			} else {
				displen += 1;
				row += c;
			}
		}
		ret.add(row);
		return ret;
	}
	
	public static ArrayList<String> fix(List<String> messages) {
		ArrayList<String> ret = new ArrayList<String>();
		for(String message : messages) {
			ret.addAll(fix(message));
		}
		return ret;
	}
	
	
	/**
	 * Removes the ending chars as long as they are deg or deg+'anychar' or a space
	 * As I see it we would never want those chars at the end of a msg.
	 */
	protected static String cleanMsgEnding (String msg) {
		
		while (msg.length() > 0) {
			if (msg.endsWith(String.valueOf(deg)) || msg.endsWith(" ")) {
				msg = msg.substring(0, msg.length()-1);
			} else if (msg.length() >= 2 && msg.charAt(msg.length() - 2) == deg) {
				msg = msg.substring(0, msg.length()-2);
			} else {
				break;
			}
		}
		return msg;
	}
	
	/**
	 * This test util assumes line break after 53 displayed chars.
	 * The fix method above breaks like that so this method should
	 * be a valid way to test if a message row would crash a client.
	 */
	public static String thisMsgWouldCrashClient(String str) {
		// There would always be crash if we end with deg or deg+'anychar'
		if (str.length() >= 1 && str.charAt(str.length() - 1) == deg) {
			return "Crash: The str ends with deg.";
		} else if (str.length() >= 2 && str.charAt(str.length() - 2) == deg) {
			return "Crash: The str ends with deg+'anychar'.";
		}
		
		int displayedChars = 0;
		
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == deg && displayedChars == lineLength) {
				return "Crash: Deg as fiftyforth \"displayed\" char";
			} else if (c == deg) {
				i++; // this and next: they are not displayed... skip them...
			} else {
				displayedChars += 1;
			}
		}
		return "all ok";
	}
	
	//----------------------------------------------//
	// Methods for effectively sending messages
	//----------------------------------------------//
	//----------------------------------------------//
	// One player
	//----------------------------------------------//
	public static void sendMessage(Player player, String message, boolean fix) {
		if (fix) {
			List<String> messages = ChatFixUtil.fix(message);
			sendMessage(player, messages, false);
		} else {
			if (player != null) {
				player.sendMessage(message);
			}
		}
	}
	public static void sendMessage(Player player, List<String> messages, boolean fix) {
		if (fix) {
			messages = ChatFixUtil.fix(messages);
		}
		for (String message : messages) {
			sendMessage(player, message, false);
		}
	}
	public static void sendMessage(Player player, String message) {
		sendMessage(player, message, true);
	}
	public static void sendMessage(Player player, List<String> messages) {
		sendMessage(player, messages, true);
	}
	//----------------------------------------------//
	// Many Players
	//----------------------------------------------//
	public static void sendMessage(Collection<Player> players, String message, boolean fix) {
		if (fix) {
			List<String> messages = ChatFixUtil.fix(message);
			sendMessage(players, messages, false);
		} else {
			for (Player player : players) {
				sendMessage(player, message, false);
			}
		}
	}
	public static void sendMessage(Collection<Player> players, List<String> messages, boolean fix) {
		if (fix) {
			messages = ChatFixUtil.fix(messages);
		}
		
		for (String message : messages) {
			sendMessage(players, message, false);
		}
	}
	public static void sendMessage(Collection<Player> players, String message) {
		sendMessage(players, message, true);
	}
	public static void sendMessage(Collection<Player> players, List<String> messages) {
		sendMessage(players, messages, true);
	}
}


















