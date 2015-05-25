package com.massivecraft.factions;

import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.WarmUpUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * Logged in players always have exactly one FPlayer instance. Logged out players may or may not have an FPlayer
 * instance. They will always have one if they are part of a faction. This is because only players with a faction are
 * saved to disk (in order to not waste disk space).
 * <p/>
 * The FPlayer is linked to a minecraft player using the player name.
 * <p/>
 * The same instance is always returned for the same player. This means you can use the == operator. No .equals method
 * necessary.
 */

public interface FPlayer extends EconomyParticipator {
    public Faction getFaction();

    public String getFactionId();

    public boolean hasFaction();

    public void setFaction(Faction faction);

    public void setMonitorJoins(boolean monitor);

    public boolean isMonitoringJoins();

    public Role getRole();

    public void setRole(Role role);

    public double getPowerBoost();

    public void setPowerBoost(double powerBoost);

    public Faction getAutoClaimFor();

    public void setAutoClaimFor(Faction faction);

    public boolean isAutoSafeClaimEnabled();

    public void setIsAutoSafeClaimEnabled(boolean enabled);

    public boolean isAutoWarClaimEnabled();

    public void setIsAutoWarClaimEnabled(boolean enabled);

    public boolean isAdminBypassing();

    public boolean isVanished();

    public void setIsAdminBypassing(boolean val);

    public void setChatMode(ChatMode chatMode);

    public ChatMode getChatMode();

    public void setIgnoreAllianceChat(boolean ignore);

    public boolean isIgnoreAllianceChat();

    public void setSpyingChat(boolean chatSpying);

    public boolean isSpyingChat();

    public boolean showScoreboard();

    public void setShowScoreboard(boolean show);

    // FIELD: account
    public String getAccountId();

    public void resetFactionData(boolean doSpoutUpdate);

    public void resetFactionData();

    public long getLastLoginTime();

    public void setLastLoginTime(long lastLoginTime);

    public boolean isMapAutoUpdating();

    public void setMapAutoUpdating(boolean mapAutoUpdating);

    public boolean hasLoginPvpDisabled();

    public FLocation getLastStoodAt();

    public void setLastStoodAt(FLocation flocation);

    public String getTitle();

    public void setTitle(String title);

    public String getName();

    public String getTag();

    // Base concatenations:

    public String getNameAndSomething(String something);

    public String getNameAndTitle();

    public String getNameAndTag();

    // Colored concatenations:
    // These are used in information messages

    public String getNameAndTitle(Faction faction);

    public String getNameAndTitle(FPlayer fplayer);

    // Chat Tag:
    // These are injected into the format of global chat messages.

    public String getChatTag();

    // Colored Chat Tag
    public String getChatTag(Faction faction);

    public String getChatTag(FPlayer fplayer);

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    @Override
    public String describeTo(RelationParticipator that, boolean ucfirst);

    @Override
    public String describeTo(RelationParticipator that);

    @Override
    public Relation getRelationTo(RelationParticipator rp);

    @Override
    public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful);

    public Relation getRelationToLocation();

    @Override
    public ChatColor getColorTo(RelationParticipator rp);

    //----------------------------------------------//
    // Health
    //----------------------------------------------//
    public void heal(int amnt);


    //----------------------------------------------//
    // Power
    //----------------------------------------------//
    public double getPower();

    public void alterPower(double delta);

    public double getPowerMax();

    public double getPowerMin();

    public int getPowerRounded();

    public int getPowerMaxRounded();

    public int getPowerMinRounded();

    public void updatePower();

    public void losePowerFromBeingOffline();

    public void onDeath();

    //----------------------------------------------//
    // Territory
    //----------------------------------------------//
    public boolean isInOwnTerritory();

    public boolean isInOthersTerritory();

    public boolean isInAllyTerritory();

    public boolean isInNeutralTerritory();

    public boolean isInEnemyTerritory();

    public void sendFactionHereMessage(Faction from);

    // -------------------------------
    // Actions
    // -------------------------------

    public void leave(boolean makePay);

    public boolean canClaimForFaction(Faction forFaction);

    public boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure);

    public boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure);

    public void msg(String str, Object... args);

    public String getId();

    public Player getPlayer();

    public boolean isOnline();

    public void sendMessage(String message);

    public void sendMessage(List<String> messages);

    public boolean isOnlineAndVisibleTo(Player me);

    public void remove();

    public boolean isOffline();

    public void setId(String id);

    // -------------------------------
    // Warmups
    // -------------------------------

    public boolean isWarmingUp();

    public WarmUpUtil.Warmup getWarmupType();

    public void addWarmup(WarmUpUtil.Warmup warmup, int taskId);

    public void stopWarmup();

    public void clearWarmup();

}
