package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.Selector;
import com.massivecraft.factions.SelectorType;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.command.type.Type;
import com.massivecraft.massivecore.command.type.TypeAbstract;
import com.massivecraft.massivecore.mson.Mson;
import org.bukkit.command.CommandSender;

import java.util.Collection;

public class TypeSelector extends TypeAbstract<Selector>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private Type<MPlayer> typeMPlayer = TypeMPlayer.get();
	private TypeRank typeRank = TypeRank.get();
	private TypeRel typeRel = TypeRel.get();
	private TypeFaction typeFaction = TypeFaction.get();
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeSelector i = new TypeSelector();
	public static TypeSelector get() { return i; }
	private TypeSelector()
	{
		super(Selector.class);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Selector read(String arg, CommandSender sender) throws MassiveException
	{
		if (arg == null) throw new MassiveException().setMsg("Selector can't be null.");
		if (arg.length() < 2) throw new MassiveException().setMsg("Selector must be longer than two characters.");
		
		// Get Prefix
		SelectorType prefix = SelectorType.getFromPrefix(arg);
		return prefix != null ? this.readPrefixed(arg, sender, prefix) : this.readPrioritized(arg, sender);
	}
	
	private Selector readPrefixed(String arg, CommandSender sender, SelectorType prefix) throws MassiveException
	{
		// Cut off prefix length
		int length = prefix.getPrefix().length();
		arg = arg.substring(length);
		
		// Use correct type to read the selector
		Type<Selector> type = this.fetchType(prefix);
		return type.read(arg, sender);
	}
	
	private Selector readPrioritized(String arg, CommandSender sender) throws MassiveException
	{
		Selector ret;
		
		// Try Relation
		ret = readSafe(arg, sender, this.typeRel);
		if (ret != null) return ret;
		
		// Try Player
		// NOTE: Player before Faction, otherwise players get interpreted as a faction
		ret = readSafe(arg, sender, this.typeMPlayer);
		if (ret != null) return ret;
		
		// Try Faction
		ret = readSafe(arg, sender, this.typeFaction);
		if (ret != null) return ret;
		
		// Try Rank
		ret = readSafe(arg, sender, this.typeRank);
		if (ret != null) return ret;
		
		// Error
		throw new MassiveException().setMsg("<h>%s<b> did not match any selector.", arg);
	}
	
	@Override
	public Collection<String> getTabList(CommandSender sender, String arg)
	{
		// Create
		Collection<String> ret = new MassiveList<>();
		
		// Choose specific if possible
		SelectorType prefix = SelectorType.getFromPrefix(arg);
		if (prefix != null) return this.fetchType(prefix).getTabList(sender, arg);
		
		// Fill All
		ret.addAll(this.typeFaction.getTabList(sender, arg));
		ret.addAll(this.typeMPlayer.getTabList(sender, arg));
		// TODO: ret.addAll(this.typeRank.getTabList(sender, arg));
		ret.addAll(this.typeRel.getTabList(sender, arg));
		
		// Return
		return ret;
	}
	
	@Override
	public Mson getVisualMsonInner(Selector selector, CommandSender sender)
	{
		// Get Type
		SelectorType selectorType = selector.getType();
		Type<Selector> type = this.fetchType(selectorType);
		
		// Get Visual
		return type.getVisualMson(selector, sender);
	}
	
	// -------------------------------------------- //
	// Type Fetching
	// -------------------------------------------- //
	
	@SuppressWarnings("unchecked")
	private <E> Type<E> fetchType(SelectorType selectorType)
	{
		switch (selectorType)
		{
			case RANK:
				return (Type<E>) typeRank;
			case RELATION:
				return (Type<E>) typeRel;
			case PLAYER:
				return (Type<E>) typeMPlayer;
			case FACTION:
				return (Type<E>) typeFaction;
			default:
				throw new IllegalStateException("SelectorType " + selectorType + " was not matchable.");
		}
	}
	
	// -------------------------------------------- //
	// SAFE READING
	// -------------------------------------------- //
	
	public Selector readSafe(String arg, CommandSender sender) { return readSafe(arg, sender, this); }
	private static <T> T readSafe(String arg, CommandSender sender, Type<T> type)
	{
		try
		{
			return type.read(arg, sender);
		}
		catch (MassiveException e)
		{
			return null;
		}
	}
	
}
