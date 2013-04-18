package com.massivecraft.factions.task;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.massivecraft.factions.Factions;
import com.massivecraft.mcore.ps.PS;


/*
 * reference diagram, task should move in this pattern out from chunk 0 in the center.
 *  8 [>][>][>][>][>] etc.
 * [^][6][>][>][>][>][>][6]
 * [^][^][4][>][>][>][4][v]
 * [^][^][^][2][>][2][v][v]
 * [^][^][^][^][0][v][v][v]
 * [^][^][^][1][1][v][v][v]
 * [^][^][3][<][<][3][v][v]
 * [^][5][<][<][<][<][5][v]
 * [7][<][<][<][<][<][<][7]
 */

public abstract class SpiralTask implements Runnable
{
	// general task-related reference data
	private transient World world = null;
	private transient boolean readyToGo = false;
	private transient int taskID = -1;
	private transient int limit = 0;

	// values for the spiral pattern routine
	private transient int x = 0;
	private transient int z = 0;
	private transient boolean isZLeg = false;
	private transient boolean isNeg = false;
	private transient int length = -1;
	private transient int current = 0;

	// @SuppressWarnings("LeakingThisInConstructor") This actually triggers a warning in Eclipse xD Could we find another way to suppress the error please? :)
	public SpiralTask(PS chunk, int radius)
	{
		chunk = chunk.getChunk(true);
		
		// limit is determined based on spiral leg length for given radius; see insideRadius()
		this.limit = (radius - 1) * 2;

		this.world = Bukkit.getWorld(chunk.getWorld());
		if (this.world == null)
		{
			Factions.get().log(Level.WARNING, "[SpiralTask] A valid world must be specified!");
			this.stop();
			return;
		}

		this.x = (int)chunk.getChunkX();
		this.z = (int)chunk.getChunkZ();

		this.readyToGo = true;

		// get this party started
		this.setTaskID(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Factions.get(), this, 2, 2));
	}

/*
 * This is where the necessary work is done; you'll need to override this method with whatever you want
 * done at each chunk in the spiral pattern.
 * Return false if the entire task needs to be aborted, otherwise return true to continue.
 */
	public abstract boolean work();

/*
 * Returns a PS pointing at the current chunk X and Z values.
 */
	public final PS currentChunk()
	{
		return PS.valueOf(this.world.getName(), null, null, null, null, null, null, this.x, this.z, null, null, null, null, null);
	}
/*
 * Returns a Location pointing at the current chunk X and Z values.
 * note that the Location is at the corner of the chunk, not the center.
 */
	public final Location currentLocation()
	{
		
		return new Location(world, this.x * 16, 65.0, this.z * 16);
	}
/*
 * Returns current chunk X and Z values.
 */
	public final int getX()
	{
		return x;
	}
	public final int getZ()
	{
		return z;
	}



/*
 * Below are the guts of the class, which you normally wouldn't need to mess with.
 */

	public final void setTaskID(int ID)
	{	
		if (ID == -1)
			this.stop();
		taskID = ID;
	}

	public final void run()
	{
		if (!this.valid() || !readyToGo) return;

		// this is set so it only does one iteration at a time, no matter how frequently the timer fires
		readyToGo = false;

		// make sure we're still inside the specified radius
		if ( ! this.insideRadius()) return;

		// track this to keep one iteration from dragging on too long and possibly choking the system
		long loopStartTime = now();

		// keep going until the task has been running for 20ms or more, then stop to take a breather
		while (now() < loopStartTime + 20)
		{
			// run the primary task on the current X/Z coordinates
			if ( ! this.work())
			{
				this.finish();
				return;
			}

			// move on to next chunk in spiral
			if ( ! this.moveToNext())
				return;
		}

		// ready for the next iteration to run
		readyToGo = true;
	}

	// step through chunks in spiral pattern from center; returns false if we're done, otherwise returns true
	public final boolean moveToNext()
	{
		if ( ! this.valid()) return false;

		// make sure we don't need to turn down the next leg of the spiral
		if (current < length)
		{
			current++;

			// if we're outside the radius, we're done
			if ( ! this.insideRadius()) return false;
		}
		else
		{	// one leg/side of the spiral down...
			current = 0;
			isZLeg ^= true;
			// every second leg (between X and Z legs, negative or positive), length increases
			if (isZLeg)
			{
				isNeg ^= true;
				length++;
			}
		}

		// move one chunk further in the appropriate direction
		if (isZLeg)
			z += (isNeg) ? -1 : 1;
		else
			x += (isNeg) ? -1 : 1;

		return true;
	}

	public final boolean insideRadius()
	{
		boolean inside = current < limit;
		if (!inside)
			this.finish();
		return inside;
	}

	// for successful completion
	public void finish()
	{
//		P.p.log("SpiralTask successfully completed!");
		this.stop();
	}

	// we're done, whether finished or cancelled
	public final void stop()
	{
		if (!this.valid()) return;

		readyToGo = false;
		Bukkit.getServer().getScheduler().cancelTask(taskID);
		taskID = -1;
	}

	// is this task still valid/workable?
	public final boolean valid()
	{
		return taskID != -1;
	}

	private static long now()
	{
		return System.currentTimeMillis();
	}
}
