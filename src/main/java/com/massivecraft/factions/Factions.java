package com.massivecraft.factions;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.ChatColor;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class Factions extends EntityCollection<Faction> {

    public static Factions i = new Factions();

    P p = P.p;

    private Factions() {
        super(Faction.class, new CopyOnWriteArrayList<Faction>(), new ConcurrentHashMap<String, Faction>(), new File(P.p.getDataFolder(), "factions.json"), P.p.gson);
    }

    @Override
    public Type getMapType() {
        return new TypeToken<Map<String, Faction>>() {
        }.getType();
    }

    @Override
    public boolean loadFromDisc() {
        if (!super.loadFromDisc()) {
            return false;
        }

        // Make sure the default neutral faction exists
        if (!this.exists("0")) {
            Faction faction = this.create("0");
            faction.setTag(TL.WILDERNESS.toString());
            faction.setDescription(TL.WILDERNESS_DESCRIPTION.toString());
        } else {
            if (!this.get("0").getTag().equalsIgnoreCase(TL.WILDERNESS.toString())) {
                get("0").setTag(TL.WILDERNESS.toString());
            }
            if (!this.get("0").getDescription().equalsIgnoreCase(TL.WILDERNESS_DESCRIPTION.toString())) {
                get("0").setDescription(TL.WILDERNESS_DESCRIPTION.toString());
            }
        }

        // Make sure the safe zone faction exists
        if (!this.exists("-1")) {
            Faction faction = this.create("-1");
            faction.setTag(TL.SAFEZONE.toString());
            faction.setDescription(TL.SAFEZONE_DESCRIPTION.toString());
        } else {
            if (!getSafeZone().getTag().equalsIgnoreCase(TL.SAFEZONE.toString())) {
                getSafeZone().setTag(TL.SAFEZONE.toString());
            }
            if (!getSafeZone().getDescription().equalsIgnoreCase(TL.SAFEZONE_DESCRIPTION.toString())) {
                getSafeZone().setDescription(TL.SAFEZONE_DESCRIPTION.toString());
            }
            // if SafeZone has old pre-1.6.0 name, rename it to remove troublesome " "
            Faction faction = this.getSafeZone();
            if (faction.getTag().contains(" ")) {
                faction.setTag(TL.SAFEZONE.toString());
            }
        }

        // Make sure the war zone faction exists
        if (!this.exists("-2")) {
            Faction faction = this.create("-2");
            faction.setTag(TL.WARZONE.toString());
            faction.setDescription(TL.WARZONE_DESCRIPTION.toString());
        } else {
            if (!getWarZone().getTag().equalsIgnoreCase(TL.WARZONE.toString())) {
                getWarZone().setTag(TL.WARZONE.toString());
            }
            if (!getWarZone().getDescription().equalsIgnoreCase(TL.WARZONE_DESCRIPTION.toString())) {
                getWarZone().setDescription(TL.WARZONE_DESCRIPTION.toString());
            }
            // if WarZone has old pre-1.6.0 name, rename it to remove troublesome " "
            Faction faction = this.getWarZone();
            if (faction.getTag().contains(" ")) {
                faction.setTag(TL.WARZONE.toString());
            }
        }

        // populate all faction player lists
        for (Faction faction : i.get()) {
            faction.refreshFPlayers();
        }

        return true;
    }


    //----------------------------------------------//
    // GET
    //----------------------------------------------//

    @Override
    public Faction get(String id) {
        if (!this.exists(id)) {
            p.log(Level.WARNING, "Non existing factionId " + id + " requested! Issuing cleaning!");
            Board.clean();
            FPlayers.i.clean();
        }

        return super.get(id);
    }

    public Faction getNone() {
        return this.get("0");
    }

    public Faction getSafeZone() {
        return this.get("-1");
    }

    public Faction getWarZone() {
        return this.get("-2");
    }


    //----------------------------------------------//
    // Faction tag
    //----------------------------------------------//

    public static ArrayList<String> validateTag(String str) {
        ArrayList<String> errors = new ArrayList<String>();

        if (MiscUtil.getComparisonString(str).length() < Conf.factionTagLengthMin) {
            errors.add(P.p.txt.parse("<i>The faction tag can't be shorter than <h>%s<i> chars.", Conf.factionTagLengthMin));
        }

        if (str.length() > Conf.factionTagLengthMax) {
            errors.add(P.p.txt.parse("<i>The faction tag can't be longer than <h>%s<i> chars.", Conf.factionTagLengthMax));
        }

        for (char c : str.toCharArray()) {
            if (!MiscUtil.substanceChars.contains(String.valueOf(c))) {
                errors.add(P.p.txt.parse("<i>Faction tag must be alphanumeric. \"<h>%s<i>\" is not allowed.", c));
            }
        }

        return errors;
    }

    // Loops through all faction tags. Case and color insensitive.
    public Faction getByTag(String str) {
        String compStr = MiscUtil.getComparisonString(str);
        for (Faction faction : this.get()) {
            if (faction.getComparisonTag().equals(compStr)) {
                return faction;
            }
        }
        return null;
    }

    public Faction getBestTagMatch(String searchFor) {
        Map<String, Faction> tag2faction = new HashMap<String, Faction>();

        // TODO: Slow index building
        for (Faction faction : this.get()) {
            tag2faction.put(ChatColor.stripColor(faction.getTag()), faction);
        }

        String tag = TextUtil.getBestStartWithCI(tag2faction.keySet(), searchFor);
        if (tag == null) {
            return null;
        }
        return tag2faction.get(tag);
    }

    public boolean isTagTaken(String str) {
        return this.getByTag(str) != null;
    }

}
