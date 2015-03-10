package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CmdSB extends FCommand {

    private YamlConfiguration yml;
    private File file;

    public CmdSB() {
        this.aliases.add("sb");
        this.permission = Permission.SCOREBOARD.node;
        this.senderMustBePlayer = true;
        // Hope I didn't miss anything.

        file = new File(P.p.getDataFolder(), "playerBoardToggle.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        yml = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void perform() {
        boolean toggle = toggle(me.getPlayer().getUniqueId());
        FScoreboard board = FScoreboard.get(fme);
        if(board == null) {
            me.sendMessage(TL.COMMAND_TOGGLESB_DISABLED.toString());
        } else {
            me.sendMessage(TL.TOGGLE_SB.toString().replace("{value}", String.valueOf(toggle)));
            board.setSidebarVisibility(toggle);
        }
    }

    /**
     * Toggle a player seeing scoreboards or not.
     *
     * @param uuid - uuid of player.
     *
     * @return - true if now set to seeing scoreboards, otherwise false.
     */
    public boolean toggle(UUID uuid) {
        if (!yml.getBoolean(uuid.toString(), true)) { // check if it's false, if never been toggled, default to false.
            yml.set(uuid.toString(), true);
            save();
            return true;
        } else {
            yml.set(uuid.toString(), false);
            save();
            return false;
        }
    }

    public void save() {
        try {
            yml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines whether or not to show the player a scoreboard.
     *
     * @param player - FPlayer in question.
     *
     * @return - true if should show, otherwise false.
     */
    public boolean showBoard(FPlayer player) {
        return showBoard(player.getPlayer());
    }

    /**
     * Determines whether or not to show the player a scoreboard.
     *
     * @param player - Player in question.
     *
     * @return - true if should show, otherwise false.
     */
    public boolean showBoard(Player player) {
        return showBoard(player.getUniqueId());
    }

    /**
     * Determines whether or not to show the player a scoreboard.
     *
     * @param uuid - UUID of player in question.
     *
     * @return - true if should show, otherwise false.
     */
    public boolean showBoard(UUID uuid) {
        return yml.getBoolean(uuid.toString(), true);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SCOREBOARD_DESCRIPTION;
    }
}
