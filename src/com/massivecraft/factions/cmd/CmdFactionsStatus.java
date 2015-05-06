package com.massivecraft.factions.cmd;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.PlayerInactivityComparator;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARSortMPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.pager.PagerSimple;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;


public class CmdFactionsStatus extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsStatus()
	{
		// Aliases
		this.addAliases("s", "status");

		// Args
		this.addArg(ARInteger.get(), "page", "1");
		this.addArg(ARFaction.get(), "faction", "you");
		this.addArg(ARSortMPlayer.get(), "sort by", "time");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.STATUS.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Integer pageHumanBased = this.readArg(1);
		Faction faction = this.readArg(msenderFaction);
		Comparator<MPlayer> sortedBy = this.readArg(PlayerInactivityComparator.get());

		// MPerm
		if ( ! MPerm.getPermStatus().has(msender, faction, true)) return;
		
		// Sort list
		final List<MPlayer> mplayers = faction.getMPlayers();
		Collections.sort(mplayers, sortedBy);
		
		// Create Pager
		final PagerSimple<MPlayer> pager = new PagerSimple<MPlayer>(mplayers, sender);
		String pagerTitle = Txt.parse("<i>Status of %s<i>.", faction.describeTo(msender, true));
		
		// Use Pager
		List<String> messages = pager.getPageTxt(pageHumanBased, pagerTitle, new Stringifier<MPlayer>(){
			
			@Override
			public String toString(MPlayer mplayer, int index)
			{
				// Name
				String displayName = mplayer.getNameAndSomething(msender.getColorTo(mplayer).toString(), "");
				int length = 15 - displayName.length();
				length = length <= 0 ? 1 : length;
				String whiteSpace = Txt.repeat(" ", length);
				
				// Power
				double currentPower = mplayer.getPower();
				double maxPower = mplayer.getPowerMax();
				String color;
				double percent = currentPower / maxPower;
				
				if (percent > 0.75)
				{
					color = "<green>";
				}
				else if (percent > 0.5)
				{
					color = "<yellow>";
				}
				else if (percent > 0.25)
				{
					color = "<rose>";
				}
				else
				{
					color = "<red>";
				}
			
				String power = Txt.parse("<art>Power: %s%.0f<gray>/<green>%.0f", Txt.parse(color), currentPower, maxPower);
				
				// Time
				long lastActiveMillis = mplayer.getLastActivityMillis() - System.currentTimeMillis();
				LinkedHashMap<TimeUnit, Long> activeTimes = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(lastActiveMillis, TimeUnit.getAllButMillis()), 3);
				String lastActive = mplayer.isOnline() ? Txt.parse("<lime>Online right now.") : Txt.parse("<i>Last active: " + TimeDiffUtil.formatedMinimal(activeTimes, "<i>"));
				
				return Txt.parse("%s%s %s %s", displayName, whiteSpace, power, lastActive);
			}

		});
		
		// Send message
		sendMessage(messages);
	}
	
}
