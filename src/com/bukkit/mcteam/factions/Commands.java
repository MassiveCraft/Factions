package com.bukkit.mcteam.factions;

import java.util.*;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import com.bukkit.mcteam.factions.entities.*;
import com.bukkit.mcteam.factions.struct.*;
import com.bukkit.mcteam.factions.util.*;

public class Commands {
	public static ArrayList<ArrayList<String>> helpPages;
	
	//----------------------------------------------//
	// Build the help pages
	//----------------------------------------------//
	
	static {
		helpPages = new ArrayList<ArrayList<String>>();
		ArrayList<String> pageLines;
		

		pageLines = new ArrayList<String>();
		pageLines.add(TextUtil.commandHelp(Conf.aliasHelp, "*[page]", "Display a help page"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasList, "", "List all factions"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasShow, "*[faction name]", "Show faction information")); // TODO display relations!
		pageLines.add(TextUtil.commandHelp(Conf.aliasMap, "*[on|off]", "Show territory map, set optional auto update."));
		pageLines.add(TextUtil.commandHelp(Conf.aliasJoin, "[faction name]", "Join a faction"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasLeave, "", "Leave your faction"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasChat, "[message]", "Send message to your faction only."));
		pageLines.add(TextUtil.commandHelp(Conf.aliasCreate, "[faction tag]", "Create new faction"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasTag, "[faction tag]", "Change the faction tag"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasDescription, "[description]", "Change the faction description"));
		
		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		pageLines.add(TextUtil.commandHelp(Conf.aliasOpen, "", "Switch if invitation is required to join"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasTitle, "[player name] *[title]", "Set or remove a players title"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasInvite, "[player name]", "Invite player"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasDeinvite, "[player name]", "Remove a pending invitation"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasClaim, "", "Claim the land where you are standing"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasUnclaim, "", "Unclaim the land where you are standing"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasKick, "[player name]", "Kick a player from the faction"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasModerator, "[player name]", "Give or revoke moderator rights"));
		pageLines.add(TextUtil.commandHelp(Conf.aliasAdmin, "[player name]", "Hand over your admin rights"));
		
		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add(TextUtil.commandHelp(Conf.aliasRelationAlly, "[faction name]", " "));
		pageLines.add(TextUtil.commandHelp(Conf.aliasRelationNeutral, "[faction name]", " "));
		pageLines.add(TextUtil.commandHelp(Conf.aliasRelationEnemy, "[faction name]", " "));
		pageLines.add("");
		pageLines.add(Conf.colorSystem+"Set which relation your WHISH you had to another faction.");
		pageLines.add(Conf.colorSystem+"Per default your relation to another faction will be neutral.");
		pageLines.add("");
		pageLines.add(Conf.colorSystem+"If BOTH factions wishes \"ally\" you will be allies.");
		pageLines.add(Conf.colorSystem+"If ONE faction wishes \"enemy\" you will be enemies.");
		
		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add(Conf.colorSystem+"You can never hurt members or allies.");
		pageLines.add(Conf.colorSystem+"You can not hurt neutrals in their own territory.");
		pageLines.add(Conf.colorSystem+"You can always hurt enemies and players without faction.");
		pageLines.add("");
		pageLines.add(Conf.colorSystem+"Damage from enemies are reduced in your own territory.");
		pageLines.add(Conf.colorSystem+"When you die you loose power. It is restored over time.");
		pageLines.add(Conf.colorSystem+"The power of a faction is the sum of all member power.");
		pageLines.add(Conf.colorSystem+"The power of a faction determines how much land it can hold.");
		pageLines.add(Conf.colorSystem+"You can claim land from a faction if it has to low power.");
		
		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add(Conf.colorSystem+"Only faction members can build and destroy in their own");
		pageLines.add(Conf.colorSystem+"territory. Usage of the following items is also restricted:");
		pageLines.add(Conf.colorSystem+"Door, Chest, Furnace and Dispenser.");
		pageLines.add(" ");
		pageLines.add(Conf.colorSystem+"Make sure to put pressure plates in front of doors for your");
		pageLines.add(Conf.colorSystem+"guest visitors. Otherwise they can't get through. You can ");
		pageLines.add(Conf.colorSystem+"also use this to create member only areas.");
		pageLines.add(Conf.colorSystem+"As dispensers are protected you can create traps without");
		pageLines.add(Conf.colorSystem+"worrying about those arrows getting stolen.");

		helpPages.add(pageLines);
		pageLines = new ArrayList<String>();
		
		pageLines.add(TextUtil.commandHelp(Conf.aliasVersion, "", "Wich version are you using"));
		
		helpPages.add(pageLines);
	}
	
	
	//----------------------------------------------//
	// Some utils
	//----------------------------------------------//
	
	// Update to work with tag and follower names
	
	public static Follower findFollower(Follower me, String name, boolean defaultsToMe) {
		if (name.length() == 0 && defaultsToMe) {
			return me;
		}
		
		Follower follower = Follower.find(name);
		if (follower != null) {
			return follower;
		}
		
		me.sendMessage(Conf.colorSystem+"The player \""+name+"\" could not be found");
		return null;
	}
	
	public static Faction findFaction(Follower me, String name, boolean defaultsToMe) {
		if (name.length() == 0 && defaultsToMe) {
			return me.getFaction();
		}
		
		// Search player names
		Follower follower = Follower.find(name);
		if (follower != null) {
			return follower.getFaction();
		}
		
		// Then faction names
		Faction faction = Faction.findByTag(name);
		if (faction != null) {
			return faction;
		}
		
		me.sendMessage(Conf.colorSystem+"No faction or player \""+name+"\" was found");
		return null;
	}
	
	public static boolean canIAdministerYou(Follower i, Follower you) {
		if ( ! i.getFaction().equals(you.getFaction())) {
			i.sendMessage(you.getNameAndRelevant(i)+Conf.colorSystem+" is not in the same faction as you.");
			return false;
		}
		
		if (i.role.value > you.role.value || i.role.equals(Role.ADMIN) ) {
			return true;
		}
		
		if (you.role.equals(Role.ADMIN)) {
			i.sendMessage(Conf.colorSystem+"Only the faction admin can do that.");
		} else if (i.role.equals(Role.MODERATOR)) {
			i.sendMessage(Conf.colorSystem+"Moderators can't controll eachother...");
		} else {
			i.sendMessage(Conf.colorSystem+"You must be a faction moderator to do that.");
		}
		
		return false;
	}
	
	//----------------------------------------------//
	// The base command
	//----------------------------------------------//
	
	public static void base(Follower me, ArrayList<String> tokens) {
		if (tokens.size() == 0) {
			help(me);
			return;
		}
		
		String command = tokens.get(0).toLowerCase();
		tokens.remove(0);
		
		if (Conf.aliasHelp.contains(command)) {
			int page = 1;
			if (tokens.size() > 0) {
				page = Integer.parseInt(tokens.get(0));
			}
			help(me, page);
		} else if (Conf.aliasLeave.contains(command)) {
			leave(me);
		} else if (Conf.aliasJoin.contains(command)) {
			join(me, TextUtil.implode(tokens));
		} else if (Conf.aliasCreate.contains(command)) {
			create(me, TextUtil.implode(tokens));
		} else if (Conf.aliasTag.contains(command)) {
			tag(me, TextUtil.implode(tokens));
		} else if (Conf.aliasDescription.contains(command)) {
			description(me, TextUtil.implode(tokens));
		} else if (Conf.aliasChat.contains(command)) {
			chat(me, TextUtil.implode(tokens));
		} else if (Conf.aliasList.contains(command)) {
			list(me);
		} else if (Conf.aliasShow.contains(command)) {
			showFaction(me, TextUtil.implode(tokens));
		} else if (Conf.aliasMap.contains(command)) {
			showMap(me, TextUtil.implode(tokens));
		} else if (Conf.aliasInvite.contains(command)) {
			invite(me, TextUtil.implode(tokens));
		} else if (Conf.aliasDeinvite.contains(command)) {
			deinvite(me, TextUtil.implode(tokens));
		} else if (Conf.aliasOpen.contains(command)) {
			open(me);
		} else if (Conf.aliasTitle.contains(command)) {
			title(me, tokens);
		} else if (Conf.aliasKick.contains(command)) {
			kick(me, TextUtil.implode(tokens));
		} else if (Conf.aliasModerator.contains(command)) {
			roleChange(me, Role.MODERATOR, TextUtil.implode(tokens));
		} else if (Conf.aliasAdmin.contains(command)) {
			roleChange(me, Role.ADMIN, TextUtil.implode(tokens));
		} else if (Conf.aliasClaim.contains(command)) {
			claim(me);
		} else if (Conf.aliasUnclaim.contains(command)) {
			unclaim(me);
		} else if (Conf.aliasRelationAlly.contains(command)) {
			relation(me, Relation.ALLY, TextUtil.implode(tokens));
		} else if (Conf.aliasRelationNeutral.contains(command)) {
			relation(me, Relation.NEUTRAL, TextUtil.implode(tokens));
		} else if (Conf.aliasRelationEnemy.contains(command)) {
			relation(me, Relation.ENEMY, TextUtil.implode(tokens));
		} else if (Conf.aliasVersion.contains(command)) {
			version(me);
		}  else {
			me.sendMessage(Conf.colorSystem+"Unknown faction command"+Conf.colorCommand+" "+command);
		}
	}
	
	//----------------------------------------------//
	// The other commands
	//----------------------------------------------//
	public static void help(Follower me) {
		help(me, 1);
	}
	
	public static void help(Follower me, Integer page) {
		me.sendMessage(TextUtil.titleize("Factions Help ("+page+"/"+helpPages.size()+")"), false);
		page -= 1;
		if (page < 0 || page >= helpPages.size()) {
			me.sendMessage(Conf.colorSystem+"That page does not exist");
			return;
		}
		me.sendMessage(helpPages.get(page), false);
	}
	
	public static void leave(Follower me) {
		Faction faction = me.getFaction();
		
		ArrayList<String> errors = me.leave();
		me.sendMessage(errors);
		
		if (errors.size() == 0) {
			faction.sendMessage(me.getNameAndRelevant(faction)+Conf.colorSystem+" left your faction.");
			me.sendMessage("You left "+faction.getTag(me));
		}
		
		if (faction.getFollowersAll().size() == 0) {
			// Remove this faction
			for (Follower follower : Follower.getAll()) {
				follower.sendMessage(Conf.colorSystem+"The faction "+faction.getTag(follower)+Conf.colorSystem+" was disbandoned.");
			}
			EM.factionDelete(faction.id);
		}
	}
	
	public static void join(Follower me, String name) {
		Faction faction = findFaction(me, name, false);
		if (faction == null) {
			return;
		}
		
		ArrayList<String> errors = me.join(faction);
		me.sendMessage(errors);
		
		if (errors.size() > 0) {
			faction.sendMessage(me.getNameAndRelevant(faction)+Conf.colorSystem+" tried to join your faction.");
		} else {
			me.sendMessage(Conf.colorSystem+"You successfully joined "+faction.getTag(me));
			faction.sendMessage(me.getNameAndRelevant(faction)+Conf.colorSystem+" joined your faction.");
		}
	}
	
	public static void create(Follower me, String tag) {
		ArrayList<String> errors = new ArrayList<String>();
		
		if (me.hasFaction()) {
			errors.add(Conf.colorSystem+"You must leave your current faction first.");
		}
		
		if (Faction.isTagTaken(tag)) {
			errors.add(Conf.colorSystem+"That tag is already in use.");
		}
		
		errors.addAll(Faction.validateTag(tag));
		
		if (errors.size() > 0) {
			me.sendMessage(errors);
			return;
		}
		
		Faction faction = EM.factionCreate();
		faction.setTag(tag);
		faction.save();
		me.join(faction);
		me.role = Role.ADMIN;
		me.save();
		
		for (Follower follower : Follower.getAll()) {
			follower.sendMessage(me.getNameAndRelevant(follower)+Conf.colorSystem+" created a new faction "+faction.getTag(follower));
		}
		
		me.sendMessage(Conf.colorSystem+"Now update your faction description. Use:");
		me.sendMessage(Conf.colorCommand+Conf.aliasBase.get(0)+" "+Conf.aliasDescription.get(0)+" "+"[description]");
	}
	
	public static void tag(Follower me, String tag) {
		ArrayList<String> errors = new ArrayList<String>();
		
		if (me.withoutFaction()) {
			errors.add(Conf.colorSystem+"You are not part of any faction");
		} else if (me.role.value < Role.MODERATOR.value) {
			errors.add(Conf.colorSystem+"You must be moderator to rename your faction");
		} 
		
		if (Faction.isTagTaken(tag) && ! TextUtil.getComparisonString(tag).equals(me.getFaction().getComparisonTag())) {
			errors.add(Conf.colorSystem+"That tag is already taken");
		}
		
		errors.addAll(Faction.validateTag(tag));
		
		if (errors.size() > 0) {
			me.sendMessage(errors);
			return;
		}

		Faction myFaction = me.getFaction();
		
		String oldtag = myFaction.getTag();
		myFaction.setTag(tag);
		
		// Inform
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed your faction tag to "+Conf.colorMember+myFaction.getTag());
		for (Faction faction : Faction.getAll()) {
			if (faction.id == me.factionId) {
				continue;
			}
			faction.sendMessage(Conf.colorSystem+"The faction "+me.getRelationColor(faction)+oldtag+Conf.colorSystem+" chainged their name to "+me.getRelationColor(faction)+myFaction.getTag());
		}
	}
	
	public static void list(Follower me) {
		me.sendMessage(TextUtil.titleize("Faction List"), false);
		for (Faction faction : Faction.getAll()) {
			if (faction.id == 0) {
				me.sendMessage(faction.getTag(me)+Conf.colorSystem+" "+faction.getFollowersWhereOnline(true).size() + " online");
			} else {
				me.sendMessage(faction.getTag(me)+Conf.colorSystem+" "+faction.getFollowersWhereOnline(true).size()+"/"+faction.getFollowersAll().size()+" online, "+faction.getLandRounded()+"/"+faction.getPowerRounded()+"/"+faction.getPowerMaxRounded());
			}
		}
	}
	
	public static void showFaction(Follower me, String name) {
		Faction faction = findFaction(me, name, true);
		if (faction == null) {
			return;
		}
		Collection<Follower> admins = faction.getFollowersWhereRole(Role.ADMIN);
		Collection<Follower> mods = faction.getFollowersWhereRole(Role.MODERATOR);
		Collection<Follower> normals = faction.getFollowersWhereRole(Role.NORMAL);
		
		me.sendMessage(TextUtil.titleize(faction.getTag(me)), false);
		me.sendMessage(Conf.colorChrome+"Description: "+Conf.colorSystem+faction.getDescription());
		if (faction.id == 0) {
			return;
		}
		
		if(faction.getOpen()) {
			me.sendMessage(Conf.colorChrome+"Joining: "+Conf.colorSystem+"no invitation is needed");
		} else {
			me.sendMessage(Conf.colorChrome+"Joining: "+Conf.colorSystem+"invitation is required");
		}
		me.sendMessage(Conf.colorChrome+"Land / Power / Maxpower: "+Conf.colorSystem+ faction.getLandRounded()+" / "+faction.getPowerRounded()+" / "+faction.getPowerMaxRounded());
	
		String listpart;
		
		// List relation
		String allyList = Conf.colorChrome+"Allies: ";
		String enemyList = Conf.colorChrome+"Enemies: ";
		for (Faction otherFaction : Faction.getAll()) {
			if (otherFaction == faction) {
				continue;
			}
			listpart = otherFaction.getTag(me)+Conf.colorSystem+", ";
			if (otherFaction.getRelation(faction) == Relation.ALLY) {
				allyList += listpart;
			} else if (otherFaction.getRelation(faction) == Relation.ENEMY) {
				enemyList += listpart;
			}
		}
		if (allyList.endsWith(", ")) {
			allyList = allyList.substring(0, allyList.length()-2);
		}
		if (enemyList.endsWith(", ")) {
			enemyList = enemyList.substring(0, enemyList.length()-2);
		}
		
		me.sendMessage(allyList);
		me.sendMessage(enemyList);
		
		// List the members...
		String onlineList = Conf.colorChrome+"Members online: ";
		String offlineList = Conf.colorChrome+"Members offline: ";
		for (Follower follower : admins) {
			listpart = follower.getNameAndTitle(me)+Conf.colorSystem+", ";
			if (follower.isOnline()) {
				onlineList += listpart;
			} else {
				offlineList += listpart;
			}
		}
		for (Follower follower : mods) {
			listpart = follower.getNameAndTitle(me)+Conf.colorSystem+", ";
			if (follower.isOnline()) {
				onlineList += listpart;
			} else {
				offlineList += listpart;
			}
		}
		for (Follower follower : normals) {
			listpart = follower.getNameAndTitle(me)+Conf.colorSystem+", ";
			if (follower.isOnline()) {
				onlineList += listpart;
			} else {
				offlineList += listpart;
			}
		}
		
		if (onlineList.endsWith(", ")) {
			onlineList = onlineList.substring(0, onlineList.length()-2);
		}
		if (offlineList.endsWith(", ")) {
			offlineList = offlineList.substring(0, offlineList.length()-2);
		}
		
		me.sendMessage(onlineList);
		me.sendMessage(offlineList);
	}
	
	
	public static void showMap(Follower me, String mapAutoUpdating) {
		if (mapAutoUpdating.length() > 0) {
			if (Conf.aliasTrue.contains(mapAutoUpdating.toLowerCase())) {
				// Turn on
				me.setMapAutoUpdating(true);
				me.sendMessage(Conf.colorSystem + "Map auto update ENABLED.");
				
				// And show the map once
				showMap(me,"");
			} else {
				// Turn off
				me.setMapAutoUpdating(false);
				me.sendMessage(Conf.colorSystem + "Map auto update DISABLED.");
			}
		} else {
			me.sendMessage(Board.getMap(me.getFaction(), Coord.from(me), me.getPlayer().getLocation().getYaw()), false);
		}
	}
	
	public static void invite(Follower me, String name) {
		Follower follower = findFollower(me, name, false);
		if (follower == null) {
			return;
		}
		
		ArrayList<String> errors = me.invite(follower);
		me.sendMessage(errors);
		
		if (errors.size() == 0) {
			follower.sendMessage(me.getNameAndRelevant(follower)+Conf.colorSystem+" invited you to "+me.getFaction().getTag(follower));
			me.getFaction().sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" invited "+follower.getNameAndRelevant(me)+Conf.colorSystem+" to your faction.");
		}
	}
	
	public static void deinvite(Follower me, String name) { // TODO Move out!
		Follower follower = findFollower(me, name, false);
		if (follower == null) {
			return;
		}
		
		ArrayList<String> errors = me.deinvite(follower);
		me.sendMessage(errors);
		
		if (errors.size() == 0) {
			follower.sendMessage(me.getNameAndRelevant(follower)+Conf.colorSystem+" revoked your invitation to "+me.getFaction().getTag(follower));
			me.getFaction().sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" revoked "+follower.getNameAndRelevant(me)+"'s"+Conf.colorSystem+" invitation.");
		}
	}
	
	public static void open(Follower me) {
		if (me.role.value < Role.MODERATOR.value) {
			me.sendMessage(Conf.colorSystem+"You must be moderator to do this");
			return;
		}
		Faction myFaction = me.getFaction();
		myFaction.setOpen( ! me.getFaction().getOpen());
		
		String open = myFaction.getOpen() ? "open" : "closed";
		
		// Inform
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed the faction to "+open);
		for (Faction faction : Faction.getAll()) {
			if (faction.id == me.factionId) {
				continue;
			}
			faction.sendMessage(Conf.colorSystem+"The faction "+myFaction.getTag(faction)+Conf.colorSystem+" is now "+open);
		}
	}
	
	public static void title(Follower me, ArrayList<String> tokens) {
		if (tokens.size() == 0) {
			me.sendMessage(Conf.colorSystem+"You must specify a player name");
			return;
		}
		
		String name = tokens.get(0);
		tokens.remove(0);
		
		Follower you = findFollower(me, name, true);
		if (you == null) {
			return;
		}
		
		if ( ! canIAdministerYou(me, you)) {
			return;
		}
		
		// All ok! Set the title!
		String title = TextUtil.implode(tokens);
		you.setTitle(title);
		
		// Inform
		Faction myFaction = me.getFaction();
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed a title: "+you.getNameAndRelevant(myFaction));
	}
	
	public static void kick(Follower me, String name) {
		if (name.length() == 0) {
			me.sendMessage(Conf.colorSystem+"You must specify a player name.");
			return;
		}
		
		Follower you = findFollower(me, name, false);
		if (you == null) {
			return;
		}
		
		ArrayList<String> errors = me.kick(you);
		me.sendMessage(errors);
		
		if (errors.size() == 0) {
			Faction myFaction = me.getFaction();
			myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" kicked "+you.getNameAndRelevant(myFaction)+Conf.colorSystem+" from the faction! :O");
			you.sendMessage(me.getNameAndRelevant(you)+Conf.colorSystem+" kicked you from "+myFaction.getTag(you)+Conf.colorSystem+"! :O");
		}
	}
	
	public static void roleChange(Follower me, Role targetRole, String name) {
		if (me.role.value < Role.ADMIN.value) {
			me.sendMessage(Conf.colorSystem+"You must be faction admin to do this");
			return;
		}
		
		if (name.length() == 0) {
			me.sendMessage(Conf.colorSystem+"You must specify a player name.");
			return;
		}
		
		Follower targetFollower = findFollower(me, name, false);
		if (targetFollower == null) {
			return;
		}
		
		if (targetFollower.factionId != me.factionId) {
			me.sendMessage(targetFollower.getNameAndRelevant(me)+Conf.colorSystem+" is not a member in your faction.");
			return;
		}
		
		if (targetFollower == me) {
			me.sendMessage(Conf.colorSystem+"The target player musn't be yourself.");
			return;
		}
		
		if (targetRole == Role.ADMIN) {
			me.role = Role.MODERATOR;
			targetFollower.role = Role.ADMIN;
			
			// Inform all players
			for (Follower follower : Follower.getAll()) {
				if (follower.factionId == me.factionId) {
					follower.sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" gave "+targetFollower.getNameAndRelevant(me)+Conf.colorSystem+" the leadership of your faction.");
				} else {
					follower.sendMessage(me.getNameAndRelevant(follower)+Conf.colorSystem+" gave "+targetFollower.getNameAndRelevant(follower)+Conf.colorSystem+" the leadership of "+me.getFaction().getTag(follower));
				}
			}
		} else if (targetRole == Role.MODERATOR) {
			if (targetFollower.role == Role.MODERATOR) {
				// Revoke
				targetFollower.role = Role.NORMAL;
				me.getFaction().sendMessage(targetFollower.getNameAndRelevant(me.getFaction())+Conf.colorSystem+" is no longer moderator in your faction.");
			} else {
				// Give
				targetFollower.role = Role.MODERATOR;
				me.getFaction().sendMessage(targetFollower.getNameAndRelevant(me.getFaction())+Conf.colorSystem+" was promoted to moderator in your faction.");
			}
		}
	}
	
	public static void claim(Follower me) {
		if (me.withoutFaction()) {
			me.sendMessage(Conf.colorSystem+"You are not part of any faction.");
			return;
		}
		
		Coord coord = Coord.from(me);
		Faction otherFaction = coord.getFaction();
		Faction myFaction = me.getFaction();
		
		if (myFaction.equals(otherFaction)) {
			me.sendMessage(Conf.colorSystem+"You already own this land.");
			return;
		}
		
		if (me.role.value < Role.MODERATOR.value) {
			me.sendMessage(Conf.colorSystem+"You must be moderator to claim land.");
			return;
		}
		
		if (myFaction.getLandRounded() >= myFaction.getPowerRounded()) {
			me.sendMessage(Conf.colorSystem+"You can't claim more land! You need more power!");
			return;
		}
		
		if (otherFaction.getRelation(me) == Relation.ALLY) {
			me.sendMessage(Conf.colorSystem+"You can't claim the land of your allies.");
			return;
		}
		
		if (otherFaction.id != 0) {
			if ( ! otherFaction.hasLandInflation()) { // TODO more messages WARN current faction most importantly
				me.sendMessage(me.getRelationColor(otherFaction)+otherFaction.getTag()+Conf.colorSystem+" owns this land and are strong enough to keep it.");
				return;
			}
			
			if ( ! Board.isBorderCoord(coord)) {
				me.sendMessage(Conf.colorSystem+"You must start claiming land at the border of the territory.");
				return;
			}
		}
		
		if (otherFaction.id == 0) {
			myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" claimed some new land :D");
		} else {
			// ASDF claimed some of your land 450 blocks NNW of you.
			// ASDf claimed some land from FACTION NAME
			otherFaction.sendMessage(me.getNameAndRelevant(otherFaction)+Conf.colorSystem+" stole some of your land :O");
			myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" claimed some land from "+otherFaction.getTag(myFaction));
		}
		
		Board.claim(coord, myFaction);
	}
	
	public static void unclaim(Follower me) {
		if (me.withoutFaction()) {
			me.sendMessage(Conf.colorSystem+"You are not part of any faction");
			return;
		}
		
		if (me.role.value < Role.MODERATOR.value) {
			me.sendMessage(Conf.colorSystem+"You must be moderator to unclaim land");
			return;
		}
		
		Coord coord = Coord.from(me.getPlayer());
		
		if ( ! me.getFaction().equals(coord.getFaction())) {
			me.sendMessage(Conf.colorSystem+"You don't own this land.");
			return;
		}
		
		Board.unclaim(coord);
		me.getFaction().sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" unclaimed some land.");
	}
	
	public static void relation(Follower me, Relation whishedRelation, String otherFactionName) {
		if (me.withoutFaction()) {
			me.sendMessage(Conf.colorSystem+"You are not part of any faction.");
			return;
		}
		
		if (me.role.value < Role.MODERATOR.value) {
			me.sendMessage(Conf.colorSystem+"You must be moderator to set relation to other factions.");
			return;
		}
		
		if (otherFactionName.length() == 0) {
			me.sendMessage(Conf.colorSystem+"You must specify another faction.");
			return;
		}
		
		Faction otherFaction = findFaction(me, otherFactionName, false);
		if (otherFaction == null) {
			return;
		}
		
		if (otherFaction.id == 0) {
			me.sendMessage(Conf.colorSystem+"Nope! You can't :) The default faction is not a real faction.");
			return;
		}
		
		if (otherFaction.equals(me.getFaction())) {
			me.sendMessage(Conf.colorSystem+"Nope! You can't declare a relation to yourself :)");
			return;
		}
		
		Faction myFaction = me.getFaction();
		myFaction.setRelationWish(otherFaction, whishedRelation);
		Relation currentRelation = myFaction.getRelation(otherFaction);
		ChatColor currentRelationColor = currentRelation.getColor();
		if (whishedRelation == currentRelation) {
			otherFaction.sendMessage(Conf.colorSystem+"Your faction is now "+currentRelationColor+whishedRelation.toString()+Conf.colorSystem+" to "+currentRelationColor+myFaction.getTag());
			myFaction.sendMessage(Conf.colorSystem+"Your faction is now "+currentRelationColor+whishedRelation.toString()+Conf.colorSystem+" to "+currentRelationColor+otherFaction.getTag());
		} else {
			otherFaction.sendMessage(currentRelationColor+myFaction.getTag()+Conf.colorSystem+ " whishes to be your "+whishedRelation.getColor()+whishedRelation.toString());
			otherFaction.sendMessage(Conf.colorSystem+"Type "+Conf.colorCommand+Conf.aliasBase.get(0)+" "+whishedRelation+" "+myFaction.getTag()+Conf.colorSystem+" to accept.");
			myFaction.sendMessage(currentRelationColor+otherFaction.getTag()+Conf.colorSystem+ " were informed you wishes to be "+whishedRelation.getColor()+whishedRelation);
		}
	}
	
	public static void description(Follower me, String desc) {
		if (me.withoutFaction()) {
			me.sendMessage(Conf.colorSystem+"You are not part of any faction");
			return;
		}
		
		if (me.role.value < Role.MODERATOR.value) {
			me.sendMessage(Conf.colorSystem+"You must be moderator to set the description");
			return;
		}
		
		me.getFaction().setDescription(desc);
		
		me.sendMessage(Conf.colorSystem+"The new decription was set :D");
		
		// Broadcast the description to everyone
		for (Follower follower : EM.followerGetAll()) {
			follower.sendMessage(Conf.colorSystem+"The faction "+follower.getRelationColor(me)+me.getFaction().getTag()+Conf.colorSystem+" changed their description to:");
			follower.sendMessage(Conf.colorSystem+desc);
		}
	}
	
	public static void chat(Follower me, String msg) {
		if (me.withoutFaction()) {
			me.sendMessage(Conf.colorSystem+"You are not part of any faction");
			return;
		}
		String message = String.format(Conf.factionChatFormat, me.getNameAndRelevant(me), msg);
		
		me.getFaction().sendMessage(message, false);
		Logger.getLogger("Minecraft").info("FactionChat "+me.getFaction().getTag()+": "+message);
	}
	
	public static void version(Follower me) {
		me.sendMessage(Conf.colorSystem+"You are running "+Factions.desc.getFullName());
	}
}










