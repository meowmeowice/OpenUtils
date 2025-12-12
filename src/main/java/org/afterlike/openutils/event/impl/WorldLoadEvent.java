package org.afterlike.openutils.event.impl;

import net.minecraft.client.multiplayer.WorldClient;
import org.afterlike.openutils.event.api.Event;
import org.jetbrains.annotations.NotNull;

public class WorldLoadEvent implements Event {
	private final WorldClient world;
	public WorldLoadEvent(final @NotNull WorldClient world) {
		this.world = world;
	}

	public @NotNull WorldClient getWorld() {
		return world;
	}
}
