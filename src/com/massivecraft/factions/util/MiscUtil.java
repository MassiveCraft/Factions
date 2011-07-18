package com.massivecraft.factions.util;

public class MiscUtil {
	
	// Inclusive range
	public static long[] range(long start, long end) {
		long[] values = new long[(int) Math.abs(end - start) + 1];
		
		if (end < start) {
			long oldstart = start;
			start = end;
			end = oldstart;
		}
	
		
		
		for (long i = start; i <= end; i++) {
			values[(int) (i - start)] = i;
		}
		
		return values;
	}
	
}

