package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.tag.FactionTag;
import com.massivecraft.factions.tag.FancyTag;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.zcore.util.TL;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class CmdShow extends FCommand {

    List<String> defaults = new ArrayList<>();

    public CmdShow() {
        this.aliases.add("show");
        this.aliases.add("who");

        // add defaults to /f show in case config doesnt have it
        defaults.add("{header}");
        defaults.add("<a>Description: <i>{description}");
        defaults.add("<a>Joining: <i>{joining}    {peaceful}");
        defaults.add("<a>Land / Power / Maxpower: <i> {chunks} / {power} / {maxPower}");
        defaults.add("<a>Raidable: {raidable}");
        defaults.add("<a>Founded: <i>{create-date}");
        defaults.add("<a>This faction is permanent, remaining even with no members.");
        defaults.add("<a>Land value: <i>{land-value} {land-refund}");
        defaults.add("<a>Balance: <i>{faction-balance}");
        defaults.add("<a>Bans: <i>{faction-bancount}");
        defaults.add("<a>Allies(<i>{allies}<a>/<i>{max-allies}<a>): {allies-list}");
        defaults.add("<a>Online: (<i>{online}<a>/<i>{members}<a>): {online-list}");
        defaults.add("<a>Offline: (<i>{offline}<a>/<i>{members}<a>): {offline-list}");

        this.optionalArgs.put("faction tag", "yours");

        this.requirements = new CommandRequirements.Builder(Permission.SHOW).noDisableOnLock().build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.faction;
        if (context.argIsSet(0)) {
            faction = context.argAsFaction(0);
        }
        if (faction == null) {
            return;
        }

        if (context.fPlayer != null && !context.player.hasPermission("factions.show.bypassexempt")
                && P.p.getConfig().getStringList("show-exempt").contains(faction.getTag())) {
            context.msg(TL.COMMAND_SHOW_EXEMPT);
            return;
        }

        // if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
        if (!context.payForCommand(Conf.econCostShow, TL.COMMAND_SHOW_TOSHOW, TL.COMMAND_SHOW_FORSHOW)) {
            return;
        }

        List<String> show = P.p.getConfig().getStringList("show");
        if (show == null || show.isEmpty()) {
            show = defaults;
        }

        if (!faction.isNormal()) {
            String tag = faction.getTag(context.fPlayer);
            // send header and that's all
            String header = show.get(0);
            if (FactionTag.HEADER.foundInString(header)) {
                context.msg(p.txt.titleize(tag));
            } else {
                context.msg(p.txt.parse(header.replace(FactionTag.FACTION.getTag(), tag)));
            }
            return; // we only show header for non-normal factions
        }

        List<String> messageList = new ArrayList<>();
        for (String raw : show) {
            String parsed = Tag.parsePlain(faction, context.fPlayer, raw); // use relations
            if (parsed == null) {
                continue; // Due to minimal f show.
            }

            if (context.fPlayer != null) {
                parsed = Tag.parsePlaceholders(context.fPlayer.getPlayer(), parsed);
            }

            if (!parsed.contains("{notFrozen}") && !parsed.contains("{notPermanent}")) {
                if (parsed.contains("{ig}")) {
                    // replaces all variables with no home TL
                    parsed = parsed.substring(0, parsed.indexOf("{ig}")) + TL.COMMAND_SHOW_NOHOME.toString();
                }
                parsed = parsed.replace("%", ""); // Just in case it got in there before we disallowed it.
                messageList.add(parsed); // TODO context.msg(p.txt.parse)
            }
        }
        if (context.fPlayer != null && this.groupPresent()) {
            new GroupGetter(messageList, context.fPlayer, faction).runTaskAsynchronously(P.p);
        } else {
            this.sendMessages(messageList, context.sender, faction, context.fPlayer);
        }
    }

    private void sendMessages(List<String> messageList, CommandSender recipient, Faction faction, FPlayer player) {
        this.sendMessages(messageList, recipient, faction, player, null);
    }

    private void sendMessages(List<String> messageList, CommandSender recipient, Faction faction, FPlayer player, Map<UUID, String> groupMap) {
        for (String parsed : messageList) {
            if (player != null && FancyTag.anyMatch(parsed)) {
                List<FancyMessage> fancy = FancyTag.parse(parsed, faction, player, groupMap);
                if (fancy != null) {
                    for (FancyMessage fancyMessage : fancy) {
                        fancyMessage.send(recipient);
                    }
                }
            } else {
                recipient.sendMessage(P.p.txt.parse(parsed));
            }
        }
    }

    private boolean groupPresent() {
        for (String line : P.p.getConfig().getStringList("tooltips.show")) {
            if (line.contains("{group}")) {
                return true;
            }
        }
        return false;
    }

    private class GroupGetter extends BukkitRunnable {
        private List<String> messageList;
        private FPlayer sender;
        private Faction faction;
        private Set<OfflinePlayer> players;

        private GroupGetter(List<String> messageList, FPlayer sender, Faction faction) {
            this.messageList = messageList;
            this.sender = sender;
            this.faction = faction;
            this.players = faction.getFPlayers().stream().map(fp -> Bukkit.getOfflinePlayer(UUID.fromString(fp.getId()))).collect(Collectors.toSet());
        }

        @Override
        public void run() {
            Map<UUID, String> map = new HashMap<>();
            for (OfflinePlayer player : this.players) {
                map.put(player.getUniqueId(), P.p.getPrimaryGroup(player));
            }
            new Sender(this.messageList, this.sender, this.faction, map).runTask(P.p);
        }
    }

    private class Sender extends BukkitRunnable {
        private List<String> messageList;
        private FPlayer sender;
        private Faction faction;
        private Map<UUID, String> map;

        private Sender(List<String> messageList, FPlayer sender, Faction faction, Map<UUID, String> map) {
            this.messageList = messageList;
            this.sender = sender;
            this.faction = faction;
            this.map = map;
        }

        @Override
        public void run() {
            Player player = Bukkit.getPlayerExact(sender.getName());
            if (player != null) {
                CmdShow.this.sendMessages(messageList, player, faction, sender, map);
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SHOW_COMMANDDESCRIPTION;
    }

}