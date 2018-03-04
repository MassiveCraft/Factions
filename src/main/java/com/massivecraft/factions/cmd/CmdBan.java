package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class CmdBan extends FCommand {

    public CmdBan() {
        super();
        this.aliases.add("ban");

        this.requiredArgs.add("target");

        this.permission = Permission.BAN.node;
        this.disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        Access access = myFaction.getAccess(fme, PermissableAction.BAN);
        if (access == Access.DENY) {
            fme.msg(TL.GENERIC_NOPERMISSION, "ban");
            return;
        }

        // Can the player ban for this faction?
        // Check for ALLOW access as well before we check for role.
        if (access != Access.ALLOW) {
            if (!Permission.BAN.has(sender, true) || !assertMinRole(Role.MODERATOR)) {
                return;
            }
        } else {
            if (!Permission.BAN.has(sender, true)) {
                return;
            }
        }

        // Good on permission checks. Now lets just ban the player.
        FPlayer target = argAsFPlayer(0);
        if (target == null) {
            return; // the above method sends a message if fails to find someone.
        }

        if (fme == target) {
            // You may not ban yourself
            fme.msg(TL.COMMAND_BAN_SELF);
            return;
        } else if (target.getFaction() == myFaction && target.getRole().value >= fme.getRole().value) {
            // You may not ban someone that has same or higher faction rank
            fme.msg(TL.COMMAND_BAN_INSUFFICIENTRANK, target.getName());
            return;
        }

        // Ban the user.
        myFaction.ban(target, fme);
        myFaction.deinvite(target); // can't hurt

        // If in same Faction, lets make sure to kick them and throw an event.
        if (target.getFaction() == myFaction) {

            FPlayerLeaveEvent event = new FPlayerLeaveEvent(target, myFaction, FPlayerLeaveEvent.PlayerLeaveReason.BANNED);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                // if someone cancels a ban, we'll get people complaining here. So lets log it.
                P.p.log(Level.WARNING, "Attempted to ban {0} but someone cancelled the kick event. This isn't good.", target.getName());
                return;
            }

            // Didn't get cancelled so remove them and reset their invite.
            myFaction.removeFPlayer(target);
            target.resetFactionData();
        }

        // Lets inform the people!
        target.msg(TL.COMMAND_BAN_TARGET, myFaction.getTag(target.getFaction()));
        myFaction.msg(TL.COMMAND_BAN_BANNED, fme.getName(), target.getName());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_BAN_DESCRIPTION;
    }
}
