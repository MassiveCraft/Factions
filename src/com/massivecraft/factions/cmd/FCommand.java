package com.massivecraft.factions.cmd;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.MCommand;


public abstract class FCommand extends MCommand<P>
{
	public boolean disableOnLock;
	
	public FPlayer fme;
	public Faction myFaction;
	public boolean senderMustBeMember;
	public boolean senderMustBeModerator;
	public boolean senderMustBeAdmin;
	
	public FCommand()
	{
		super(P.p);
		
		// Due to safety reasons it defaults to disable on lock.
		disableOnLock = true;
		
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void execute(CommandSender sender, List<String> args, List<MCommand<?>> commandChain)
	{
		if (sender instanceof Player)
		{
			this.fme = FPlayers.i.get((Player)sender);
			this.myFaction = this.fme.getFaction();
		}
		else
		{
			this.fme = null;
			this.myFaction = null;
		}
		super.execute(sender, args, commandChain);
	}
	
	@Override
	public boolean isEnabled()
	{
		if (p.getLocked() && this.disableOnLock)
		{
			msg("<b>Factions was locked by an admin. Please try again later.");
			return false;
		}
		return true;
	}
	
	@Override
	public boolean validSenderType(CommandSender sender, boolean informSenderIfNot)
	{
		boolean superValid = super.validSenderType(sender, informSenderIfNot);
		if ( ! superValid) return false;
		
		if ( ! (this.senderMustBeMember || this.senderMustBeModerator || this.senderMustBeAdmin)) return true;
		
		if ( ! (sender instanceof Player)) return false;
		
		FPlayer fplayer = FPlayers.i.get((Player)sender);
		
		if ( ! fplayer.hasFaction())
		{
			sender.sendMessage(p.txt.parse("<b>You are not member of any faction."));
			return false;
		}
		
		if (this.senderMustBeModerator && ! fplayer.getRole().isAtLeast(Role.MODERATOR))
		{
			sender.sendMessage(p.txt.parse("<b>Only faction moderators can %s.", this.getHelpShort()));
			return false;
		}
		
		if (this.senderMustBeAdmin && ! fplayer.getRole().isAtLeast(Role.ADMIN))
		{
			sender.sendMessage(p.txt.parse("<b>Only faction admins can %s.", this.getHelpShort()));
			return false;
		}
			
		return true;
	}
	
	// -------------------------------------------- //
	// Assertions
	// -------------------------------------------- //

	public boolean assertHasFaction()
	{
		if (me == null) return true;
		
		if ( ! fme.hasFaction())
		{
			sendMessage("You are not member of any faction.");
			return false;
		}
		return true;
	}

	public boolean assertMinRole(Role role)
	{
		if (me == null) return true;
		
		if (fme.getRole().value < role.value)
		{
			msg("<b>You <h>must be "+role+"<b> to "+this.getHelpShort()+".");
			return false;
		}
		return true;
	}
	
	// -------------------------------------------- //
	// Argument Readers
	// -------------------------------------------- //
	
	// ARG AS FPLAYER
	public FPlayer argAsFPlayer(int idx, FPlayer def, boolean msg)
	{
		FPlayer ret = def;
		
		String name = this.argAsString(idx);
		if (name != null)
		{
			FPlayer fplayer = FPlayers.i.get(name); 
			if (fplayer != null)
			{
				ret = fplayer;
			}
		}
		
		if (msg && ret == null)
		{
			this.sendMessage(p.txt.parse("<b>The player \"<p>%s<b>\" could not be found.", name));
		}
		
		return ret;
	}
	public FPlayer argAsFPlayer(int idx, FPlayer def)
	{
		return this.argAsFPlayer(idx, def, true);
	}
	public FPlayer argAsFPlayer(int idx)
	{
		return this.argAsFPlayer(idx, null);
	}
	
	// ARG AS BEST FPLAYER MATCH
	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def, boolean msg)
	{
		FPlayer ret = def;
		
		String name = this.argAsString(idx);
		if (name != null)
		{
			FPlayer fplayer = FPlayers.i.find(name);
			if (fplayer != null)
			{
				ret = fplayer;
			}
		}
		
		if (msg && ret == null)
		{
			this.sendMessage(p.txt.parse("<b>The player \"<p>%s<b>\" could not be found.", name));
		}
		
		return ret;
	}
	public FPlayer argAsBestFPlayerMatch(int idx, FPlayer def)
	{
		return this.argAsBestFPlayerMatch(idx, def, true);
	}
	public FPlayer argAsBestFPlayerMatch(int idx)
	{
		return this.argAsBestFPlayerMatch(idx, null);
	}
	
	// ARG AS FACTION
	public Faction argAsFaction(int idx, Faction def, boolean msg)
	{
		Faction ret = def;
		
		String name = this.argAsString(idx);
		if (name != null)
		{
			// First we search faction names
			Faction faction = Factions.i.findByTag(name);
			if (faction != null)
			{
				ret = faction;
			}

			// Next we search player names
			FPlayer fplayer = FPlayers.i.find(name);
			if (fplayer != null)
			{
				ret = fplayer.getFaction();
			}
			
		}
		
		if (msg && ret == null)
		{
			this.sendMessage(p.txt.parse("<b>The faction or player \"<p>%s<b>\" could not be found.", name));
		}
		
		return ret;
	}
	public Faction argAsFaction(int idx, Faction def)
	{
		return this.argAsFaction(idx, def, true);
	}
	public Faction argAsFaction(int idx)
	{
		return this.argAsFaction(idx, null);
	}
	
	// -------------------------------------------- //
	// Commonly used logic
	// -------------------------------------------- //
	
	public boolean canIAdministerYou(FPlayer i, FPlayer you)
	{
		if ( ! i.getFaction().equals(you.getFaction()))
		{
			i.sendMessage(p.txt.parse("%s <b>is not in the same faction as you.",you.getNameAndRelevant(i)));
			return false;
		}
		
		if (i.getRole().value > you.getRole().value || i.getRole().equals(Role.ADMIN) )
		{
			return true;
		}
		
		if (you.getRole().equals(Role.ADMIN))
		{
			i.sendMessage(p.txt.parse("<b>Only the faction admin can do that."));
		}
		else if (i.getRole().equals(Role.MODERATOR))
		{
			if ( i == you )
			{
				return true; //Moderators can control themselves
			}
			else
			{
				i.sendMessage(p.txt.parse("<b>Moderators can't control each other..."));
			}
		}
		else
		{
			i.sendMessage(p.txt.parse("<b>You must be a faction moderator to do that."));
		}
		
		return false;
	}
	
	// if economy is enabled and they're not on the bypass list, make 'em pay; returns true unless person can't afford the cost
	public boolean payForCommand(double cost)
	{
		if ( ! Econ.shouldBeUsed() || this.fme == null || cost == 0.0 || fme.isAdminBypassing())
		{
			return true;
		}

		String desc = this.getHelpShort().toLowerCase();

		if(Conf.bankFactionPaysLandCosts && fme.hasFaction())
		{
			if ( ! Econ.modifyMoney(myFaction, -cost, "to "+desc, "for "+desc)) return false;
		}
		else
		{
			if ( ! Econ.modifyMoney(fme, -cost, "to "+desc, "for "+desc)) return false;
		}
		return true;
		/*
		
		
		
		// pay up
		if (cost > 0.0)
		{
			String costString = Econ.moneyString(cost);
			if(Conf.bankFactionPaysCosts && fme.hasFaction() )
			{
				if( ! faction.getAccount().subtract(cost))
				{
					sendMessage("It costs "+costString+" to "+desc+", which your faction can't currently afford.");
					return false;
				}
				else
				{
					sendMessage(faction.getTag()+" has paid "+costString+" to "+desc+".");
				}
					
			}
			else
			{
				if ( ! Econ.deductMoney(fme.getName(), cost))
				{
					sendMessage("It costs "+costString+" to "+desc+", which you can't currently afford.");
					return false;
				}
				sendMessage("You have paid "+costString+" to "+desc+".");
			}
		}
		// wait... we pay you to use this command?
		else
		{
			String costString = Econ.moneyString(-cost);
			
			if(Conf.bankFactionPaysCosts && fme.hasFaction() )
			{
				faction.getAccount().add(-cost);
				sendMessage(faction.getTag()+" has been paid "+costString+" to "+desc+".");
			}
			else
			{
				Econ.addMoney(fme.getName(), -cost);
			}
			
			
			sendMessage("You have been paid "+costString+" to "+desc+".");
		}
		return true;*/
	}
}
