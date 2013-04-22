package com.massivecraft.factions.entity;

import com.massivecraft.mcore.store.Entity;

public class UConf extends Entity<UConf>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static UConf get(Object worldNameExtractable)
	{
		return UConfColls.get().get2(worldNameExtractable);
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	
	
}