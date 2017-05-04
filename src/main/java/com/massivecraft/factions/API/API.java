package com.massivecraft.factions.API;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Rel;
import org.bukkit.entity.Player;

/**
 *
 * @author James137137
 */
public class API {
    
    
    public String getFactionName(Player player) {
        return ((FPlayer) FPlayers.i.get(player)).getFaction().getTag();
    }

    
    public String getFactionID(Player player) {
        return ((FPlayer) FPlayers.i.get(player)).getFaction().getId();
    }

    
    public MyRel getRelationship(Player player1, Player player2) {
        FPlayer fSenderPlayer = (FPlayer) FPlayers.i.get(player1);
        Faction SenderFaction = fSenderPlayer.getFaction();
        FPlayer fplayer = (FPlayer) FPlayers.i.get(player2);
        Rel rel = SenderFaction.getRelationTo(fplayer);
        if (rel == Rel.NEUTRAL)
        {
            return MyRel.NEUTRAL;
        }
        if (rel == Rel.ALLY)
        {
            return MyRel.ALLY;
        }
        if (rel == Rel.TRUCE)
        {
            return MyRel.TRUCE;
        }
        if (rel == Rel.ENEMY)
        {
            return MyRel.ENEMY;
        }
        if (rel == Rel.LEADER)
        {
            return MyRel.LEADER;
        }
        if (rel == Rel.MEMBER)
        {
            return MyRel.MEMBER;
        }
        if (rel == Rel.RECRUIT)
        {
            return MyRel.RECRUIT;
        }
        if (rel == Rel.OFFICER)
        {
            return MyRel.OFFICER;
        }
        
        return null;
    }

    
    public boolean isFactionless(Player player) {
        return getFactionName(player).contains("Wilderness");
    }

    
    public String getPlayerTitle(Player player) {
        String title = ((FPlayer) FPlayers.i.get(player)).getTitle();
        if (title.contains("no title set")) {
            return "";
        }
        return title;
    }

    
    public String getPlayerRank(Player player) {
        Rel role = ((FPlayer) FPlayers.i.get(player)).getRole();
        if (role.equals(Rel.LEADER)) {
            return FactionChat.LeaderRank;
        } else if (role.equals(Rel.OFFICER)) {
            return FactionChat.OfficerRank;
        } else if (role.equals(Rel.MEMBER)) {
            return FactionChat.MemberRank;
        } else if (role.equals(Rel.RECRUIT)) {
            return FactionChat.RecruitRank;
        } else {
            return "";
        }
    }
}
