package com.massivecraft.factions;

import org.bukkit.event.Event;

import com.massivecraft.factions.listeners.FactionsServerListener;

import com.iConomy.*;
import com.iConomy.system.*;


public class Econ {
	private static iConomy iConomyPlugin;

	public static void monitorPlugins() {
		Factions.instance.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, new FactionsServerListener(), Event.Priority.Monitor, Factions.instance);
		Factions.instance.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, new FactionsServerListener(), Event.Priority.Monitor, Factions.instance);
	}

	public static void iConomySet(iConomy instance) {
		iConomyPlugin = instance;
	}

	public static boolean iConomyHooked() {
		return iConomyPlugin != null;
	}

	// If economy is enabled in conf.json, and we're successfully hooked into an economy plugin
	public static boolean enabled() {
		return Conf.econIConomyEnabled && iConomyPlugin != null;
	}

	// mainly for internal use, for a little less code repetition
	public static Holdings getIconomyHoldings(String playerName) {
		if (!enabled()) {
			return null;
		}

		Account account = iConomy.getAccount(playerName);
		if (account == null) {
			return null;
		}
		Holdings holdings = account.getHoldings();
		return holdings;
	}


	// format money string based on server's set currency type, like "24 gold" or "$24.50"
	public static String moneyString(double amount) {
		return iConomy.format(amount);
	}

	// whether a player can afford specified amount
	public static boolean canAfford(String playerName, double amount) {
		// if Economy support is not enabled, they can certainly afford to pay nothing
		if (!enabled()) {
			return true;
		}

		Holdings holdings = getIconomyHoldings(playerName);
		if (holdings == null) {
			return false;
		}

		return holdings.hasEnough(amount);
	}

	// deduct money from their account; returns true if successful
	public static boolean deductMoney(String playerName, double amount) {
		if (!enabled()) {
			return true;
		}

		Holdings holdings = getIconomyHoldings(playerName);
		if (holdings == null || !holdings.hasEnough(amount)) {
			return false;
		}

		holdings.subtract(amount);
		return true;
	}

	// add money to their account; returns true if successful
	public static boolean addMoney(String playerName, double amount) {
		if (!enabled()) {
			return true;
		}

		Holdings holdings = getIconomyHoldings(playerName);
		if (holdings == null) {
			return false;
		}

		holdings.add(amount);
		return true;
	}


	// calculate the cost for claiming land
	public static double calculateClaimCost(int ownedLand, boolean takingFromAnotherFaction) {
		if (!enabled()) {
			return 0.0;
		}

		// basic claim cost, plus land inflation cost, minus the potential bonus given for claiming from another faction
		return Conf.econCostClaimWilderness
			+ (Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * ownedLand)
			- (takingFromAnotherFaction ? Conf.econCostClaimFromFactionBonus: 0);
	}

	// calculate refund amount for unclaiming land
	public static double calculateClaimRefund(int ownedLand) {
		return calculateClaimCost(ownedLand - 1, false) * Conf.econClaimRefundMultiplier;
	}

	// calculate value of all owned land
	public static double calculateTotalLandValue(int ownedLand) {
		double amount = 0;
		for (int x = 0; x < ownedLand; x++) {
			amount += calculateClaimCost(x, false);
		}
		return amount;
	}

	// calculate refund amount for all owned land
	public static double calculateTotalLandRefund(int ownedLand) {
		return calculateTotalLandValue(ownedLand) * Conf.econClaimRefundMultiplier;
	}
}
