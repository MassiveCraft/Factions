package com.massivecraft.factions.scoreboards;

import com.google.common.base.Splitter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Method;
import java.util.*;

public class BufferedObjective {
    private static final Method addEntryMethod;
    private static final int MAX_LINE_LENGTH;

    private final Scoreboard scoreboard;
    private final String baseName;

    private Objective current;
    private List<Team> currentTeams = new ArrayList<Team>();
    private String title;
    private DisplaySlot displaySlot;

    private int objPtr;
    private int teamPtr;
    private boolean requiresUpdate = false;

    private final Map<Integer, String> contents = new HashMap<Integer, String>();

    static {
        // Check for long line support.
        // We require use of Spigot's `addEntry(String)` method on
        // Teams, as adding OfflinePlayers to a team is far too slow.

        Method addEntryMethodLookup = null;
        try {
            addEntryMethodLookup = Team.class.getMethod("addEntry", String.class);
        } catch (NoSuchMethodException ignored) {
        }

        addEntryMethod = addEntryMethodLookup;

        if (addEntryMethod != null) {
            MAX_LINE_LENGTH = 48;
        } else {
            MAX_LINE_LENGTH = 16;
        }
    }

    public BufferedObjective(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
        this.baseName = createBaseName();

        current = scoreboard.registerNewObjective(getNextObjectiveName(), "dummy");
    }

    private String createBaseName() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        while (builder.length() < 14) {
            builder.append(Integer.toHexString(random.nextInt()));
        }
        return builder.toString().substring(0, 14);
    }

    public void setTitle(String title) {
        if (this.title == null || !this.title.equals(title)) {
            this.title = title;
            requiresUpdate = true;
        }
    }

    public void setDisplaySlot(DisplaySlot slot) {
        this.displaySlot = slot;
        current.setDisplaySlot(slot);
    }

    public void setAllLines(List<String> lines) {
        if (lines.size() != contents.size()) {
            contents.clear();
        }
        for (int i = 0; i < lines.size(); i++) {
            setLine(lines.size() - i, lines.get(i));
        }
    }

    public void setLine(int lineNumber, String content) {
        if (content.length() > MAX_LINE_LENGTH) {
            content = content.substring(0, MAX_LINE_LENGTH);
        }
        content = ChatColor.translateAlternateColorCodes('&', content);

        if (contents.get(lineNumber) == null || !contents.get(lineNumber).equals(content)) {
            contents.put(lineNumber, content);
            requiresUpdate = true;
        }
    }

    // Hides the objective from the display slot until flip() is called
    public void hide() {
        if (displaySlot != null) {
            scoreboard.clearSlot(displaySlot);
        }
    }

    public void flip() {
        if (!requiresUpdate) {
            return;
        }
        requiresUpdate = false;

        Objective buffer = scoreboard.registerNewObjective(getNextObjectiveName(), "dummy");
        buffer.setDisplayName(title);

        List<Team> bufferTeams = new ArrayList<Team>();

        for (Map.Entry<Integer, String> entry : contents.entrySet()) {
            if (entry.getValue().length() > 16) {
                Team team = scoreboard.registerNewTeam(getNextTeamName());
                bufferTeams.add(team);

                Iterator<String> split = Splitter.fixedLength(16).split(entry.getValue()).iterator();

                team.setPrefix(split.next());
                String name = split.next();
                if (split.hasNext()) { // We only guarantee two splits
                    team.setSuffix(split.next());
                }

                try {
                    addEntryMethod.invoke(team, name);
                } catch (ReflectiveOperationException ignored) {
                }
                buffer.getScore(name).setScore(entry.getKey());
            } else {
                buffer.getScore(entry.getValue()).setScore(entry.getKey());
            }
        }

        if (displaySlot != null) {
            buffer.setDisplaySlot(displaySlot);
        }

        // Unregister _ALL_ the old things
        current.unregister();

        Iterator<Team> it = currentTeams.iterator();
        while (it.hasNext()) {
            it.next().unregister();
            it.remove();
        }

        current = buffer;
        currentTeams = bufferTeams;
    }

    private String getNextObjectiveName() {
        return baseName + "_" + ((objPtr++) % 2);
    }

    private String getNextTeamName() {
        return baseName.substring(0, 10) + "_" + ((teamPtr++) % 999999);
    }
}
