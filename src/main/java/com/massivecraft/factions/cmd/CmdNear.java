package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdNear extends FCommand {

    public CmdNear() {
        super();
        this.aliases.add("near");

        this.requirements = new CommandRequirements.Builder(Permission.NEAR)
                .memberOnly()
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        int radius = P.p.getConfig().getInt("f-near.radius", 20);
        List<Entity> nearbyEntities = context.player.getNearbyEntities(radius, radius, radius);
        List<FPlayer> nearbyMembers = new ArrayList<>();

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                FPlayer target = FPlayers.getInstance().getByPlayer((Player) entity);
                if (target.getFaction() ==context.fPlayer.getFaction()) {
                    nearbyMembers.add(target);
                }
            }
        }

        StringBuilder playerMessageBuilder = new StringBuilder();
        String playerMessage = TL.COMMAND_NEAR_PLAYER.toString();
        for (FPlayer member : nearbyMembers) {
            playerMessageBuilder.append(parsePlaceholders(context.fPlayer, member, playerMessage));
        }
        // Append none text if no players where found
        if (playerMessageBuilder.toString().isEmpty()) {
            playerMessageBuilder.append(TL.COMMAND_NEAR_NONE);
        }

        context.msg(TL.COMMAND_NEAR_PLAYERLIST.toString().replace("{players-nearby}", playerMessageBuilder.toString()));
    }

    private String parsePlaceholders(FPlayer user, FPlayer target, String string) {
        string = TagUtil.parsePlain(target, string);
        string = TagUtil.parsePlaceholders(target.getPlayer(), string);
        string = string.replace("{role}", target.getRole().toString());
        string = string.replace("{role-prefix}", target.getRole().getPrefix());
        // Only run distance calculation if needed
        if (string.contains("{distance}")) {
            double distance = Math.round(user.getPlayer().getLocation().distance(target.getPlayer().getLocation()));
            string = string.replace("{distance}", distance + "");
        }
        return string;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_NEAR_DESCRIPTION;
    }

}
