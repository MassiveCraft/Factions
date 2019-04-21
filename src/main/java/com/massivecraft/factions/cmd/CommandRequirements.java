package com.massivecraft.factions.cmd;

import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.ChatColor;

public class CommandRequirements {

    // Permission required to execute command
    public Permission permission;

    // Must be player
    public boolean playerOnly;
    // Must be member of faction
    public boolean memberOnly;

    // Must be atleast this role
    public Role role;

    // PermissableAction check if the player has allow for this before checking the role
    public PermissableAction action;

    // Commodore stuffs
    public Class<? extends BrigadierProvider> brigadier;

    // Edge case handling
    public boolean errorOnManyArgs;
    public boolean disableOnLock;

    private CommandRequirements(Permission permission, boolean playerOnly, boolean memberOnly, Role role, PermissableAction action, Class<? extends BrigadierProvider> brigadier) {
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.memberOnly = memberOnly;
        this.role = role;
        this.action = action;
        this.brigadier = brigadier;
    }

    public boolean computeRequirements(CommandContext context, boolean informIfNot) {
        // Did not modify CommandRequirements return true
        if (permission == null) {
            return true;
        }

        if (context.player != null) {
            // Is Player
            if (!context.fPlayer.hasFaction() && memberOnly) {
                if (informIfNot) {
                    context.msg(TL.GENERIC_MEMBERONLY);
                }
                return false;
            }

            if (!P.p.perm.has(context.sender, permission.node, informIfNot)) {
                return false;
            }

            // Permissable Action provided compute that before role
            if (action != null) {
                Access access = context.faction.getAccess(context.fPlayer, action);
                if (access == Access.DENY) {
                    if (informIfNot) {
                        context.msg(TL.GENERIC_NOPERMISSION, action.getName());
                    }
                    return false;
                }

                if (access != Access.ALLOW) {
                    // They have undefined assert their role
                    if (role != null && !context.fPlayer.getRole().isAtLeast(role)) {
                        // They do not fullfill the role
                        if (informIfNot) {
                            context.msg(TL.GENERIC_YOUMUSTBE, role.translation);
                        }
                        return false;
                    }
                }
                // They have been explicitly allowed
                return true;
            } else {
                if ((role != null && !context.fPlayer.getRole().isAtLeast(role)) && informIfNot) {
                    context.msg(TL.GENERIC_YOUMUSTBE, role.translation);
                }
                return role == null || context.fPlayer.getRole().isAtLeast(role);
            }
        } else {
            if (playerOnly) {
                if (informIfNot) {
                    context.sender.sendMessage(TL.GENERIC_PLAYERONLY.toString());
                }
                return false;
            }
            return context.sender.hasPermission(permission.node);
        }
    }

    public static class Builder {

        private Permission permission;

        private boolean playerOnly = false;
        private boolean memberOnly = false;

        private Role role = null;
        private PermissableAction action;

        private Class<? extends BrigadierProvider> brigadier;

        private boolean errorOnManyArgs = true;
        private boolean disableOnLock = true;

        public Builder(Permission permission) {
            this.permission = permission;
        }

        public Builder playerOnly() {
            playerOnly = true;
            return this;
        }

        public Builder memberOnly() {
            playerOnly = true;
            memberOnly = true;
            return this;
        }

        public Builder withRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder withAction(PermissableAction action) {
            this.action = action;
            return this;
        }

        public Builder brigadier(Class<? extends BrigadierProvider> brigadier) {
            this.brigadier = brigadier;
            return this;
        }

        public CommandRequirements build() {
            CommandRequirements requirements = new CommandRequirements(permission, playerOnly, memberOnly, role, action, brigadier);
            requirements.errorOnManyArgs = errorOnManyArgs;
            requirements.disableOnLock = disableOnLock;
            return requirements;
        }

        public Builder noErrorOnManyArgs() {
            errorOnManyArgs = false;
            return this;
        }

        public Builder noDisableOnLock() {
            disableOnLock = false;
            return this;
        }

    }

}
