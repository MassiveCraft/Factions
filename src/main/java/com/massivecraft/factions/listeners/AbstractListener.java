package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.material.FactionMaterial;
import com.massivecraft.factions.util.material.MaterialDb;
import com.massivecraft.factions.zcore.fperms.Access;
import com.massivecraft.factions.zcore.fperms.PermissableAction;
import com.massivecraft.factions.zcore.util.TL;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public abstract class AbstractListener implements Listener {
    public boolean canPlayerUseBlock(Player player, Material material, Location location, boolean justCheck) {
        if (Conf.playersWhoBypassAllProtection.contains(player.getName())) {
            return true;
        }

        FPlayer me = FPlayers.getInstance().getByPlayer(player);
        if (me.isAdminBypassing()) {
            return true;
        }

        FLocation loc = new FLocation(location);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);

        // no door/chest/whatever protection in wilderness, war zones, or safe zones
        if (!otherFaction.isNormal()) {
            return true;
        }

        if (P.p.getConfig().getBoolean("hcf.raidable", false) && otherFaction.getLandRounded() >= otherFaction.getPowerRounded()) {
            return true;
        }

        PermissableAction action = null;

        switch (material) {
            case LEVER:
                action = PermissableAction.LEVER;
                break;
            case STONE_BUTTON:
            case BIRCH_BUTTON:
            case ACACIA_BUTTON:
            case DARK_OAK_BUTTON:
            case JUNGLE_BUTTON:
            case OAK_BUTTON:
            case SPRUCE_BUTTON:
                action = PermissableAction.BUTTON;
                break;
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case IRON_DOOR:
            case JUNGLE_DOOR:
            case SPRUCE_DOOR:
            case ACACIA_TRAPDOOR:
            case OAK_DOOR:
            case BIRCH_TRAPDOOR:
            case DARK_OAK_TRAPDOOR:
            case IRON_TRAPDOOR:
            case JUNGLE_TRAPDOOR:
            case OAK_TRAPDOOR:
            case SPRUCE_TRAPDOOR:
                action = PermissableAction.DOOR;
                break;
            case CHEST:
            case ENDER_CHEST:
            case TRAPPED_CHEST:
            case BARREL:
            case FURNACE:
            case DROPPER:
            case DISPENSER:
            case HOPPER:
            case BLAST_FURNACE:
            case CAULDRON:
            case BREWING_STAND:
            case CARTOGRAPHY_TABLE:
            case GRINDSTONE:
            case SMOKER:
            case STONECUTTER:
            case ITEM_FRAME:
            case JUKEBOX:
            case ARMOR_STAND:
                action = PermissableAction.CONTAINER;
                break;
            default:
                // Check for doors that might have diff material name in old version.
                if (material.name().contains("DOOR")) {
                    action = PermissableAction.DOOR;
                }
                // Lazier than checking all the combinations
                if (material.name().contains("SHULKER_BOX") || material.name().contains("ANVIL")) {
                    action = PermissableAction.CONTAINER;
                }
                break;
        }

        // F PERM check runs through before other checks.
        Access access = otherFaction.getAccess(me, action);
        if (access == null || access == Access.DENY) {
            me.msg(TL.GENERIC_NOPERMISSION, action);
            return false;
        } else if (access == Access.ALLOW) {
            return true; // explicitly allowed
        }

        // Dupe fix.
        Faction myFaction = me.getFaction();
        Relation rel = myFaction.getRelationTo(otherFaction);
        if (!rel.isMember() || !otherFaction.playerHasOwnershipRights(me, loc)) {
            Material mainHand = player.getItemInHand().getType();

            // Check if material is at risk for dupe in either hand.
            if (isDupeMaterial(mainHand)) {
                return false;
            }
        }

        // We only care about some material types.
        if (otherFaction.hasPlayersOnline()) {
            if (!Conf.territoryProtectedMaterials.contains(material)) {
                return true;
            }
        } else {
            if (!Conf.territoryProtectedMaterialsWhenOffline.contains(material)) {
                return true;
            }
        }

        // You may use any block unless it is another faction's territory...
        if (rel.isNeutral() || (rel.isEnemy() && Conf.territoryEnemyProtectMaterials) || (rel.isAlly() && Conf.territoryAllyProtectMaterials) || (rel.isTruce() && Conf.territoryTruceProtectMaterials)) {
            if (!justCheck) {
                me.msg(TL.PLAYER_USE_TERRITORY, (material == FactionMaterial.from("FARMLAND").get() ? "trample " : "use ") + TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
            }

            return false;
        }

        // Also cancel if player doesn't have ownership rights for this claim
        if (Conf.ownedAreasEnabled && Conf.ownedAreaProtectMaterials && !otherFaction.playerHasOwnershipRights(me, loc)) {
            if (!justCheck) {
                me.msg(TL.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
            }

            return false;
        }

        return true;
    }

    private boolean isDupeMaterial(Material material) {
        if (MaterialDb.getInstance().provider.isSign(material)) {
            return true;
        }

        switch (material) {
            case CHEST:
            case TRAPPED_CHEST:
            case DARK_OAK_DOOR:
            case ACACIA_DOOR:
            case BIRCH_DOOR:
            case JUNGLE_DOOR:
            case OAK_DOOR:
            case SPRUCE_DOOR:
            case IRON_DOOR:
                return true;
            default:
                break;
        }

        return false;
    }
}
