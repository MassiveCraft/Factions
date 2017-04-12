package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.comparator.ComparatorMPlayerInactivity;
import com.massivecraft.factions.comparator.ComparatorMPlayerPower;
import com.massivecraft.factions.comparator.ComparatorMPlayerRole;
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
			ComparatorMPlayerRole.get(),
			ComparatorMPlayerPower.get(),
			ComparatorMPlayerInactivity.get()
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
