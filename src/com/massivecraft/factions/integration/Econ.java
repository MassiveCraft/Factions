package com.massivecraft.factions.integration;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.nijikokun.register.Register;
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Method.MethodAccount;
import com.nijikokun.register.payment.Methods;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.util.RelationUtil;

public class Econ
{
	private static Register register = null;
	
	public static Method getMethod()
	{
		if ( ! isSetup()) return null;
		return Methods.getMethod();
	}
	
	public static boolean shouldBeUsed()
	{
		return Conf.econEnabled && register != null && register.isEnabled() && getMethod() != null;
	}
	
	public static boolean isSetup()
	{
		return register != null;
	}
	
	public static void doSetup()
	{
		if (isSetup()) return;
		
		Plugin plug = Bukkit.getServer().getPluginManager().getPlugin("Register");
		
		if (plug != null && plug.getClass().getName().equals("com.nijikokun.register.Register"))
		{
			register = (Register)plug;
				
			P.p.log("Economy integration through Register plugin successful.");

			if ( ! Conf.econEnabled)
				P.p.log("NOTE: Economy is disabled. Enable in conf \"econEnabled\": true");
		}
		else
		{
			P.p.log("Economy integration is "+(Conf.econEnabled ? "enabled, but" : "disabled, and")+" the plugin \"Register\" is not installed.");
		}
		
		P.p.cmdBase.cmdHelp.updateHelp();
	}
	
	public static MethodAccount getUniverseAccount()
	{
		if (Conf.econUniverseAccount == null) return null;
		if (Conf.econUniverseAccount.length() == 0) return null;
		return getMethod().getAccount(Conf.econUniverseAccount);
	}
	
	public static void modifyUniverseMoney(double delta)
	{
		if (!shouldBeUsed()) return;

		MethodAccount acc = getUniverseAccount();
		if (acc == null) return;
		acc.add(delta);
	}
	
	public static void sendBalanceInfo(FPlayer to, EconomyParticipator about)
	{
		to.msg("<a>%s's<i> balance is <h>%s<i>.", about.describeTo(to, true), Econ.moneyString(about.getAccount().balance()));
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
		i.msg("<h>%s<i> lack permission to controll <h>%s's<i> money.", i.describeTo(i, true), you.describeTo(i));
		return false;
	}
	
	public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount)
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
		if ( ! from.getAccount().hasEnough(amount))
		{
			// There was not enough money to pay
			if (invoker != null)
			{
				invoker.msg("<h>%s<b> can't afford to transfer <h>%s<b> to %s<b>.", from.describeTo(invoker, true), moneyString(amount), to.describeTo(invoker));
			}
			return false;
		}
		
		// Transfer money
		from.getAccount().subtract(amount);
		to.getAccount().add(amount);
		
		// Inform
		sendTransferInfo(invoker, from, to, amount);
		
		return true;
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
	
	public static boolean modifyMoney(EconomyParticipator ep, double delta, String toDoThis, String forDoingThis)
	{
		if ( ! shouldBeUsed()) return false;

		MethodAccount acc = ep.getAccount();
		String You = ep.describeTo(ep, true);
		
		if (delta >= 0)
		{
			// The player should gain money
			// There is no risk of failure
			acc.add(delta);
			modifyUniverseMoney(-delta);
			ep.msg("<h>%s<i> gained <h>%s<i> %s.", You, moneyString(delta), forDoingThis);
			return true;
		}
		else
		{
			// The player should loose money
			// The player might not have enough.
			
			if (acc.hasEnough(-delta))
			{
				// There is enough money to pay
				acc.add(delta);
				modifyUniverseMoney(-delta);
				ep.msg("<h>%s<i> lost <h>%s<i> %s.", You, moneyString(-delta), forDoingThis);
				return true;
			}
			else
			{
				// There was not enough money to pay
				ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", You, moneyString(-delta), toDoThis);
				return false;
			}
		}
	}

	// format money string based on server's set currency type, like "24 gold" or "$24.50"
	public static String moneyString(double amount)
	{
		return getMethod().format(amount);
	}
	
	public static void oldMoneyDoTransfer()
	{
		if ( ! shouldBeUsed()) return;
		
		for (Faction faction : Factions.i.get())
		{
			faction.getAccount().add(faction.money);
			faction.money = 0;
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
}
