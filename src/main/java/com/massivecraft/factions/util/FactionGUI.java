package com.massivecraft.factions.util;

import org.bukkit.event.inventory.ClickType;

public interface FactionGUI {

    public void onClick(int slot, ClickType action);

    public void build();

}
