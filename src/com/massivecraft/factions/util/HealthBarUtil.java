package com.massivecraft.factions.util;

import java.util.Map.Entry;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.mcore.util.Txt;

public class HealthBarUtil
{   
    public static String getHealthbar(double healthQuota, int barLength)
    {
    	// Ensure between 0 and 1;
    	healthQuota = fixQuota(healthQuota);
    	
    	// What color is the health bar?
    	String color = getColorFromHealthQuota(healthQuota);
    	
    	// how much solid should there be?
    	int solidCount = (int) Math.ceil(barLength * healthQuota);
    	
    	// The rest is empty
    	int emptyCount = (int) ((barLength - solidCount) / ConfServer.spoutHealthBarSolidsPerEmpty);
    	
    	// Create the non-parsed bar
    	String ret = ConfServer.spoutHealthBarLeft + Txt.repeat(ConfServer.spoutHealthBarSolid, solidCount) + ConfServer.spoutHealthBarBetween + Txt.repeat(ConfServer.spoutHealthBarEmpty, emptyCount) + ConfServer.spoutHealthBarRight;
    	
    	// Replace color tag
    	ret = ret.replace("{c}", color);
    			
    	// Parse amp color codes
    	ret = Txt.parse(ret);
    	
    	return ret;
    }
    
    public static String getHealthbar(double healthQuota)
    {
    	return getHealthbar(healthQuota, ConfServer.spoutHealthBarWidth);
    }
    
    public static double fixQuota(double healthQuota)
    {
    	if (healthQuota > 1)
    	{
    		return 1d;
    	}
    	else if (healthQuota < 0)
    	{
    		return 0d;
    	}
    	return healthQuota;
    }
    
    public static String getColorFromHealthQuota(double healthQuota)
    {
        Double currentRoof = null;
        String ret = null;
        for (Entry<Double, String> entry : ConfServer.spoutHealthBarColorUnderQuota.entrySet())
        {
        	double roof = entry.getKey();
        	String color = entry.getValue();
        	if (healthQuota <= roof && (currentRoof == null || roof <= currentRoof))
        	{
        		currentRoof = roof;
        		ret = color;
        	}
        }
        return ret;
    }
}