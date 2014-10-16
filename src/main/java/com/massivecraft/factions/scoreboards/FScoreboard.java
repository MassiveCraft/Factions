package com.massivecraft.factions.scoreboards;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public interface FScoreboard {

    public void apply(Player player);

    public void remove(Player player);

    public void update(Objective objective);

    public void setup();

    public Scoreboard getScoreboard();
}
