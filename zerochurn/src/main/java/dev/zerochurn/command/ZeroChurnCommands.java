package dev.zerochurn.command;

import dev.zerochurn.compat.ModCompat;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.metrics.ZeroChurnStats;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class ZeroChurnCommands {
    private ZeroChurnCommands() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
            Commands.literal("zerochurn")
                .then(Commands.literal("stats").executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal(ZeroChurnStats.summary()), false);
                    return 1;
                }))
                .then(Commands.literal("status").executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal(ZeroChurnStats.statusLine()), false);
                    context.getSource().sendSuccess(() -> Component.literal(compatLine()), false);
                    return 1;
                }))
                .then(Commands.literal("reload").executes(context -> {
                    ZeroChurnConfigLoader.load();
                    context.getSource().sendSuccess(
                        () -> Component.literal("Reloaded ZeroChurn config. Pool capacities stay at their first-use values."),
                        false
                    );
                    context.getSource().sendSuccess(() -> Component.literal(ZeroChurnStats.statusLine()), false);
                    return 1;
                }))
        ));
    }

    private static String compatLine() {
        StringBuilder out = new StringBuilder(96);
        out.append("Compat: lithium=");
        out.append(ModCompat.isLithiumLoaded());
        out.append(" sodium=");
        out.append(ModCompat.isSodiumLoaded());
        out.append(" immediatelyfast=");
        out.append(ModCompat.isImmediatelyFastLoaded());
        out.append(" infoforge=");
        out.append(ModCompat.isInfoForgeLoaded());
        return out.toString();
    }
}
