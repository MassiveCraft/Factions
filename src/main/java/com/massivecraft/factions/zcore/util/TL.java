/*
 * Copyright (C) 2013 drtshock
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.massivecraft.factions.zcore.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.text.SimpleDateFormat;

/**
 * An enum for requesting strings from the language file. The contents of this enum file may be subject to frequent
 * changes.
 */
public enum TL {
    /**
     * Translation meta
     */
    _AUTHOR("misc"),
    _RESPONSIBLE("misc"),
    _LANGUAGE("English"),
    _ENCODING("UTF-8"),
    _LOCALE("en_US"),
    _REQUIRESUNICODE("false"),
    _DEFAULT("true"),
    _STATE("complete"), //incomplete, limited, partial, majority, complete

    /**
     * Localised translation meta
     */
    _LOCAL_AUTHOR("misc"),
    _LOCAL_RESPONSIBLE("misc"),
    _LOCAL_LANGUAGE("English"),
    _LOCAL_REGION("US"),
    _LOCAL_STATE("complete"), //And this is the English version. It's not ever going to be not complete.

    /**
     * Command translations
     */
    COMMAND_ADMIN_NOTMEMBER("%1$s&e is not a member in your faction."),
    COMMAND_ADMIN_NOTADMIN("&cYou are not the faction admin."),
    COMMAND_ADMIN_TARGETSELF("&cThe target player musn't be yourself."),
    COMMAND_ADMIN_DEMOTES("&eYou have demoted %1$s&e from the position of faction admin."),
    COMMAND_ADMIN_DEMOTED("&eYou have been demoted from the position of faction admin by %1$s&e."),
    COMMAND_ADMIN_PROMOTES("&eYou have promoted %1$s&e to the position of faction admin."),
    COMMAND_ADMIN_PROMOTED("%1$s&e gave %2$s&e the leadership of %3$s&e."),
    COMMAND_ADMIN_DESCRIPTION("Hand over your admin rights"),

    COMMAND_AHOME_DESCRIPTION("Send a player to their f home no matter what."),
    COMMAND_AHOME_NOHOME("%1$s doesn't have an f home."),
    COMMAND_AHOME_SUCCESS("$1%s was sent to their f home."),
    COMMAND_AHOME_OFFLINE("%1$s is offline."),
    COMMAND_AHOME_TARGET("You were sent to your f home."),

    COMMAND_ANNOUNCE_DESCRIPTION("Announce a message to players in faction."),

    COMMAND_AUTOCLAIM_ENABLED("&eNow auto-claiming land for &d%1$s&e."),
    COMMAND_AUTOCLAIM_DISABLED("&eAuto-claiming of land disabled."),
    COMMAND_AUTOCLAIM_REQUIREDRANK("&cYou must be &d%1$s&c to claim land."),
    COMMAND_AUTOCLAIM_OTHERFACTION("&cYou can't claim land for &d%1$s&c."),
    COMMAND_AUTOCLAIM_DESCRIPTION("Auto-claim land as you walk around"),

    COMMAND_AUTOHELP_HELPFOR("Help for command \""),

    COMMAND_BAN_DESCRIPTION("Ban players from joining your Faction."),
    COMMAND_BAN_TARGET("&cYou were banned from &7%1$s"), // banned player perspective
    COMMAND_BAN_BANNED("&e%1$s &cbanned &7%2$s"),
    COMMAND_BAN_ALREADYBANNED("&c%1$s is already banned"),
    COMMAND_BAN_SELF("&cYou may not ban yourself"),
    COMMAND_BAN_INSUFFICIENTRANK("&cYour rank is too low to ban &7%1$s"),

    COMMAND_BANLIST_DESCRIPTION("View a Faction's ban list"),
    COMMAND_BANLIST_HEADER("&6There are &c%d&6 bans for %s"),
    COMMAND_BANLIST_ENTRY("&7%d. &c%s &r- &a%s &r- &e%s"),
    COMMAND_BANLIST_NOFACTION("&4You are not in a Faction."),
    COMMAND_BANLIST_INVALID("We couldn't find a Faction by the name %s"),

    COMMAND_BOOM_PEACEFULONLY("&cThis command is only usable by factions which are specifically designated as peaceful."),
    COMMAND_BOOM_TOTOGGLE("to toggle explosions"),
    COMMAND_BOOM_FORTOGGLE("for toggling explosions"),
    COMMAND_BOOM_ENABLED("%1$s&e has %2$s explosions in your faction's territory."),
    COMMAND_BOOM_DESCRIPTION("Toggle explosions (peaceful factions only)"),

    COMMAND_BYPASS_ENABLE("&eYou have enabled admin bypass mode. You will be able to build or destroy anywhere."),
    COMMAND_BYPASS_ENABLELOG(" has ENABLED admin bypass mode."),
    COMMAND_BYPASS_DISABLE("&eYou have disabled admin bypass mode."),
    COMMAND_BYPASS_DISABLELOG(" has DISABLED admin bypass mode."),
    COMMAND_BYPASS_DESCRIPTION("Enable admin bypass mode"),

    COMMAND_CHAT_DISABLED("&cThe built in chat channels are disabled on this server."),
    COMMAND_CHAT_INVALIDMODE("&cUnrecognised chat mode. &ePlease enter either 'a','f','m','t' or 'p'"),
    COMMAND_CHAT_INSUFFICIENTRANK("&cYou must be at least moderator to enter this chat."),
    COMMAND_CHAT_DESCRIPTION("Change chat mode"),

    COMMAND_CHAT_MODE_PUBLIC("&ePublic chat mode."),
    COMMAND_CHAT_MODE_ALLIANCE("&eAlliance only chat mode."),
    COMMAND_CHAT_MODE_TRUCE("&eTruce only chat mode."),
    COMMAND_CHAT_MODE_FACTION("&eFaction only chat mode."),
    COMMAND_CHAT_MODE_MOD("&eMod only chat mode."),

    COMMAND_CHATSPY_ENABLE("&eYou have enabled chat spying mode."),
    COMMAND_CHATSPY_ENABLELOG(" has ENABLED chat spying mode."),
    COMMAND_CHATSPY_DISABLE("&eYou have disabled chat spying mode."),
    COMMAND_CHATSPY_DISABLELOG(" has DISABLED chat spying mode."),
    COMMAND_CHATSPY_DESCRIPTION("Enable admin chat spy mode"),

    COMMAND_CLAIM_INVALIDRADIUS("&cIf you specify a radius, it must be at least 1."),
    COMMAND_CLAIM_DENIED("&cYou do not have permission to claim in a radius."),
    COMMAND_CLAIM_DESCRIPTION("Claim land from where you are standing"),

    COMMAND_CLAIMLINE_INVALIDRADIUS("&cIf you specify a distance, it must be at least 1."),
    COMMAND_CLAIMLINE_DENIED("&cYou do not have permission to claim in a line."),
    COMMAND_CLAIMLINE_DESCRIPTION("Claim land in a straight line."),
    COMMAND_CLAIMLINE_ABOVEMAX("&cThe maximum limit for claim line is &c%s&c."),
    COMMAND_CLAIMLINE_NOTVALID("%s&c is not a cardinal direction. You may use &dnorth&c, &deast&c, &dsouth &cor &dwest&c."),

    COMMAND_COLEADER_CANDIDATES("Players you can promote: "),
    COMMAND_COLEADER_CLICKTOPROMOTE("Click to promote "),
    COMMAND_COLEADER_NOTMEMBER("%1$s&c is not a member in your faction."),
    COMMAND_COLEADER_NOTADMIN("&cYou are not the faction admin."),
    COMMAND_COLEADER_SELF("&cThe target player musn't be yourself."),
    COMMAND_COLEADER_TARGETISADMIN("&cThe target player is a faction admin. Demote them first."),
    COMMAND_COLEADER_REVOKES("&eYou have removed coleader status from %1$s&e."),
    COMMAND_COLEADER_REVOKED("%1$s&e is no longer coleader in your faction."),
    COMMAND_COLEADER_PROMOTES("%1$s&e was promoted to coleader in your faction."),
    COMMAND_COLEADER_PROMOTED("&eYou have promoted %1$s&e to coleader."),
    COMMAND_COLEADER_DESCRIPTION("Give or revoke coleader rights"),
    COMMAND_COLEADER_ALREADY_COLEADER("The faction already has a coleader. There can only be 1."),

    COMMAND_CONFIG_NOEXIST("&cNo configuration setting \"&d%1$s&c\" exists."),
    COMMAND_CONFIG_SET_TRUE("\" option set to true (enabled)."),
    COMMAND_CONFIG_SET_FALSE("\" option set to false (disabled)."),
    COMMAND_CONFIG_OPTIONSET("\" option set to "),
    COMMAND_CONFIG_COLOURSET("\" color option set to \""),
    COMMAND_CONFIG_INTREQUIRED("Cannot set \"%1$s\": An integer (whole number) value required."),
    COMMAND_CONFIG_LONGREQUIRED("Cannot set \"%1$s\": A long integer (whole number) value required."),
    COMMAND_CONFIG_DOUBLEREQUIRED("Cannot set \"%1$s\": A double (numeric) value required."),
    COMMAND_CONFIG_FLOATREQUIRED("Cannot set \"%1$s\": A float (numeric) value required."),
    COMMAND_CONFIG_INVALID_COLOUR("Cannot set \"%1$s\": \"%2$s\" is not a valid color."),
    COMMAND_CONFIG_INVALID_COLLECTION("\"%1$s\" is not a data collection type which can be modified with this command."),
    COMMAND_CONFIG_INVALID_MATERIAL("Cannot change \"%1$s\" set: \"%2$s\" is not a valid material."),
    COMMAND_CONFIG_INVALID_TYPESET("\"%1$s\" is not a data type set which can be modified with this command."),
    COMMAND_CONFIG_MATERIAL_ADDED("\"%1$s\" set: Material \"%2$s\" added."),
    COMMAND_CONFIG_MATERIAL_REMOVED("\"%1$s\" set: Material \"%2$s\" removed."),
    COMMAND_CONFIG_SET_ADDED("\"%1$s\" set: \"%2$s\" added."),
    COMMAND_CONFIG_SET_REMOVED("\"%1$s\" set: \"%2$s\" removed."),
    COMMAND_CONFIG_LOG(" (Command was run by %1$s.)"),
    COMMAND_CONFIG_ERROR_SETTING("Error setting configuration setting \"%1$s\" to \"%2$s\"."),
    COMMAND_CONFIG_ERROR_MATCHING("Configuration setting \"%1$s\" couldn't be matched, though it should be... please report this error."),
    COMMAND_CONFIG_ERROR_TYPE("'%1$s' is of type '%2$s', which cannot be modified with this command."),
    COMMAND_CONFIG_DESCRIPTION("Change a conf.json setting"),

    COMMAND_CONVERT_BACKEND_RUNNING("Already running that backend."),
    COMMAND_CONVERT_BACKEND_INVALID("Invalid backend"),
    COMMAND_CONVERT_DESCRIPTION("Convert the plugin backend"),

    COMMAND_CREATE_MUSTLEAVE("&cYou must leave your current faction first."),
    COMMAND_CREATE_INUSE("&cThat tag is already in use."),
    COMMAND_CREATE_TOCREATE("to create a new faction"),
    COMMAND_CREATE_FORCREATE("for creating a new faction"),
    COMMAND_CREATE_ERROR("&cThere was an internal error while trying to create your faction. Please try again."),
    COMMAND_CREATE_CREATED("%1$s&e created a new faction %2$s"),
    COMMAND_CREATE_YOUSHOULD("&eYou should now: %1$s"),
    COMMAND_CREATE_CREATEDLOG(" created a new faction: "),
    COMMAND_CREATE_DESCRIPTION("Create a new faction"),

    COMMAND_DEINVITE_CANDEINVITE("Players you can deinvite: "),
    COMMAND_DEINVITE_CLICKTODEINVITE("Click to revoke invite for %1$s"),
    COMMAND_DEINVITE_ALREADYMEMBER("%1$s&e is already a member of %2$s"),
    COMMAND_DEINVITE_MIGHTWANT("&eYou might want to: %1$s"),
    COMMAND_DEINVITE_REVOKED("%1$s&e revoked your invitation to &d%2$s&e."),
    COMMAND_DEINVITE_REVOKES("%1$s&e revoked %2$s's&e invitation."),
    COMMAND_DEINVITE_DESCRIPTION("Remove a pending invitation"),

    COMMAND_DELFWARP_DELETED("&eDeleted warp &6%1$s"),
    COMMAND_DELFWARP_INVALID("&eCouldn't find warp &6%1$s"),
    COMMAND_DELFWARP_TODELETE("to delete warp"),
    COMMAND_DELFWARP_FORDELETE("for deleting warp"),
    COMMAND_DELFWARP_DESCRIPTION("Delete a faction warp"),

    COMMAND_DESCRIPTION_CHANGES("You have changed the description for &d%1$s&e to:"),
    COMMAND_DESCRIPTION_CHANGED("&eThe faction %1$s&e changed their description to:"),
    COMMAND_DESCRIPTION_TOCHANGE("to change faction description"),
    COMMAND_DESCRIPTION_FORCHANGE("for changing faction description"),
    COMMAND_DESCRIPTION_DESCRIPTION("Change the faction description"),

    COMMAND_DISBAND_IMMUTABLE("&eYou cannot disband the Wilderness, SafeZone, or WarZone."),
    COMMAND_DISBAND_MARKEDPERMANENT("&eThis faction is designated as permanent, so you cannot disband it."),
    COMMAND_DISBAND_BROADCAST_YOURS("&d%1$s&e disbanded your faction."),
    COMMAND_DISBAND_BROADCAST_NOTYOURS("&d%1$s&e disbanded the faction %2$s."),
    COMMAND_DISBAND_HOLDINGS("&eYou have been given the disbanded faction's bank, totaling %1$s."),
    COMMAND_DISBAND_DESCRIPTION("Disband a faction"),

    COMMAND_FLY_DESCRIPTION("Enter or leave Faction flight mode"),
    COMMAND_FLY_CHANGE("&eFaction flight &d%1$s"),
    COMMAND_FLY_DAMAGE("&eFaction flight &ddisabled&e due to entering combat"),
    COMMAND_FLY_AUTO("&eFaction auto flight &d%1$s"),
    COMMAND_FLY_NO_ACCESS("&cCannot fly in territory of %1$s"),
    COMMAND_FLY_ENEMY_NEARBY("&cCannot enable fly, enemy nearby"),
    COMMAND_FLY_ENEMY_DISABLE("&cEnemy nearby, disabling fly"),

    COMMAND_FWARP_CLICKTOWARP("Click to warp!"),
    COMMAND_FWARP_COMMANDFORMAT("&e/f warp <warpname> [password]"),
    COMMAND_FWARP_WARPED("&eWarped to &6%1$s"),
    COMMAND_FWARP_INVALID_WARP("&eCouldn't find warp &6%1$s"),
    COMMAND_FWARP_TOWARP("to warp"),
    COMMAND_FWARP_FORWARPING("for warping"),
    COMMAND_FWARP_WARPS("Warps: "),
    COMMAND_FWARP_DESCRIPTION("Teleport to a faction warp"),
    COMMAND_FWARP_INVALID_PASSWORD("&4Invalid password!"),
    COMMAND_FWARP_PASSWORD_REQUIRED("&aPlease enter warp password. This wil not be visible to anyone else"),
    COMMAND_FWARP_PASSWORD_CANCEL("&cWarp canceled"),

    COMMAND_HELP_404("&cThis page does not exist"),
    COMMAND_HELP_NEXTCREATE("&eLearn how to create a faction on the next page."),
    COMMAND_HELP_INVITATIONS("command.help.invitations", "&eYou might want to close it and use invitations:"),
    COMMAND_HELP_HOME("&eAnd don't forget to set your home:"),
    COMMAND_HELP_BANK_1("&eYour faction has a bank which is used to pay for certain"),
    COMMAND_HELP_BANK_2("&ethings, so it will need to have money deposited into it."),
    COMMAND_HELP_BANK_3("&eTo learn more, use the money command."),
    COMMAND_HELP_PLAYERTITLES("&ePlayer titles are just for fun. No rules connected to them."),
    COMMAND_HELP_OWNERSHIP_1("&eClaimed land with ownership set is further protected so"),
    COMMAND_HELP_OWNERSHIP_2("&ethat only the owner(s), faction admin, and possibly the"),
    COMMAND_HELP_OWNERSHIP_3("&efaction moderators have full access."),
    COMMAND_HELP_RELATIONS_1("&eSet the relation you WISH to have with another faction."),
    COMMAND_HELP_RELATIONS_2("&eYour default relation with other factions will be neutral."),
    COMMAND_HELP_RELATIONS_3("&eIf BOTH factions choose \"ally\" you will be allies."),
    COMMAND_HELP_RELATIONS_4("&eIf ONE faction chooses \"enemy\" you will be enemies."),
    COMMAND_HELP_RELATIONS_5("&eYou can never hurt members or allies."),
    COMMAND_HELP_RELATIONS_6("&eYou can not hurt neutrals in their own territory."),
    COMMAND_HELP_RELATIONS_7("&eYou can always hurt enemies and players without faction."),
    COMMAND_HELP_RELATIONS_8(""),
    COMMAND_HELP_RELATIONS_9("&eDamage from enemies is reduced in your own territory."),
    COMMAND_HELP_RELATIONS_10("&eWhen you die you lose power. It is restored over time."),
    COMMAND_HELP_RELATIONS_11("&eThe power of a faction is the sum of all member power."),
    COMMAND_HELP_RELATIONS_12("&eThe power of a faction determines how much land it can hold."),
    COMMAND_HELP_RELATIONS_13("&eYou can claim land from factions with too little power."),
    COMMAND_HELP_PERMISSIONS_1("&eOnly faction members can build and destroy in their own"),
    COMMAND_HELP_PERMISSIONS_2("&eterritory. Usage of the following items is also restricted:"),
    COMMAND_HELP_PERMISSIONS_3("&eDoor, Chest, Furnace, Dispenser, Diode."),
    COMMAND_HELP_PERMISSIONS_4(""),
    COMMAND_HELP_PERMISSIONS_5("&eMake sure to put pressure plates in front of doors for your"),
    COMMAND_HELP_PERMISSIONS_6("&eguest visitors. Otherwise they can't get through. You can"),
    COMMAND_HELP_PERMISSIONS_7("&ealso use this to create member only areas."),
    COMMAND_HELP_PERMISSIONS_8("&eAs dispensers are protected, you can create traps without"),
    COMMAND_HELP_PERMISSIONS_9("&eworrying about those arrows getting stolen."),
    COMMAND_HELP_ADMIN_1("&b/f claim safezone &eclaim land for the Safe Zone"),
    COMMAND_HELP_ADMIN_2("&b/f claim warzone &eclaim land for the War Zone"),
    COMMAND_HELP_ADMIN_3("&b/f autoclaim [safezone|warzone] &etake a guess"),
    COMMAND_HELP_MOAR_1("Finally some commands for the server admins:"),
    COMMAND_HELP_MOAR_2("&eMore commands for server admins:"),
    COMMAND_HELP_MOAR_3("&eEven more commands for server admins:"),
    COMMAND_HELP_DESCRIPTION("Display a help page"),

    COMMAND_HOME_DISABLED("&cSorry, Faction homes are disabled on this server."),
    COMMAND_HOME_TELEPORTDISABLED("&cSorry, the ability to teleport to Faction homes is disabled on this server."),
    COMMAND_HOME_NOHOME("&cYour faction does not have a home. "),
    COMMAND_HOME_INENEMY("&cYou cannot teleport to your faction home while in the territory of an enemy faction."),
    COMMAND_HOME_WRONGWORLD("&cYou cannot teleport to your faction home while in a different world."),
    COMMAND_HOME_ENEMYNEAR("&cYou cannot teleport to your faction home while an enemy is within %s blocks of you."),
    COMMAND_HOME_TOTELEPORT("to teleport to your faction home"),
    COMMAND_HOME_FORTELEPORT("for teleporting to your faction home"),
    COMMAND_HOME_DESCRIPTION("Teleport to the faction home"),

    COMMAND_INVITE_TOINVITE("to invite someone"),
    COMMAND_INVITE_FORINVITE("for inviting someone"),
    COMMAND_INVITE_CLICKTOJOIN("Click to join!"),
    COMMAND_INVITE_INVITEDYOU(" has invited you to join "),
    COMMAND_INVITE_INVITED("%1$s&e invited %2$s&e to your faction."),
    COMMAND_INVITE_ALREADYMEMBER("%1$s&e is already a member of %2$s"),
    COMMAND_INVITE_DESCRIPTION("Invite a player to your faction"),
    COMMAND_INVITE_BANNED("&7%1$s &cis banned from your Faction. Not sending an invite."),

    COMMAND_JOIN_CANNOTFORCE("&cYou do not have permission to move other players into a faction."),
    COMMAND_JOIN_SYSTEMFACTION("&cPlayers may only join normal factions. This is a system faction."),
    COMMAND_JOIN_ALREADYMEMBER("&c%1$s %2$s already a member of %3$s"),
    COMMAND_JOIN_ATLIMIT(" &c!<white> The faction %1$s is at the limit of %2$d members, so %3$s cannot currently join."),
    COMMAND_JOIN_INOTHERFACTION("&c%1$s must leave %2$s current faction first."),
    COMMAND_JOIN_NEGATIVEPOWER("&c%1$s cannot join a faction with a negative power level."),
    COMMAND_JOIN_REQUIRESINVITATION("&eThis faction requires invitation."),
    COMMAND_JOIN_ATTEMPTEDJOIN("%1$s&e tried to join your faction."),
    COMMAND_JOIN_TOJOIN("to join a faction"),
    COMMAND_JOIN_FORJOIN("for joining a faction"),
    COMMAND_JOIN_SUCCESS("&e%1$s successfully joined %2$s."),
    COMMAND_JOIN_MOVED("&e%1$s moved you into the faction %2$s."),
    COMMAND_JOIN_JOINED("&e%1$s joined your faction."),
    COMMAND_JOIN_JOINEDLOG("%1$s joined the faction %2$s."),
    COMMAND_JOIN_MOVEDLOG("%1$s moved the player %2$s into the faction %3$s."),
    COMMAND_JOIN_DESCRIPTION("Join a faction"),
    COMMAND_JOIN_BANNED("&cYou are banned from %1$s &c:("),

    COMMAND_KICK_CANDIDATES("Players you can kick: "),
    COMMAND_KICK_CLICKTOKICK("Click to kick "),
    COMMAND_KICK_SELF("&cYou cannot kick yourself."),
    COMMAND_KICK_NONE("That player is not in a faction."),
    COMMAND_KICK_NOTMEMBER("%1$s&c is not a member of %2$s"),
    COMMAND_KICK_INSUFFICIENTRANK("&cYour rank is too low to kick this player."),
    COMMAND_KICK_NEGATIVEPOWER("&cYou cannot kick that member until their power is positive."),
    COMMAND_KICK_TOKICK("to kick someone from the faction"),
    COMMAND_KICK_FORKICK("for kicking someone from the faction"),
    COMMAND_KICK_FACTION("%1$s&e kicked %2$s&e from the faction! :O"), //message given to faction members
    COMMAND_KICK_KICKS("&eYou kicked %1$s&e from the faction %2$s&e!"), //kicker perspective
    COMMAND_KICK_KICKED("%1$s&e kicked you from %2$s&e! :O"), //kicked player perspective
    COMMAND_KICK_DESCRIPTION("Kick a player from the faction"),

    COMMAND_LIST_FACTIONLIST("Faction List "),
    COMMAND_LIST_TOLIST("to list the factions"),
    COMMAND_LIST_FORLIST("for listing the factions"),
    COMMAND_LIST_ONLINEFACTIONLESS("Online factionless: "),
    COMMAND_LIST_DESCRIPTION("See a list of the factions"),

    COMMAND_LOCK_LOCKED("&eFactions is now locked"),
    COMMAND_LOCK_UNLOCKED("&eFactions in now unlocked"),
    COMMAND_LOCK_DESCRIPTION("Lock all write stuff. Apparently."),

    COMMAND_LOGINS_TOGGLE("&eSet login / logout notifications for Faction members to: &6%s"),
    COMMAND_LOGINS_DESCRIPTION("Toggle(?) login / logout notifications for Faction members"),

    COMMAND_MAP_TOSHOW("to show the map"),
    COMMAND_MAP_FORSHOW("for showing the map"),
    COMMAND_MAP_UPDATE_ENABLED("&eMap auto update <green>ENABLED."),
    COMMAND_MAP_UPDATE_DISABLED("&eMap auto update <red>DISABLED."),
    COMMAND_MAP_DESCRIPTION("Show the territory map, and set optional auto update"),

    COMMAND_MAPHEIGHT_DESCRIPTION("&eUpdate the lines that /f map sends"),
    COMMAND_MAPHEIGHT_SET("&eSet /f map lines to &a%1$d"),
    COMMAND_MAPHEIGHT_CURRENT("&eCurrent mapheight: &a%1$d"),

    COMMAND_MOD_CANDIDATES("Players you can promote: "),
    COMMAND_MOD_CLICKTOPROMOTE("Click to promote "),
    COMMAND_MOD_NOTMEMBER("%1$s&c is not a member in your faction."),
    COMMAND_MOD_NOTADMIN("&cYou are not the faction admin."),
    COMMAND_MOD_SELF("&cThe target player musn't be yourself."),
    COMMAND_MOD_TARGETISADMIN("&cThe target player is a faction admin. Demote them first."),
    COMMAND_MOD_REVOKES("&eYou have removed moderator status from %1$s&e."),
    COMMAND_MOD_REVOKED("%1$s&e is no longer moderator in your faction."),
    COMMAND_MOD_PROMOTES("%1$s&e was promoted to moderator in your faction."),
    COMMAND_MOD_PROMOTED("&eYou have promoted %1$s&e to moderator."),
    COMMAND_MOD_DESCRIPTION("Give or revoke moderator rights"),

    COMMAND_MODIFYPOWER_ADDED("&eAdded &6%1$f &epower to &6%2$s. &eNew total rounded power: &6%3$d"),
    COMMAND_MODIFYPOWER_DESCRIPTION("Modify the power of a faction/player"),

    COMMAND_MONEY_LONG("&eThe faction money commands."),
    COMMAND_MONEY_DESCRIPTION("Faction money commands"),

    COMMAND_MONEYBALANCE_SHORT("show faction balance"),
    COMMAND_MONEYBALANCE_DESCRIPTION("Show your factions current money balance"),

    COMMAND_MONEYDEPOSIT_DESCRIPTION("Deposit money"),
    COMMAND_MONEYDEPOSIT_DEPOSITED("%1$s deposited %2$s in the faction bank: %3$s"),

    COMMAND_MONEYTRANSFERFF_DESCRIPTION("Transfer f -> f"),
    COMMAND_MONEYTRANSFERFF_TRANSFER("%1$s transferred %2$s from the faction \"%3$s\" to the faction \"%4$s\""),

    COMMAND_MONEYTRANSFERFP_DESCRIPTION("Transfer f -> p"),
    COMMAND_MONEYTRANSFERFP_TRANSFER("%1$s transferred %2$s from the faction \"%3$s\" to the player \"%4$s\""),

    COMMAND_MONEYTRANSFERPF_DESCRIPTION("Transfer p -> f"),
    COMMAND_MONEYTRANSFERPF_TRANSFER("%1$s transferred %2$s from the player \"%3$s\" to the faction \"%4$s\""),

    COMMAND_MONEYWITHDRAW_DESCRIPTION("Withdraw money"),
    COMMAND_MONEYWITHDRAW_WITHDRAW("%1$s withdrew %2$s from the faction bank: %3$s"),

    COMMAND_NEAR_DESCRIPTION("Show nearby faction members"),
    COMMAND_NEAR_PLAYER("&a&l{role-prefix}&8{name} (&d{distance}m&8)"),
    COMMAND_NEAR_PLAYERLIST("&eNear: {players-nearby}"),
    COMMAND_NEAR_NONE("&8None"),

    COMMAND_OPEN_TOOPEN("to open or close the faction"),
    COMMAND_OPEN_FOROPEN("for opening or closing the faction"),
    COMMAND_OPEN_OPEN("open"),
    COMMAND_OPEN_CLOSED("closed"),
    COMMAND_OPEN_CHANGES("%1$s&e changed the faction to &d%2$s&e."),
    COMMAND_OPEN_CHANGED("&eThe faction %1$s&e is now %2$s"),
    COMMAND_OPEN_DESCRIPTION("Switch if invitation is required to join"),

    COMMAND_OWNER_DISABLED("&cSorry, but owned areas are disabled on this server."),
    COMMAND_OWNER_LIMIT("&cSorry, but you have reached the server's &dlimit of %1$d &cowned areas per faction."),
    COMMAND_OWNER_WRONGFACTION("&cThis land is not claimed by your faction, so you can't set ownership of it."),
    COMMAND_OWNER_NOTCLAIMED("&cThis land is not claimed by a faction. Ownership is not possible."),
    COMMAND_OWNER_NOTMEMBER("%1$s&e is not a member of this faction."),
    COMMAND_OWNER_CLEARED("&eYou have cleared ownership for this claimed area."),
    COMMAND_OWNER_REMOVED("&eYou have removed ownership of this claimed land from %1$s&e."),
    COMMAND_OWNER_TOSET("to set ownership of claimed land"),
    COMMAND_OWNER_FORSET("for setting ownership of claimed land"),
    COMMAND_OWNER_ADDED("&eYou have added %1$s&e to the owner list for this claimed land."),
    COMMAND_OWNER_DESCRIPTION("Set ownership of claimed land"),

    COMMAND_OWNERLIST_DISABLED("&cSorry, but owned areas are disabled on this server."),//dup->
    COMMAND_OWNERLIST_WRONGFACTION("&cThis land is not claimed by your faction."),//eq
    COMMAND_OWNERLIST_NOTCLAIMED("&eThis land is not claimed by any faction, thus no owners."),//eq
    COMMAND_OWNERLIST_NONE("&eNo owners are set here; everyone in the faction has access."),
    COMMAND_OWNERLIST_OWNERS("&eCurrent owner(s) of this land: %1$s"),
    COMMAND_OWNERLIST_DESCRIPTION("List owner(s) of this claimed land"),

    COMMAND_PEACEFUL_DESCRIPTION("Set a faction to peaceful"),
    COMMAND_PEACEFUL_YOURS("%1$s has %2$s your faction"),
    COMMAND_PEACEFUL_OTHER("%s&e has %s the faction '%s&e'."),
    COMMAND_PEACEFUL_GRANT("granted peaceful status to"),
    COMMAND_PEACEFUL_REVOKE("removed peaceful status from"),

    COMMAND_PERM_DESCRIPTION("&6Edit or list your Faction's permissions."),
    COMMAND_PERM_INVALID_RELATION("Invalid relation defined. Try something like 'ally'"),
    COMMAND_PERM_INVALID_ACCESS("Invalid access defined. Try something like 'allow'"),
    COMMAND_PERM_INVALID_ACTION("Invalid action defined. Try something like 'build'"),
    COMMAND_PERM_SET("&aSet permission &e%1$s &ato &b%2$s &afor relation &c%3$s"),
    COMMAND_PERM_TOP("RCT MEM OFF ALLY TRUCE NEUT ENEMY"),

    COMMAND_PERMANENT_DESCRIPTION("Toggles a faction's permanence"), //TODO: Real word?
    COMMAND_PERMANENT_GRANT("added permanent status to"),
    COMMAND_PERMANENT_REVOKE("removed permanent status from"),
    COMMAND_PERMANENT_YOURS("%1$s has %2$s your faction"),
    COMMAND_PERMANENT_OTHER("%s&e has %s the faction '%s&e'."),
    COMMAND_PROMOTE_TARGET("You've been %1$s to %2$s"),
    COMMAND_PROMOTE_SUCCESS("You successfully %1$s %2$s to %3$s"),
    COMMAND_PROMOTE_PROMOTED("promoted"),
    COMMAND_PROMOTE_DEMOTED("demoted"),

    COMMAND_PERMANENTPOWER_DESCRIPTION("Toggle faction power permanence"), //TODO: This a real word?
    COMMAND_PERMANENTPOWER_GRANT("added permanentpower status to"),
    COMMAND_PERMANENTPOWER_REVOKE("removed permanentpower status from"),
    COMMAND_PERMANENTPOWER_SUCCESS("&eYou %s &d%s&e."),
    COMMAND_PERMANENTPOWER_FACTION("%s&e %s your faction"),

    COMMAND_PROMOTE_DESCRIPTION("/f promote <name>"),
    COMMAND_PROMOTE_WRONGFACTION("%1$s is not part of your faction."),
    COMMAND_NOACCESS("You don't have access to that."),
    COMMAND_PROMOTE_NOTTHATPLAYER("That player cannot be promoted."),
    COMMAND_PROMOTE_NOT_ALLOWED("You can't promote or demote that player."),

    COMMAND_POWER_TOSHOW("to show player power info"),
    COMMAND_POWER_FORSHOW("for showing player power info"),
    COMMAND_POWER_POWER("%1$s&6 - Power / Maxpower: &e%2$d / %3$d %4$s"),
    COMMAND_POWER_BONUS(" (bonus: "),
    COMMAND_POWER_PENALTY(" (penalty: "),
    COMMAND_POWER_DESCRIPTION("Show player power info"),

    COMMAND_POWERBOOST_HELP_1("&cYou must specify \"p\" or \"player\" to target a player or \"f\" or \"faction\" to target a faction."),
    COMMAND_POWERBOOST_HELP_2("&cex. /f powerboost p SomePlayer 0.5  -or-  /f powerboost f SomeFaction -5"),
    COMMAND_POWERBOOST_INVALIDNUM("&cYou must specify a valid numeric value for the power bonus/penalty amount."),
    COMMAND_POWERBOOST_PLAYER("Player \"%1$s\""),
    COMMAND_POWERBOOST_FACTION("Faction \"%1$s\""),
    COMMAND_POWERBOOST_BOOST("&e%1$s now has a power bonus/penalty of %2$d to min and max power levels."),
    COMMAND_POWERBOOST_BOOSTLOG("%1$s has set the power bonus/penalty for %2$s to %3$d."),
    COMMAND_POWERBOOST_DESCRIPTION("Apply permanent power bonus/penalty to specified player or faction"),

    COMMAND_RELATIONS_ALLTHENOPE("&cNope! You can't."),
    COMMAND_RELATIONS_MORENOPE("&cNope! You can't declare a relation to yourself :)"),
    COMMAND_RELATIONS_ALREADYINRELATIONSHIP("&cYou already have that relation wish set with %1$s."),
    COMMAND_RELATIONS_TOMARRY("to change a relation wish"),
    COMMAND_RELATIONS_FORMARRY("for changing a relation wish"),
    COMMAND_RELATIONS_MUTUAL("&eYour faction is now %1$s&e to %2$s"),
    COMMAND_RELATIONS_PEACEFUL("&eThis will have no effect while your faction is peaceful."),
    COMMAND_RELATIONS_PEACEFULOTHER("&eThis will have no effect while their faction is peaceful."),
    COMMAND_RELATIONS_DESCRIPTION("Set relation wish to another faction"),
    COMMAND_RELATIONS_EXCEEDS_ME("&eFailed to set relation wish. You can only have %1$s %2$s."),
    COMMAND_RELATIONS_EXCEEDS_THEY("&eFailed to set relation wish. They can only have %1$s %2$s."),

    COMMAND_RELATIONS_PROPOSAL_1("%1$s&e wishes to be your %2$s"),
    COMMAND_RELATIONS_PROPOSAL_2("&eType &b/%1$s %2$s %3$s&e to accept."),
    COMMAND_RELATIONS_PROPOSAL_SENT("%1$s&e were informed that you wish to be %2$s"),

    COMMAND_RELOAD_TIME("&eReloaded &dall configuration files &efrom disk, took &d%1$d ms&e."),
    COMMAND_RELOAD_DESCRIPTION("Reload data file(s) from disk"),

    COMMAND_SAFEUNCLAIMALL_DESCRIPTION("Unclaim all safezone land"),
    COMMAND_SAFEUNCLAIMALL_UNCLAIMED("&eYou unclaimed ALL safe zone land."),
    COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG("%1$s unclaimed all safe zones."),

    COMMAND_SAVEALL_SUCCESS("&eFactions saved to disk!"),
    COMMAND_SAVEALL_DESCRIPTION("Save all data to disk"),

    COMMAND_SCOREBOARD_DESCRIPTION("Scoreboardy things"),

    COMMAND_SETDEFAULTROLE_DESCRIPTION("/f defaultrole <role> - set your Faction's default role."),
    COMMAND_SETDEFAULTROLE_NOTTHATROLE("You cannot set the default to admin."),
    COMMAND_SETDEFAULTROLE_SUCCESS("Set default role of your faction to %1$s"),
    COMMAND_SETDEFAULTROLE_INVALIDROLE("Couldn't find matching role for %1$s"),

    COMMAND_SETFWARP_NOTCLAIMED("&eYou can only set warps in your faction territory."),
    COMMAND_SETFWARP_LIMIT("&eYour Faction already has the max amount of warps set &6(%1$d)."),
    COMMAND_SETFWARP_SET("&eSet warp &6%1$s and password &b'%2$s' &eto your location."),
    COMMAND_SETFWARP_TOSET("to set warp"),
    COMMAND_SETFWARP_FORSET("for setting warp"),
    COMMAND_SETFWARP_DESCRIPTION("Set a faction warp"),

    COMMAND_SETHOME_DISABLED("&cSorry, Faction homes are disabled on this server."),
    COMMAND_SETHOME_NOTCLAIMED("&cSorry, your faction home can only be set inside your own claimed territory."),
    COMMAND_SETHOME_TOSET("to set the faction home"),
    COMMAND_SETHOME_FORSET("for setting the faction home"),
    COMMAND_SETHOME_SET("%1$s&e set the home for your faction. You can now use:"),
    COMMAND_SETHOME_SETOTHER("&cYou have set the home for the %1$s&e faction."),
    COMMAND_SETHOME_DESCRIPTION("Set the faction home"),

    COMMAND_SETMAXVAULTS_DESCRIPTION("Set max vaults for a Faction."),
    COMMAND_SETMAXVAULTS_SUCCESS("&aSet max vaults for &e%s &ato &b%d"),

    COMMAND_VAULT_DESCRIPTION("/f vault <number> to open one of your Faction's vaults."),
    COMMAND_VAULT_TOOHIGH("&cYou tried to open vault %d but your Faction only has %d vaults."),

    COMMAND_SEECHUNK_DESCRIPTION("Show chunk boundaries"),
    COMMAND_SEECHUNK_TOGGLE("&eSeechunk &d%1$s"),

    COMMAND_SHOW_NOFACTION_SELF("You are not in a faction"),
    COMMAND_SHOW_NOFACTION_OTHER("That's not a faction"),
    COMMAND_SHOW_TOSHOW("to show faction information"),
    COMMAND_SHOW_FORSHOW("for showing faction information"),
    COMMAND_SHOW_DESCRIPTION("&6Description: &e%1$s"),
    COMMAND_SHOW_PEACEFUL("This faction is Peaceful"),
    COMMAND_SHOW_PERMANENT("&6This faction is permanent, remaining even with no members."),
    COMMAND_SHOW_JOINING("&6Joining: &e%1$s "),
    COMMAND_SHOW_INVITATION("invitation is required"),
    COMMAND_SHOW_UNINVITED("no invitation is needed"),
    COMMAND_SHOW_NOHOME("n/a"),
    COMMAND_SHOW_POWER("&6Land / Power / Maxpower: &e %1$d/%2$d/%3$d %4$s."),
    COMMAND_SHOW_BONUS(" (bonus: "),
    COMMAND_SHOW_PENALTY(" (penalty: "),
    COMMAND_SHOW_DEPRECIATED("(%1$s depreciated)"), //This is spelled correctly.
    COMMAND_SHOW_LANDVALUE("&6Total land value: &e%1$s %2$s"),
    COMMAND_SHOW_BANKCONTAINS("&6Bank contains: &e%1$s"),
    COMMAND_SHOW_ALLIES("Allies: "),
    COMMAND_SHOW_ENEMIES("Enemies: "),
    COMMAND_SHOW_MEMBERSONLINE("Members online: "),
    COMMAND_SHOW_MEMBERSOFFLINE("Members offline: "),
    COMMAND_SHOW_COMMANDDESCRIPTION("Show faction information"),
    COMMAND_SHOW_DEATHS_TIL_RAIDABLE("&eDTR: %1$d"),
    COMMAND_SHOW_EXEMPT("&cThis faction is exempt and cannot be seen."),

    COMMAND_SHOWINVITES_PENDING("Players with pending invites: "),
    COMMAND_SHOWINVITES_CLICKTOREVOKE("Click to revoke invite for %1$s"),
    COMMAND_SHOWINVITES_DESCRIPTION("Show pending faction invites"),

    COMMAND_STATUS_FORMAT("%1$s Power: %2$s Last Seen: %3$s"),
    COMMAND_STATUS_ONLINE("Online"),
    COMMAND_STATUS_AGOSUFFIX(" ago."),
    COMMAND_STATUS_DESCRIPTION("Show the status of a player"),

    COMMAND_STUCK_TIMEFORMAT("m 'minutes', s 'seconds.'"),
    COMMAND_STUCK_CANCELLED("&6Teleport cancelled because you were damaged"),
    COMMAND_STUCK_OUTSIDE("&6Teleport cancelled because you left &e%1$d &6block radius"),
    COMMAND_STUCK_EXISTS("&6You are already teleporting, you must wait &e%1$s"),
    COMMAND_STUCK_START("&6Teleport will commence in &e%s&6. Don't take or deal damage. "),
    COMMAND_STUCK_TELEPORT("&6Teleported safely to %1$d, %2$d, %3$d."),
    COMMAND_STUCK_TOSTUCK("to safely teleport %1$s out"),
    COMMAND_STUCK_FORSTUCK("for %1$s initiating a safe teleport out"),
    COMMAND_STUCK_DESCRIPTION("Safely teleports you out of enemy faction"),

    COMMAND_TAG_TAKEN("&cThat tag is already taken"),
    COMMAND_TAG_TOCHANGE("to change the faction tag"),
    COMMAND_TAG_FORCHANGE("for changing the faction tag"),
    COMMAND_TAG_FACTION("%1$s&e changed your faction tag to %2$s"),
    COMMAND_TAG_CHANGED("&eThe faction %1$s&e changed their name to %2$s."),
    COMMAND_TAG_DESCRIPTION("Change the faction tag"),

    COMMAND_TITLE_TOCHANGE("to change a players title"),
    COMMAND_TITLE_FORCHANGE("for changing a players title"),
    COMMAND_TITLE_CHANGED("%1$s&e changed a title: %2$s"),
    COMMAND_TITLE_DESCRIPTION("Set or remove a players title"),

    COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION("Toggles whether or not you will see alliance chat"),
    COMMAND_TOGGLEALLIANCECHAT_IGNORE("Alliance chat is now ignored"),
    COMMAND_TOGGLEALLIANCECHAT_UNIGNORE("Alliance chat is no longer ignored"),

    COMMAND_TOGGLESB_DISABLED("You can't toggle scoreboards while they are disabled."),

    COMMAND_TOP_DESCRIPTION("Sort Factions to see the top of some criteria."),
    COMMAND_TOP_TOP("Top Factions by %s. Page %d/%d"),
    COMMAND_TOP_LINE("%d. &6%s: &c%s"), // Rank. Faction: Value
    COMMAND_TOP_INVALID("Could not sort by %s. Try balance, online, members, power or land."),

    COMMAND_UNBAN_DESCRIPTION("Unban someone from your Faction"),
    COMMAND_UNBAN_NOTBANNED("&7%s &cisn't banned. Not doing anything."),
    COMMAND_UNBAN_UNBANNED("&e%1$s &cunbanned &7%2$s"),
    COMMAND_UNBAN_TARGET("&aYou were unbanned from &r%s"),

    COMMAND_UNCLAIM_SAFEZONE_SUCCESS("&eSafe zone was unclaimed."),
    COMMAND_UNCLAIM_SAFEZONE_NOPERM("&cThis is a safe zone. You lack permissions to unclaim."),
    COMMAND_UNCLAIM_WARZONE_SUCCESS("&eWar zone was unclaimed."),
    COMMAND_UNCLAIM_WARZONE_NOPERM("&cThis is a war zone. You lack permissions to unclaim."),
    COMMAND_UNCLAIM_UNCLAIMED("%1$s&e unclaimed some of your land."),
    COMMAND_UNCLAIM_UNCLAIMS("&eYou unclaimed this land."),
    COMMAND_UNCLAIM_LOG("%1$s unclaimed land at (%2$s) from the faction: %3$s"),
    COMMAND_UNCLAIM_WRONGFACTION("&cYou don't own this land."),
    COMMAND_UNCLAIM_TOUNCLAIM("to unclaim this land"),
    COMMAND_UNCLAIM_FORUNCLAIM("for unclaiming this land"),
    COMMAND_UNCLAIM_FACTIONUNCLAIMED("%1$s&e unclaimed some land."),
    COMMAND_UNCLAIM_DESCRIPTION("Unclaim the land where you are standing"),

    COMMAND_UNCLAIMALL_TOUNCLAIM("to unclaim all faction land"),
    COMMAND_UNCLAIMALL_FORUNCLAIM("for unclaiming all faction land"),
    COMMAND_UNCLAIMALL_UNCLAIMED("%1$s&e unclaimed ALL of your faction's land."),
    COMMAND_UNCLAIMALL_LOG("%1$s unclaimed everything for the faction: %2$s"),
    COMMAND_UNCLAIMALL_DESCRIPTION("Unclaim all of your factions land"),

    COMMAND_VERSION_VERSION("&eYou are running %1$s"),
    COMMAND_VERSION_DESCRIPTION("Show plugin and translation version information"),

    COMMAND_WARUNCLAIMALL_DESCRIPTION("Unclaim all warzone land"),
    COMMAND_WARUNCLAIMALL_SUCCESS("&eYou unclaimed ALL war zone land."),
    COMMAND_WARUNCLAIMALL_LOG("%1$s unclaimed all war zones."),


    /**
     * Leaving - This is accessed through a command, and so it MAY need a COMMAND_* slug :s
     */
    LEAVE_PASSADMIN("&cYou must give the admin role to someone else first."),
    LEAVE_NEGATIVEPOWER("&cYou cannot leave until your power is positive."),
    LEAVE_TOLEAVE("to leave your faction."),
    LEAVE_FORLEAVE("for leaving your faction."),
    LEAVE_LEFT("%s&e left faction %s&e."),
    LEAVE_DISBANDED("&e%s&e was disbanded."),
    LEAVE_DISBANDEDLOG("The faction %s (%s) was disbanded due to the last player (%s) leaving."),
    LEAVE_DESCRIPTION("Leave your faction"),

    /**
     * Claiming - Same as above basically. No COMMAND_* because it's not in a command class, but...
     */
    CLAIM_PROTECTED("&cThis land is protected"),
    CLAIM_DISABLED("&cSorry, this world has land claiming disabled."),
    CLAIM_CANTCLAIM("&cYou can't claim land for &d%s&c."),
    CLAIM_ALREADYOWN("%s&e already own this land."),
    CLAIM_MUSTBE("&cYou must be &d%s&c to claim land."),
    CLAIM_MEMBERS("Factions must have at least &d%s&c members to claim land."),
    CLAIM_SAFEZONE("&cYou can not claim a Safe Zone."),
    CLAIM_WARZONE("&cYou can not claim a War Zone."),
    CLAIM_POWER("&cYou can't claim more land! You need more power!"),
    CLAIM_LIMIT("&cLimit reached. You can't claim more land!"),
    CLAIM_ALLY("&cYou can't claim the land of your allies."),
    CLAIM_CONTIGIOUS("&cYou can only claim additional land which is connected to your first claim or controlled by another faction!"),
    CLAIM_FACTIONCONTIGUOUS("&cYou can only claim additional land which is connected to your first claim!"),
    CLAIM_PEACEFUL("%s&e owns this land. Your faction is peaceful, so you cannot claim land from other factions."),
    CLAIM_PEACEFULTARGET("%s&e owns this land, and is a peaceful faction. You cannot claim land from them."),
    CLAIM_THISISSPARTA("%s&e owns this land and is strong enough to keep it."),
    CLAIM_BORDER("&cYou must start claiming land at the border of the territory."),
    CLAIM_TOCLAIM("to claim this land"),
    CLAIM_FORCLAIM("for claiming this land"),
    CLAIM_TOOVERCLAIM("to overclaim this land"),
    CLAIM_FOROVERCLAIM("for over claiming this land"),
    CLAIM_CLAIMED("&d%s&e claimed land for &d%s&e from &d%s&e."),
    CLAIM_CLAIMEDLOG("%s claimed land at (%s) for the faction: %s"),
    CLAIM_OVERCLAIM_DISABLED("&eOver claiming is disabled on this server."),
    CLAIM_TOOCLOSETOOTHERFACTION("&eYour claim is too close to another Faction. Buffer required is %d"),
    CLAIM_OUTSIDEWORLDBORDER("&eYour claim is outside the border."),
    CLAIM_OUTSIDEBORDERBUFFER("&eYour claim is outside the border. %d chunks away world edge required."),
    CLAIM_CLICK_TO_CLAIM("Click to try to claim &2(%1$d, %2$d)"),
    CLAIM_YOUAREHERE("You are here"),

    /**
     * More generic, or less easily categorisable translations, which may apply to more than one class
     */
    GENERIC_YOU("you"),
    GENERIC_YOURFACTION("your faction"),
    GENERIC_NOPERMISSION("&cYou don't have permission to %1$s."),
    GENERIC_DOTHAT("do that"),  //Ugh nuke this from high orbit
    GENERIC_NOPLAYERMATCH("&cNo player match found for \"&3%1$s&c\"."),
    GENERIC_NOPLAYERFOUND("&cNo player \"&3%1$s&c\" could not be found."),
    GENERIC_NOFACTIONMATCH("&cNo player match found for \"&3%1$s&c\"."),
    GENERIC_ARGS_TOOFEW("&cToo few arguments. &eUse like this:"),
    GENERIC_ARGS_TOOMANY("&cStrange argument \"&3%1$s&c\". &eUse the command like this:"),
    GENERIC_DEFAULTDESCRIPTION("Default faction description :("),
    GENERIC_OWNERS("Owner(s): %1$s"),
    GENERIC_PUBLICLAND("Public faction land."),
    GENERIC_FACTIONLESS("factionless"),
    GENERIC_SERVERADMIN("A server admin"),
    GENERIC_DISABLED("disabled"),
    GENERIC_ENABLED("enabled"),
    GENERIC_INFINITY("∞"),
    GENERIC_CONSOLEONLY("This command cannot be run as a player."),
    GENERIC_PLAYERONLY("&cThis command can only be used by ingame players."),
    GENERIC_MEMBERONLY("&cYou are not member of any faction."),
    GENERIC_ASKYOURLEADER("&e Ask your leader to:"),
    GENERIC_YOUSHOULD("&eYou should:"),
    GENERIC_YOUMAYWANT("&eYou may want to: "),
    GENERIC_TRANSLATION_VERSION("Translation: %1$s(%2$s,%3$s) State: %4$s"),
    GENERIC_TRANSLATION_CONTRIBUTORS("Translation contributors: %1$s"),
    GENERIC_TRANSLATION_RESPONSIBLE("Responsible for translation: %1$s"),
    GENERIC_FACTIONTAG_TOOSHORT("&eThe faction tag can't be shorter than &d%1$s&e chars."),
    GENERIC_FACTIONTAG_TOOLONG("&eThe faction tag can't be longer than &d%s&e chars."),
    GENERIC_FACTIONTAG_ALPHANUMERIC("&eFaction tag must be alphanumeric. \"&d%s&e\" is not allowed."),
    GENERIC_PLACEHOLDER("<This is a placeholder for a message you should not see>"),

    /**
     * Clip placeholder stuff
     */
    PLACEHOLDER_ROLE_NAME("None"),

    /**
     * ASCII compass (for chat map)
     */
    COMPASS_SHORT_NORTH("N"),
    COMPASS_SHORT_EAST("E"),
    COMPASS_SHORT_SOUTH("S"),
    COMPASS_SHORT_WEST("W"),

    /**
     * Chat modes
     */
    CHAT_MOD("mod chat"),
    CHAT_FACTION("faction chat"),
    CHAT_ALLIANCE("alliance chat"),
    CHAT_TRUCE("truce chat"),
    CHAT_PUBLIC("public chat"),

    /**
     * Economy stuff
     */

    ECON_OFF("no %s"), // no balance, no value, no refund, etc
    ECON_FORMAT("###,###.###"),
    ECON_DISABLED("Faction econ is disabled."),
    ECON_OVER_BAL_CAP("&4The amount &e%s &4is over Essentials' balance cap."),

    /**
     * Relations
     */
    RELATION_MEMBER_SINGULAR("member"),
    RELATION_MEMBER_PLURAL("members"),
    RELATION_ALLY_SINGULAR("ally"),
    RELATION_ALLY_PLURAL("allies"),
    RELATION_TRUCE_SINGULAR("truce"),
    RELATION_TRUCE_PLURAL("truces"),
    RELATION_NEUTRAL_SINGULAR("neutral"),
    RELATION_NEUTRAL_PLURAL("neutrals"),
    RELATION_ENEMY_SINGULAR("enemy"),
    RELATION_ENEMY_PLURAL("enemies"),

    /**
     * Roles
     */
    ROLE_ADMIN("admin"),
    ROLE_COLEADER("coleader"),
    ROLE_MODERATOR("moderator"),
    ROLE_NORMAL("normal member"),
    ROLE_RECRUIT("recruit"),

    /**
     * Region types.
     */
    REGION_SAFEZONE("safezone"),
    REGION_WARZONE("warzone"),
    REGION_WILDERNESS("wilderness"),

    REGION_PEACEFUL("peaceful territory"),
    /**
     * In the player and entity listeners
     */
    PLAYER_CANTHURT("&eYou may not harm other players in %s"),
    PLAYER_SAFEAUTO("&eThis land is now a safe zone."),
    PLAYER_WARAUTO("&eThis land is now a war zone."),
    PLAYER_OUCH("&cOuch, that is starting to hurt. You should give it a rest."),
    PLAYER_USE_WILDERNESS("&cYou can't use &d%s&c in the wilderness."),
    PLAYER_USE_SAFEZONE("&cYou can't use &d%s&c in a safe zone."),
    PLAYER_USE_WARZONE("&cYou can't use &d%s&c in a war zone."),
    PLAYER_USE_TERRITORY("&cYou can't &d%s&c in the territory of &d%s&c."),
    PLAYER_USE_OWNED("&cYou can't use &d%s&c in this territory, it is owned by: %s&c."),
    PLAYER_COMMAND_WARZONE("&cYou can't use the command '%s' in warzone."),
    PLAYER_COMMAND_NEUTRAL("&cYou can't use the command '%s' in neutral territory."),
    PLAYER_COMMAND_ENEMY("&cYou can't use the command '%s' in enemy territory."),
    PLAYER_COMMAND_PERMANENT("&cYou can't use the command '%s' because you are in a permanent faction."),
    PLAYER_COMMAND_ALLY("&cYou can't use the command '%s' in ally territory."),
    PLAYER_COMMAND_WILDERNESS("&cYou can't use the command '%s' in the wilderness."),

    PLAYER_POWER_NOLOSS_PEACEFUL("&eYou didn't lose any power since you are in a peaceful faction."),
    PLAYER_POWER_NOLOSS_WORLD("&eYou didn't lose any power due to the world you died in."),
    PLAYER_POWER_NOLOSS_WILDERNESS("&eYou didn't lose any power since you were in the wilderness."),
    PLAYER_POWER_NOLOSS_WARZONE("&eYou didn't lose any power since you were in a war zone."),
    PLAYER_POWER_LOSS_WARZONE("&cThe world you are in has power loss normally disabled, but you still lost power since you were in a war zone.\n&eYour power is now &d%d / %d"),
    PLAYER_POWER_NOW("&eYour power is now &d%d / %d"),

    PLAYER_PVP_LOGIN("&eYou can't hurt other players for %d seconds after logging in."),
    PLAYER_PVP_REQUIREFACTION("&eYou can't hurt other players until you join a faction."),
    PLAYER_PVP_FACTIONLESS("&eYou can't hurt players who are not currently in a faction."),
    PLAYER_PVP_PEACEFUL("&ePeaceful players cannot participate in combat."),
    PLAYER_PVP_NEUTRAL("&eYou can't hurt neutral factions. Declare them as an enemy."),
    PLAYER_PVP_CANTHURT("&eYou can't hurt %s&e."),

    PLAYER_PVP_NEUTRALFAIL("&eYou can't hurt %s&e in their own territory unless you declare them as an enemy."),
    PLAYER_PVP_TRIED("%s&e tried to hurt you."),

    /**
     * Strings lying around in other bits of the plugins
     */
    NOPAGES("&eSorry. No Pages available."),
    INVALIDPAGE("&eInvalid page. Must be between 1 and %1$d"),

    /**
     * The ones here before I started messing around with this
     */
    TITLE("title", "&bFactions &0|&r"),
    WILDERNESS("wilderness", "&2Wilderness"),
    WILDERNESS_DESCRIPTION("wilderness-description", ""),
    WARZONE("warzone", "&4Warzone"),
    WARZONE_DESCRIPTION("warzone-description", "Not the safest place to be."),
    SAFEZONE("safezone", "&6Safezone"),
    SAFEZONE_DESCRIPTION("safezone-description", "Free from pvp and monsters."),
    TOGGLE_SB("toggle-sb", "You now have scoreboards set to {value}"),
    FACTION_LEAVE("faction-leave", "&6Leaving %1$s, &6Entering %2$s"),
    FACTION_ENTER_TITLE("faction-enter-title", ""),
    FACTION_ENTER_SUBTITLE("faction-enter-subtitle", "%s"),
    FACTIONS_ANNOUNCEMENT_TOP("faction-announcement-top", "&d--Unread Faction Announcements--"),
    FACTIONS_ANNOUNCEMENT_BOTTOM("faction-announcement-bottom", "&d--Unread Faction Announcements--"),
    DEFAULT_PREFIX("default-prefix", "{relationcolor}[{faction}] &r"),
    FACTION_LOGIN("faction-login", "&e%1$s &9logged in."),
    FACTION_LOGOUT("faction-logout", "&e%1$s &9logged out.."),
    NOFACTION_PREFIX("nofactions-prefix", "&6[&a4-&6]&r"),
    DATE_FORMAT("date-format", "MM/d/yy h:ma"), // 3/31/15 07:49AM

    /**
     * Raidable is used in multiple places. Allow more than just true/false.
     */
    RAIDABLE_TRUE("raidable-true", "true"),
    RAIDABLE_FALSE("raidable-false", "false"),
    /**
     * Warmups
     */
    WARMUPS_NOTIFY_FLIGHT("&eFlight will enable in &d%2$d &eseconds."),
    WARMUPS_NOTIFY_TELEPORT("&eYou will teleport to &d%1$s &ein &d%2$d &eseconds."),
    WARMUPS_ALREADY("&cYou are already warming up."),
    WARMUPS_CANCELLED("&cYou have cancelled your warmup.");

    private String path;
    private String def;
    private static YamlConfiguration LANG;
    public static SimpleDateFormat sdf;

    /**
     * Lang enum constructor.
     *
     * @param path  The string path.
     * @param start The default string.
     */
    TL(String path, String start) {
        this.path = path;
        this.def = start;
    }

    /**
     * Lang enum constructor. Use this when your desired path simply exchanges '_' for '.'
     *
     * @param start The default string.
     */
    TL(String start) {
        this.path = this.name().replace('_', '.');
        if (this.path.startsWith(".")) {
            path = "root" + path;
        }
        this.def = start;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
        sdf = new SimpleDateFormat(DATE_FORMAT.toString());
    }

    @Override
    public String toString() {
        return this == TITLE ? ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)) + " " : ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }

    public String format(Object... args) {
        return String.format(toString(), args);
    }

    /**
     * Get the default value of the path.
     *
     * @return The default value of the path.
     */
    public String getDefault() {
        return this.def;
    }

    /**
     * Get the path to the string.
     *
     * @return The path to the string.
     */
    public String getPath() {
        return this.path;
    }
}
