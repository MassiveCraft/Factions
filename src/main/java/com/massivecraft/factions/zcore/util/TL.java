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
    COMMAND_ADMIN_NOTMEMBER("%1$s<i> is not a member in your faction."),
    COMMAND_ADMIN_NOTADMIN("<b>You are not the faction admin."),
    COMMAND_ADMIN_TARGETSELF("<b>The target player musn't be yourself."),
    COMMAND_ADMIN_DEMOTES("<i>You have demoted %1$s<i> from the position of faction admin."),
    COMMAND_ADMIN_DEMOTED("<i>You have been demoted from the position of faction admin by %1$s<i>."),
    COMMAND_ADMIN_PROMOTES("<i>You have promoted %1$s<i> to the position of faction admin."),
    COMMAND_ADMIN_PROMOTED("%1$s<i> gave %2$s<i> the leadership of %3$s<i>."),
    COMMAND_ADMIN_DESCRIPTION("Hand over your admin rights"),

    COMMAND_ANNOUNCE_DESCRIPTION("Announce a message to players in faction."),

    COMMAND_AUTOCLAIM_ENABLED("<i>Now auto-claiming land for <h>%1$s<i>."),
    COMMAND_AUTOCLAIM_DISABLED("<i>Auto-claiming of land disabled."),
    COMMAND_AUTOCLAIM_REQUIREDRANK("<b>You must be <h>%1$s<b> to claim land."),
    COMMAND_AUTOCLAIM_OTHERFACTION("<b>You can't claim land for <h>%1$s<b>."),
    COMMAND_AUTOCLAIM_DESCRIPTION("Auto-claim land as you walk around"),

    COMMAND_AUTOHELP_HELPFOR("Help for command \""),

    COMMAND_BOOM_PEACEFULONLY("<b>This command is only usable by factions which are specifically designated as peaceful."),
    COMMAND_BOOM_TOTOGGLE("to toggle explosions"),
    COMMAND_BOOM_FORTOGGLE("for toggling explosions"),
    COMMAND_BOOM_ENABLED("%1$s<i> has %2$s explosions in your faction's territory."),
    COMMAND_BOOM_DESCRIPTION("Toggle explosions (peaceful factions only)"),

    COMMAND_BYPASS_ENABLE("<i>You have enabled admin bypass mode. You will be able to build or destroy anywhere."),
    COMMAND_BYPASS_ENABLELOG(" has ENABLED admin bypass mode."),
    COMMAND_BYPASS_DISABLE("<i>You have disabled admin bypass mode."),
    COMMAND_BYPASS_DISABLELOG(" has DISABLED admin bypass mode."),
    COMMAND_BYPASS_DESCRIPTION("Enable admin bypass mode"),

    COMMAND_CHAT_DISABLED("<b>The built in chat channels are disabled on this server."),
    COMMAND_CHAT_INVALIDMODE("<b>Unrecognised chat mode. <i>Please enter either 'a','f' or 'p'"),
    COMMAND_CHAT_DESCRIPTION("Change chat mode"),

    COMMAND_CHAT_MODE_PUBLIC("<i>Public chat mode."),
    COMMAND_CHAT_MODE_ALLIANCE("<i>Alliance only chat mode."),
    COMMAND_CHAT_MODE_TRUCE("<i>Truce only chat mode."),
    COMMAND_CHAT_MODE_FACTION("<i>Faction only chat mode."),

    COMMAND_CHATSPY_ENABLE("<i>You have enabled chat spying mode."),
    COMMAND_CHATSPY_ENABLELOG(" has ENABLED chat spying mode."),
    COMMAND_CHATSPY_DISABLE("<i>You have disabled chat spying mode."),
    COMMAND_CHATSPY_DISABLELOG(" has DISABLED chat spying mode."),
    COMMAND_CHATSPY_DESCRIPTION("Enable admin chat spy mode"),

    COMMAND_CLAIM_INVALIDRADIUS("<b>If you specify a radius, it must be at least 1."),
    COMMAND_CLAIM_DENIED("<b>You do not have permission to claim in a radius."),
    COMMAND_CLAIM_DESCRIPTION("Claim land from where you are standing"),

    COMMAND_CLAIMLINE_INVALIDRADIUS("<b>If you specify a distance, it must be at least 1."),
    COMMAND_CLAIMLINE_DENIED("<b>You do not have permission to claim in a line."),
    COMMAND_CLAIMLINE_DESCRIPTION("Claim land in a straight line."),
    COMMAND_CLAIMLINE_ABOVEMAX("<b>The maximum limit for claim line is <b>%s<b>."),
    COMMAND_CLAIMLINE_NOTVALID("%s<b> is not a cardinal direction. You may use <h>north<b>, <h>east<b>, <h>south <b>or <h>west<b>."),

    COMMAND_CONFIG_NOEXIST("<b>No configuration setting \"<h>%1$s<b>\" exists."),
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

    COMMAND_CREATE_MUSTLEAVE("<b>You must leave your current faction first."),
    COMMAND_CREATE_INUSE("<b>That tag is already in use."),
    COMMAND_CREATE_TOCREATE("to create a new faction"),
    COMMAND_CREATE_FORCREATE("for creating a new faction"),
    COMMAND_CREATE_ERROR("<b>There was an internal error while trying to create your faction. Please try again."),
    COMMAND_CREATE_CREATED("%1$s<i> created a new faction %2$s"),
    COMMAND_CREATE_YOUSHOULD("<i>You should now: %1$s"),
    COMMAND_CREATE_CREATEDLOG(" created a new faction: "),
    COMMAND_CREATE_DESCRIPTION("Create a new faction"),

    COMMAND_DEINVITE_CANDEINVITE("Players you can deinvite: "),
    COMMAND_DEINVITE_CLICKTODEINVITE("Click to revoke invite for %1$s"),
    COMMAND_DEINVITE_ALREADYMEMBER("%1$s<i> is already a member of %2$s"),
    COMMAND_DEINVITE_MIGHTWANT("<i>You might want to: %1$s"),
    COMMAND_DEINVITE_REVOKED("%1$s<i> revoked your invitation to <h>%2$s<i>."),
    COMMAND_DEINVITE_REVOKES("%1$s<i> revoked %2$s's<i> invitation."),
    COMMAND_DEINVITE_DESCRIPTION("Remove a pending invitation"),

    COMMAND_DELFWARP_DELETED("<i>Deleted warp <a>%1$s"),
    COMMAND_DELFWARP_INVALID("<i>Couldn't find warp <a>%1$s"),
    COMMAND_DELFWARP_TODELETE("to delete warp"),
    COMMAND_DELFWARP_FORDELETE("for deleting warp"),
    COMMAND_DELFWARP_DESCRIPTION("Delete a faction warp"),

    COMMAND_DESCRIPTION_CHANGES("You have changed the description for <h>%1$s<i> to:"),
    COMMAND_DESCRIPTION_CHANGED("<i>The faction %1$s<i> changed their description to:"),
    COMMAND_DESCRIPTION_TOCHANGE("to change faction description"),
    COMMAND_DESCRIPTION_FORCHANGE("for changing faction description"),
    COMMAND_DESCRIPTION_DESCRIPTION("Change the faction description"),

    COMMAND_DISBAND_IMMUTABLE("<i>You cannot disband the Wilderness, SafeZone, or WarZone."),
    COMMAND_DISBAND_MARKEDPERMANENT("<i>This faction is designated as permanent, so you cannot disband it."),
    COMMAND_DISBAND_BROADCAST_YOURS("<h>%1$s<i> disbanded your faction."),
    COMMAND_DISBAND_BROADCAST_NOTYOURS("<h>%1$s<i> disbanded the faction %2$s."),
    COMMAND_DISBAND_HOLDINGS("<i>You have been given the disbanded faction's bank, totaling %1$s."),
    COMMAND_DISBAND_DESCRIPTION("Disband a faction"),

    COMMAND_FWARP_CLICKTOWARP("Click to warp!"),
    COMMAND_FWARP_COMMANDFORMAT("<i>/f warp <warpname>"),
    COMMAND_FWARP_WARPED("<i>Warped to <a>%1$s"),
    COMMAND_FWARP_INVALID("<i>Couldn't find warp <a>%1$s"),
    COMMAND_FWARP_TOWARP("to warp"),
    COMMAND_FWARP_FORWARPING("for warping"),
    COMMAND_FWARP_WARPS("Warps: "),
    COMMAND_FWARP_DESCRIPTION("Teleport to a faction warp"),

    COMMAND_HELP_404("<b>This page does not exist"),
    COMMAND_HELP_NEXTCREATE("<i>Learn how to create a faction on the next page."),
    COMMAND_HELP_INVITATIONS("command.help.invitations", "<i>You might want to close it and use invitations:"),
    COMMAND_HELP_HOME("<i>And don't forget to set your home:"),
    COMMAND_HELP_BANK_1("<i>Your faction has a bank which is used to pay for certain"),
    COMMAND_HELP_BANK_2("<i>things, so it will need to have money deposited into it."),
    COMMAND_HELP_BANK_3("<i>To learn more, use the money command."),
    COMMAND_HELP_PLAYERTITLES("<i>Player titles are just for fun. No rules connected to them."),
    COMMAND_HELP_OWNERSHIP_1("<i>Claimed land with ownership set is further protected so"),
    COMMAND_HELP_OWNERSHIP_2("<i>that only the owner(s), faction admin, and possibly the"),
    COMMAND_HELP_OWNERSHIP_3("<i>faction moderators have full access."),
    COMMAND_HELP_RELATIONS_1("<i>Set the relation you WISH to have with another faction."),
    COMMAND_HELP_RELATIONS_2("<i>Your default relation with other factions will be neutral."),
    COMMAND_HELP_RELATIONS_3("<i>If BOTH factions choose \"ally\" you will be allies."),
    COMMAND_HELP_RELATIONS_4("<i>If ONE faction chooses \"enemy\" you will be enemies."),
    COMMAND_HELP_RELATIONS_5("<i>You can never hurt members or allies."),
    COMMAND_HELP_RELATIONS_6("<i>You can not hurt neutrals in their own territory."),
    COMMAND_HELP_RELATIONS_7("<i>You can always hurt enemies and players without faction."),
    COMMAND_HELP_RELATIONS_8(""),
    COMMAND_HELP_RELATIONS_9("<i>Damage from enemies is reduced in your own territory."),
    COMMAND_HELP_RELATIONS_10("<i>When you die you lose power. It is restored over time."),
    COMMAND_HELP_RELATIONS_11("<i>The power of a faction is the sum of all member power."),
    COMMAND_HELP_RELATIONS_12("<i>The power of a faction determines how much land it can hold."),
    COMMAND_HELP_RELATIONS_13("<i>You can claim land from factions with too little power."),
    COMMAND_HELP_PERMISSIONS_1("<i>Only faction members can build and destroy in their own"),
    COMMAND_HELP_PERMISSIONS_2("<i>territory. Usage of the following items is also restricted:"),
    COMMAND_HELP_PERMISSIONS_3("<i>Door, Chest, Furnace, Dispenser, Diode."),
    COMMAND_HELP_PERMISSIONS_4(""),
    COMMAND_HELP_PERMISSIONS_5("<i>Make sure to put pressure plates in front of doors for your"),
    COMMAND_HELP_PERMISSIONS_6("<i>guest visitors. Otherwise they can't get through. You can"),
    COMMAND_HELP_PERMISSIONS_7("<i>also use this to create member only areas."),
    COMMAND_HELP_PERMISSIONS_8("<i>As dispensers are protected, you can create traps without"),
    COMMAND_HELP_PERMISSIONS_9("<i>worrying about those arrows getting stolen."),
    COMMAND_HELP_ADMIN_1("<c>/f claim safezone <i>claim land for the Safe Zone"),
    COMMAND_HELP_ADMIN_2("<c>/f claim warzone <i>claim land for the War Zone"),
    COMMAND_HELP_ADMIN_3("<c>/f autoclaim [safezone|warzone] <i>take a guess"),
    COMMAND_HELP_MOAR_1("Finally some commands for the server admins:"),
    COMMAND_HELP_MOAR_2("<i>More commands for server admins:"),
    COMMAND_HELP_MOAR_3("<i>Even more commands for server admins:"),
    COMMAND_HELP_DESCRIPTION("Display a help page"),

    COMMAND_HOME_DISABLED("<b>Sorry, Faction homes are disabled on this server."),
    COMMAND_HOME_TELEPORTDISABLED("<b>Sorry, the ability to teleport to Faction homes is disabled on this server."),
    COMMAND_HOME_NOHOME("<b>Your faction does not have a home. "),
    COMMAND_HOME_INENEMY("<b>You cannot teleport to your faction home while in the territory of an enemy faction."),
    COMMAND_HOME_WRONGWORLD("<b>You cannot teleport to your faction home while in a different world."),
    COMMAND_HOME_ENEMYNEAR("<b>You cannot teleport to your faction home while an enemy is within %s blocks of you."),
    COMMAND_HOME_TOTELEPORT("to teleport to your faction home"),
    COMMAND_HOME_FORTELEPORT("for teleporting to your faction home"),
    COMMAND_HOME_DESCRIPTION("Teleport to the faction home"),

    COMMAND_INVITE_TOINVITE("to invite someone"),
    COMMAND_INVITE_FORINVITE("for inviting someone"),
    COMMAND_INVITE_CLICKTOJOIN("Click to join!"),
    COMMAND_INVITE_INVITEDYOU(" has invited you to join "),
    COMMAND_INVITE_INVITED("%1$s<i> invited %2$s<i> to your faction."),
    COMMAND_INVITE_ALREADYMEMBER("%1$s<i> is already a member of %2$s"),
    COMMAND_INVITE_DESCRIPTION("Invite a player to your faction"),

    COMMAND_JOIN_CANNOTFORCE("<b>You do not have permission to move other players into a faction."),
    COMMAND_JOIN_SYSTEMFACTION("<b>Players may only join normal factions. This is a system faction."),
    COMMAND_JOIN_ALREADYMEMBER("<b>%1$s %2$s already a member of %3$s"),
    COMMAND_JOIN_ATLIMIT(" <b>!<white> The faction %1$s is at the limit of %2$d members, so %3$s cannot currently join."),
    COMMAND_JOIN_INOTHERFACTION("<b>%1$s must leave %2$s current faction first."),
    COMMAND_JOIN_NEGATIVEPOWER("<b>%1$s cannot join a faction with a negative power level."),
    COMMAND_JOIN_REQUIRESINVITATION("<i>This faction requires invitation."),
    COMMAND_JOIN_ATTEMPTEDJOIN("%1$s<i> tried to join your faction."),
    COMMAND_JOIN_TOJOIN("to join a faction"),
    COMMAND_JOIN_FORJOIN("for joining a faction"),
    COMMAND_JOIN_SUCCESS("<i>%1$s successfully joined %2$s."),
    COMMAND_JOIN_MOVED("<i>%1$s moved you into the faction %2$s."),
    COMMAND_JOIN_JOINED("<i>%1$s joined your faction."),
    COMMAND_JOIN_JOINEDLOG("%1$s joined the faction %2$s."),
    COMMAND_JOIN_MOVEDLOG("%1$s moved the player %2$s into the faction %3$s."),
    COMMAND_JOIN_DESCRIPTION("Join a faction"),

    COMMAND_KICK_CANDIDATES("Players you can kick: "),
    COMMAND_KICK_CLICKTOKICK("Click to kick "),
    COMMAND_KICK_SELF("<b>You cannot kick yourself."),
    COMMAND_KICK_NONE("That player is not in a faction."),
    COMMAND_KICK_NOTMEMBER("%1$s<b> is not a member of %2$s"),
    COMMAND_KICK_INSUFFICIENTRANK("<b>Your rank is too low to kick this player."),
    COMMAND_KICK_NEGATIVEPOWER("<b>You cannot kick that member until their power is positive."),
    COMMAND_KICK_TOKICK("to kick someone from the faction"),
    COMMAND_KICK_FORKICK("for kicking someone from the faction"),
    COMMAND_KICK_FACTION("%1$s<i> kicked %2$s<i> from the faction! :O"), //message given to faction members
    COMMAND_KICK_KICKS("<i>You kicked %1$s<i> from the faction %2$s<i>!"), //kicker perspective
    COMMAND_KICK_KICKED("%1$s<i> kicked you from %2$s<i>! :O"), //kicked player perspective
    COMMAND_KICK_DESCRIPTION("Kick a player from the faction"),

    COMMAND_LIST_FACTIONLIST("Faction List "),
    COMMAND_LIST_TOLIST("to list the factions"),
    COMMAND_LIST_FORLIST("for listing the factions"),
    COMMAND_LIST_ONLINEFACTIONLESS("Online factionless: "),
    COMMAND_LIST_DESCRIPTION("See a list of the factions"),

    COMMAND_LOCK_LOCKED("<i>Factions is now locked"),
    COMMAND_LOCK_UNLOCKED("<i>Factions in now unlocked"),
    COMMAND_LOCK_DESCRIPTION("Lock all write stuff. Apparently."),

    COMMAND_LOGINS_TOGGLE("<i>Set login / logout notifications for Faction members to: <a>%s"),
    COMMAND_LOGINS_DESCRIPTION("Toggle(?) login / logout notifications for Faction members"),

    COMMAND_MAP_TOSHOW("to show the map"),
    COMMAND_MAP_FORSHOW("for showing the map"),
    COMMAND_MAP_UPDATE_ENABLED("<i>Map auto update <green>ENABLED."),
    COMMAND_MAP_UPDATE_DISABLED("<i>Map auto update <red>DISABLED."),
    COMMAND_MAP_DESCRIPTION("Show the territory map, and set optional auto update"),

    COMMAND_MOD_CANDIDATES("Players you can promote: "),
    COMMAND_MOD_CLICKTOPROMOTE("Click to promote "),
    COMMAND_MOD_NOTMEMBER("%1$s<b> is not a member in your faction."),
    COMMAND_MOD_NOTADMIN("<b>You are not the faction admin."),
    COMMAND_MOD_SELF("<b>The target player musn't be yourself."),
    COMMAND_MOD_TARGETISADMIN("<b>The target player is a faction admin. Demote them first."),
    COMMAND_MOD_REVOKES("<i>You have removed moderator status from %1$s<i>."),
    COMMAND_MOD_REVOKED("%1$s<i> is no longer moderator in your faction."),
    COMMAND_MOD_PROMOTES("%1$s<i> was promoted to moderator in your faction."),
    COMMAND_MOD_PROMOTED("<i>You have promoted %1$s<i> to moderator."),
    COMMAND_MOD_DESCRIPTION("Give or revoke moderator rights"),

    COMMAND_MODIFYPOWER_ADDED("<i>Added <a>%1$f <i>power to <a>%2$s. <i>New total rounded power: <a>%3$d"),
    COMMAND_MODIFYPOWER_DESCRIPTION("Modify the power of a faction/player"),

    COMMAND_MONEY_LONG("<i>The faction money commands."),
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

    COMMAND_OPEN_TOOPEN("to open or close the faction"),
    COMMAND_OPEN_FOROPEN("for opening or closing the faction"),
    COMMAND_OPEN_OPEN("open"),
    COMMAND_OPEN_CLOSED("closed"),
    COMMAND_OPEN_CHANGES("%1$s<i> changed the faction to <h>%2$s<i>."),
    COMMAND_OPEN_CHANGED("<i>The faction %1$s<i> is now %2$s"),
    COMMAND_OPEN_DESCRIPTION("Switch if invitation is required to join"),

    COMMAND_OWNER_DISABLED("<b>Sorry, but owned areas are disabled on this server."),
    COMMAND_OWNER_LIMIT("<b>Sorry, but you have reached the server's <h>limit of %1$d <b>owned areas per faction."),
    COMMAND_OWNER_WRONGFACTION("<b>This land is not claimed by your faction, so you can't set ownership of it."),
    COMMAND_OWNER_NOTCLAIMED("<b>This land is not claimed by a faction. Ownership is not possible."),
    COMMAND_OWNER_NOTMEMBER("%1$s<i> is not a member of this faction."),
    COMMAND_OWNER_CLEARED("<i>You have cleared ownership for this claimed area."),
    COMMAND_OWNER_REMOVED("<i>You have removed ownership of this claimed land from %1$s<i>."),
    COMMAND_OWNER_TOSET("to set ownership of claimed land"),
    COMMAND_OWNER_FORSET("for setting ownership of claimed land"),
    COMMAND_OWNER_ADDED("<i>You have added %1$s<i> to the owner list for this claimed land."),
    COMMAND_OWNER_DESCRIPTION("Set ownership of claimed land"),

    COMMAND_OWNERLIST_DISABLED("<b>Sorry, but owned areas are disabled on this server."),//dup->
    COMMAND_OWNERLIST_WRONGFACTION("<b>This land is not claimed by your faction."),//eq
    COMMAND_OWNERLIST_NOTCLAIMED("<i>This land is not claimed by any faction, thus no owners."),//eq
    COMMAND_OWNERLIST_NONE("<i>No owners are set here; everyone in the faction has access."),
    COMMAND_OWNERLIST_OWNERS("<i>Current owner(s) of this land: %1$s"),
    COMMAND_OWNERLIST_DESCRIPTION("List owner(s) of this claimed land"),

    COMMAND_PEACEFUL_DESCRIPTION("Set a faction to peaceful"),
    COMMAND_PEACEFUL_YOURS("%1$s has %2$s your faction"),
    COMMAND_PEACEFUL_OTHER("%s<i> has %s the faction '%s<i>'."),
    COMMAND_PEACEFUL_GRANT("granted peaceful status to"),
    COMMAND_PEACEFUL_REVOKE("removed peaceful status from"),

    COMMAND_PERMANENT_DESCRIPTION("Toggles a faction's permanence"), //TODO: Real word?
    COMMAND_PERMANENT_GRANT("added permanent status to"),
    COMMAND_PERMANENT_REVOKE("removed permanent status from"),
    COMMAND_PERMANENT_YOURS("%1$s has %2$s your faction"),
    COMMAND_PERMANENT_OTHER("%s<i> has %s the faction '%s<i>'."),

    COMMAND_PERMANENTPOWER_DESCRIPTION("Toggle faction power permanence"), //TODO: This a real word?
    COMMAND_PERMANENTPOWER_GRANT("added permanentpower status to"),
    COMMAND_PERMANENTPOWER_REVOKE("removed permanentpower status from"),
    COMMAND_PERMANENTPOWER_SUCCESS("<i>You %s <h>%s<i>."),
    COMMAND_PERMANENTPOWER_FACTION("%s<i> %s your faction"),

    COMMAND_POWER_TOSHOW("to show player power info"),
    COMMAND_POWER_FORSHOW("for showing player power info"),
    COMMAND_POWER_POWER("%1$s<a> - Power / Maxpower: <i>%2$d / %3$d %4$s"),
    COMMAND_POWER_BONUS(" (bonus: "),
    COMMAND_POWER_PENALTY(" (penalty: "),
    COMMAND_POWER_DESCRIPTION("Show player power info"),

    COMMAND_POWERBOOST_HELP_1("<b>You must specify \"p\" or \"player\" to target a player or \"f\" or \"faction\" to target a faction."),
    COMMAND_POWERBOOST_HELP_2("<b>ex. /f powerboost p SomePlayer 0.5  -or-  /f powerboost f SomeFaction -5"),
    COMMAND_POWERBOOST_INVALIDNUM("<b>You must specify a valid numeric value for the power bonus/penalty amount."),
    COMMAND_POWERBOOST_PLAYER("Player \"%1$s\""),
    COMMAND_POWERBOOST_FACTION("Faction \"%1$s\""),
    COMMAND_POWERBOOST_BOOST("<i>%1$s now has a power bonus/penalty of %2$d to min and max power levels."),
    COMMAND_POWERBOOST_BOOSTLOG("%1$s has set the power bonus/penalty for %2$s to %3$d."),
    COMMAND_POWERBOOST_DESCRIPTION("Apply permanent power bonus/penalty to specified player or faction"),

    COMMAND_RELATIONS_ALLTHENOPE("<b>Nope! You can't."),
    COMMAND_RELATIONS_MORENOPE("<b>Nope! You can't declare a relation to yourself :)"),
    COMMAND_RELATIONS_ALREADYINRELATIONSHIP("<b>You already have that relation wish set with %1$s."),
    COMMAND_RELATIONS_TOMARRY("to change a relation wish"),
    COMMAND_RELATIONS_FORMARRY("for changing a relation wish"),
    COMMAND_RELATIONS_MUTUAL("<i>Your faction is now %1$s<i> to %2$s"),
    COMMAND_RELATIONS_PEACEFUL("<i>This will have no effect while your faction is peaceful."),
    COMMAND_RELATIONS_PEACEFULOTHER("<i>This will have no effect while their faction is peaceful."),
    COMMAND_RELATIONS_DESCRIPTION("Set relation wish to another faction"),
    COMMAND_RELATIONS_EXCEEDS_ME("<i>Failed to set relation wish. You can only have %1$s %2$s."),
    COMMAND_RELATIONS_EXCEEDS_THEY("<i>Failed to set relation wish. They can only have %1$s %2$s."),

    COMMAND_RELATIONS_PROPOSAL_1("%1$s<i> wishes to be your %2$s"),
    COMMAND_RELATIONS_PROPOSAL_2("<i>Type <c>/%1$s %2$s %3$s<i> to accept."),
    COMMAND_RELATIONS_PROPOSAL_SENT("%1$s<i> were informed that you wish to be %2$s"),

    COMMAND_RELOAD_TIME("<i>Reloaded <h>all configuration files <i>from disk, took <h>%1$d ms<i>."),
    COMMAND_RELOAD_DESCRIPTION("Reload data file(s) from disk"),

    COMMAND_SAFEUNCLAIMALL_DESCRIPTION("Unclaim all safezone land"),
    COMMAND_SAFEUNCLAIMALL_UNCLAIMED("<i>You unclaimed ALL safe zone land."),
    COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG("%1$s unclaimed all safe zones."),

    COMMAND_SAVEALL_SUCCESS("<i>Factions saved to disk!"),
    COMMAND_SAVEALL_DESCRIPTION("Save all data to disk"),

    COMMAND_SCOREBOARD_DESCRIPTION("Scoreboardy things"),

    COMMAND_SETFWARP_NOTCLAIMED("<i>You can only set warps in your faction territory."),
    COMMAND_SETFWARP_LIMIT("<i>Your Faction already has the max amount of warps set <a>(%1$d)."),
    COMMAND_SETFWARP_SET("<i>Set warp <a>%1$s <i>to your location."),
    COMMAND_SETFWARP_TOSET("to set warp"),
    COMMAND_SETFWARP_FORSET("for setting warp"),
    COMMAND_SETFWARP_DESCRIPTION("Set a faction warp"),

    COMMAND_SETHOME_DISABLED("<b>Sorry, Faction homes are disabled on this server."),
    COMMAND_SETHOME_NOTCLAIMED("<b>Sorry, your faction home can only be set inside your own claimed territory."),
    COMMAND_SETHOME_TOSET("to set the faction home"),
    COMMAND_SETHOME_FORSET("for setting the faction home"),
    COMMAND_SETHOME_SET("%1$s<i> set the home for your faction. You can now use:"),
    COMMAND_SETHOME_SETOTHER("<b>You have set the home for the %1$s<i> faction."),
    COMMAND_SETHOME_DESCRIPTION("Set the faction home"),

    COMMAND_SHOW_NOFACTION_SELF("You are not in a faction"),
    COMMAND_SHOW_NOFACTION_OTHER("That's not a faction"),
    COMMAND_SHOW_TOSHOW("to show faction information"),
    COMMAND_SHOW_FORSHOW("for showing faction information"),
    COMMAND_SHOW_DESCRIPTION("<a>Description: <i>%1$s"),
    COMMAND_SHOW_PEACEFUL("This faction is Peaceful"),
    COMMAND_SHOW_PERMANENT("<a>This faction is permanent, remaining even with no members."),
    COMMAND_SHOW_JOINING("<a>Joining: <i>%1$s "),
    COMMAND_SHOW_INVITATION("invitation is required"),
    COMMAND_SHOW_UNINVITED("no invitation is needed"),
    COMMAND_SHOW_NOHOME("n/a"),
    COMMAND_SHOW_POWER("<a>Land / Power / Maxpower: <i> %1$d/%2$d/%3$d %4$s."),
    COMMAND_SHOW_BONUS(" (bonus: "),
    COMMAND_SHOW_PENALTY(" (penalty: "),
    COMMAND_SHOW_DEPRECIATED("(%1$s depreciated)"), //This is spelled correctly.
    COMMAND_SHOW_LANDVALUE("<a>Total land value: <i>%1$s %2$s"),
    COMMAND_SHOW_BANKCONTAINS("<a>Bank contains: <i>%1$s"),
    COMMAND_SHOW_ALLIES("Allies: "),
    COMMAND_SHOW_ENEMIES("Enemies: "),
    COMMAND_SHOW_MEMBERSONLINE("Members online: "),
    COMMAND_SHOW_MEMBERSOFFLINE("Members offline: "),
    COMMAND_SHOW_COMMANDDESCRIPTION("Show faction information"),
    COMMAND_SHOW_DEATHS_TIL_RAIDABLE("<i>DTR: %1$d"),

    COMMAND_SHOWINVITES_PENDING("Players with pending invites: "),
    COMMAND_SHOWINVITES_CLICKTOREVOKE("Click to revoke invite for %1$s"),
    COMMAND_SHOWINVITES_DESCRIPTION("Show pending faction invites"),

    COMMAND_STATUS_FORMAT("%1$s Power: %2$s Last Seen: %3$s"),
    COMMAND_STATUS_ONLINE("Online"),
    COMMAND_STATUS_AGOSUFFIX(" ago."),
    COMMAND_STATUS_DESCRIPTION("Show the status of a player"),

    COMMAND_STUCK_TIMEFORMAT("m 'minutes', s 'seconds.'"),
    COMMAND_STUCK_CANCELLED("<a>Teleport cancelled because you were damaged"),
    COMMAND_STUCK_OUTSIDE("<a>Teleport cancelled because you left <i>%1$d <a>block radius"),
    COMMAND_STUCK_EXISTS("<a>You are already teleporting, you must wait <i>%1$s"),
    COMMAND_STUCK_START("<a>Teleport will commence in <i>%s<a>. Don't take or deal damage. "),
    COMMAND_STUCK_TELEPORT("<a>Teleported safely to %1$d, %2$d, %3$d."),
    COMMAND_STUCK_TOSTUCK("to safely teleport %1$s out"),
    COMMAND_STUCK_FORSTUCK("for %1$s initiating a safe teleport out"),
    COMMAND_STUCK_DESCRIPTION("Safely teleports you out of enemy faction"),

    COMMAND_TAG_TAKEN("<b>That tag is already taken"),
    COMMAND_TAG_TOCHANGE("to change the faction tag"),
    COMMAND_TAG_FORCHANGE("for changing the faction tag"),
    COMMAND_TAG_FACTION("%1$s<i> changed your faction tag to %2$s"),
    COMMAND_TAG_CHANGED("<i>The faction %1$s<i> changed their name to %2$s."),
    COMMAND_TAG_DESCRIPTION("Change the faction tag"),

    COMMAND_TITLE_TOCHANGE("to change a players title"),
    COMMAND_TITLE_FORCHANGE("for changing a players title"),
    COMMAND_TITLE_CHANGED("%1$s<i> changed a title: %2$s"),
    COMMAND_TITLE_DESCRIPTION("Set or remove a players title"),

    COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION("Toggles whether or not you will see alliance chat"),
    COMMAND_TOGGLEALLIANCECHAT_IGNORE("Alliance chat is now ignored"),
    COMMAND_TOGGLEALLIANCECHAT_UNIGNORE("Alliance chat is no longer ignored"),

    COMMAND_TOGGLESB_DISABLED("You can't toggle scoreboards while they are disabled."),

    COMMAND_TOP_DESCRIPTION("Sort Factions to see the top of some criteria."),
    COMMAND_TOP_TOP("Top Factions by %s. Page %d/%d"),
    COMMAND_TOP_LINE("%d. &6%s: &c%s"), // Rank. Faction: Value
    COMMAND_TOP_INVALID("Could not sort by %s. Try balance, online, members, power or land."),

    COMMAND_UNCLAIM_SAFEZONE_SUCCESS("<i>Safe zone was unclaimed."),
    COMMAND_UNCLAIM_SAFEZONE_NOPERM("<b>This is a safe zone. You lack permissions to unclaim."),
    COMMAND_UNCLAIM_WARZONE_SUCCESS("<i>War zone was unclaimed."),
    COMMAND_UNCLAIM_WARZONE_NOPERM("<b>This is a war zone. You lack permissions to unclaim."),
    COMMAND_UNCLAIM_UNCLAIMED("%1$s<i> unclaimed some of your land."),
    COMMAND_UNCLAIM_UNCLAIMS("<i>You unclaimed this land."),
    COMMAND_UNCLAIM_LOG("%1$s unclaimed land at (%2$s) from the faction: %3$s"),
    COMMAND_UNCLAIM_WRONGFACTION("<b>You don't own this land."),
    COMMAND_UNCLAIM_TOUNCLAIM("to unclaim this land"),
    COMMAND_UNCLAIM_FORUNCLAIM("for unclaiming this land"),
    COMMAND_UNCLAIM_FACTIONUNCLAIMED("%1$s<i> unclaimed some land."),
    COMMAND_UNCLAIM_DESCRIPTION("Unclaim the land where you are standing"),

    COMMAND_UNCLAIMALL_TOUNCLAIM("to unclaim all faction land"),
    COMMAND_UNCLAIMALL_FORUNCLAIM("for unclaiming all faction land"),
    COMMAND_UNCLAIMALL_UNCLAIMED("%1$s<i> unclaimed ALL of your faction's land."),
    COMMAND_UNCLAIMALL_LOG("%1$s unclaimed everything for the faction: %2$s"),
    COMMAND_UNCLAIMALL_DESCRIPTION("Unclaim all of your factions land"),

    COMMAND_VERSION_VERSION("<i>You are running %1$s"),
    COMMAND_VERSION_DESCRIPTION("Show plugin and translation version information"),

    COMMAND_WARUNCLAIMALL_DESCRIPTION("Unclaim all warzone land"),
    COMMAND_WARUNCLAIMALL_SUCCESS("<i>You unclaimed ALL war zone land."),
    COMMAND_WARUNCLAIMALL_LOG("%1$s unclaimed all war zones."),

    /**
     * Leaving - This is accessed through a command, and so it MAY need a COMMAND_* slug :s
     */
    LEAVE_PASSADMIN("<b>You must give the admin role to someone else first."),
    LEAVE_NEGATIVEPOWER("<b>You cannot leave until your power is positive."),
    LEAVE_TOLEAVE("to leave your faction."),
    LEAVE_FORLEAVE("for leaving your faction."),
    LEAVE_LEFT("%s<i> left faction %s<i>."),
    LEAVE_DISBANDED("<i>%s<i> was disbanded."),
    LEAVE_DISBANDEDLOG("The faction %s (%s) was disbanded due to the last player (%s) leaving."),
    LEAVE_DESCRIPTION("Leave your faction"),

    /**
     * Claiming - Same as above basically. No COMMAND_* because it's not in a command class, but...
     */
    CLAIM_PROTECTED("<b>This land is protected"),
    CLAIM_DISABLED("<b>Sorry, this world has land claiming disabled."),
    CLAIM_CANTCLAIM("<b>You can't claim land for <h>%s<b>."),
    CLAIM_ALREADYOWN("%s<i> already own this land."),
    CLAIM_MUSTBE("<b>You must be <h>%s<b> to claim land."),
    CLAIM_MEMBERS("Factions must have at least <h>%s<b> members to claim land."),
    CLAIM_SAFEZONE("<b>You can not claim a Safe Zone."),
    CLAIM_WARZONE("<b>You can not claim a War Zone."),
    CLAIM_POWER("<b>You can't claim more land! You need more power!"),
    CLAIM_LIMIT("<b>Limit reached. You can't claim more land!"),
    CLAIM_ALLY("<b>You can't claim the land of your allies."),
    CLAIM_CONTIGIOUS("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!"),
    CLAIM_FACTIONCONTIGUOUS("<b>You can only claim additional land which is connected to your first claim!"),
    CLAIM_PEACEFUL("%s<i> owns this land. Your faction is peaceful, so you cannot claim land from other factions."),
    CLAIM_PEACEFULTARGET("%s<i> owns this land, and is a peaceful faction. You cannot claim land from them."),
    CLAIM_THISISSPARTA("%s<i> owns this land and is strong enough to keep it."),
    CLAIM_BORDER("<b>You must start claiming land at the border of the territory."),
    CLAIM_TOCLAIM("to claim this land"),
    CLAIM_FORCLAIM("for claiming this land"),
    CLAIM_CLAIMED("<h>%s<i> claimed land for <h>%s<i> from <h>%s<i>."),
    CLAIM_CLAIMEDLOG("%s claimed land at (%s) for the faction: %s"),
    CLAIM_OVERCLAIM_DISABLED("<i>Over claiming is disabled on this server."),
    CLAIM_TOOCLOSETOOTHERFACTION("<i>Your claim is too close to another Faction. Buffer required is %d"),
    CLAIM_OUTSIDEWORLDBORDER("<i>Your claim is outside the border."),
    CLAIM_OUTSIDEBORDERBUFFER("<i>Your claim is outside the border. %d chunks away world edge required."),
    /**
     * More generic, or less easily categorisable translations, which may apply to more than one class
     */
    GENERIC_YOU("you"),
    GENERIC_YOURFACTION("your faction"),
    GENERIC_NOPERMISSION("<b>You don't have permission to %1$s."),
    GENERIC_DOTHAT("do that"),  //Ugh nuke this from high orbit
    GENERIC_NOPLAYERMATCH("<b>No player match found for \"<p>%1$s<b>\"."),
    GENERIC_NOPLAYERFOUND("<b>No player \"<p>%1$s<b>\" could not be found."),
    GENERIC_ARGS_TOOFEW("<b>Too few arguments. <i>Use like this:"),
    GENERIC_ARGS_TOOMANY("<b>Strange argument \"<p>%1$s<b>\". <i>Use the command like this:"),
    GENERIC_DEFAULTDESCRIPTION("Default faction description :("),
    GENERIC_OWNERS("Owner(s): %1$s"),
    GENERIC_PUBLICLAND("Public faction land."),
    GENERIC_FACTIONLESS("factionless"),
    GENERIC_SERVERADMIN("A server admin"),
    GENERIC_DISABLED("disabled"),
    GENERIC_ENABLED("enabled"),
    GENERIC_INFINITY("∞"),
    GENERIC_CONSOLEONLY("This command cannot be run as a player."),
    GENERIC_PLAYERONLY("<b>This command can only be used by ingame players."),
    GENERIC_ASKYOURLEADER("<i> Ask your leader to:"),
    GENERIC_YOUSHOULD("<i>You should:"),
    GENERIC_YOUMAYWANT("<i>You may want to: "),
    GENERIC_TRANSLATION_VERSION("Translation: %1$s(%2$s,%3$s) State: %4$s"),
    GENERIC_TRANSLATION_CONTRIBUTORS("Translation contributors: %1$s"),
    GENERIC_TRANSLATION_RESPONSIBLE("Responsible for translation: %1$s"),
    GENERIC_FACTIONTAG_TOOSHORT("<i>The faction tag can't be shorter than <h>%1$s<i> chars."),
    GENERIC_FACTIONTAG_TOOLONG("<i>The faction tag can't be longer than <h>%s<i> chars."),
    GENERIC_FACTIONTAG_ALPHANUMERIC("<i>Faction tag must be alphanumeric. \"<h>%s<i>\" is not allowed."),
    GENERIC_PLACEHOLDER("<This is a placeholder for a message you should not see>"),

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
    CHAT_FACTION("faction chat"),
    CHAT_ALLIANCE("alliance chat"),
    CHAT_TRUCE("truce chat"),
    CHAT_PUBLIC("public chat"),

    /**
     * Economy stuff
     */

    ECON_OFF("no %s"), // no balance, no value, no refund, etc

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
    ROLE_MODERATOR("moderator"),
    ROLE_NORMAL("normal member"),

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
    PLAYER_CANTHURT("<i>You may not harm other players in %s"),
    PLAYER_SAFEAUTO("<i>This land is now a safe zone."),
    PLAYER_WARAUTO("<i>This land is now a war zone."),
    PLAYER_OUCH("<b>Ouch, that is starting to hurt. You should give it a rest."),
    PLAYER_USE_WILDERNESS("<b>You can't use <h>%s<b> in the wilderness."),
    PLAYER_USE_SAFEZONE("<b>You can't use <h>%s<b> in a safe zone."),
    PLAYER_USE_WARZONE("<b>You can't use <h>%s<b> in a war zone."),
    PLAYER_USE_TERRITORY("<b>You can't <h>%s<b> in the territory of <h>%s<b>."),
    PLAYER_USE_OWNED("<b>You can't use <h>%s<b> in this territory, it is owned by: %s<b>."),
    PLAYER_COMMAND_WARZONE("<b>You can't use the command '%s' in warzone."),
    PLAYER_COMMAND_NEUTRAL("<b>You can't use the command '%s' in neutral territory."),
    PLAYER_COMMAND_ENEMY("<b>You can't use the command '%s' in enemy territory."),
    PLAYER_COMMAND_PERMANENT("<b>You can't use the command '%s' because you are in a permanent faction."),

    PLAYER_POWER_NOLOSS_PEACEFUL("<i>You didn't lose any power since you are in a peaceful faction."),
    PLAYER_POWER_NOLOSS_WORLD("<i>You didn't lose any power due to the world you died in."),
    PLAYER_POWER_NOLOSS_WILDERNESS("<i>You didn't lose any power since you were in the wilderness."),
    PLAYER_POWER_NOLOSS_WARZONE("<i>You didn't lose any power since you were in a war zone."),
    PLAYER_POWER_LOSS_WARZONE("<b>The world you are in has power loss normally disabled, but you still lost power since you were in a war zone.\n<i>Your power is now <h>%d / %d"),
    PLAYER_POWER_NOW("<i>Your power is now <h>%d / %d"),

    PLAYER_PVP_LOGIN("<i>You can't hurt other players for %d seconds after logging in."),
    PLAYER_PVP_REQUIREFACTION("<i>You can't hurt other players until you join a faction."),
    PLAYER_PVP_FACTIONLESS("<i>You can't hurt players who are not currently in a faction."),
    PLAYER_PVP_PEACEFUL("<i>Peaceful players cannot participate in combat."),
    PLAYER_PVP_NEUTRAL("<i>You can't hurt neutral factions. Declare them as an enemy."),
    PLAYER_PVP_CANTHURT("<i>You can't hurt %s<i>."),

    PLAYER_PVP_NEUTRALFAIL("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy."),
    PLAYER_PVP_TRIED("%s<i> tried to hurt you."),

    /**
     * Strings lying around in other bits of the plugins
     */
    NOPAGES("<i>Sorry. No Pages available."),
    INVALIDPAGE("<i>Invalid page. Must be between 1 and %1$d"),

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
    FACTION_LEAVE("faction-leave", "<a>Leaving %1$s, <a>Entering %2$s"),
    DEFAULT_PREFIX("default-prefix", "{relationcolor}[{faction}] &r"),
    FACTION_LOGIN("faction-login", "&e%1$s &9logged in."),
    FACTION_LOGOUT("faction-logout", "&e%1$s &9logged out.."),
    DATE_FORMAT("date-format", "MM/d/yy h:ma"), // 3/31/15 07:49AM

    /**
     * Raidable is used in multiple places. Allow more than just true/false.
     */
    RAIDABLE_TRUE("raidable-true", "true"),
    RAIDABLE_FALSE("raidable-false", "false"),
    /**
     * Warmups
     */
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
