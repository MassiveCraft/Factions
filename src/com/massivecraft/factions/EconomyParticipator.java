package com.massivecraft.factions;

public interface EconomyParticipator extends RelationParticipator
{
	boolean msg(String msg, Object... args);
}
