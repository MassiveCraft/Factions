package com.massivecraft.factions;


public interface EconomyParticipator extends RelationParticipator
{
	public String getAccountId();
	public boolean msg(String msg, Object... args);
}