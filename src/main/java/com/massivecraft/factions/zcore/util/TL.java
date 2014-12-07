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

/**
 * An enum for requesting strings from the language file.
 * The contents of this enum file may be subject to frequent changes.
 */
public enum TL {
	/**
	 * Translation meta
	 */
	_AUTHOR("misc"),
	_LANGUAGE("English"),
	_ENCODING("UTF-8"),
	_REQUIRESUNICODE("false"),
	_DEFAULT("true"),
	/**
	 * Command translations
	 */
	COMMAND_ADMIN_NOTMEMBER("%s<i> is not a member in your faction."),
	COMMAND_ADMIN_NOTADMIN("<b>You are not the faction admin."),
	COMMAND_ADMIN_TARGETSELF("<b>The target player musn't be yourself."),
	COMMAND_ADMIN_DEMOTES("<i>You have demoted %s<i> from the position of faction admin."),
	COMMAND_ADMIN_DEMOTED("<i>You have been demoted from the position of faction admin by %s<i>."),
	COMMAND_ADMIN_PROMOTES("<i>You have promoted %s<i> to the position of faction admin."),
	COMMAND_ADMIN_PROMOTED("%s<i> gave %s<i> the leadership of %s<i>."),
	
	COMMAND_AUTOCLAIM_ENABLED("<i>Now auto-claiming land for <h>%s<i>."),
	COMMAND_AUTOCLAIM_DISABLED("<i>Auto-claiming of land disabled."),
	COMMAND_AUTOCLAIM_REQUIREDRANK("<b>You must be <h>%s<b> to claim land."),
	COMMAND_AUTOCLAIM_OTHERFACTION("<b>You can't claim land for <h>%s<b>."),
	
	COMMAND_AUTOHELP_HELPFOR("Help for command \""),
	
	COMMAND_BOOM_PEACEFULONLY("<b>This command is only usable by factions which are specifically designated as peaceful."),
	COMMAND_BOOM_TOTOGGLE("to toggle explosions"),
	COMMAND_BOOM_FORTOGGLE("for toggling explosions"),
	COMMAND_BOOM_ENABLED("%s<i> has %s explosions in your faction's territory."),
	
	COMMAND_BYPASS_ENABLE("<i>You have enabled admin bypass mode. You will be able to build or destroy anywhere."),
	COMMAND_BYPASS_ENABLELOG(" has ENABLED admin bypass mode."),
	COMMAND_BYPASS_DISABLE("<i>You have disabled admin bypass mode."),
	COMMAND_BYPASS_DISABLELOG(" has DISABLED admin bypass mode."),
	
	COMMAND_CHAT_DISABLED("<b>The built in chat chat channels are disabled on this server."),
	COMMAND_CHAT_INVALIDMODE("<b>Unrecognised chat mode. <i>Please enter either 'a','f' or 'p'"),
	
	COMMAND_CHAT_MODE_PUBLIC("<i>Public chat mode."),
	COMMAND_CHAT_MODE_ALLIANCE("<i>Alliance only chat mode."),
	COMMAND_CHAT_MODE_FACTION("<i>Faction only chat mode."),
	
	COMMAND_CHATSPY_ENABLE("<i>You have enabled chat spying mode."),
	COMMAND_CHATSPY_ENABLELOG(" has ENABLED chat spying mode."),
	COMMAND_CHATSPY_DISABLE("<i>You have disabled chat spying mode."),
	COMMAND_CHATSPY_DISABLELOG(" has DISABLED chat spying mode."),
	
	COMMAND_CLAIM_INVALIDRADIUS("<b>If you specify a radius, it must be at least 1."),
	COMMAND_CLAIM_DENIED("<b>You do not have permission to claim in a radius."),
	
	COMMAND_CONFIG_NOEXIST("<b>No configuration setting \"<h>%s<b>\" exists."),
	COMMAND_CONFIG_SET_TRUE("\" option set to true (enabled)."),
	COMMAND_CONFIG_SET_FALSE("\" option set to false (disabled)."),
	COMMAND_CONFIG_OPTIONSET("\" option set to "),
	COMMAND_CONFIG_COLOURSET("\" color option set to \""),
	COMMAND_CONFIG_INTREQUIRED("Cannot set \"%s\": An integer (whole number) value required."),
	COMMAND_CONFIG_LONGREQUIRED("Cannot set \"%s\": A long integer (whole number) value required."),
	COMMAND_CONFIG_DOUBLEREQUIRED("Cannot set \"%s\": A double (numeric) value required."),
	COMMAND_CONFIG_FLOATREQUIRED("Cannot set \"%s\": A float (numeric) value required."),
	COMMAND_CONFIG_INVALID_COLOUR("Cannot set \"%s\": \"%s\" is not a valid color."),
	COMMAND_CONFIG_INVALID_COLLECTION("\"%s\" is not a data collection type which can be modified with this command."),
	COMMAND_CONFIG_INVALID_MATERIAL("Cannot change \"%s\" set: \"%s\" is not a valid material."),
	COMMAND_CONFIG_INVALID_TYPESET("\"%s\" is not a data type set which can be modified with this command."),
	COMMAND_CONFIG_MATERIAL_ADDED("\"%s\" set: Material \"%s\" added."),
	COMMAND_CONFIG_MATERIAL_REMOVED("\"%s\" set: Material \"%s\" removed."),
	COMMAND_CONFIG_SET_ADDED("\"%s\" set: \"%s\" added."),
	COMMAND_CONFIG_SET_REMOVED("\"%s\" set: \"%s\" removed."),
	COMMAND_CONFIG_LOG(" (Command was run by %s.)"),
	COMMAND_CONFIG_ERROR_SETTING("Error setting configuration setting \"%s\" to \"%s\"."),
	COMMAND_CONFIG_ERROR_MATCHING("Configuration setting \"%s\" couldn't be matched, though it should be... please report this error."),
	COMMAND_CONFIG_ERROR_TYPE("'%s' is of type '%s', which cannot be modified with this command."),
	
	COMMAND_CONVERT_BACKEND_RUNNING("command.convert.backend.running","Already running that backend."),
	COMMAND_CONVERT_BACKEND_INVALID("command.convert.backend.invalid","Invalid backend"),
	
	COMMAND_CREATE_MUSTLEAVE("<b>You must leave your current faction first."),
	COMMAND_CREATE_INUSE("<b>That tag is already in use."),
	COMMAND_CREATE_TOCREATE("to create a new faction"),
	COMMAND_CREATE_FORCREATE("for creating a new faction"),
	COMMAND_CREATE_ERROR("<b>There was an internal error while trying to create your faction. Please try again."),
	COMMAND_CREATE_CREATED("%s<i> created a new faction %s"),
	COMMAND_CREATE_YOUSHOULD("<i>You should now: %s"),
	COMMAND_CREATE_CREATED_LOG(" created a new faction: "),
	
	COMMAND_DEINVITE_CANDEINVITE("Players you can deinvite: "),
	COMMAND_DEINVITE_CLICKTODEINVITE("Click to revoke invite for "),
	COMMAND_DEINVITE_ALREADYMEMBER("%s<i> is already a member of %s"),
	COMMAND_DEINVITE_MIGHTWANT("<i>You might want to: %s"),
	COMMAND_DEINVITE_REVOKED("%s<i> revoked your invitation to <h>%s<i>."),
	COMMAND_DEINVITE_REVOKES("%s<i> revoked %s's<i> invitation."),
	
	COMMAND_DELFWARP_DELETED("<i>Deleted warp <a>%s"),
	COMMAND_DELFWARP_INVALID("<i>Couldn't find warp <a>%s"),
	COMMAND_DELFWARP_TODELETE("to delete warp"),
	COMMAND_DELFWARP_FORDELETE("for deleting warp"),
	
	COMMAND_DESCRIPTION_CHANGES("You have changed the description for <h>%s<i> to:"),
	COMMAND_DESCRIPTION_CHANGED("<i>The faction %s<i> changed their description to:"),
	COMMAND_DESCRIPTION_TOCHANGE("to change faction description"),
	COMMAND_DESCRIPTION_FORCHANGE("for changing faction description"),
	
	COMMAND_DISBAND_IMMUTABLE("<i>You cannot disband the Wilderness, SafeZone, or WarZone."),
	COMMAND_DISBAND_MARKEDPERMANENT("<i>This faction is designated as permanent, so you cannot disband it."),
	COMMAND_DISBAND_BROADCAST_YOURS("<h>%s<i> disbanded your faction."),
	COMMAND_DISBAND_BROADCAST_NOTYOURS("<h>%s<i> disbanded the faction %s."),
	COMMAND_DISBAND_HOLDINGS("<i>You have been given the disbanded faction's bank, totaling %s."),
	
	COMMAND_FWARP_CLICKTOWARP("Click to warp!"),
	COMMAND_FWARP_COMMANDFORMAT("<i>/f warp <warpname>"),
	COMMAND_FWARP_WARPED("<i>Warped to <a>%s"),
	COMMAND_FWARP_INVALID("<i>Couldn't find warp <a>%s"),
	COMMAND_FWARP_TOWARP("to warp"),
	COMMAND_FWARP_FORWARPING("for warping"),
	COMMAND_FWARP_WARPS("Warps: "),
	
	COMMAND_HELP_404("<b>This page does not exist"),
	COMMAND_HELP_NEXTCREATE("<i>Learn how to create a faction on the next page."),
	COMMAND_HELP_INVITATIONS("command.help.invitations","<i>You might want to close it and use invitations:"),
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
    
    COMMAND_HOME_DISABLED("<b>Sorry, Faction homes are disabled on this server."),
    COMMAND_HOME_TELEPORTDISABLED("<b>Sorry, the ability to teleport to Faction homes is disabled on this server."),
    COMMAND_HOME_NOHOME("<b>Your faction does not have a home. "),
    COMMAND_HOME_INENEMY("<b>You cannot teleport to your faction home while in the territory of an enemy faction."),
    COMMAND_HOME_WRONGWORLD("<b>You cannot teleport to your faction home while in a different world."),
    COMMAND_HOME_ENEMYNEAR("<b>You cannot teleport to your faction home while an enemy is within %s blocks of you."),
	COMMAND_HOME_TOTELEPORT("to teleport to your faction home"),
	COMMAND_HOME_FORTELEPORT("for teleporting to your faction home"),
	
	COMMAND_INVITE_TOINVITE("to invite someone"),
	COMMAND_INVITE_FORINVITE("for inviting someone"),
	COMMAND_INVITE_CLICKTOJOIN("Click to join!"),
	COMMAND_INVITE_INVITEDYOU(" has invited you to join "),
	COMMAND_INVITE_INVITED("%s<i> invited %s<i> to your faction."),
	COMMAND_INVITE_ALREADYMEMBER("%s<i> is already a member of %s"),
	
	COMMAND_JOIN_CANNOTFORCE("<b>You do not have permission to move other players into a faction."),
	COMMAND_JOIN_SYSTEMFACTION("<b>Players may only join normal factions. This is a system faction."),
	COMMAND_JOIN_ALREADYMEMBER("<b>%s %s already a member of %s"),
	COMMAND_JOIN_ATLIMIT(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join."),
	COMMAND_JOIN_INOTHERFACTION("<b>%s must leave %s current faction first."),
	COMMAND_JOIN_NEGATIVEPOWER("<b>%s cannot join a faction with a negative power level."),
	COMMAND_JOIN_REQUIRESINVITATION("<i>This faction requires invitation."),
	COMMAND_JOIN_ATTEMPTEDJOIN("%s<i> tried to join your faction."),
	COMMAND_JOIN_TOJOIN("to join a faction"),
	COMMAND_JOIN_FORJOIN("for joining a faction"),
	COMMAND_JOIN_SUCCESS("<i>%s successfully joined %s."),
	COMMAND_JOIN_MOVED("<i>%s moved you into the faction %s."),
	COMMAND_JOIN_JOINED("<i>%s joined your faction."),
	COMMAND_JOIN_JOINEDLOG("%s joined the faction %s."),
	COMMAND_JOIN_MOVEDLOG("%s moved the player %s into the faction %s."),
	
	COMMAND_KICK_CANDIDATES("Players you can kick: "),
	COMMAND_KICK_CLICKTOKICK("Click to kick "),
	COMMAND_KICK_SELF("<b>You cannot kick yourself."),
	COMMAND_KICK_OFFLINE("Something went wrong with getting the offline player's faction."),
	COMMAND_KICK_NOTMEMBER("%s<b> is not a member of %s"),
	COMMAND_KICK_INSUFFICIENTRANK("<b>Your rank is too low to kick this player."),
	COMMAND_KICK_NEGATIVEPOWER("<b>You cannot kick that member until their power is positive."),
	COMMAND_KICK_TOKICK("to kick someone from the faction"),
	COMMAND_KICK_FORKICK("for kicking someone from the faction"),
	COMMAND_KICK_FACTION("%s<i> kicked %s<i> from the faction! :O"), //message given to faction members
	COMMAND_KICK_KICKS("<i>You kicked %s<i> from the faction %s<i>!"), //kicker perspective
	COMMAND_KICK_KICKED("%s<i> kicked you from %s<i>! :O"), //kicked player perspective
	
	
	COMMAND_LIST_FACTIONLIST("Faction List "),
	COMMAND_LIST_TOLIST("to list the factions"),
	COMMAND_LIST_FORLIST("for listing the factions"),
	COMMAND_LIST_ONLINEFACTIONLESS("Online factionless: "),
	
	COMMAND_LOCK_LOCKED("<i>Factions is now locked"),
	COMMAND_LOCK_UNLOCKED("<i>Factions in now unlocked"),
	
	COMMAND_MAP_TOSHOW("to show the map"),
	COMMAND_MAP_FORSHOW("for showing the map"),
	COMMAND_MAP_UPDATE_ENABLED("<i>Map auto update <green>ENABLED."),
	COMMAND_MAP_UPDATE_DISABLED("<i>Map auto update <red>DISABLED."),
	
	COMMAND_MOD_CANDIDATES("Players you can promote: "),
	COMMAND_MOD_CLICKTOPROMOTE("Click to promote "),
	COMMAND_MOD_NOTMEMBER("%s<b> is not a member in your faction."),
	COMMAND_MOD_NOTADMIN("<b>You are not the faction admin."),
	COMMAND_MOD_SELF("<b>The target player musn't be yourself."),
	COMMAND_MOD_TARGETISADMIN("<b>The target player is a faction admin. Demote them first."),
	COMMAND_MOD_REVOKES("<i>You have removed moderator status from %s<i>."),
	COMMAND_MOD_REVOKED("%s<i> is no longer moderator in your faction."),
	COMMAND_MOD_PROMOTES("%s<i> was promoted to moderator in your faction."),
	COMMAND_MOD_PROMOTED("<i>You have promoted %s<i> to moderator."),
	
	COMMAND_MODIFYPOWER_ADDED("<i>Added <a>%f <i>power to <a>%s. <i>New total rounded power: <a>%d"),
	
	COMMAND_MONEY_SHORT("faction money commands"),
	COMMAND_MONEY_LONG("<i>The faction money commands."),
	
	COMMAND_MONEYBALANCE_SHORT("show faction balance"),
	
	COMMAND_MONEYDEPOSIT_SHORT("deposit money"),
	COMMAND_MONEYDEPOSIT_DEPOSITED("%s deposited %s in the faction bank: %s"),
	
	COMMAND_MONEYTRANSFERFF_SHORT("transfer f -> f"),
	COMMAND_MONEYTRANSFERFF_TRANSFER("%s transferred %s from the faction \"%s\" to the faction \"%s\""),
	
	COMMAND_MONEYTRANSFERFP_SHORT("transfer f -> p"),
	COMMAND_MONEYTRANSFERFP_TRANSFER("%s transferred %s from the faction \"%s\" to the player \"%s\""),
	
	COMMAND_MONEYTRANSFERPF_SHORT("transfer p -> f"),
	COMMAND_MONEYTRANSFERPF_TRANSFER("%s transferred %s from the player \"%s\" to the faction \"%s\""),
    
	COMMAND_MONEYWITHDRAW_SHORT("withdraw money"),
	COMMAND_MONEYWITHDRAW_WITHDRAW("%s withdrew %s from the faction bank: %s"),
	
	COMMAND_OPEN_TOOPEN("to open or close the faction"),
	COMMAND_OPEN_FOROPEN("for opening or closing the faction"),
	COMMAND_OPEN_OPEN("open"),
	COMMAND_OPEN_CLOSED("closed"),
	COMMAND_OPEN_CHANGES("%s<i> changed the faction to <h>%s<i>."),
	COMMAND_OPEN_CHANGED("<i>The faction %s<i> is now %s"),
	
	COMMAND_OWNER_DISABLED("<b>Sorry, but owned areas are disabled on this server."),
	COMMAND_OWNER_LIMIT("<b>Sorry, but you have reached the server's <h>limit of %d <b>owned areas per faction."),
	COMMAND_OWNER_WRONGFACTION("<b>This land is not claimed by your faction, so you can't set ownership of it."),
	COMMAND_OWNER_NOTCLAIMED("<b>This land is not claimed by a faction. Ownership is not possible."),
	COMMAND_OWNER_NOTMEMBER("%s<i> is not a member of this faction."),
	COMMAND_OWNER_CLEARED("<i>You have cleared ownership for this claimed area."),
	COMMAND_OWNER_REMOVED("<i>You have removed ownership of this claimed land from %s<i>."),
	COMMAND_OWNER_TOSET("to set ownership of claimed land"),
	COMMAND_OWNER_FORSET("for setting ownership of claimed land"),
	COMMAND_OWNER_ADDED("<i>You have added %s<i> to the owner list for this claimed land."),
	
	COMMAND_OWNERLIST_DISABLED("<b>Sorry, but owned areas are disabled on this server."),//dup->
	COMMAND_OWNERLIST_WRONGFACTION("<b>This land is not claimed by your faction."),//eq
	COMMAND_OWNERLIST_NOTCLAIMED("<i>This land is not claimed by any faction, thus no owners."),//eq
	COMMAND_OWNERLIST_NONE("<i>No owners are set here; everyone in the faction has access."),
	COMMAND_OWNERLIST_OWNERS("<i>Current owner(s) of this land: %s"),
	
	COMMAND_POWER_TOSHOW("to show player power info"),
	COMMAND_POWER_FORSHOW("for showing player power info"),
	COMMAND_POWER_POWER("%s<a> - Power / Maxpower: <i>%d / %d %s"),
	COMMAND_POWER_BONUS(" (bonus: "),
	COMMAND_POWER_PENALTY(" (penalty: "),
	
    COMMAND_POWERBOOST_HELP_1("<b>You must specify \"p\" or \"player\" to target a player or \"f\" or \"faction\" to target a faction."),
    COMMAND_POWERBOOST_HELP_2("<b>ex. /f powerboost p SomePlayer 0.5  -or-  /f powerboost f SomeFaction -5"),
    COMMAND_POWERBOOST_INVALIDNUM("<b>You must specify a valid numeric value for the power bonus/penalty amount."),
    COMMAND_POWERBOOST_PLAYER("Player \"%s\""),
    COMMAND_POWERBOOST_FACTION("Faction \"%s\""),
    COMMAND_POWERBOOST_BOOST("<i>%s now has a power bonus/penalty of %d to min and max power levels."),
    COMMAND_POWERBOOST_BOOSTLOG("%s has set the power bonus/penalty for %s to %d."),
    
    COMMAND_RELOAD_TIME("<i>Reloaded <h>conf.json <i>from disk, took <h>%dms<i>."),
    
    COMMAND_SAFEUNCLAIMALL_SHORT("Unclaim all safezone land"),
    COMMAND_SAFEUNCLAIMALL_UNCLAIMED("<i>You unclaimed ALL safe zone land."),
    COMMAND_SAFEUNCLAIMALL_UNCLAIMEDLOG("%s unclaimed all safe zones."),
    
    COMMAND_SAVEALL("<i>Factions saved to disk!"),
    
    COMMAND_SETFWARP_NOTCLAIMED("<i>You can only set warps in your faction territory."),
    COMMAND_SETFWARP_LIMIT("<i>Your Faction already has the max amount of warps set <a>(%d)."),
    COMMAND_SETFWARP_SET("<i>Set warp <a>%s <i>to your location."),
    COMMAND_SETFWARP_TOSET("to set warp"),
    COMMAND_SETFWARP_FORSET("for setting warp"),
    
    COMMAND_SETHOME_DISABLED("<b>Sorry, Faction homes are disabled on this server."),
    COMMAND_SETHOME_NOTCLAIMED("<b>Sorry, your faction home can only be set inside your own claimed territory."),
    COMMAND_SETHOME_TOSET("to set the faction home"),
    COMMAND_SETHOME_FORSET("for setting the faction home"),
    COMMAND_SETHOME_SET("%s<i> set the home for your faction. You can now use:"),
    COMMAND_SETHOME_SETOTHER("<b>You have set the home for the %s<i> faction."),
    
    COMMAND_SHOW_TOSHOW("to show faction information"),
    COMMAND_SHOW_FORSHOW("for showing faction information"),
    COMMAND_SHOW_DESCRIPTION("<a>Description: <i>%s"),
    COMMAND_SHOW_PEACEFUL("This faction is Peaceful"),
    COMMAND_SHOW_PERMANENT("<a>This faction is permanent, remaining even with no members."),
    COMMAND_SHOW_JOINING("<a>Joining: <i>%s "),
    COMMAND_SHOW_INVITATION("invitation is required"),
    COMMAND_SHOW_UNINVITED("no invitation is needed"),
    COMMAND_SHOW_POWER("<a>Land / Power / Maxpower: <i> %d/%d/%d %s"),
	COMMAND_SHOW_BONUS(" (bonus: "),
	COMMAND_SHOW_PENALTY(" (penalty: "),
	COMMAND_SHOW_DEPRECIATED("(%s depreciated)"), //This is spelled correctly.
	COMMAND_SHOW_LANDVALUE("<a>Total land value: <i>%s%s"),
	COMMAND_SHOW_BANKCONTAINS("<a>Bank contains: <i>%s"),
	COMMAND_SHOW_ALLIES("Allies: "),
	COMMAND_SHOW_ENEMIES("Enemies: "),
	COMMAND_SHOW_MEMBERSONLINE("Members online: "),
	COMMAND_SHOW_MEMBERSOFFLINE("Members offline: "),
	
	COMMAND_SHOWINVITES_PENDING("Players with pending invites: "),
	COMMAND_SHOWINVITES_CLICKTOREVOKE("Click to revoke invite for %s"),
	
	COMMAND_STATUS_FORMAT("%s Power: %s Last Seen: %s"),
	COMMAND_STATUS_ONLINE("Online"),
	COMMAND_STATUS_AGOSUFFIX(" ago."),
	
	COMMAND_TAG_TAKEN("<b>That tag is already taken"),
	COMMAND_TAG_TOCHANGE("to change the faction tag"),
	COMMAND_TAG_FORCHANGE("for changing the faction tag"),
	COMMAND_TAG_FACTION("%s<i> changed your faction tag to %s"),
	COMMAND_TAG_CHANGED("<i>The faction %s<i> changed their name to %s."),
	
	COMMAND_TITLE_TOCHANGE("to change a players title"),
	COMMAND_TITLE_FORCHANGE("for changing a players title"),
	COMMAND_TITLE_CHANGED("%s<i> changed a title: %s"),
	
	COMMAND_UNCLAIM_SAFEZONE_SUCCESS("<i>Safe zone was unclaimed."),
	COMMAND_UNCLAIM_SAFEZONE_NOPERM("<b>This is a safe zone. You lack permissions to unclaim."),
	COMMAND_UNCLAIM_WARZONE_SUCCESS("<i>War zone was unclaimed."),
	COMMAND_UNCLAIM_WARZONE_NOPERM("<b>This is a war zone. You lack permissions to unclaim."),
	COMMAND_UNCLAIM_UNCLAIMED("%s<i> unclaimed some of your land."),
	COMMAND_UNCLAIM_UNCLAIMS("<i>You unclaimed this land."),
	COMMAND_UNCLAIM_LOG("%s unclaimed land at (%s) from the faction: %s"),
	COMMAND_UNCLAIM_WRONGFACTION("<b>You don't own this land."),
	COMMAND_UNCLAIM_TOUNCLAIM("to unclaim this land"),
	COMMAND_UNCLAIM_FORUNCLAIM("for unclaiming this land"),
	COMMAND_UNCLAIM_FACTIONUNCLAIMED("%s<i> unclaimed some land."),
	
	COMMAND_UNCLAIMALL_TOUNCLAIM("to unclaim all faction land"),
	COMMAND_UNCLAIMALL_FORUNCLAIM("for unclaiming all faction land"),
	COMMAND_UNCLAIMALL_UNCLAIMED("%s<i> unclaimed ALL of your faction's land."),
	COMMAND_UNCLAIMALL_LOG("%s unclaimed everything for the faction: %s"),
	
	COMMAND_VERSION_VERSION("<i>You are running %s"),
	
	COMMAND_WARUNCLAIMALL_SHORT("unclaim all warzone land"),
	COMMAND_WARUNCLAIMALL_SUCCESS("<i>You unclaimed ALL war zone land."),
	COMMAND_WARUNCLAIMALL_LOG("%s unclaimed all war zones."),
	
	COMMAND_RELATIONS_ALLTHENOPE("<b>Nope! You can't."),
	COMMAND_RELATIONS_MORENOPE("<b>Nope! You can't declare a relation to yourself :)"),
	COMMAND_RELATIONS_ALREADYINRELATIONSHIP("<b>You already have that relation wish set with %s."),
	COMMAND_RELATIONS_TOMARRY("to change a relation wish"),
	COMMAND_RELATIONS_FORMARRY("for changing a relation wish"),
	COMMAND_RELATIONS_MUTUAL("<i>Your faction is now %s<i> to %s"),
	COMMAND_RELATIONS_PEACEFUL("<i>This will have no effect while your faction is peaceful."),
	COMMAND_RELATIONS_PEACEFULOTHER("<i>This will have no effect while their faction is peaceful."),
	
	COMMAND_RELATIONS_PROPOSAL_1("%s<i> wishes to be your %s"),
	COMMAND_RELATIONS_PROPOSAL_2("<i>Type <c>/%s %s %s<i> to accept."),
	COMMAND_RELATIONS_PROPOSAL_SENT("%s<i> were informed that you wish to be %s"),
	/**
	 * More generic translations, which will apply to more than one class. 
	 */
	GENERIC_SERVERADMIN("generic.serveradmin","A server admin"),
	GENERIC_DISABLED("generic.disabled","disabled"),
	GENERIC_ENABLED("generic.enabled","enabled"),
	GENERIC_CONSOLEONLY("generic.consoleonly","This command cannot be run as a player."),
	GENERIC_ASKYOURLEADER("<i> Ask your leader to:"),
	GENERIC_YOUSHOULD("<i>You should:"),
	GENERIC_YOUMAYWANT("<i>You may want to: "),
	/**
	 * Relations
	 */
    RELATION_MEMBER("member"),
    RELATION_ALLY("ally"),
    RELATION_NEUTRAL("neutral"),
    RELATION_ENEMY("enemy"),
    /**
     * Strings lying around in other bits of the plugins
     */
    NOPAGES("<i>Sorry. No Pages available."),
    INVALIDPAGE("<i>Invalid page. Must be between 1 and %d"),
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
    DEFAULT_PREFIX("default-prefix", "{relationcolor}[{faction}] &r");

    private String path;
    private String def;
    private static YamlConfiguration LANG;

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
     * @param start The default string.
     */
    TL(String start) {
        this.path = this.name().replace('_', '.');
        if(this.path.startsWith(".")) path="root"+path;
        this.def = start;
    }

    /**
     * Set the {@code YamlConfiguration} to use.
     *
     * @param config The config to set.
     */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }

    @Override
    public String toString() {
        return this == TITLE ? ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)) + " " : ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }
    
    public String format(Object... args){
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