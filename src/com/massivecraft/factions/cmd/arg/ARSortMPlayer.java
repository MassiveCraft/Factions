package com.massivecraft.factions.cmd.arg;

import java.util.Collection;
import java.util.Comparator;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.PlayerInactivityComparator;
import com.massivecraft.factions.PlayerPowerComparator;
import com.massivecraft.factions.PlayerRoleComparator;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.cmd.arg.ARAbstractSelect;
import com.massivecraft.massivecore.util.MUtil;

public class ARSortMPlayer extends ARAbstractSelect<Comparator<MPlayer>>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ARSortMPlayer i = new ARSortMPlayer();
	public static ARSortMPlayer get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String typename()
	{
		return "player sorter";
	}

	@Override
	public Comparator<MPlayer> select(String sortedBy, CommandSender sender)
	{
		sortedBy = sortedBy.toLowerCase();
		
		if (sortedBy.startsWith("r"))
		{
			// Sort by rank
			return PlayerRoleComparator.get();
		}
		else if (sortedBy.startsWith("p"))
		{
			// Sort by power
			return PlayerPowerComparator.get();
		}
		else if (sortedBy.startsWith("t"))
		{
			// Sort by time
			return PlayerInactivityComparator.get();
		}
		
		return null;
	}

	@Override
	public Collection<String> altNames(CommandSender sender)
	{
		return MUtil.list("rank", "power", "time");
	}
	
}
