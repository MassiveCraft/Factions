package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;

public class CmdAdmin extends FCommand {

    public CmdAdmin() {
        super();
        this.aliases.add("admin");
        this.aliases.add("setadmin");
        this.aliases.add("leader");
        this.aliases.add("setleader");

        this.requiredArgs.add("player");

        this.requirements = new CommandRequirements.Builder(Permission.ADMIN).build();
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer fyou = context.argAsBestFPlayerMatch(0);
        if (fyou == null) {
            return;
        }

        boolean permAny = Permission.ADMIN_ANY.has(context.sender, false);
        Faction targetFaction = fyou.getFaction();

        if (targetFaction != context.faction && !permAny) {
            context.msg(TL.COMMAND_ADMIN_NOTMEMBER, fyou.describeTo(context.fPlayer, true));
            return;
        }

        if (context.fPlayer != null && context.fPlayer.getRole() != Role.ADMIN && !permAny) {
            context.msg(TL.COMMAND_ADMIN_NOTADMIN);
            return;
        }

        if (fyou == context.fPlayer && !permAny) {
            context.msg(TL.COMMAND_ADMIN_TARGETSELF);
            return;
        }

        // only perform a FPlayerJoinEvent when newLeader isn't actually in the faction
        if (fyou.getFaction() != targetFaction) {
            FPlayerJoinEvent event = new FPlayerJoinEvent(FPlayers.getInstance().getByPlayer(context.player), targetFaction, FPlayerJoinEvent.PlayerJoinReason.LEADER);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
        }

        FPlayer admin = targetFaction.getFPlayerAdmin();

        // if target player is currently admin, demote and replace him
        if (fyou == admin) {
            targetFaction.promoteNewLeader();
            context.msg(TL.COMMAND_ADMIN_DEMOTES, fyou.describeTo(context.fPlayer, true));
            fyou.msg(TL.COMMAND_ADMIN_DEMOTED, context.player == null ? TL.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(fyou, true));
            return;
        }

        // promote target player, and demote existing admin if one exists
        if (admin != null) {
            admin.setRole(Role.COLEADER);
        }
        fyou.setRole(Role.ADMIN);
        context.msg(TL.COMMAND_ADMIN_PROMOTES, fyou.describeTo(context.fPlayer, true));

        // Inform all players
        for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
            fplayer.msg(TL.COMMAND_ADMIN_PROMOTED, context.player == null ? TL.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(fplayer, true), fyou.describeTo(fplayer), targetFaction.describeTo(fplayer));
        }
    }

    public TL getUsageTranslation() {
        return TL.COMMAND_ADMIN_DESCRIPTION;
    }

}
