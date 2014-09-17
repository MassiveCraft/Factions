package com.massivecraft.factions.entity;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.Colls;
import com.massivecraft.massivecore.store.Entity;
import com.massivecraft.massivecore.util.MUtil;

public abstract class XColls<C extends Coll<E>, E> extends Colls<C, E>
{
	@Override
	public C get(Object o)
	{
		if (o == null) return null;
		
		if (o instanceof Entity)
		{
			String universe = ((Entity<?>)o).getUniverse();
			if (universe == null) return null;
			return this.getForUniverse(universe);
		}
		
		if (o instanceof Coll)
		{
			String universe = ((Coll<?>)o).getUniverse();
			if (universe == null) return null;
			return this.getForUniverse(universe);
		}
		
		if ((o instanceof CommandSender) && !(o instanceof Player))
		{
			return this.getForWorld(Bukkit.getWorlds().get(0).getName());
		}
		
		String worldName = MUtil.extract(String.class, "worldName", o);
		if (worldName == null) return null;
		return this.getForWorld(worldName);
	}
}
