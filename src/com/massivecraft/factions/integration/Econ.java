package com.massivecraft.factions.integration;

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
import com.massivecraft.factions.struct.Role;
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
				
			P.p.log("Economy integration through register successful.");
			if ( ! Conf.econEnabled)
			{
				P.p.log("NOTE: Economy is disabled. Enable in conf \"econEnabled\": true");
			}
		}
		else
		{
			P.p.log("Economy integration failed. The plugin \"Register\" is not installed.");
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
		MethodAccount acc = getUniverseAccount();
		if (acc == null) return;
		acc.add(delta);
	}
	
	public static boolean canInvokerTransferFrom(EconomyParticipator invoker, EconomyParticipator from)
	{
		Faction fInvoker = RelationUtil.getFaction(invoker);
		Faction fFrom = RelationUtil.getFaction(from);
		
		// This is a system invoker. Accept it.
		if (fInvoker == null) return true;
		
		// Bypassing players can do any kind of transaction
		if (invoker instanceof FPlayer && ((FPlayer)invoker).isAdminBypassing()) return true;
		
		// You can deposit to anywhere you feel like. It's your loss if you can't withdraw it again.
		if (invoker == from) return true;
		
		// A faction can always transfer away the money of it's members and its own money...
		// This will however probably never happen as a faction does not have free will.
		// Ohh by the way... Yes it could. For daily rent to the faction.
		if (invoker == fInvoker && fInvoker == fFrom) return true;
		
		// If you are part of the same faction as from and members can withdraw or you are at least moderator... then it is ok.
		if (fInvoker == fFrom && (Conf.bankMembersCanWithdraw || ((FPlayer)invoker).getRole().value < Role.MODERATOR.value)) return true;
		
		// Otherwise you may not! ;,,;
		invoker.msg("<h>%s<b> don't have the right to transfer money from <h>%s<b>.", invoker.describeTo(invoker, true), from.describeTo(invoker));
		return false;
	}
	
	public static boolean transferMoney(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount)
	{
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
		if ( ! canInvokerTransferFrom(invoker, from)) return false;
		
		//Faction fFrom = RelationUtil.getFaction(from);
		//Faction fTo = RelationUtil.getFaction(to);
		//Faction fInvoker = RelationUtil.getFaction(invoker);
		
		// Is there enough money for the transaction to happen?
		
		P.p.log("from "+from);
		P.p.log("from.getAccount() "+from.getAccount());
		
		if ( ! from.getAccount().hasEnough(amount))
		{
			// There was not enough money to pay
			if (invoker != null)
			{
				invoker.msg("<h>%s<b> can't afford to transfer <h>%s<b> to %s.", from.describeTo(invoker, true), moneyString(amount), to.describeTo(invoker));
			}
			return false;
		}
		
		// Transfer money
		from.getAccount().subtract(amount);
		to.getAccount().add(amount);
		
		// Inform
		if (invoker == null)
		{
			from.msg("<h>%s<i> was transfered from <h>%s<i> to <h>%s<i>.", moneyString(amount), from.describeTo(from), to.describeTo(from));
			to.msg  ("<h>%s<i> was transfered from <h>%s<i> to <h>%s<i>.", moneyString(amount), from.describeTo(to), to.describeTo(to));
		}
		else if (invoker == from || invoker == to)
		{
			from.msg("<h>%s<i> transfered <h>%s<i> to <h>%s<i>.", from.describeTo(from, true), moneyString(amount), to.describeTo(from));
			to.msg  ("<h>%s<i> transfered <h>%s<i> to <h>%s<i>.", from.describeTo(to, true), moneyString(amount), to.describeTo(to));
		}
		else
		{
			from.msg("<h>%s<i> was transfered from <h>%s<i> to <h>%s<i> by <h>%s<i>.", moneyString(amount), from.describeTo(from), to.describeTo(from), invoker.describeTo(from));
			to.msg  ("<h>%s<i> was transfered from <h>%s<i> to <h>%s<i> by <h>%s<i>.", moneyString(amount), from.describeTo(to), to.describeTo(to), invoker.describeTo(to));
		}
		
		return true;
	}
	
	public static boolean modifyMoney(EconomyParticipator ep, double delta, String toDoThis, String forDoingThis)
	{
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

	// whether a player can afford specified amount
	/*public static boolean canAfford(String playerName, double amount) {
		// if Economy support is not enabled, they can certainly afford to pay nothing
		if (!enabled())
		{
			return true;
		}

		if (registerAvailable())
		{
			MethodAccount holdings = getRegisterAccount(playerName);
			if (holdings == null)
			{
				return false;
			}

			return holdings.hasEnough(amount);
		}
		else if (iConomyUse)
		{
			Holdings holdings = getIconomyHoldings(playerName);
			if (holdings == null)
			{
				return false;
			}

			return holdings.hasEnough(amount);
		}
		else
		{
			try
			{
				return Economy.hasEnough(playerName, amount);
			}
			catch (Exception ex)
			{
				return false;
			}
		}
	}*/

	// deduct money from their account; returns true if successful
	/*public static boolean deductMoney(String playerName, double amount)
	{
		if (!enabled())
		{
			return true;
		}

		if (registerAvailable())
		{
			MethodAccount holdings = getRegisterAccount(playerName);
			if (holdings == null || !holdings.hasEnough(amount))
			{
				return false;
			}

			return holdings.subtract(amount);
		}
		else if (iConomyUse)
		{
			Holdings holdings = getIconomyHoldings(playerName);
			if (holdings == null || !holdings.hasEnough(amount))
			{
				return false;
			}

			holdings.subtract(amount);
			return true;
		}
		else
		{
			try
			{
				if (!Economy.hasEnough(playerName, amount))
				{
					return false;
				}
				Economy.subtract(playerName, amount);
				return true;
			}
			catch (Exception ex)
			{
				return false;
			}
		}
	}*/

	// add money to their account; returns true if successful
	/*public static boolean addMoney(String playerName, double amount)
	{
		if (!enabled())
		{
			return true;
		}

		if (registerAvailable())
		{
			MethodAccount holdings = getRegisterAccount(playerName);
			if (holdings == null)
			{
				return false;
			}

			return holdings.add(amount);
		}
		else if (iConomyUse) 
		{
			Holdings holdings = getIconomyHoldings(playerName);
			if (holdings == null)
			{
				return false;
			}

			holdings.add(amount);
			return true;
		}
		else
		{
			try
			{
				Economy.add(playerName, amount);
				return true;
			}
			catch (Exception ex)
			{
				return false;
			}
		}
	}*/


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
