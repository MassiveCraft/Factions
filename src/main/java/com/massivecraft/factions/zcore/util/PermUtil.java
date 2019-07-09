package com.massivecraft.factions.zcore.util;

import com.massivecraft.factions.zcore.MPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class PermUtil {

    public Map<String, String> permissionDescriptions = new HashMap<>();

    protected MPlugin p;

    public PermUtil(MPlugin p) {
        this.p = p;
        this.setup();
    }

    public String getForbiddenMessage(String perm) {
        return p.txt.parse(TL.GENERIC_NOPERMISSION.toString(), getPermissionDescription(perm));
    }

    /**
     * This method hooks into all permission plugins we are supporting
     */
    public final void setup() {
        for (Permission permission : p.getDescription().getPermissions()) {
            //p.log("\""+permission.getName()+"\" = \""+permission.getDescription()+"\"");
            this.permissionDescriptions.put(permission.getName(), permission.getDescription());
        }
    }

    public String getPermissionDescription(String perm) {
        String desc = permissionDescriptions.get(perm);
        if (desc == null) {
            return TL.GENERIC_DOTHAT.toString();
        }
        return desc;
    }

    /**
     * This method tests if me has a certain permission and returns true if me has. Otherwise false
     */
    public boolean has(CommandSender me, String perm) {
        if (me == null) {
            return false;
        }

        if (!(me instanceof Player)) {
            return me.hasPermission(perm);
        }

        return me.hasPermission(perm);
    }

    public boolean has(CommandSender me, String perm, boolean informSenderIfNot) {
        if (has(me, perm)) {
            return true;
        } else if (informSenderIfNot && me != null) {
            me.sendMessage(this.getForbiddenMessage(perm));
        }
        return false;
    }

    public <T> T pickFirstVal(CommandSender me, Map<String, T> perm2val) {
        if (perm2val == null) {
            return null;
        }
        T ret = null;

        for (Entry<String, T> entry : perm2val.entrySet()) {
            ret = entry.getValue();
            if (has(me, entry.getKey())) {
                break;
            }
        }

        return ret;
    }

}
