package com.massivecraft.factions.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

//import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FPlayer;
import org.bukkit.entity.Player;

public class LandUnclaimAllEvent extends Event
{	
	private static final HandlerList handlers = new HandlerList();

	// Location is commented out because there is no clean way to hook currently.
	// faction and fplayer should be enough to filter needed information.
  // private FLocation[] location;
  private Faction faction;
  private FPlayer fplayer;

	public LandUnclaimAllEvent(Faction f, FPlayer p)
	{
		faction = f;
		fplayer = p;
	}

	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
		return handlers;
	}

/*
	public FLocation getLocation()
	{
	  return this.location;
	}
 */

	public Faction getFaction()
	{
		return faction;
	}

  public String getFactionId()
  {
    return faction.getId();
  }

  public String getFactionTag()
  {
    return faction.getTag();
  }

  public FPlayer getFPlayer()
  {
    return fplayer;
  }

  public Player getPlayer()
  {
    return fplayer.getPlayer();
  }
}
