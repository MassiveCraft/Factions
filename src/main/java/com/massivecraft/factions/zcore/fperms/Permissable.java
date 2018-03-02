package com.massivecraft.factions.zcore.fperms;

import org.bukkit.inventory.ItemStack;

public interface Permissable {

    public ItemStack buildItem();

    public String replacePlaceholders(String string);

    public String name();

}
