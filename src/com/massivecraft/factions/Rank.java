package com.massivecraft.factions;

import java.util.Objects;
import java.util.Set;

import com.massivecraft.massivecore.Named;
import com.massivecraft.massivecore.Prioritized;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.store.MStore;
import com.massivecraft.massivecore.util.MUtil;

public class Rank implements Named, Comparable<Rank>, Prioritized, PermissionIdentifiable
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	private static Rank LEADER = new Rank(1, "Leader");
	private static Rank OFFICER = new Rank(2, "Officer");
	private static Rank MEMBER = new Rank(3, "Member");
	private static Rank RECRUIT = new Rank(4, "Recruit");
	public static Rank DEFAULT = new Rank(null, null);
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private transient String id;
	public String getId() { return this.id; }
	public void setId(String id) { this.id = id; }
	
	private transient Integer order;
	public Integer getOrder() { return this.order; }
	@Override public int getPriority() { return this.getOrder(); }
	public void setOrder(Integer order) { this.order = order; }
	
	private String name;
	@Override public String getName() { return this.name; }
	public void setName(String name) { this.name = name; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public Rank(Integer order, String name)
	{
		this.id = MStore.createId();
		this.order = order;
		this.name = name;
	}
	
	// -------------------------------------------- //
	// CONVENIENCE
	// -------------------------------------------- //
	
	public Rank copy()
	{
		return new Rank(order, name);
	}
	
	public boolean isLeader()
	{
		return this.getOrder() == 1;
	}
	
	public boolean isHigherThan(Rank rank)
	{
		return this.getOrder() < rank.getOrder();
	}
	
	public boolean isLessThan(Rank rank)
	{
		return this.getOrder() > rank.getOrder();
	}
	
	@Override
	public String getPermissibleId()
	{
		return "rank" + this.getOrder();
	}
	
	// -------------------------------------------- //
	// CREATE DEFAULT
	// -------------------------------------------- //
	
	public static Set<Rank> createDefaultRanks()
	{
		// Create
		Set<Rank> ret = new MassiveSet<>(4);
		
		// Fill
		ret.add(LEADER.copy());
		ret.add(OFFICER.copy());
		ret.add(MEMBER.copy());
		ret.add(RECRUIT.copy());
		
		// Return
		return ret;
	}
	
	// -------------------------------------------- //
	// VISUALIZE
	// -------------------------------------------- //
	
	public Mson visualize()
	{
		return Mson.mson(
			String.valueOf(this.getOrder()),
			Mson.DOT,
			Mson.SPACE,
			this.getName()
		);
	}
	
	// -------------------------------------------- //
	// EQUALS & HASHCODE
	// -------------------------------------------- //
	
	@Override
	public boolean equals(Object object)
	{
		if (this == object) return true;
		if (!(object instanceof Rank)) return false;
		Rank that = (Rank) object;
		
		return MUtil.equals(
			this.order, that.order,
			this.name, that.name
		);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(order, name);
	}
	
	// -------------------------------------------- //
	// COMPARE TO
	// -------------------------------------------- //
	
	@Override
	public int compareTo(Rank that)
	{
		if (that == null) return +1;
		
		int orderOne = this.getOrder();
		int orderTwo = that.getOrder();
		
		if (orderOne == orderTwo) return 0;
		if (orderOne > orderTwo) return +1;
		if (orderOne < orderTwo) return -1;
		
		return 0;
	}
	
}
