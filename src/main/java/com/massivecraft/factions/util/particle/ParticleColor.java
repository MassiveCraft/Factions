package com.massivecraft.factions.util.particle;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public class ParticleColor {

    private Color color;

    private float red;
    private float green;
    private float blue;

    ParticleColor(Color color) {
        this.color = color;
        this.red = color.getRed();
        this.green = color.getGreen();
        this.blue = color.getBlue();
    }

    public float getOffsetX() {
        if (red == 0) {
            return Float.MIN_VALUE;
        }
        return red/255;
    }

    public float getOffsetY() {
        return green/255;
    }

    public float getOffsetZ() {
        return blue/255;
    }

    // Why Spigot?
    public static ParticleColor fromChatColor(ChatColor chatColor) {
        switch (chatColor) {
            case AQUA:
                return new ParticleColor(Color.AQUA);
            case BLACK:
                return new ParticleColor(Color.BLACK);
            case BLUE:
                return new ParticleColor(Color.BLUE);
            case DARK_AQUA:
                return new ParticleColor(Color.BLUE);
            case DARK_BLUE:
                return new ParticleColor(Color.BLUE);
            case DARK_GRAY:
                return new ParticleColor(Color.GRAY);
            case DARK_GREEN:
                return new ParticleColor(Color.GREEN);
            case DARK_PURPLE:
                return new ParticleColor(Color.PURPLE);
            case DARK_RED:
                return new ParticleColor(Color.RED);
            case GOLD:
                return new ParticleColor(Color.YELLOW);
            case GRAY:
                return new ParticleColor(Color.GRAY);
            case GREEN:
                return new ParticleColor(Color.LIME);
            case LIGHT_PURPLE:
                return new ParticleColor(Color.PURPLE);
            case RED:
                return new ParticleColor(Color.RED);
            case WHITE:
                return new ParticleColor(Color.WHITE);
            case YELLOW:
                return new ParticleColor(Color.YELLOW);
            default:
                break;
        }

        return null;
    }

    public Color getColor() {
        return color;
    }
}
