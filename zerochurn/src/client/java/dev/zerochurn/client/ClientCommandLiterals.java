package dev.zerochurn.client;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.lang.reflect.Method;

/**
 * Fabric renamed ClientCommandManager -> ClientCommands between 1.21.11 and 26.x.
 * Resolve at runtime so one source tree builds for both eras.
 */
final class ClientCommandLiterals {
    private static final Method LITERAL = resolveLiteral();

    private ClientCommandLiterals() {
    }

    @SuppressWarnings("unchecked")
    static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        try {
            return (LiteralArgumentBuilder<FabricClientCommandSource>) LITERAL.invoke(null, name);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Fabric client command literal() unavailable", e);
        }
    }

    private static Method resolveLiteral() {
        String[] types = {
            "net.fabricmc.fabric.api.client.command.v2.ClientCommands",
            "net.fabricmc.fabric.api.client.command.v2.ClientCommandManager"
        };
        for (String typeName : types) {
            try {
                return Class.forName(typeName).getMethod("literal", String.class);
            } catch (ReflectiveOperationException ignored) {
            }
        }
        throw new IllegalStateException("Neither ClientCommands nor ClientCommandManager found");
    }
}
