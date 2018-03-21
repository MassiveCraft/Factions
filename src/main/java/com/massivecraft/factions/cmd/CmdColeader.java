package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdColeader extends FCommand {

    public CmdColeader() {
        super();
        this.aliases.add("coleader");
        this.aliases.add("setcoleader");
        this.aliases.add("cl");
        this.aliases.add("setcl");

        this.optionalArgs.put("player name", "name");

        this.permission = Permission.COLEADER.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = true;
    }

    @Override
    public void perform() {
        FPlayer target = this.argAsBestFPlayerMatch(0);
        if (target == null) {
            FancyMessage msg = new FancyMessage(TL.COMMAND_COLEADER_CANDIDATES.toString()).color(ChatColor.GOLD);
            for (FPlayer player : myFaction.getFPlayersWhereRole(Role.MODERATOR)) {
                String s = player.getName();

                msg.then(s + " ").color(ChatColor.WHITE)
                        .tooltip(TL.COMMAND_COLEADER_CLICKTOPROMOTE.toString() + s)
                        .command("/" + Conf.baseCommandAliases.get(0) + " coleader " + s);
            }

            sendFancyMessage(msg);
            return;
        }

        boolean permAny = Permission.COLEADER_ANY.has(sender, false);
        Faction targetFaction = target.getFaction();

        if (targetFaction != myFaction && !permAny) {
            msg(TL.COMMAND_COLEADER_NOTMEMBER, target.describeTo(fme, true));
            return;
        }

        if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
            msg(TL.COMMAND_COLEADER_NOTADMIN);
            return;
        }

        if (target == fme && !permAny) {
            msg(TL.COMMAND_COLEADER_SELF);
            return;
        }

        if (target.getRole() == Role.ADMIN) {
            msg(TL.COMMAND_COLEADER_TARGETISADMIN);
            return;
        }

        if (target.getRole() == Role.COLEADER) {
            // Revoke
            target.setRole(Role.MODERATOR);
            targetFaction.msg(TL.COMMAND_COLEADER_REVOKED, target.describeTo(targetFaction, true));
            msg(TL.COMMAND_COLEADER_REVOKES, target.describeTo(fme, true));
            return;
        }

        // Check to see if we should allow multiple coleaders or not.
        if (!Conf.allowMultipleColeaders && !targetFaction.getFPlayersWhereRole(Role.COLEADER).isEmpty()) {
            msg(TL.COMMAND_COLEADER_ALREADY_COLEADER);
            return;
        }

        // Give
        target.setRole(Role.COLEADER);
        targetFaction.msg(TL.COMMAND_COLEADER_PROMOTED, target.describeTo(targetFaction, true));
        msg(TL.COMMAND_COLEADER_PROMOTES, target.describeTo(fme, true));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_COLEADER_DESCRIPTION;
    }

}
