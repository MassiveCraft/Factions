package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.FactionBan;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.cmd.type.TypeSelector;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.pager.Msonifier;
import com.massivecraft.massivecore.pager.Pager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdFactionsBanList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsBanList()
	{
		// Parameters
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "your");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameter
		final int page = this.readArg();
		final Faction faction = this.readArg(msenderFaction);
		final MPlayer mplayer = msender;
		final CommandSender sendee = sender;
		final TypeSelector type = TypeSelector.get();
		
		// Pager create
		String title = "Faction Bans for " + faction.describeTo(mplayer);
		final Pager<FactionBan> pager = new Pager<>(this, title, page, faction.getFactionBans().getAll(), new Msonifier<FactionBan>()
		{
			@Override
			public Mson toMson(FactionBan factionBan, int index)
			{
				Mson visualSelector = type.getVisualMson(factionBan.getSelector(), sendee);
				Mson visualExecutor = TypeMPlayer.get().getVisualMson(factionBan.getExecutor(), sendee);
				Mson visualReason = TypeString.get().getVisualMson(factionBan.getReason(), sendee);
				return mson(visualSelector, " was banned by ", visualExecutor, ". Reason: ", visualReason).color(ChatColor.YELLOW);
			}
		});
		
		// Pager message
		pager.messageAsync();
	}

}
