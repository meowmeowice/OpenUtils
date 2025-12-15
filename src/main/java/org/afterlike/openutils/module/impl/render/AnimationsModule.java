package org.afterlike.openutils.module.impl.render;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;

public class AnimationsModule extends Module {
	private final BooleanSetting forceBlockAnimation;
	private final BooleanSetting requireMouseDown;
	public AnimationsModule() {
		super("Animations", ModuleCategory.RENDER);
		forceBlockAnimation = this
				.registerSetting(new BooleanSetting("Force block animation", true));
		// TODO: this setting needs to work with clients that simulate right clicking
		requireMouseDown = this.registerSetting(new BooleanSetting("Require mouse down", false));
	}
}
