package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.PlayerInactivityComparator;
import com.massivecraft.factions.PlayerPowerComparator;
import com.massivecraft.factions.PlayerRoleComparator;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.command.type.TypeAbstractChoice;

import java.util.Comparator;

public class TypeSortMPlayer extends TypeAbstractChoice<Comparator<MPlayer>>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeSortMPlayer i = new TypeSortMPlayer();
	public static TypeSortMPlayer get() { return i; }
	public TypeSortMPlayer()
	{
		super(Comparator.class);
		this.setAll(
			PlayerRoleComparator.get(),
			PlayerPowerComparator.get(),
			PlayerInactivityComparator.get()
		);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getName()
	{
		return "player sorter";
	}
	
}
