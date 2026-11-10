package dev.zerochurn.client;

import dev.zerochurn.config.ZeroChurnConfigLoader;
import dev.zerochurn.metrics.ZeroChurnStats;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.minecraft.network.chat.Component;

public final class ZeroChurnClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
            ClientCommands.literal("zerochurn")
                .then(ClientCommands.literal("stats").executes(context -> {
                    context.getSource().sendFeedback(Component.literal(ZeroChurnStats.summary()));
                    return 1;
                }))
                .then(ClientCommands.literal("reload").executes(context -> {
                    ZeroChurnConfigLoader.load();
                    context.getSource().sendFeedback(
                        Component.literal("Reloaded ZeroChurn config. Pool capacities stay at their first-use values.")
                    );
                    return 1;
                }))
        ));
    }
}
