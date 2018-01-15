package com.massivecraft.factions.zcore.fperms;

public enum Action {
    BUILD("build"),
    DESTROY("destroy"),
    FROST_WALK("frostwalk"),
    PAIN_BUILD("painbuild"),
    DOOR("door"),
    BUTTON("button"),
    LEVER("lever"),
    CONTAINER("container"),
    INVITE("invite"),
    KICK("kick"),
    ITEM("items"), // generic for most items
    SETHOME("sethome"),
    WITHDRAW("withdraw"),
    TERRITORY("territory"),
    ACCESS("access"),
    DISBAND("disband"),
    PROMOTE("promote"),
    PERMS("perms");

    private String name;

    Action(String name) {
        this.name = name;
    }

    /**
     * Get the friendly name of this action. Used for editing in commands.
     *
     * @return friendly name of the action as a String.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Case insensitive check for action.
     *
     * @param check
     * @return
     */
    public static Action fromString(String check) {
        for (Action action : values()) {
            if (action.name().equalsIgnoreCase(check)) {
                return action;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
