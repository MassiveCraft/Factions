package com.massivecraft.factions.util.particle;

import com.darkblade12.particleeffect.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PacketParticleProvider implements ParticleProvider<ParticleEffect> {

    @Override
    public String name() {
        return "PACKETS";
    }


    @Override
    public void spawn(ParticleEffect particleEffect, Location location, int count) {
        particleEffect.display(0, 0, 0, 1, count, location, new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    @Override
    public void playerSpawn(Player player, ParticleEffect particleEffect, Location location, int count) {
        particleEffect.display(0, 0, 0, 1, count, location, player);
    }

    @Override
    public void spawn(ParticleEffect particleEffect, Location location, int count, double speed, double offsetX, double offsetY, double offsetZ) {
        particleEffect.display((float) offsetX, (float) offsetY, (float) offsetZ, (float) speed, count, location, new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    @Override
    public void playerSpawn(Player player, ParticleEffect particleEffect, Location location, int count, double speed, double offsetX, double offsetY, double offsetZ) {
        particleEffect.display((float) offsetX, (float) offsetY, (float) offsetZ, (float) speed, count, location, player);
    }

    @Override
    public void spawn(ParticleEffect particleEffect, Location location, ParticleColor color) {
        spawn(particleEffect, location, 0, 1, color.getOffsetX(), color.getOffsetY(), color.getOffsetZ());
    }

    @Override
    public void playerSpawn(Player player, ParticleEffect particleEffect, Location location, ParticleColor color) {
        playerSpawn(player, particleEffect, location, 0, 1, color.getOffsetX(), color.getOffsetY(), color.getOffsetZ());
    }

    @Override
    public ParticleEffect effectFromString(String string) {
        for (ParticleEffect effect : ParticleEffect.values()) {
            if (effect.name().equalsIgnoreCase(string)) {
                return effect;
            }
        }
        // If none of the Enum name matches fallback to the other names
        return ParticleEffect.fromName(string);
    }

    @Override
    public String effectName(ParticleEffect particleEffect) {
        return particleEffect.name();
    }

}
