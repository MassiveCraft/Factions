package com.massivecraft.factions;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.bukkit.ChatColor;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.persist.EntityCollection;

public class Factions extends EntityCollection<Faction>
{
	public static Factions i = new Factions();
	
	P p = P.p;
	
	private Factions()
	{
		super
		(
			Faction.class,
			new CopyOnWriteArrayList<Faction>(),
			new ConcurrentHashMap<String, Faction>(),
			new File(P.p.getDataFolder(), "factions.json"),
			P.p.gson
		);
	}
	
	@Override
	public Type getMapType()
	{
		return new TypeToken<Map<String, Faction>>(){}.getType();
	}
	
	@Override
	public boolean loadFromDisc()
	{
		if ( ! super.loadFromDisc()) return false;
		
		// Make sure the default neutral faction exists
		if ( ! this.exists("0"))
		{
			Faction faction = this.create("0");
			faction.setTag(ChatColor.DARK_GREEN+"Wilderness");
			faction.setDescription("");
		}
		
		// Make sure the safe zone faction exists
		if ( ! this.exists("-1"))
		{
			Faction faction = this.create("-1");
			faction.setTag(ChatColor.GOLD+"Safe Zone");
			faction.setDescription("Free from PVP and monsters");
		}
		
		// Make sure the war zone faction exists
		if ( ! this.exists("-2"))
		{
			Faction faction = this.create("-2");
			faction.setTag(ChatColor.DARK_RED+"War Zone");
			faction.setDescription("Not the safest place to be");
		}
		
		return true;
	}
	
	
	//----------------------------------------------//
	// GET
	//----------------------------------------------//
	
	@Override
	public Faction get(String id)
	{
		if ( ! this.exists(id))
		{
			p.log(Level.WARNING, "Non existing factionId "+id+" requested! Issuing cleaning!");
			Board.clean();
			FPlayers.i.clean();
		}
		
		return super.get(id);
	}
	
	public Faction getNone()
	{
		return this.get("0");
	}
	
	public Faction getSafeZone()
	{
		return this.get("-1");
	}
	
	public Faction getWarZone()
	{
		return this.get("-2");
	}
	
	
	//----------------------------------------------//
	// Faction tag
	//----------------------------------------------//
	
	public static ArrayList<String> validateTag(String str)
	{
		ArrayList<String> errors = new ArrayList<String>();
		
		if(MiscUtil.getComparisonString(str).length() < Conf.factionTagLengthMin)
		{
			errors.add(P.p.txt.parse("<i>The faction tag can't be shorter than <h>%s<i> chars.", Conf.factionTagLengthMin));
		}
		
		if(str.length() > Conf.factionTagLengthMax)
		{
			errors.add(P.p.txt.parse("<i>The faction tag can't be longer than <h>%s<i> chars.", Conf.factionTagLengthMax));
		}
		
		for (char c : str.toCharArray())
		{
			if ( ! MiscUtil.substanceChars.contains(String.valueOf(c)))
			{
				errors.add(P.p.txt.parse("<i>Faction tag must be alphanumeric. \"<h>%s<i>\" is not allowed.", c));
			}
		}
		
		return errors;
	}
	
	public Faction findByTag(String str)
	{
		String compStr = MiscUtil.getComparisonString(str);
		for (Faction faction : this.get())
		{
			if (faction.getComparisonTag().equals(compStr))
			{
				return faction;
			}
		}
		return null;
	}
	
	public boolean isTagTaken(String str)
	{
		return this.findByTag(str) != null;
	}

}
