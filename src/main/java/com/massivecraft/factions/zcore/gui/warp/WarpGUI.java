package com.massivecraft.factions.zcore.gui.warp;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.gui.FactionGUI;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WarpGUI extends FactionGUI<Integer> implements FactionGUI.Dynamic {

    private List<String> warps;
    private int maxWarps;

    public WarpGUI(FPlayer user) {
        super("fwarp-gui", user);
        warps = new ArrayList<>(user.getFaction().getWarps().keySet());
        maxWarps = P.p.getConfig().getInt("max-warps", 5);
        build();
    }

    @Override
    protected Integer convert(String key) {
        try {
            int index = Integer.parseInt(key);
            // Only register the warp index if it's within bounds
            if (maxWarps > index && 0 <= index) {
                return index;
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected String convert(Integer integer) {
        return integer.toString();
    }

    @Override
    protected String parse(String toParse, Integer index) {
        if (warps.size() > index) {
            toParse = toParse.replace("{warp}", warps.get(index));
        } else {
            toParse = toParse.replace("{warp}", "Undefined");
        }
        return toParse;
    }

    @Override
    protected void onClick(Integer index, ClickType clickType) {
        // Check if there are enough faction warps for this index
        if (warps.size() > index) {
            String warp = warps.get(index);
            if (!user.getFaction().hasWarpPassword(warp)) {
                if (transact()) {
                    doWarmup(warp);
                }
            } else {
                HashMap<Object, Object> sessionData = new HashMap<>();
                sessionData.put("warp", warp);
                PasswordPrompt passwordPrompt = new PasswordPrompt();
                ConversationFactory inputFactory = new ConversationFactory(P.p)
                        .withModality(false)
                        .withLocalEcho(false)
                        .withInitialSessionData(sessionData)
                        .withFirstPrompt(passwordPrompt)
                        .addConversationAbandonedListener(passwordPrompt)
                        .withTimeout(config.getInt("password-timeout", 5));

                user.getPlayer().closeInventory();
                inputFactory.buildConversation(user.getPlayer()).begin();
            }
        }
    }

    @Override
    public String getState(Integer index) {
        if (warps.size() > index) {
            if (user.getFaction().hasWarpPassword(warps.get(index))) {
                return "password";
            } else {
                return "exist";
            }
        } else {
            return "non_exist";
        }
    }

    private class PasswordPrompt extends StringPrompt implements ConversationAbandonedListener {

        @Override
        public String getPromptText(ConversationContext context) {
            return TL.COMMAND_FWARP_PASSWORD_REQUIRED.toString();
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
            String warp = (String) context.getSessionData("warp");
            if (user.getFaction().isWarpPassword(warp, input)) {
                // Valid Password, make em pay
                if (transact()) {
                    doWarmup(warp);
                }
            } else {
                // Invalid Password
                user.msg(TL.COMMAND_FWARP_INVALID_PASSWORD);
            }
            return END_OF_CONVERSATION;
        }

        @Override
        public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
            if (abandonedEvent.getCanceller() instanceof ManuallyAbandonedConversationCanceller ||
                    abandonedEvent.getCanceller() instanceof InactivityConversationCanceller) {
                user.msg(TL.COMMAND_FWARP_PASSWORD_CANCEL);
                open();
            }
        }
    }

    private void doWarmup(final String warp) {
        WarmUpUtil.process(user, WarmUpUtil.Warmup.WARP, TL.WARMUPS_NOTIFY_TELEPORT, warp, new Runnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(user.getPlayer().getUniqueId());
                if (player != null) {
                    player.teleport(user.getFaction().getWarp(warp).getLocation());
                    user.msg(TL.COMMAND_FWARP_WARPED, warp);
                }
            }
        }, P.p.getConfig().getLong("warmups.f-warp", 0));
    }

    private boolean transact() {
        if (!P.p.getConfig().getBoolean("warp-cost.enabled", false) || user.isAdminBypassing()) {
            return true;
        }

        double cost = P.p.getConfig().getDouble("warp-cost.warp", 5);

        if (!Econ.shouldBeUsed() || this.user == null || cost == 0.0 || user.isAdminBypassing()) {
            return true;
        }

        if (Conf.bankEnabled && Conf.bankFactionPaysCosts && user.hasFaction()) {
            return Econ.modifyMoney(user.getFaction(), -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
        } else {
            return Econ.modifyMoney(user, -cost, TL.COMMAND_FWARP_TOWARP.toString(), TL.COMMAND_FWARP_FORWARPING.toString());
        }
    }

}
