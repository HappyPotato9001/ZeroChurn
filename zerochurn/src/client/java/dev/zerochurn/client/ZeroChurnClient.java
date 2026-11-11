package dev.zerochurn.client;

import dev.zerochurn.compat.ModCompat;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.metrics.ZeroChurnStats;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;

public final class ZeroChurnClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommandLiterals.literal("zerochurn")
                .then(ClientCommandLiterals.literal("stats").executes(context -> {
                    context.getSource().sendFeedback(Component.literal(ZeroChurnStats.summary()));
                    return 1;
                }))
                .then(ClientCommandLiterals.literal("status").executes(context -> {
                    context.getSource().sendFeedback(Component.literal(ZeroChurnStats.statusLine()));
                    context.getSource().sendFeedback(Component.literal(
                        "Compat: lithium=" + ModCompat.isLithiumLoaded()
                            + " sodium=" + ModCompat.isSodiumLoaded()
                            + " immediatelyfast=" + ModCompat.isImmediatelyFastLoaded()
                            + " infoforge=" + ModCompat.isInfoForgeLoaded()
                    ));
                    return 1;
                }))
                .then(ClientCommandLiterals.literal("reload").executes(context -> {
                    ZeroChurnConfigLoader.load();
                    context.getSource().sendFeedback(
                        Component.literal("Reloaded ZeroChurn config. Pool capacities stay at their first-use values.")
                    );
                    context.getSource().sendFeedback(Component.literal(ZeroChurnStats.statusLine()));
                    return 1;
                }))
        ));
    }
}
