package com.massivecraft.factions.scoreboards.tasks;

import com.massivecraft.factions.scoreboards.FScoreboard;

import java.util.Random;

public class UpdateTask implements Runnable {

    private FScoreboard board;
    private final Random random;

    public UpdateTask(FScoreboard board) {
        this.board = board;
        this.random = new Random();
    }

    @Override
    public void run() {
        board.update(board.getScoreboard().registerNewObjective(getRandomString(), "dummy"));
    }

    // Just can't be the same as the last one. WHAT ARE THE ODDS
    private String getRandomString() {
        return String.valueOf(random.nextInt(10000)) + String.valueOf(random.nextInt(10000));
    }
}
