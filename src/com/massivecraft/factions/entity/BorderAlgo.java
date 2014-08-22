package com.massivecraft.factions.entity;

import com.massivecraft.factions.Rel;
import com.massivecraft.mcore.ps.PS;

public enum BorderAlgo {
	ORIGINAL {
		@Override
		public boolean isBorderPs(PS ps, Board board) {
			return board.isBorderPsOriginal(ps);
		}
	},
	ORTHOGANAL_THREE {
		@Override
		public boolean isBorderPs(PS ps, Board board) {
			Faction fac = board.getFactionAt(ps);
			PS[] orthoganalPSes = new PS[]{
				ps.withChunkX(ps.getChunkX() - 1),
				ps.withChunkX(ps.getChunkX() + 1),
				ps.withChunkZ(ps.getChunkZ() - 1),
				ps.withChunkZ(ps.getChunkZ() + 1)
			};
			int orthoganalCount = 0;
			for (PS psI : orthoganalPSes) {
				if (Rel.MEMBER == board.getFactionAt(psI).getRelationTo(fac)) {
					orthoganalCount ++;
				}
			}
			return orthoganalCount < 3;
		}
	},
	SURROUND_FIVE {
		@Override
		public boolean isBorderPs(PS ps, Board board) {
			Faction fac = board.getFactionAt(ps);
			PS psXSubOne = ps.withChunkX(ps.getChunkX() - 1);
			PS psXAddOne = ps.withChunkX(ps.getChunkX() + 1);
			PS[] surroundingPSes = new PS[]{
				ps       .withChunkZ(ps.getChunkZ() - 1),
				ps       .withChunkZ(ps.getChunkZ() + 1),
				psXSubOne                               ,
				psXSubOne.withChunkZ(ps.getChunkZ() - 1),
				psXSubOne.withChunkZ(ps.getChunkZ() + 1),
				psXAddOne                               ,
				psXAddOne.withChunkZ(ps.getChunkZ() - 1),
				psXAddOne.withChunkZ(ps.getChunkZ() + 1)
			};
			int surroundCount = 0;
			for (PS psI : surroundingPSes) {
				if (Rel.MEMBER == board.getFactionAt(psI).getRelationTo(fac)) {
					surroundCount ++;
				}
			}
			return surroundCount < 5;
		}
	};
	abstract public boolean isBorderPs(PS ps, Board board);
}