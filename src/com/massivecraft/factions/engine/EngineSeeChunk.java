package com.massivecraft.factions.engine;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.event.EventMassiveCorePlayerLeave;
import com.massivecraft.massivecore.particleeffect.ParticleEffect;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.PeriodUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class EngineSeeChunk extends Engine
{	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineSeeChunk i = new EngineSeeChunk();
	public static EngineSeeChunk get() { return i; }
	public EngineSeeChunk()
	{
		this.setPeriod(1L);
	}
	
	// -------------------------------------------- //
	// LEAVE AND WORLD CHANGE REMOVAL
	// -------------------------------------------- //

	public static void leaveAndWorldChangeRemoval(Player player)
	{
		if (MUtil.isntPlayer(player)) return;
		
		final MPlayer mplayer = MPlayer.get(player);
		mplayer.setSeeingChunk(false);
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void leaveAndWorldChangeRemoval(EventMassiveCorePlayerLeave event)
	{
		leaveAndWorldChangeRemoval(event.getPlayer());
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void leaveAndWorldChangeRemoval(PlayerChangedWorldEvent event)
	{
		leaveAndWorldChangeRemoval(event.getPlayer());
	}
	
	// -------------------------------------------- //
	// MODULO REPEAT TASK
	// -------------------------------------------- //
	
	@Override
	public void run()
	{
		// Do we have a new period?
		final long now = System.currentTimeMillis();
		final long length = MConf.get().seeChunkPeriodMillis;
		if ( ! PeriodUtil.isNewPeriod(this, length, now)) return;
		
		// Get the period number
		final long period = PeriodUtil.getPeriod(length, now);
		
		// Calculate the "step" from the period number
		final int steps = MConf.get().seeChunkSteps; // Example: 4
		final int step = (int) (period % steps); // Example: 0, 1, 2, 3
		
		// Load other related config options
		final float offsetX = 0.0f;
		final float offsetY = MConf.get().seeChunkParticleOffsetY;
		final float offsetZ = 0.0f;
		final float speed = 0;
		final int amount = MConf.get().seeChunkParticleAmount;
		
		// For each player
		for (Player player : MUtil.getOnlinePlayers())
		{
			// Hide for dead players since the death screen looks better without.
			if (player.isDead()) continue;
			
			// The player must obviously have the feature activated.
			MPlayer mplayer = MPlayer.get(player);
			if ( ! mplayer.isSeeingChunk()) continue;
			
			// Calculate locations and play the effect there.
			List<Location> locations = getLocations(player, steps, step);
			for (Location location : locations)
			{
				ParticleEffect.EXPLOSION_NORMAL.display(location, offsetX, offsetY, offsetZ, speed, amount, player);
			}
		}
	}
	
	public static List<Location> getLocations(Player player, int steps, int step)
	{
		// Clean Args
		if (player == null) throw new NullPointerException("player");
		if (steps < 1) throw new InvalidParameterException("steps must be larger than 0");
		if (step < 0) throw new InvalidParameterException("step must at least be 0");
		if (step >= steps) throw new InvalidParameterException("step must be less than steps");
		
		// Create Ret
		List<Location> ret = new ArrayList<>();
		
		final Location location = player.getLocation();
		final World world = location.getWorld();
		PS chunk = PS.valueOf(location).getChunk(true);
		
		final int xmin = chunk.getChunkX() * 16;
		final int xmax = xmin + 15;
		final double y = location.getBlockY() + MConf.get().seeChunkParticleDeltaY;
		final int zmin = chunk.getChunkZ() * 16;
		final int zmax = zmin + 15;
		
		int keepEvery = MConf.get().seeChunkKeepEvery;
		if (keepEvery <= 0) keepEvery = Integer.MAX_VALUE;
		
		int skipEvery = MConf.get().seeChunkSkipEvery;
		if (skipEvery <= 0) skipEvery = Integer.MAX_VALUE;
		
		int x = xmin;
		int z = zmin;
		int i = 0;
		
		// Add #1
		while (x + 1 <= xmax)
		{
			x++;
			i++;
			if (i % steps == step && (i % keepEvery == 0 && i % skipEvery != 0)) ret.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
		}
		
		// Add #2
		while (z + 1 <= zmax)
		{
			z++;
			i++;
			if (i % steps == step && (i % keepEvery == 0 && i % skipEvery != 0)) ret.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
		}
		
		// Add #3
		while (x - 1 >= xmin)
		{
			x--;
			i++;
			if (i % steps == step && (i % keepEvery == 0 && i % skipEvery != 0)) ret.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
		}
		
		// Add #4
		while (z - 1 >= zmin)
		{
			z--;
			i++;
			if (i % steps == step && (i % keepEvery == 0 && i % skipEvery != 0)) ret.add(new Location(world, x + 0.5, y + 0.5, z + 0.5));
		}
		
		// Return Ret
		return ret;
	}
}
