package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;


public class CmdVersion extends FCommand {

    public CmdVersion() {
        this.aliases.add("version");

        //this.requiredArgs.add("");
        //this.optionalArgs.put("", "");

        this.permission = Permission.VERSION.node;
        this.disableOnLock = false;

        senderMustBePlayer = false;
        senderMustBeMember = false;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;
    }

    @Override
    public void perform() {
        msg(TL.COMMAND_VERSION_VERSION,P.p.getDescription().getFullName());
        msg(TL.GENERIC_TRANSLATION_VERSION,TL._LOCALE,TL._LOCAL_LANGUAGE,TL._LOCAL_REGION,TL._LOCAL_STATE);
        msg(TL.GENERIC_TRANSLATION_CONTRIBUTORS,TL._LOCAL_AUTHOR);
        msg(TL.GENERIC_TRANSLATION_RESPONSIBLE,TL._LOCAL_RESPONSIBLE);
    }
}
