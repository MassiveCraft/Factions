package com.massivecraft.factions.zcore.fperms;

import com.massivecraft.factions.FPlayer;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public interface Permissable {

    ItemStack buildItem(FPlayer fme);

    String replacePlaceholders(String string, FPlayer fme);

    String name();

    ChatColor getColor();

}
