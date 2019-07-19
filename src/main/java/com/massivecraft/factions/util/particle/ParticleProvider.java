package com.massivecraft.factions.util.particle;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ParticleProvider<Effect> {

    String name();

    void spawn(Effect effect, Location location, int count);

    void playerSpawn(Player player, Effect effect, Location location, int count);

    void spawn(Effect effect, Location location, int count, double speed, double offsetX, double offsetY, double offsetZ);

    void playerSpawn(Player player, Effect effect, Location location, int count, double speed, double offsetX, double offsetY, double offsetZ);

    void spawn(Effect effect, Location location, ParticleColor color);

    void playerSpawn(Player player, Effect effect, Location location, ParticleColor color);

    Effect effectFromString(String string);

    String effectName(Effect effect);

}
