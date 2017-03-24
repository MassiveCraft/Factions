package com.massivecraft.factions.cmd;

import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.ps.PSFormatHumanSpace;
import com.massivecraft.massivecore.util.Txt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public abstract class CmdFactionsAccessAbstract extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public PS chunk;
	public TerritoryAccess ta;
	public Faction hostFaction;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAccessAbstract()
	{
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		chunk = PS.valueOf(me.getLocation()).getChunk(true);
		ta = BoardColl.get().getTerritoryAccessAt(chunk);
		hostFaction = ta.getHostFaction();
		
		this.innerPerform();
	}
	
	public abstract void innerPerform() throws MassiveException;

	public void sendAccessInfo()
	{
		Object title = "Access at " + chunk.toString(PSFormatHumanSpace.get());
		title = Txt.titleize(title);
		message(title);
		
		msg("<k>Host Faction: %s", hostFaction.describeTo(msender, true));
		msg("<k>Host Faction Allowed: %s", ta.isHostFactionAllowed() ? Txt.parse("<lime>TRUE") : Txt.parse("<rose>FALSE"));
		msg("<k>Granted Players: %s", describeRelationParticipators(ta.getGrantedMPlayers(), msender));
		msg("<k>Granted Factions: %s", describeRelationParticipators(ta.getGrantedFactions(), msender));
	}
	
	public static String describeRelationParticipators(Collection<? extends RelationParticipator> relationParticipators, RelationParticipator observer)
	{
		if (relationParticipators.size() == 0) return Txt.parse("<silver><em>none");
		List<String> descriptions = new ArrayList<>();
		for (RelationParticipator relationParticipator : relationParticipators)
		{
			descriptions.add(relationParticipator.describeTo(observer));
		}
		return Txt.implodeCommaAnd(descriptions, Txt.parse("<i>, "), Txt.parse(" <i>and "));
	}
	
}
