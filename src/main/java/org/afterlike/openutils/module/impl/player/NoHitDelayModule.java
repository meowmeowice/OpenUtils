package org.afterlike.openutils.module.impl.player;

import org.afterlike.openutils.event.api.EventPhase;
import org.afterlike.openutils.event.handler.EventHandler;
import org.afterlike.openutils.event.impl.GameTickEvent;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.platform.mixin.minecraft.client.MinecraftAccessor;
import org.afterlike.openutils.util.client.ClientUtil;
import org.jetbrains.annotations.NotNull;

public class NoHitDelayModule extends Module {

    public NoHitDelayModule() {
        super("No Hit Delay", ModuleCategory.PLAYER);
    }

    @EventHandler
    private void onTick(final @NotNull GameTickEvent event) {
        if (event.getPhase() != EventPhase.PRE) return;
        if (!ClientUtil.notNull()) return;

        MinecraftAccessor accessor = (MinecraftAccessor) mc;
        accessor.ou$setLeftClickCounter(0);
    }
}
