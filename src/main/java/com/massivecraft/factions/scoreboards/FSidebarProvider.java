package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;

import java.util.List;

public abstract class FSidebarProvider {
    public abstract String getTitle(FPlayer fplayer);
    public abstract List<String> getLines(FPlayer fplayer);
}
