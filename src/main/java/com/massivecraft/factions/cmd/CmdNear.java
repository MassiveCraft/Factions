package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TagReplacer;
import com.massivecraft.factions.zcore.util.TagUtil;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdNear extends FCommand {

    public CmdNear() {
        super();
        this.aliases.add("near");

        this.permission = Permission.NEAR.node;

        senderMustBePlayer = true;
        senderMustBeMember = true;
    }

    @Override
    public void perform() {
        int radius = P.p.getConfig().getInt("f-near.radius", 64);
        List<Entity> nearbyEntities = me.getNearbyEntities(radius, radius, radius);
        List<FPlayer> nearbyMembers = new ArrayList<>();

        for (Entity entity : nearbyEntities) {
            if (entity instanceof Player) {
                FPlayer target = FPlayers.getInstance().getByPlayer((Player) entity);
                if (target.getFaction() == fme.getFaction()) {
                    nearbyMembers.add(target);
                }
            }
        }

        StringBuilder playerMessageBuilder = new StringBuilder();
        String playerMessage = P.p.getConfig().getString("f-near.player", "&a&l{role} &a{role-prefix}{name}");
        for (FPlayer member : nearbyMembers) {
            playerMessageBuilder.append(parsePlaceholders(member, playerMessage));
        }

        String finalMessage = TextUtil.parseColor(P.p.getConfig().getString("f-near.playerlist", "&eNear: {players-nearby}"));
        finalMessage = finalMessage.replace("{players-nearby}", playerMessageBuilder.toString());
        fme.msg(finalMessage);
    }

    private String parsePlaceholders(FPlayer target, String string) {
        string = TextUtil.parseColor(string);
        string = TagUtil.parsePlain(target, string);
        string = TagUtil.parsePlaceholders(target.getPlayer(), string);
        string = string.replace("{role}", target.getRole().toString());
        return string.replace("{role-prefix}", target.getRole().getPrefix());
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }

}
