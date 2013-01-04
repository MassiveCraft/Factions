package com.massivecraft.factions.integration;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.util.RelationUtil;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;


public class Econ
{
	private static Economy econ = null;

	public static void setup()
	{
		if (isSetup()) return;

		String integrationFail = "Economy integration is "+(Conf.econEnabled ? "enabled, but" : "disabled, and")+" the plugin \"Vault\" ";

		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null)
		{
			P.p.log(integrationFail+"is not installed.");
			return;
		}

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null)
		{
			P.p.log(integrationFail+"is not hooked into an economy plugin.");
			return;
		}
		econ = rsp.getProvider();

		P.p.log("Economy integration through Vault plugin successful.");

		if ( ! Conf.econEnabled)
			P.p.log("NOTE: Economy is disabled. You can enable it with the command: f config econEnabled true");

		oldMoneyDoTransfer();
	}

	public static boolean shouldBeUsed()
	{
		return Conf.econEnabled && econ != null && econ.isEnabled();
	}
	
	public static boolean isSetup()
	{
		return econ != null;
	}


	public static void modifyUniverseMoney(double delta)
	{
		if (!shouldBeUsed()) return;

		if (Conf.econUniverseAccount == null) return;
		if (Conf.econUniverseAccount.length() == 0) return;
		if ( ! econ.hasAccount(Conf.econUniverseAccount)) return;

		modifyBalance(Conf.econUniverseAccount, delta);
	}

	public static void sendBalanceInfo(FPlayer to, EconomyParticipator about)
	{
		if (!shouldBeUsed())
		{
			P.p.log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
			return;
		}
		to.msg("<a>%s's<i> balance is <h>%s<i>.", about.describeTo(to, true), Econ.moneyString(econ.getBalance(about.getAccountId())));
	}

	public static boolean canIControllYou(EconomyParticipator i, EconomyParticipator you)
	{
		Faction fI = RelationUtil.getFaction(i);
		Faction fYou = RelationUtil.getFaction(you);
		
		// This is a system invoker. Accept it.
		if (fI == null) return true;
		
		// Bypassing players can do any kind of transaction
		if (i instanceof FPlayer && ((FPlayer)i).hasAdminMode()) return true;
		
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
		if ( ! shouldBeUsed()) return false;

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
		if ( ! econ.has(from.getAccountId(), amount))
		{
			// There was not enough money to pay
			if (invoker != null && notify)
				invoker.msg("<h>%s<b> can't afford to transfer <h>%s<b> to %s<b>.", from.describeTo(invoker, true), moneyString(amount), to.describeTo(invoker));

			return false;
		}
		
		// Transfer money
		EconomyResponse erw = econ.withdrawPlayer(from.getAccountId(), amount);
		
		if (erw.transactionSuccess()) {
			EconomyResponse erd = econ.depositPlayer(to.getAccountId(), amount);
			if (erd.transactionSuccess()) {
				if (notify) sendTransferInfo(invoker, from, to, amount);
				return true;
			} else {
				// transaction failed, refund account
				econ.depositPlayer(from.getAccountId(), amount);
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
		if ( ! shouldBeUsed()) return true;

		if ( ! econ.has(ep.getAccountId(), delta))
		{
			if (toDoThis != null && !toDoThis.isEmpty())
				ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", ep.describeTo(ep, true), moneyString(delta), toDoThis);
			return false;
		}
		return true;
	}

	public static boolean modifyMoney(EconomyParticipator ep, double delta, String toDoThis, String forDoingThis)
	{
		if ( ! shouldBeUsed()) return false;

		String acc = ep.getAccountId();
		String You = ep.describeTo(ep, true);
		
		if (delta == 0)
		{
			// no money actually transferred?
//			ep.msg("<h>%s<i> didn't have to pay anything %s.", You, forDoingThis);  // might be for gains, might be for losses
			return true;
		}

		if (delta > 0)
		{
			// The player should gain money
			// The account might not have enough space
			EconomyResponse er = econ.depositPlayer(acc, delta);
			if (er.transactionSuccess()) {
				modifyUniverseMoney(-delta);
				if (forDoingThis != null && !forDoingThis.isEmpty())
					ep.msg("<h>%s<i> gained <h>%s<i> %s.", You, moneyString(delta), forDoingThis);
				return true;
			} else {
				// transfer to account failed
				if (forDoingThis != null && !forDoingThis.isEmpty())
					ep.msg("<h>%s<i> would have gained <h>%s<i> %s, but the deposit failed.", You, moneyString(delta), forDoingThis);
				return false;
			}
			
		}
		else
		{
			// The player should loose money
			// The player might not have enough.
			
			if (econ.has(acc, -delta) && econ.withdrawPlayer(acc, -delta).transactionSuccess())
			{
				// There is enough money to pay
				modifyUniverseMoney(-delta);
				if (forDoingThis != null && !forDoingThis.isEmpty())
					ep.msg("<h>%s<i> lost <h>%s<i> %s.", You, moneyString(-delta), forDoingThis);
				return true;
			}
			else
			{
				// There was not enough money to pay
				if (toDoThis != null && !toDoThis.isEmpty())
					ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", You, moneyString(-delta), toDoThis);
				return false;
			}
		}
	}

	// format money string based on server's set currency type, like "24 gold" or "$24.50"
	public static String moneyString(double amount)
	{
		return econ.format(amount);
	}
	
	public static void oldMoneyDoTransfer()
	{
		if ( ! shouldBeUsed()) return;
		
		for (Faction faction : Factions.i.get())
		{
			if (faction.money > 0)
			{
				econ.depositPlayer(faction.getAccountId(), faction.money);
				faction.money = 0;
			}
		}
	}

	// calculate the cost for claiming land
	public static double calculateClaimCost(int ownedLand, boolean takingFromAnotherFaction)
	{
		if ( ! shouldBeUsed())
		{
			return 0d;
		}

		// basic claim cost, plus land inflation cost, minus the potential bonus given for claiming from another faction
		return Conf.econCostClaimWilderness
			+ (Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * ownedLand)
			- (takingFromAnotherFaction ? Conf.econCostClaimFromFactionBonus: 0);
	}

	// calculate refund amount for unclaiming land
	public static double calculateClaimRefund(int ownedLand)
	{
		return calculateClaimCost(ownedLand - 1, false) * Conf.econClaimRefundMultiplier;
	}

	// calculate value of all owned land
	public static double calculateTotalLandValue(int ownedLand)
	{
		double amount = 0;
		for (int x = 0; x < ownedLand; x++) {
			amount += calculateClaimCost(x, false);
		}
		return amount;
	}

	// calculate refund amount for all owned land
	public static double calculateTotalLandRefund(int ownedLand)
	{
		return calculateTotalLandValue(ownedLand) * Conf.econClaimRefundMultiplier;
	}


	// -------------------------------------------- //
	// Standard account management methods
	// -------------------------------------------- //

	public static boolean hasAccount(String name)
	{
		return econ.hasAccount(name);
	}

	public static double getBalance(String account)
	{
		return econ.getBalance(account);
	}

	public static boolean setBalance(String account, double amount)
	{
		double current = econ.getBalance(account);
		if (current > amount)
			return econ.withdrawPlayer(account, current - amount).transactionSuccess();
		else
			return econ.depositPlayer(account, amount - current).transactionSuccess();
	}

	public static boolean modifyBalance(String account, double amount)
	{
		if (amount < 0)
			return econ.withdrawPlayer(account, -amount).transactionSuccess();
		else
			return econ.depositPlayer(account, amount).transactionSuccess();
	}

	public static boolean deposit(String account, double amount)
	{
		return econ.depositPlayer(account, amount).transactionSuccess();
	}

	public static boolean withdraw(String account, double amount)
	{
		return econ.withdrawPlayer(account, amount).transactionSuccess();
	}
}
