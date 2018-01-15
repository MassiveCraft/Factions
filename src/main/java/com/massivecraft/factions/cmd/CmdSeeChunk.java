package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CmdSeeChunk extends FCommand {

    public CmdSeeChunk() {
        super();
        aliases.add("seechunk");
        aliases.add("sc");

        permission = Permission.SEECHUNK.node;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
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

    @SuppressWarnings("deprecation")
    public static void showPillar(Player player, World world, int blockX, int blockZ) {
        for (int blockY = 0; blockY < player.getLocation().getBlockY() + 30; blockY++) {
            Location loc = new Location(world, blockX, blockY, blockZ);
            if (loc.getBlock().getType() != Material.AIR) {
                continue;
            }
            int typeId = blockY % 5 == 0 ? Material.REDSTONE_LAMP_ON.getId() : Material.STAINED_GLASS.getId();
            VisualizeUtil.addLocation(player, loc, typeId);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.GENERIC_PLACEHOLDER;
    }

}
