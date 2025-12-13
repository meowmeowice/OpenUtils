package org.afterlike.openutils.module.impl.world;

import net.minecraft.client.Minecraft;
import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;
import org.afterlike.openutils.module.api.setting.impl.NumberSetting;

public class TimeChangerModule extends Module {
	private final NumberSetting time;
	private final BooleanSetting fastTime;
	private final NumberSetting fastSpeed;
	public TimeChangerModule() {
		super("Time Changer", ModuleCategory.WORLD);
		time = this.registerSetting(new NumberSetting("Time", 6.0, 0.0, 24.0, 0.1));
		fastTime = this.registerSetting(new BooleanSetting("Fast Time", false));
		fastSpeed = this.registerSetting(new NumberSetting("Fast Speed", 1.0, 0.1, 10.0, 0.1));
	}

	public long timeToTicks() {
		if (fastTime.getValue()) {
			final long speed = fastSpeed.getValue().longValue();
			if (speed <= 0) {
				return timeToMcTime(time.getValue().floatValue());
			}
			return (Minecraft.getSystemTime() % (24000L / speed)) * speed;
		}
		return timeToMcTime(time.getValue().floatValue());
	}

	private long timeToMcTime(final float time) {
		return (long) (time * 1000L) + 18000L;
	}
}
