package com.massivecraft.factions.integration;

import java.util.HashSet;
import java.util.Set;

import com.massivecraft.factions.EconomyParticipator;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.mcore.money.Money;

public class Econ
{
	// -------------------------------------------- //
	// STATE
	// -------------------------------------------- //
	
	// TODO: Do we really need that config option?
	// TODO: Could we not have it enabled as long as Money.enabled is true?
	public static boolean isEnabled(Object universe)
	{
		return UConf.get(universe).econEnabled && Money.enabled(universe);
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static boolean payForAction(double cost, UPlayer usender, String actionDescription)
	{
		if (!isEnabled(usender)) return true;
		if (cost == 0D) return true;
		
		if (usender.isUsingAdminMode()) return true;
		UConf uconf = UConf.get(usender);
		Faction usenderFaction = usender.getFaction();
		
		if (uconf.bankEnabled && uconf.bankFactionPaysCosts && usenderFaction.isNormal())
		{
			return modifyMoney(usenderFaction, -cost, actionDescription);
		}
		else
		{
			return modifyMoney(usender, -cost, actionDescription);
		}
	}
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //

	public static void modifyUniverseMoney(Object universe, double delta)
	{
		if (!isEnabled(universe)) return;
		UConf uconf = UConf.get(universe);

		if (uconf.econUniverseAccount == null) return;
		if (uconf.econUniverseAccount.length() == 0) return;
		
		if (!Money.exists(universe, uconf.econUniverseAccount)) return;

		Money.add(universe, uconf.econUniverseAccount, delta);
	}

	public static void sendBalanceInfo(UPlayer to, EconomyParticipator about)
	{
		to.msg("<a>%s's<i> balance is <h>%s<i>.", about.describeTo(to, true), Money.format(about, Money.get(about)));
	}

	public static boolean canIControllYou(EconomyParticipator i, EconomyParticipator you)
	{
		Faction fI = RelationUtil.getFaction(i);
		Faction fYou = RelationUtil.getFaction(you);
		
		// This is a system invoker. Accept it.
		if (fI == null) return true;
		
		// Bypassing players can do any kind of transaction
		if (i instanceof UPlayer && ((UPlayer)i).isUsingAdminMode()) return true;
		
		// You can deposit to anywhere you feel like. It's your loss if you can't withdraw it again.
		if (i == you) return true;
		
		// A faction can always transfer away the money of it's members and its own money...
		// This will however probably never happen as a faction does not have free will.
		// Ohh by the way... Yes it could. For daily rent to the faction.
		if (i == fI && fI == fYou) return true;
		
		// Factions can be controlled by those that have permissions
		if (you instanceof Faction)
		{
			if (i instanceof Faction && FPerm.WITHDRAW.has((Faction)i, fYou)) return true;
			if (i instanceof UPlayer && FPerm.WITHDRAW.has((UPlayer)i, fYou, false)) return true;
		}
		
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
		if (!isEnabled(from)) return false;

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
		if (Money.get(from) < amount)
		{
			// There was not enough money to pay
			if (invoker != null && notify)
			{
				invoker.msg("<h>%s<b> can't afford to transfer <h>%s<b> to %s<b>.", from.describeTo(invoker, true), Money.format(from, amount), to.describeTo(invoker));
			}
			return false;
		}
		
		// Transfer money
		if (Money.subtract(from, amount))
		{
			if (Money.add(to, amount))
			{
				if (notify)
				{
					sendTransferInfo(invoker, from, to, amount);
				}
				return true;
			}
			else
			{
				// We failed. Try a rollback
				Money.add(from, amount);
			}
		}
		
		// if we get here something with the transaction failed
		if (notify)
		{
			invoker.msg("Unable to transfer %s<b> to <h>%s<b> from <h>%s<b>.", Money.format(from, amount), to.describeTo(invoker), from.describeTo(invoker, true));
		}
		
		return false;
	}
	
	public static Set<UPlayer> getUPlayers(EconomyParticipator ep)
	{
		Set<UPlayer> uplayers = new HashSet<UPlayer>();
		
		if (ep == null)
		{
			// Add nothing
		}
		else if (ep instanceof UPlayer)
		{
			uplayers.add((UPlayer)ep);
		}
		else if (ep instanceof Faction)
		{
			uplayers.addAll(((Faction)ep).getUPlayers());
		}
		
		return uplayers;
	}
	
	public static void sendTransferInfo(EconomyParticipator invoker, EconomyParticipator from, EconomyParticipator to, double amount)
	{
		Set<UPlayer> recipients = new HashSet<UPlayer>();
		recipients.addAll(getUPlayers(invoker));
		recipients.addAll(getUPlayers(from));
		recipients.addAll(getUPlayers(to));
		
		if (invoker == null)
		{
			for (UPlayer recipient : recipients)
			{
				recipient.msg("<h>%s<i> was transfered from <h>%s<i> to <h>%s<i>.", Money.format(from, amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		}
		else if (invoker == from)
		{
			for (UPlayer recipient : recipients)
			{
				recipient.msg("<h>%s<i> <h>gave %s<i> to <h>%s<i>.", from.describeTo(recipient, true), Money.format(from, amount), to.describeTo(recipient));
			}
		}
		else if (invoker == to)
		{
			for (UPlayer recipient : recipients)
			{
				recipient.msg("<h>%s<i> <h>took %s<i> from <h>%s<i>.", to.describeTo(recipient, true), Money.format(from, amount), from.describeTo(recipient));
			}
		}
		else
		{
			for (UPlayer recipient : recipients)
			{
				recipient.msg("<h>%s<i> transfered <h>%s<i> from <h>%s<i> to <h>%s<i>.", invoker.describeTo(recipient, true), Money.format(from, amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		}
	}

	public static boolean hasAtLeast(EconomyParticipator ep, double delta, String toDoThis)
	{
		if (!isEnabled(ep)) return true;

		if (Money.get(ep) < delta)
		{
			if (toDoThis != null && !toDoThis.isEmpty())
			{
				ep.msg("<h>%s<i> can't afford <h>%s<i> %s.", ep.describeTo(ep, true), Money.format(ep, delta), toDoThis);
			}
			return false;
		}
		return true;
	}

	public static boolean modifyMoney(EconomyParticipator ep, double delta, String actionDescription)
	{
		if (!isEnabled(ep)) return false;
		if (delta == 0) return true;
		
		String You = ep.describeTo(ep, true);
		
		boolean hasActionDesctription = (actionDescription != null && !actionDescription.isEmpty());

		if (Money.add(ep, delta))
		{
			modifyUniverseMoney(ep, -delta);
			
			if (hasActionDesctription)
			{
				if (delta > 0)
				{
					ep.msg("<h>%s<i> gained <h>%s<i> since did %s.", You, Money.format(ep, delta), actionDescription);
				}
				else
				{
					ep.msg("<h>%s<i> lost <h>%s<i> since did %s.", You, Money.format(ep, -delta), actionDescription);
				}
			}
			return true;
		}
		else
		{
			if (hasActionDesctription)
			{
				if (delta > 0)
				{
					ep.msg("<h>%s<i> would have gained <h>%s<i> since did %s, but the deposit failed.", You, Money.format(ep, delta), actionDescription);
				}
				else
				{
					ep.msg("<h>%s<i> can't afford <h>%s<i> to %s.", You, Money.format(ep, -delta), actionDescription);
				}
			}
			return false;
		}
	}
	
}
