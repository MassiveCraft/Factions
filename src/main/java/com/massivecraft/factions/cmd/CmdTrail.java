package com.massivecraft.factions.cmd;

import com.darkblade12.particleeffect.ParticleEffect;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;

public class CmdTrail extends FCommand {

    public CmdTrail() {
        super();
        this.aliases.add("trail");
        this.aliases.add("trails");

        this.optionalArgs.put("on/off/effect", "flip");
        this.optionalArgs.put("particle", "particle");

        this.requirements = new CommandRequirements.Builder(Permission.FLY_TRAILS).memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        // Let's flip it
        if (!context.argIsSet(0)) {
            context.fPlayer.setFlyTrailsState(!context.fPlayer.getFlyTrailsState());
        } else {
            // They are setting a particle type
            if (context.argAsString(0).equalsIgnoreCase("effect")) {
                if (context.argIsSet(1)) {
                    String effectName = context.argAsString(1);
                    Object particleEffect = p.particleProvider.effectFromString(effectName);
                    if (particleEffect == null) {
                        context.fPlayer.msg(TL.COMMAND_FLYTRAILS_PARTICLE_INVALID);
                        return;
                    }

                    if (p.perm.has(context.player, Permission.FLY_TRAILS.node + "." + effectName)) {
                        context.fPlayer.setFlyTrailsEffect(effectName);
                    } else {
                        context.fPlayer.msg(TL.COMMAND_FLYTRAILS_PARTICLE_PERMS, effectName);
                    }
                } else {
                    context.msg(getUsageTranslation());
                }
            } else {
                boolean state = context.argAsBool(0);
                context.fPlayer.setFlyTrailsState(state);
            }
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_FLY_DESCRIPTION;
    }

}