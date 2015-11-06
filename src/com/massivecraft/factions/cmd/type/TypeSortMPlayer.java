package com.massivecraft.factions.cmd.type;

import java.util.Collection;
import java.util.Comparator;

import com.massivecraft.factions.PlayerInactivityComparator;
import com.massivecraft.factions.PlayerPowerComparator;
import com.massivecraft.factions.PlayerRoleComparator;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.command.type.TypeAbstractChoice;

public class TypeSortMPlayer extends TypeAbstractChoice<Comparator<MPlayer>>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeSortMPlayer i = new TypeSortMPlayer();
	public static TypeSortMPlayer get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getTypeName()
	{
		return "player sorter";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Comparator<MPlayer>> getAll()
	{
		return new MassiveList<Comparator<MPlayer>>(
			PlayerRoleComparator.get(),
			PlayerPowerComparator.get(),
			PlayerInactivityComparator.get()
		);
	}

	
	
}
