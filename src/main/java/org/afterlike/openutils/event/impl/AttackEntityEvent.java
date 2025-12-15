package org.afterlike.openutils.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.afterlike.openutils.event.api.Event;
import org.jetbrains.annotations.NotNull;

public class AttackEntityEvent implements Event {
	private final @NotNull EntityPlayer playerIn;
	private final @NotNull Entity target;
	public AttackEntityEvent(@NotNull EntityPlayer playerIn, @NotNull Entity target) {
		this.playerIn = playerIn;
		this.target = target;
	}

	public @NotNull EntityPlayer getPlayerIn() {
		return playerIn;
	}

	public @NotNull Entity getTarget() {
		return target;
	}
}
