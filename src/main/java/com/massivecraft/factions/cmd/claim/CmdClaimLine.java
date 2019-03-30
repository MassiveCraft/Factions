package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class CmdClaimLine extends FCommand {

    public static final BlockFace[] axis = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

    public CmdClaimLine() {

        // Aliases
        this.aliases.add("claimline");
        this.aliases.add("cl");

        // Args
        this.optionalArgs.put("amount", "1");
        this.optionalArgs.put("direction", "facing");
        this.optionalArgs.put("faction", "you");

        this.requirements = new CommandRequirements.Builder(Permission.CLAIM_LINE)
                .playerOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        // Args
        Integer amount = context.argAsInt(0, 1); // Default to 1

        if (amount > Conf.lineClaimLimit) {
            context.msg(TL.COMMAND_CLAIMLINE_ABOVEMAX, Conf.lineClaimLimit);
            return;
        }

        String direction = context.argAsString(1);
        BlockFace blockFace;

        if (direction == null) {
            blockFace = axis[Math.round(context.player.getLocation().getYaw() / 90f) & 0x3];
        } else if (direction.equalsIgnoreCase("north")) {
            blockFace = BlockFace.NORTH;
        } else if (direction.equalsIgnoreCase("east")) {
            blockFace = BlockFace.EAST;
        } else if (direction.equalsIgnoreCase("south")) {
            blockFace = BlockFace.SOUTH;
        } else if (direction.equalsIgnoreCase("west")) {
            blockFace = BlockFace.WEST;
        } else {
            context.fPlayer.msg(TL.COMMAND_CLAIMLINE_NOTVALID, direction);
            return;
        }

        final Faction forFaction = context.argAsFaction(2, context.faction);
        Location location = context.player.getLocation();

        // TODO: make this a task like claiming a radius?
        for (int i = 0; i < amount; i++) {
            context.fPlayer.attemptClaim(forFaction, location, true);
            location = location.add(blockFace.getModX() * 16, 0, blockFace.getModZ() * 16);
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CLAIMLINE_DESCRIPTION;
    }
}
