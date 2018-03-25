package com.massivecraft.factions.cmd;

import com.darkblade12.particleeffect.ParticleEffect;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class CmdSeeChunk extends FCommand {

    private boolean useParticles;
    private int length;
    private ParticleEffect effect;

    public CmdSeeChunk() {
        super();
        aliases.add("seechunk");
        aliases.add("sc");

        permission = Permission.SEECHUNK.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        this.useParticles = p.getConfig().getBoolean("see-chunk.particles", true);
        this.length = p.getConfig().getInt("see-chunk.length", 10);
        String effectName = p.getConfig().getString("see-chunk.particle", "BARRIER");
        this.effect = ParticleEffect.fromName(effectName.toUpperCase());
        if (this.effect == null) {
            this.effect = ParticleEffect.BARRIER;
        }

        p.log(Level.INFO, "Using %s as the ParticleEffect for /f sc", effect.getName());
    }

    @Override
    public void perform() {
        World world = me.getWorld();
        FLocation flocation = new FLocation(me);
        int chunkX = (int) flocation.getX();
        int chunkZ = (int) flocation.getZ();

        int blockX;
        int blockZ;

        blockX = chunkX * 16;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ);

        blockX = chunkX * 16 + 15;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ);

        blockX = chunkX * 16;
        blockZ = chunkZ * 16 + 15;
        showPillar(me, world, blockX, blockZ);

        blockX = chunkX * 16 + 15;
        blockZ = chunkZ * 16 + 15;
        showPillar(me, world, blockX, blockZ);
    }

    private void showPillar(Player player, World world, int blockX, int blockZ) {
        for (int blockY = 0; blockY < player.getLocation().getBlockY() + 30; blockY++) {
            Location loc = new Location(world, blockX, blockY, blockZ);
            if (loc.getBlock().getType() != Material.AIR) {
                continue;
            }

            if (useParticles) {
                this.effect.display(0, 0, 0, 10, 1, loc, player);
            } else {
                int typeId = blockY % 5 == 0 ? Material.REDSTONE_LAMP_ON.getId() : Material.STAINED_GLASS.getId();
                VisualizeUtil.addLocation(player, loc, typeId);
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}
