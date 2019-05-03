package com.massivecraft.factions.zcore.gui.perm;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.P;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.gui.FactionGUI;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.event.inventory.ClickType;

public class PermissableActionGUI extends FactionGUI<PermissableAction> implements FactionGUI.Dynamic, FactionGUI.Backable {

    private Permissable permissable;

    public PermissableActionGUI(FPlayer user, Permissable permissable) {
        super("fperm-gui.action", user);
        this.permissable = permissable;
        build();
    }

    @Override
    protected PermissableAction convert(String key) {
        return PermissableAction.fromString(key);
    }

    @Override
    protected String convert(PermissableAction action) {
        return action.name().toLowerCase();
    }

    @Override
    protected String parse(String toParse, PermissableAction action) {
        String actionName = action.getName().substring(0, 1).toUpperCase() + action.getName().substring(1);
        toParse = toParse.replace("{action}", actionName);

        Access access = user.getFaction().getAccess(permissable, action);
        if (access == null) {
            access = Access.UNDEFINED;
        }
        String actionAccess = access.getName();
        toParse = toParse.replace("{action-access}", actionAccess);
        toParse = toParse.replace("{action-access-color}", access.getColor().toString());

        return toParse;
    }

    @Override
    protected void onClick(PermissableAction action, ClickType click) {
        Access access;
        if (click == ClickType.LEFT) {
            access = Access.ALLOW;
            user.getFaction().setPermission(permissable, action, access);
        } else if (click == ClickType.RIGHT) {
            access = Access.DENY;
            user.getFaction().setPermission(permissable, action, access);
        } else if (click == ClickType.MIDDLE) {
            access = Access.UNDEFINED;
            user.getFaction().setPermission(permissable, action, access);
        } else {
            return;
        }

        // Reload items to reparse placeholders
        buildItems();
        user.msg(TL.COMMAND_PERM_SET, action.name(), access.name(), permissable.name());
        P.p.log(String.format(TL.COMMAND_PERM_SET.toString(), action.name(), access.name(), permissable.name()) + " for faction " + user.getTag());
    }

    @Override
    public String getState(PermissableAction action) {
        return user.getFaction().getAccess(permissable, action).name().toLowerCase();
    }

    // For dummy items only parseDefault is called, but we want to provide the relation placeholders, so: Override
    @Override
    protected String parseDefault(String string) {
        String parsed = super.parseDefault(string);

        String permissableName = permissable.toString().substring(0, 1).toUpperCase() + permissable.toString().substring(1);
        parsed = parsed.replace("{relation-color}", permissable.getColor().toString());
        parsed = parsed.replace("{relation}", permissableName);
        return parsed;
    }

    @Override
    public void onBack() {
        new PermissableRelationGUI(user).open();
    }
}
