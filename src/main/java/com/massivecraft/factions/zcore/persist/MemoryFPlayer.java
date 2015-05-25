package com.massivecraft.factions.zcore.persist;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.Essentials;
import com.massivecraft.factions.integration.Worldguard;
import com.massivecraft.factions.scoreboards.FScoreboard;
import com.massivecraft.factions.scoreboards.sidebar.FInfoSidebar;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.WarmUpUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


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

public abstract class MemoryFPlayer implements FPlayer {
    // FIELD: factionId
    protected String factionId;

    // FIELD: role
    protected Role role;
    // FIELD: title
    protected String title;

    // FIELD: power
    protected double power;

    // FIELD: powerBoost
    // special increase/decrease to min and max power for this player
    protected double powerBoost;

    // FIELD: lastPowerUpdateTime
    protected long lastPowerUpdateTime;

    // FIELD: lastLoginTime
    protected long lastLoginTime;

    // FIELD: chatMode
    protected ChatMode chatMode;

    // FIELD: ignoreAllianceChat
    protected boolean ignoreAllianceChat = false;

    protected String id;
    protected String name;

    protected boolean monitorJoins;

    //private transient String playerName;
    protected transient FLocation lastStoodAt = new FLocation(); // Where did this player stand the last time we checked?

    // FIELD: mapAutoUpdating
    protected transient boolean mapAutoUpdating;

    // FIELD: autoClaimEnabled
    protected transient Faction autoClaimFor;

    // FIELD: autoSafeZoneEnabled
    protected transient boolean autoSafeZoneEnabled;

    // FIELD: autoWarZoneEnabled
    protected transient boolean autoWarZoneEnabled;

    protected transient boolean isAdminBypassing = false;

    // FIELD: loginPvpDisabled
    protected transient boolean loginPvpDisabled;

    protected boolean spyingChat = false;
    protected boolean showScoreboard;

    protected WarmUpUtil.Warmup warmup;
    protected int warmupTask;

    public Faction getFaction() {
        if (this.factionId == null) {
            this.factionId = "0";
        }
        return Factions.getInstance().getFactionById(this.factionId);
    }

    public String getFactionId() {
        return this.factionId;
    }

    public boolean hasFaction() {
        return !factionId.equals("0");
    }

    public void setFaction(Faction faction) {
        Faction oldFaction = this.getFaction();
        if (oldFaction != null) {
            oldFaction.removeFPlayer(this);
        }
        faction.addFPlayer(this);
        this.factionId = faction.getId();
    }

    public void setMonitorJoins(boolean monitor) {
        this.monitorJoins = monitor;
    }

    public boolean isMonitoringJoins() {
        return this.monitorJoins;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public double getPowerBoost() {
        return this.powerBoost;
    }

    public void setPowerBoost(double powerBoost) {
        this.powerBoost = powerBoost;
    }

    public Faction getAutoClaimFor() {
        return autoClaimFor;
    }

    public void setAutoClaimFor(Faction faction) {
        this.autoClaimFor = faction;
        if (this.autoClaimFor != null) {
            // TODO: merge these into same autoclaim
            this.autoSafeZoneEnabled = false;
            this.autoWarZoneEnabled = false;
        }
    }

    public boolean isAutoSafeClaimEnabled() {
        return autoSafeZoneEnabled;
    }

    public void setIsAutoSafeClaimEnabled(boolean enabled) {
        this.autoSafeZoneEnabled = enabled;
        if (enabled) {
            this.autoClaimFor = null;
            this.autoWarZoneEnabled = false;
        }
    }

    public boolean isAutoWarClaimEnabled() {
        return autoWarZoneEnabled;
    }

    public void setIsAutoWarClaimEnabled(boolean enabled) {
        this.autoWarZoneEnabled = enabled;
        if (enabled) {
            this.autoClaimFor = null;
            this.autoSafeZoneEnabled = false;
        }
    }

    public boolean isAdminBypassing() {
        return this.isAdminBypassing;
    }

    public boolean isVanished() {
        return Essentials.isVanished(getPlayer());
    }

    public void setIsAdminBypassing(boolean val) {
        this.isAdminBypassing = val;
    }

    public void setChatMode(ChatMode chatMode) {
        this.chatMode = chatMode;
    }

    public ChatMode getChatMode() {
        if (this.factionId.equals("0") || !Conf.factionOnlyChat) {
            this.chatMode = ChatMode.PUBLIC;
        }
        return chatMode;
    }

    public void setIgnoreAllianceChat(boolean ignore) {
        this.ignoreAllianceChat = ignore;
    }

    public boolean isIgnoreAllianceChat() {
        return ignoreAllianceChat;
    }

    public void setSpyingChat(boolean chatSpying) {
        this.spyingChat = chatSpying;
    }

    public boolean isSpyingChat() {
        return spyingChat;
    }

    // FIELD: account
    public String getAccountId() {
        return this.getId();
    }

    public MemoryFPlayer() {
    }

    public MemoryFPlayer(String id) {
        this.id = id;
        this.resetFactionData(false);
        this.power = Conf.powerPlayerStarting;
        this.lastPowerUpdateTime = System.currentTimeMillis();
        this.lastLoginTime = System.currentTimeMillis();
        this.mapAutoUpdating = false;
        this.autoClaimFor = null;
        this.autoSafeZoneEnabled = false;
        this.autoWarZoneEnabled = false;
        this.loginPvpDisabled = Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0;
        this.powerBoost = 0.0;
        this.showScoreboard = P.p.getConfig().getBoolean("scoreboard.default-enabled", false);

        if (!Conf.newPlayerStartingFactionID.equals("0") && Factions.getInstance().isValidFactionId(Conf.newPlayerStartingFactionID)) {
            this.factionId = Conf.newPlayerStartingFactionID;
        }
    }

    public MemoryFPlayer(MemoryFPlayer other) {
        this.factionId = other.factionId;
        this.id = other.id;
        this.power = other.power;
        this.lastLoginTime = other.lastLoginTime;
        this.mapAutoUpdating = other.mapAutoUpdating;
        this.autoClaimFor = other.autoClaimFor;
        this.autoSafeZoneEnabled = other.autoSafeZoneEnabled;
        this.autoWarZoneEnabled = other.autoWarZoneEnabled;
        this.loginPvpDisabled = other.loginPvpDisabled;
        this.powerBoost = other.powerBoost;
        this.role = other.role;
        this.title = other.title;
        this.chatMode = other.chatMode;
        this.spyingChat = other.spyingChat;
        this.lastStoodAt = other.lastStoodAt;
        this.isAdminBypassing = other.isAdminBypassing;
        this.showScoreboard = P.p.getConfig().getBoolean("scoreboard.default-enabled", false);
    }

    public void resetFactionData(boolean doSpoutUpdate) {
        // clean up any territory ownership in old faction, if there is one
        if (factionId != null && Factions.getInstance().isValidFactionId(this.getFactionId())) {
            Faction currentFaction = this.getFaction();
            currentFaction.removeFPlayer(this);
            if (currentFaction.isNormal()) {
                currentFaction.clearClaimOwnership(this);
            }
        }

        this.factionId = "0"; // The default neutral faction
        this.chatMode = ChatMode.PUBLIC;
        this.role = Role.NORMAL;
        this.title = "";
        this.autoClaimFor = null;
    }

    public void resetFactionData() {
        this.resetFactionData(true);
    }

    // -------------------------------------------- //
    // Getters And Setters
    // -------------------------------------------- //


    public long getLastLoginTime() {
        return lastLoginTime;
    }


    public void setLastLoginTime(long lastLoginTime) {
        losePowerFromBeingOffline();
        this.lastLoginTime = lastLoginTime;
        this.lastPowerUpdateTime = lastLoginTime;
        if (Conf.noPVPDamageToOthersForXSecondsAfterLogin > 0) {
            this.loginPvpDisabled = true;
        }
    }

    public boolean isMapAutoUpdating() {
        return mapAutoUpdating;
    }

    public void setMapAutoUpdating(boolean mapAutoUpdating) {
        this.mapAutoUpdating = mapAutoUpdating;
    }

    public boolean hasLoginPvpDisabled() {
        if (!loginPvpDisabled) {
            return false;
        }
        if (this.lastLoginTime + (Conf.noPVPDamageToOthersForXSecondsAfterLogin * 1000) < System.currentTimeMillis()) {
            this.loginPvpDisabled = false;
            return false;
        }
        return true;
    }

    public FLocation getLastStoodAt() {
        return this.lastStoodAt;
    }

    public void setLastStoodAt(FLocation flocation) {
        this.lastStoodAt = flocation;
    }

    //----------------------------------------------//
    // Title, Name, Faction Tag and Chat
    //----------------------------------------------//

    // Base:

    public String getTitle() {
        return this.hasFaction() ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        if (this.name == null) {
            // Older versions of FactionsUUID don't save the name,
            // so `name` will be null the first time it's retrieved
            // after updating
            OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(getId()));
            this.name = offline.getName() != null ? offline.getName() : getId();
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return this.hasFaction() ? this.getFaction().getTag() : "";
    }

    // Base concatenations:

    public String getNameAndSomething(String something) {
        String ret = this.role.getPrefix();
        if (something.length() > 0) {
            ret += something + " ";
        }
        ret += this.getName();
        return ret;
    }

    public String getNameAndTitle() {
        return this.getNameAndSomething(this.getTitle());
    }

    public String getNameAndTag() {
        return this.getNameAndSomething(this.getTag());
    }

    // Colored concatenations:
    // These are used in information messages

    public String getNameAndTitle(Faction faction) {
        return this.getColorTo(faction) + this.getNameAndTitle();
    }

    public String getNameAndTitle(MemoryFPlayer fplayer) {
        return this.getColorTo(fplayer) + this.getNameAndTitle();
    }

    // Chat Tag:
    // These are injected into the format of global chat messages.

    public String getChatTag() {
        return this.hasFaction() ? String.format(Conf.chatTagFormat, this.role.getPrefix() + this.getTag()) : "";
    }

    // Colored Chat Tag
    public String getChatTag(Faction faction) {
        return this.hasFaction() ? this.getRelationTo(faction).getColor() + getChatTag() : "";
    }

    public String getChatTag(MemoryFPlayer fplayer) {
        return this.hasFaction() ? this.getColorTo(fplayer) + getChatTag() : "";
    }

    // -------------------------------
    // Relation and relation colors
    // -------------------------------

    @Override
    public String describeTo(RelationParticipator that, boolean ucfirst) {
        return RelationUtil.describeThatToMe(this, that, ucfirst);
    }

    @Override
    public String describeTo(RelationParticipator that) {
        return RelationUtil.describeThatToMe(this, that);
    }

    @Override
    public Relation getRelationTo(RelationParticipator rp) {
        return RelationUtil.getRelationTo(this, rp);
    }

    @Override
    public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful) {
        return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
    }

    public Relation getRelationToLocation() {
        return Board.getInstance().getFactionAt(new FLocation(this)).getRelationTo(this);
    }

    @Override
    public ChatColor getColorTo(RelationParticipator rp) {
        return RelationUtil.getColorOfThatToMe(this, rp);
    }

    //----------------------------------------------//
    // Health
    //----------------------------------------------//
    public void heal(int amnt) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.setHealth(player.getHealth() + amnt);
    }


    //----------------------------------------------//
    // Power
    //----------------------------------------------//
    public double getPower() {
        this.updatePower();
        return this.power;
    }

    public void alterPower(double delta) {
        this.power += delta;
        if (this.power > this.getPowerMax()) {
            this.power = this.getPowerMax();
        } else if (this.power < this.getPowerMin()) {
            this.power = this.getPowerMin();
        }
    }

    public double getPowerMax() {
        return Conf.powerPlayerMax + this.powerBoost;
    }

    public double getPowerMin() {
        return Conf.powerPlayerMin + this.powerBoost;
    }

    public int getPowerRounded() {
        return (int) Math.round(this.getPower());
    }

    public int getPowerMaxRounded() {
        return (int) Math.round(this.getPowerMax());
    }

    public int getPowerMinRounded() {
        return (int) Math.round(this.getPowerMin());
    }

    public void updatePower() {
        if (this.isOffline()) {
            losePowerFromBeingOffline();
            if (!Conf.powerRegenOffline) {
                return;
            }
        } else if (hasFaction() && getFaction().isPowerFrozen()) {
            return; // Don't let power regen if faction power is frozen.
        }
        long now = System.currentTimeMillis();
        long millisPassed = now - this.lastPowerUpdateTime;
        this.lastPowerUpdateTime = now;

        Player thisPlayer = this.getPlayer();
        if (thisPlayer != null && thisPlayer.isDead()) {
            return;  // don't let dead players regain power until they respawn
        }

        int millisPerMinute = 60 * 1000;
        this.alterPower(millisPassed * Conf.powerPerMinute / millisPerMinute);
    }

    public void losePowerFromBeingOffline() {
        if (Conf.powerOfflineLossPerDay > 0.0 && this.power > Conf.powerOfflineLossLimit) {
            long now = System.currentTimeMillis();
            long millisPassed = now - this.lastPowerUpdateTime;
            this.lastPowerUpdateTime = now;

            double loss = millisPassed * Conf.powerOfflineLossPerDay / (24 * 60 * 60 * 1000);
            if (this.power - loss < Conf.powerOfflineLossLimit) {
                loss = this.power;
            }
            this.alterPower(-loss);
        }
    }

    public void onDeath() {
        this.updatePower();
        this.alterPower(-Conf.powerPerDeath);
        if (hasFaction()) {
            getFaction().setLastDeath(System.currentTimeMillis());
        }
    }

    //----------------------------------------------//
    // Territory
    //----------------------------------------------//
    public boolean isInOwnTerritory() {
        return Board.getInstance().getFactionAt(new FLocation(this)) == this.getFaction();
    }

    public boolean isInOthersTerritory() {
        Faction factionHere = Board.getInstance().getFactionAt(new FLocation(this));
        return factionHere != null && factionHere.isNormal() && factionHere != this.getFaction();
    }

    public boolean isInAllyTerritory() {
        return Board.getInstance().getFactionAt(new FLocation(this)).getRelationTo(this).isAlly();
    }

    public boolean isInNeutralTerritory() {
        return Board.getInstance().getFactionAt(new FLocation(this)).getRelationTo(this).isNeutral();
    }

    public boolean isInEnemyTerritory() {
        return Board.getInstance().getFactionAt(new FLocation(this)).getRelationTo(this).isEnemy();
    }

    public void sendFactionHereMessage(Faction from) {
        Faction toShow = Board.getInstance().getFactionAt(getLastStoodAt());
        boolean showChat = true;
        if (showInfoBoard(toShow)) {
            FScoreboard.get(this).setTemporarySidebar(new FInfoSidebar(toShow));
            showChat = P.p.getConfig().getBoolean("scoreboard.also-send-chat", true);
        }
        if (showChat) {
            this.sendMessage(P.p.txt.parse(TL.FACTION_LEAVE.format(from.getTag(this), toShow.getTag(this))));
        }
    }

    /**
     * Check if the scoreboard should be shown. Simple method to be used by above method.
     *
     * @param toShow Faction to be shown.
     *
     * @return true if should show, otherwise false.
     */
    public boolean showInfoBoard(Faction toShow) {
        return showScoreboard && !toShow.isWarZone() && !toShow.isNone() && !toShow.isSafeZone() && P.p.getConfig().contains("scoreboard.finfo") && P.p.getConfig().getBoolean("scoreboard.finfo-enabled", false) && FScoreboard.get(this) != null;
    }

    @Override
    public boolean showScoreboard() {
        return this.showScoreboard;
    }

    @Override
    public void setShowScoreboard(boolean show) {
        this.showScoreboard = show;
    }

    // -------------------------------
    // Actions
    // -------------------------------

    public void leave(boolean makePay) {
        Faction myFaction = this.getFaction();
        makePay = makePay && Econ.shouldBeUsed() && !this.isAdminBypassing();

        if (myFaction == null) {
            resetFactionData();
            return;
        }

        boolean perm = myFaction.isPermanent();

        if (!perm && this.getRole() == Role.ADMIN && myFaction.getFPlayers().size() > 1) {
            msg(TL.LEAVE_PASSADMIN);
            return;
        }

        if (!Conf.canLeaveWithNegativePower && this.getPower() < 0) {
            msg(TL.LEAVE_NEGATIVEPOWER);
            return;
        }

        // if economy is enabled and they're not on the bypass list, make sure they can pay
        if (makePay && !Econ.hasAtLeast(this, Conf.econCostLeave, TL.LEAVE_TOLEAVE.toString())) {
            return;
        }

        FPlayerLeaveEvent leaveEvent = new FPlayerLeaveEvent(this, myFaction, FPlayerLeaveEvent.PlayerLeaveReason.LEAVE);
        Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
        if (leaveEvent.isCancelled()) {
            return;
        }

        // then make 'em pay (if applicable)
        if (makePay && !Econ.modifyMoney(this, -Conf.econCostLeave, TL.LEAVE_TOLEAVE.toString(), TL.LEAVE_FORLEAVE.toString())) {
            return;
        }

        // Am I the last one in the faction?
        if (myFaction.getFPlayers().size() == 1) {
            // Transfer all money
            if (Econ.shouldBeUsed()) {
                Econ.transferMoney(this, myFaction, this, Econ.getBalance(myFaction.getAccountId()));
            }
        }

        if (myFaction.isNormal()) {
            for (FPlayer fplayer : myFaction.getFPlayersWhereOnline(true)) {
                fplayer.msg(TL.LEAVE_LEFT, this.describeTo(fplayer, true), myFaction.describeTo(fplayer));
            }

            if (Conf.logFactionLeave) {
                P.p.log(TL.LEAVE_LEFT.format(this.getName(), myFaction.getTag()));
            }
        }

        myFaction.removeAnnouncements(this);
        this.resetFactionData();

        if (myFaction.isNormal() && !perm && myFaction.getFPlayers().isEmpty()) {
            // Remove this faction
            for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
                fplayer.msg(TL.LEAVE_DISBANDED, myFaction.describeTo(fplayer, true));
            }

            Factions.getInstance().removeFaction(myFaction.getId());
            if (Conf.logFactionDisband) {
                P.p.log(TL.LEAVE_DISBANDEDLOG.format(myFaction.getTag(), myFaction.getId(), this.getName()));
            }
        }
    }

    public boolean canClaimForFaction(Faction forFaction) {
        return !forFaction.isNone() && (this.isAdminBypassing() || (forFaction == this.getFaction() && this.getRole().isAtLeast(Role.MODERATOR)) || (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer())) || (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer())));
    }

    public boolean canClaimForFactionAtLocation(Faction forFaction, Location location, boolean notifyFailure) {
        String error = null;
        FLocation flocation = new FLocation(location);
        Faction myFaction = getFaction();
        Faction currentFaction = Board.getInstance().getFactionAt(flocation);
        int ownedLand = forFaction.getLandRounded();
        int factionBuffer = P.p.getConfig().getInt("hcf.buffer-zone", 0);
        int worldBuffer = P.p.getConfig().getInt("world-border.buffer", 0);

        if (Conf.worldGuardChecking && Worldguard.checkForRegionsInChunk(location)) {
            // Checks for WorldGuard regions in the chunk attempting to be claimed
            error = P.p.txt.parse(TL.CLAIM_PROTECTED.toString());
        } else if (Conf.worldsNoClaiming.contains(flocation.getWorldName())) {
            error = P.p.txt.parse(TL.CLAIM_DISABLED.toString());
        } else if (this.isAdminBypassing()) {
            return true;
        } else if (forFaction.isSafeZone() && Permission.MANAGE_SAFE_ZONE.has(getPlayer())) {
            return true;
        } else if (forFaction.isWarZone() && Permission.MANAGE_WAR_ZONE.has(getPlayer())) {
            return true;
        } else if (myFaction != forFaction) {
            error = P.p.txt.parse(TL.CLAIM_CANTCLAIM.toString(), forFaction.describeTo(this));
        } else if (forFaction == currentFaction) {
            error = P.p.txt.parse(TL.CLAIM_ALREADYOWN.toString(), forFaction.describeTo(this, true));
        } else if (this.getRole().value < Role.MODERATOR.value) {
            error = P.p.txt.parse(TL.CLAIM_MUSTBE.toString(), Role.MODERATOR.getTranslation());
        } else if (forFaction.getFPlayers().size() < Conf.claimsRequireMinFactionMembers) {
            error = P.p.txt.parse(TL.CLAIM_MEMBERS.toString(), Conf.claimsRequireMinFactionMembers);
        } else if (currentFaction.isSafeZone()) {
            error = P.p.txt.parse(TL.CLAIM_SAFEZONE.toString());
        } else if (currentFaction.isWarZone()) {
            error = P.p.txt.parse(TL.CLAIM_WARZONE.toString());
        } else if (P.p.getConfig().getBoolean("hcf.overclaim", true) && ownedLand >= forFaction.getPowerRounded()) {
            error = P.p.txt.parse(TL.CLAIM_POWER.toString());
        } else if (Conf.claimedLandsMax != 0 && ownedLand >= Conf.claimedLandsMax && forFaction.isNormal()) {
            error = P.p.txt.parse(TL.CLAIM_LIMIT.toString());
        } else if (currentFaction.getRelationTo(forFaction) == Relation.ALLY) {
            error = P.p.txt.parse(TL.CLAIM_ALLY.toString());
        } else if (Conf.claimsMustBeConnected && !this.isAdminBypassing() && myFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0 && !Board.getInstance().isConnectedLocation(flocation, myFaction) && (!Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction || !currentFaction.isNormal())) {
            if (Conf.claimsCanBeUnconnectedIfOwnedByOtherFaction) {
                error = P.p.txt.parse(TL.CLAIM_CONTIGIOUS.toString());
            } else {
                error = P.p.txt.parse(TL.CLAIM_FACTIONCONTIGUOUS.toString());
            }
        } else if (factionBuffer > 0 && Board.getInstance().hasFactionWithin(flocation, myFaction, factionBuffer)) {
            error = P.p.txt.parse(TL.CLAIM_TOOCLOSETOOTHERFACTION.format(factionBuffer));
        } else if (flocation.isOutsideWorldBorder(worldBuffer)) {
            if (worldBuffer > 0) {
                error = P.p.txt.parse(TL.CLAIM_OUTSIDEBORDERBUFFER.format(worldBuffer));
            } else {
                error = P.p.txt.parse(TL.CLAIM_OUTSIDEWORLDBORDER.toString());
            }
        } else if (currentFaction.isNormal()) {
            if (myFaction.isPeaceful()) {
                error = P.p.txt.parse(TL.CLAIM_PEACEFUL.toString(), currentFaction.getTag(this));
            } else if (currentFaction.isPeaceful()) {
                error = P.p.txt.parse(TL.CLAIM_PEACEFULTARGET.toString(), currentFaction.getTag(this));
            } else if (!currentFaction.hasLandInflation()) {
                // TODO more messages WARN current faction most importantly
                error = P.p.txt.parse(TL.CLAIM_THISISSPARTA.toString(), currentFaction.getTag(this));
            } else if (currentFaction.hasLandInflation() && !P.p.getConfig().getBoolean("hcf.allow-overclaim", true)) {
                // deny over claim when it normally would be allowed.
                error = P.p.txt.parse(TL.CLAIM_OVERCLAIM_DISABLED.toString());
            } else if (!Board.getInstance().isBorderLocation(flocation)) {
                error = P.p.txt.parse(TL.CLAIM_BORDER.toString());
            }
        }
        // TODO: Add more else if statements.

        if (notifyFailure && error != null) {
            msg(error);
        }
        return error == null;
    }

    public boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure) {
        // notifyFailure is false if called by auto-claim; no need to notify on every failure for it
        // return value is false on failure, true on success

        FLocation flocation = new FLocation(location);
        Faction currentFaction = Board.getInstance().getFactionAt(flocation);

        int ownedLand = forFaction.getLandRounded();

        if (!this.canClaimForFactionAtLocation(forFaction, location, notifyFailure)) {
            return false;
        }

        // if economy is enabled and they're not on the bypass list, make sure they can pay
        boolean mustPay = Econ.shouldBeUsed() && !this.isAdminBypassing() && !forFaction.isSafeZone() && !forFaction.isWarZone();
        double cost = 0.0;
        EconomyParticipator payee = null;
        if (mustPay) {
            cost = Econ.calculateClaimCost(ownedLand, currentFaction.isNormal());

            if (Conf.econClaimUnconnectedFee != 0.0 && forFaction.getLandRoundedInWorld(flocation.getWorldName()) > 0 && !Board.getInstance().isConnectedLocation(flocation, forFaction)) {
                cost += Conf.econClaimUnconnectedFee;
            }

            if (Conf.bankEnabled && Conf.bankFactionPaysLandCosts && this.hasFaction()) {
                payee = this.getFaction();
            } else {
                payee = this;
            }

            if (!Econ.hasAtLeast(payee, cost, TL.CLAIM_TOCLAIM.toString())) {
                return false;
            }
        }

        LandClaimEvent claimEvent = new LandClaimEvent(flocation, forFaction, this);
        Bukkit.getServer().getPluginManager().callEvent(claimEvent);
        if (claimEvent.isCancelled()) {
            return false;
        }

        // then make 'em pay (if applicable)
        if (mustPay && !Econ.modifyMoney(payee, -cost, TL.CLAIM_TOCLAIM.toString(), TL.CLAIM_FORCLAIM.toString())) {
            return false;
        }

        // announce success
        Set<FPlayer> informTheseFPlayers = new HashSet<FPlayer>();
        informTheseFPlayers.add(this);
        informTheseFPlayers.addAll(forFaction.getFPlayersWhereOnline(true));
        for (FPlayer fp : informTheseFPlayers) {
            fp.msg(TL.CLAIM_CLAIMED, this.describeTo(fp, true), forFaction.describeTo(fp), currentFaction.describeTo(fp));
        }

        Board.getInstance().setFactionAt(forFaction, flocation);

        if (Conf.logLandClaims) {
            P.p.log(TL.CLAIM_CLAIMEDLOG.toString(), this.getName(), flocation.getCoordString(), forFaction.getTag());
        }

        return true;
    }

    public boolean shouldBeSaved() {
        if (!this.hasFaction() && (this.getPowerRounded() == this.getPowerMaxRounded() || this.getPowerRounded() == (int) Math.round(Conf.powerPlayerStarting))) {
            return false;
        }
        return true;
    }

    public void msg(String str, Object... args) {
        this.sendMessage(P.p.txt.parse(str, args));
    }

    public void msg(TL translation, Object... args) {
        this.msg(translation.toString(), args);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(UUID.fromString(this.getId()));
    }

    public boolean isOnline() {
        return this.getPlayer() != null;
    }

    // make sure target player should be able to detect that this player is online
    public boolean isOnlineAndVisibleTo(Player player) {
        Player target = this.getPlayer();
        return target != null && player.canSee(target);
    }

    public boolean isOffline() {
        return !isOnline();
    }

    // -------------------------------------------- //
    // Message Sending Helpers
    // -------------------------------------------- //

    public void sendMessage(String msg) {
        Player player = this.getPlayer();
        if (player == null) {
            return;
        }
        player.sendMessage(msg);
    }

    public void sendMessage(List<String> msgs) {
        for (String msg : msgs) {
            this.sendMessage(msg);
        }
    }

    public String getNameAndTitle(FPlayer fplayer) {
        return this.getColorTo(fplayer) + this.getNameAndTitle();
    }

    @Override
    public String getChatTag(FPlayer fplayer) {
        return this.hasFaction() ? this.getRelationTo(fplayer).getColor() + getChatTag() : "";
    }

    @Override
    public String getId() {
        return id;
    }

    public abstract void remove();

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void clearWarmup() {
        if (warmup != null) {
            Bukkit.getScheduler().cancelTask(warmupTask);
            this.stopWarmup();
        }
    }

    @Override
    public void stopWarmup() {
        warmup = null;
    }

    @Override
    public boolean isWarmingUp() {
        return warmup != null;
    }

    @Override
    public WarmUpUtil.Warmup getWarmupType() {
        return warmup;
    }

    @Override
    public void addWarmup(WarmUpUtil.Warmup warmup, int taskId) {
        if (this.warmup != null) {
            this.clearWarmup();
        }
        this.warmup = warmup;
        this.warmupTask = taskId;
    }
}
