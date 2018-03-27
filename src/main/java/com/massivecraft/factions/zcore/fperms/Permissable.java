package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.FPlayer;
import org.bukkit.inventory.ItemStack;

public interface Permissable {

    public ItemStack buildItem(FPlayer fme);

    public String replacePlaceholders(String string, FPlayer fme);

    public String name();

}
