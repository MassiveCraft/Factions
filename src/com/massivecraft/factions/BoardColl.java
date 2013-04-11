package com.massivecraft.factions;

import com.massivecraft.mcore.store.Coll;
import com.massivecraft.mcore.store.MStore;

public class BoardColl extends Coll<Board, String>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static BoardColl i = new BoardColl();
	public static BoardColl get() { return i; }
	private BoardColl()
	{
		super(MStore.getDb(ConfServer.dburi), Factions.get(), "ai", Const.COLLECTION_BASENAME_BOARD, Board.class, String.class, true);
	}
	
}
