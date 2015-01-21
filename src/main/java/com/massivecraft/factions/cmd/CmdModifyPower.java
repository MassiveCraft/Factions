package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdModifyPower extends FCommand {

    public CmdModifyPower() {
        super();

        this.aliases.add("pm");
        this.aliases.add("mp");
        this.aliases.add("modifypower");
        this.aliases.add("modpower");

        this.requiredArgs.add("name");
        this.requiredArgs.add("power");

        this.permission = Permission.MODIFY_POWER.node; // admin only perm.

        // Let's not require anything and let console modify this as well.
        this.senderMustBeAdmin = false;
        this.senderMustBePlayer = false;
        this.senderMustBeMember = false;
        this.senderMustBeModerator = false;
    }

    @Override
    public void perform() {
        // /f modify <name> #
        FPlayer player = argAsBestFPlayerMatch(0);
        Double number = argAsDouble(1); // returns null if not a Double.

        if (player == null || number == null) {
            sender.sendMessage(getHelpShort());
            return;
        }

        player.alterPower(number);
        int newPower = player.getPowerRounded(); // int so we don't have super long doubles.
        msg(TL.COMMAND_MODIFYPOWER_ADDED, number, player.getName(), newPower);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MODIFYPOWER_DESCRIPTION;
    }
}
