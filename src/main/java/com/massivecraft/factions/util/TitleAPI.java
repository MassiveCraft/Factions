package com.massivecraft.factions.util;

import com.massivecraft.factions.P;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * With help from https://www.spigotmc.org/threads/send-titles-to-players-using-spigot-1-8-1-11-2.48819/
 */
public class TitleAPI {

    private static TitleAPI instance;
    private boolean supportsAPI = false;

    private Map<String, Class> classCache = new HashMap<>();

    public TitleAPI() {
        instance = this;

        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            supportsAPI = true;
            P.p.getLogger().info("Found API support for sending player titles :D");
        } catch (NoSuchMethodException e) {
            P.p.getLogger().info("Didn't find API support for sending titles :( Will attempt reflection. No promises.");
        }
    }

    /**
     * Send a title to player
     *
     * @param player      Player to send the title to
     * @param title        The text displayed in the title
     * @param subtitle        The text displayed in the subtitle
     * @param fadeInTime  The time the title takes to fade in
     * @param showTime    The time the title is displayed
     * @param fadeOutTime The time the title takes to fade out
     */
    public void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
        if (supportsAPI) {
            player.sendTitle(title, subtitle, fadeInTime, showTime, fadeOutTime);
            return;
        }

        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + title + "\"}");
            Object chatsubTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + subtitle + "\"}");


            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object titlePacket = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeInTime, showTime, fadeOutTime);
            Object subTitlePacket = titleConstructor.newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsubTitle, fadeInTime, showTime, fadeOutTime);

            sendPacket(player, titlePacket);
            sendPacket(player, subTitlePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get NMS class using reflection
     *
     * @param name Name of the class
     * @return Class
     */
    private Class<?> getNMSClass(String name) {
        String versionName = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name;

        if (classCache.containsKey(versionName)) {
            return classCache.get(versionName);
        }

        try {
            Class clazz = Class.forName(versionName);
            classCache.put(name, clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TitleAPI getInstance() {
        return instance;
    }
}
