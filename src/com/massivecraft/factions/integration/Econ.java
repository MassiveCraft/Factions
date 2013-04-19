package com.massivecraft.factions.integration;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.util.RelationUtil;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;


public class Econ
{
	// -------------------------------------------- //
	// DERPY OLDSCHOOL SETUP
	// -------------------------------------------- //
	
	public static void setup()
	{
		if (economy != null) return;

		String integrationFail = "Economy integration is "+(ConfServer.econEnabled ? "enabled, but" : "disabled, and")+" the plugin \"Vault\" ";

		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
		{
			Factions.get().log(integrationFail+"is not installed.");
			return;
		}

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
		{
			Factions.get().log(integrationFail+"is not hooked into an economy plugin.");
			return;
		}
		economy = rsp.getProvider();

		Factions.get().log("Economy integration through Vault plugin successful.");

		if ( ! ConfServer.econEnabled)
			Factions.get().log("NOTE: Economy is disabled. You can enable it with the command: f config econEnabled true");
	}

	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private static Economy economy = null;
	
	// -------------------------------------------- //
	// STATE
	// -------------------------------------------- //
	
	public static boolean isEnabled()
	{
		return ConfServer.econEnabled && economy != null && economy.isEnabled();
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static boolean payForAction(double cost, CommandSender sender, String actionDescription)
	{
		if (!isEnabled()) return true;
		if (cost == 0D) return true;
		
		FPlayer fsender = FPlayer.get(sender);
		if (fsender.isUsingAdminMode()) return true;
		Faction fsenderFaction = fsender.getFaction();

		if (ConfServer.bankEnabled && ConfServer.bankFactionPaysCosts && fsenderFaction.isNormal())
		{
			return modifyMoney(fsenderFaction, -cost, actionDescription);
		}
		else
		{
			return modifyMoney(fsender, -cost, actionDescription);
		}
	}
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //

	public static void modifyUniverseMoney(double delta)
	{
		if (!isEnabled()) return;

		if (ConfServer.econUniverseAccount == null) return;
		if (ConfServer.econUniverseAccount.length() == 0) return;
		if ( ! economy.hasAccount(ConfServer.econUniverseAccount)) return;

		modifyBalance(ConfServer.econUniverseAccount, delta);
	}

	public static void sendBalanceInfo(FPlayer to, EconomyParticipator about)
	{
		if (!isEnabled())
		{
			Factions.get().log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
			return;
		}
		to.msg("<a>%s's<i> balance is <h>%s<i>.", about.describeTo(to, true), Econ.moneyString(economy.getBalance(about.getAccountId())));
	}

	public static boolean canIControllYou(EconomyParticipator i, EconomyParticipator you)
	{
		Faction fI = RelationUtil.getFaction(i);
		Faction fYou = RelationUtil.getFaction(you);
		
		// This is a system invoker. Accept it.
		if (fI == null) return true;
		
		// Bypassing players can do any kind of transaction
		if (i instanceof FPlayer && ((FPlayer)i).isUsingAdminMode()) return true;
		
		// You can deposit to anywhere you feel like. It's your loss if you can't withdraw it again.
		if (i == you) return true;
		
		// A faction can always transfer away the money of it's members and its own money...
		// This will however probably never happen as a faction does not have free will.
		// Ohh by the way... Yes it could. For daily rent to the faction.
		if (i == fI && fI == fYou) return true;
		
		// Factions can be controlled by those that have permissions
		if (you instanceof Faction && FPerm.WITHDRAW.has(i, fYou)) return true;
		
		// Otherwise you may not! ;,,;
		i.msg("<h>%s<i> lacks permission to control <h>%s's<i> money.", i.describeTo(i, true), you.describeTo(i));
		return false;
	}
	
	public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount)
	{
		return transferMoney(invoker, from, to, amount, true);
	}
	public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount, boolean notify)
	{
		if ( ! isEnabled()) return false;

		// The amount must be positive.
		// If the amount is negative we must flip and multiply amount with -1.
		if (amount < 0)
		{
			amount *= -1;
			EconomyParticipator temp = from;
			from = to;
			to = temp;
		}
		
		// Check the rights
		if ( ! canIControllYou(invoker, from)) return false;
		
		// Is there enough money for the transaction to happen?
		if ( ! economy.has(from.getAccountId(), amount))
		{
			// There was not enough money to pay
			if (invoker != null && notify)
			{
				invoker.msg("<h>%s<b> can't afford to transfer <h>%s<b> to %s<b>.", from.describeTo(invoker, true), moneyString(amount), to.describeTo(invoker));
			}
			return false;
		}
		
		// Transfer money
		EconomyResponse erw = economy.withdrawPlayer(from.getAccountId(), amount);
		
		if (erw.transactionSuccess())
		{
			EconomyResponse erd = economy.depositPlayer(to.getAccountId(), amount);
			if (erd.transactionSuccess())
			{
				if (notify)
				{
					sendTransferInfo(invoker, from, to, amount);
				}
				return true;
			}
			else
			{
				// transaction failed, refund account
				economy.depositPlayer(from.getAccountId(), amount);
			}
		}
		
		// if we get here something with the transaction failed
		if (notify)
			invoker.msg("Unable to transfer %s<b> to <h>%s<b> from <h>%s<b>.", moneyString(amount), to.describeTo(invoker), from.describeTo(invoker, true));
			
		
		return false;
	}
	
	public static Set<FPlayer> getFplayers(EconomyParticipator ep)
	{
		Set<FPlayer> fplayers = new HashSet<FPlayer>();
		
		if (ep == null)
		{
			// Add nothing
		}
		else if (ep instanceof FPlayer)
		{
			fplayers.add((FPlayer)ep);
		}
		else if (ep instanceof Faction)
		{
			fplayers.addAll(((Faction)ep).getFPlayers());
		}
		
		return fplayers;
	}
	
	public static void sendTransferInfo(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount)
	{
		Set<FPlayer> recipients = new HashSet<FPlayer>();
		recipients.addAll(getFplayers(invoker));
		recipients.addAll(getFplayers(from));
		recipients.addAll(getFplayers(to));
		
		if (invoker == null)
		{
			for (FPlayer recipient : recipients)
			{
				recipient.msg("<h>%s<i> was transfered from <h>%s<i> to <h>%s<i>.", moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		}
		else if (invoker == from)
		{
			for (FPlayer recipient : recipients)
			{
				recipient.msg("<h>%s<i> <h>gave %s<i> to <h>%s<i>.", from.describeTo(recipient, true), moneyString(amount), to.describeTo(recipient));
			}
		}
		else if (invoker == to)
		{
			for (FPlayer recipient : recipients)
			{
				recipient.msg("<h>%s<i> <h>took %s<i> from <h>%s<i>.", to.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient));
			}
		}
		else
		{
			for (FPlayer recipient : recipients)
			{
				recipient.msg("<h>%s<i> transfered <h>%s<i> from <h>%s<i> to <h>%s<i>.", invoker.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		}
	}

	public static boolean hasAtLeast(EconomyParticipator ep, double delta, String toDoThis)
	{
		if (!isEnabled()) return true;

		if ( ! economy.has(ep.getAccountId(), delta))
		{
			if (toDoThis != null && !toDoThis.isEmpty())
				ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", ep.describeTo(ep, true), moneyString(delta), toDoThis);
			return false;
		}
		return true;
	}

	public static boolean modifyMoney(EconomyParticipator ep, double delta, String actionDescription)
	{
		if (!isEnabled()) return false;
		if (delta == 0) return true;
		
		String accountId = ep.getAccountId();
		String You = ep.describeTo(ep, true);
		
		boolean hasActionDesctription = (actionDescription != null && !actionDescription.isEmpty());

		if (delta > 0)
		{
			// The player should gain money
			// The account might not have enough space
			EconomyResponse er = economy.depositPlayer(accountId, delta);
			if (er.transactionSuccess())
			{
				modifyUniverseMoney(-delta);
				if (hasActionDesctription)
				{
					ep.msg("<h>%s<i> gained <h>%s<i> since did %s.", You, moneyString(delta), actionDescription);
				}
				return true;
			}
			else
			{
				// transfer to account failed
				if (hasActionDesctription)
				{
					ep.msg("<h>%s<i> would have gained <h>%s<i> since did %s, but the deposit failed.", You, moneyString(delta), actionDescription);
				}
				return false;
			}
			
		}
		else
		{
			// The player should loose money
			// The player might not have enough.
			EconomyResponse er = economy.withdrawPlayer(accountId, -delta);
			if (er.transactionSuccess())
			{
				// There is enough money to pay
				modifyUniverseMoney(-delta);
				if (hasActionDesctription)
				{
					ep.msg("<h>%s<i> lost <h>%s<i> since did %s.", You, moneyString(delta), actionDescription);
				}
				return true;
			}
			else
			{
				// There was not enough money to pay
				if (hasActionDesctription)
				{
					ep.msg("<h>%s<i> can't afford <h>%s<i> to %s.", You, moneyString(-delta), actionDescription);
				}
				return false;
			}
		}
	}

	// format money string based on server's set currency type, like "24 gold" or "$24.50"
	public static String moneyString(double amount)
	{
		return economy.format(amount);
	}

	// calculate the cost for claiming land
	public static double calculateClaimCost(int ownedLand, boolean takingFromAnotherFaction)
	{
		if (!isEnabled()) return 0D;
		
		// basic claim cost, plus land inflation cost, minus the potential bonus given for claiming from another faction
		return ConfServer.econCostClaimWilderness
			+ (ConfServer.econCostClaimWilderness * ConfServer.econClaimAdditionalMultiplier * ownedLand)
			- (takingFromAnotherFaction ? ConfServer.econCostClaimFromFactionBonus: 0);
	}

	// calculate refund amount for unclaiming land
	public static double calculateClaimRefund(int ownedLand)
	{
		return calculateClaimCost(ownedLand - 1, false) * ConfServer.econClaimRefundMultiplier;
	}

	// calculate value of all owned land
	public static double calculateTotalLandValue(int ownedLand)
	{
		double amount = 0;
		for (int x = 0; x < ownedLand; x++)
		{
			amount += calculateClaimCost(x, false);
		}
		return amount;
	}

	// calculate refund amount for all owned land
	public static double calculateTotalLandRefund(int ownedLand)
	{
		return calculateTotalLandValue(ownedLand) * ConfServer.econClaimRefundMultiplier;
	}

	// -------------------------------------------- //
	// Standard account management methods
	// -------------------------------------------- //

	public static boolean hasAccount(String name)
	{
		return economy.hasAccount(name);
	}

	public static double getBalance(String account)
	{
		return economy.getBalance(account);
	}

	public static boolean setBalance(String account, double amount)
	{
		double current = economy.getBalance(account);
		if (current > amount)
			return economy.withdrawPlayer(account, current - amount).transactionSuccess();
		else
			return economy.depositPlayer(account, amount - current).transactionSuccess();
	}

	public static boolean modifyBalance(String account, double amount)
	{
		if (amount < 0)
			return economy.withdrawPlayer(account, -amount).transactionSuccess();
		else
			return economy.depositPlayer(account, amount).transactionSuccess();
	}

	public static boolean deposit(String account, double amount)
	{
		return economy.depositPlayer(account, amount).transactionSuccess();
	}

	public static boolean withdraw(String account, double amount)
	{
		return economy.withdrawPlayer(account, amount).transactionSuccess();
	}
}
