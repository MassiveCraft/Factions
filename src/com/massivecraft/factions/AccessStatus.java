package com.massivecraft.factions;


import com.massivecraft.massivecore.Colorized;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.ChatColor;

public enum AccessStatus implements Colorized
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	STANDARD(ChatColor.YELLOW, null),
	ELEVATED(ChatColor.GREEN, true),
	DECREASED(ChatColor.RED, false),
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final ChatColor color;
	@Override public ChatColor getColor() { return this.color; }
	
	private final Boolean access;
	public Boolean hasAccess() { return access; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	AccessStatus(ChatColor color, Boolean access)
	{
		this.color = color;
		this.access = access;
	}
	
	// -------------------------------------------- //
	// MESSAGE
	// -------------------------------------------- //
	
	public String getStatusMessage()
	{
		ChatColor color = this.getColor();
		String status = Txt.getNicedEnum(this).toLowerCase();
		return Txt.parse("%sYou have %s access to this area.", color.toString(), status);
	}
	
}
