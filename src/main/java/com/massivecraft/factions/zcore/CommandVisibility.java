package com.massivecraft.factions.zcore;

public enum CommandVisibility {
    VISIBLE, // Visible commands are visible to anyone. Even those who don't have permission to use it or is of invalid sender type.
    SECRET, // Secret commands are visible only to those who can use the command. These commands are usually some kind of admin commands.
    INVISIBLE // Invisible commands are invisible to everyone, even those who can use the command.
}
