package com.massivecraft.factions.zcore.gui.perm;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Permissable;
import com.massivecraft.factions.zcore.gui.FactionGUI;
import org.bukkit.event.inventory.ClickType;

public class PermissableRelationGUI extends FactionGUI<Permissable> {

    public PermissableRelationGUI(FPlayer user) {
        super("fperm-gui.relation", user);
        build();
    }

    @Override
    protected Permissable convert(String key) {
        if (Role.fromString(key) != null) {
            return Role.fromString(key);
        } else if (Relation.fromString(key) != null) {
            return Relation.fromString(key);
        } else {
            return null;
        }
    }

    @Override
    protected String convert(Permissable permissable) {
        return permissable.name().toLowerCase();
    }

    @Override
    protected String parse(String toParse, Permissable permissable) {
        // Uppercase the first letter
        String name = permissable.toString().substring(0, 1).toUpperCase() + permissable.toString().substring(1);

        toParse = toParse.replace("{relation-color}", permissable.getColor().toString());
        toParse = toParse.replace("{relation}", name);
        return toParse;
    }

    @Override
    protected void onClick(Permissable permissable, ClickType clickType) {
        new PermissableActionGUI(user, permissable).open();
    }

}
