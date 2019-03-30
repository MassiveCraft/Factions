package com.massivecraft.factions.cmd;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.massivecraft.factions.P;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;

public class BrigadierManager {

    public Commodore commodore;
    public LiteralArgumentBuilder<Object> brigadier = LiteralArgumentBuilder.literal("factions");

    public BrigadierManager() {
        commodore = CommodoreProvider.getCommodore(P.p);
    }

    public void build() {
        commodore.register(brigadier.build());

        // Add factions children to f alias
        LiteralArgumentBuilder<Object> fLiteral = LiteralArgumentBuilder.literal("f");
        for (CommandNode<Object> node : brigadier.getArguments()) {
            fLiteral.then(node);
        }
        commodore.register(fLiteral.build());
    }

    public void addSubCommand(FCommand subCommand) {
        // Register brigadier to all command aliases
        for (String alias : subCommand.aliases) {
            LiteralArgumentBuilder<Object> literal = LiteralArgumentBuilder.literal(alias);

            if (subCommand.requirements.brigadier != null) {
                // If the requirements explicitly provide a BrigadierProvider then use it
                Class<? extends BrigadierProvider> brigadierProvider = subCommand.requirements.brigadier;

                try {
                    Constructor<? extends BrigadierProvider> constructor = brigadierProvider.getDeclaredConstructor(subCommand.getClass());
                    brigadier.then(constructor.newInstance(subCommand).get(literal));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Generate our own based on args - quite ugly

                // We create an orderly stack of all args, required and optional, format them differently
                List<RequiredArgumentBuilder<Object, ?>> stack = new ArrayList<>();
                for (String required : subCommand.requiredArgs) {
                    // Simply add the arg name as required
                    stack.add(RequiredArgumentBuilder.argument(required, StringArgumentType.word()));
                }

                for (Map.Entry<String, String> optionalEntry : subCommand.optionalArgs.entrySet()) {
                    RequiredArgumentBuilder<Object, ?> optional;

                    // Optional without default
                    if (optionalEntry.getKey().equalsIgnoreCase(optionalEntry.getValue())) {
                        optional = RequiredArgumentBuilder.argument(":" + optionalEntry.getKey(), StringArgumentType.word());
                    // Optional with default, explain
                    } else {
                        optional = RequiredArgumentBuilder.argument(optionalEntry.getKey() + "|" + optionalEntry.getValue(), StringArgumentType.word());
                    }
                    
                    stack.add(optional);
                }
                
                // Reverse the stack and apply .then()
                RequiredArgumentBuilder<Object, ?> previous = null;
                for (int i = stack.size() - 1; i >= 0; i--) {
                    if (previous == null) {
                        previous = stack.get(i);
                    } else {
                        previous = stack.get(i).then(previous);
                    }
                }
                
                if (previous == null) {
                    brigadier.then(literal);
                } else {
                    brigadier.then(literal.then(previous));
                }
            }
        }
    }
    
}
