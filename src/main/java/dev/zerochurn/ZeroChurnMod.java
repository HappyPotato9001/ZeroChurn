package dev.zerochurn;

import dev.zerochurn.command.ZeroChurnCommands;
import dev.zerochurn.compat.CompatibilityReport;
import dev.zerochurn.config.ZeroChurnConfigLoader;
import net.fabricmc.api.ModInitializer;

public final class ZeroChurnMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ZeroChurnConfigLoader.load();
        ZeroChurnCommands.register();
        CompatibilityReport.log();
    }
}
