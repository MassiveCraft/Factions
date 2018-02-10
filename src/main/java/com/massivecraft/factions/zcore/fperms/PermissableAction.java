package com.massivecraft.factions.zcore.fperms;

public enum PermissableAction {
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
    PERMS("perms"),
    SETWARP("setwarp"),
    WARP("warp"),;

    private String name;

    PermissableAction(String name) {
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
    public static PermissableAction fromString(String check) {
        for (PermissableAction permissableAction : values()) {
            if (permissableAction.name().equalsIgnoreCase(check)) {
                return permissableAction;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
