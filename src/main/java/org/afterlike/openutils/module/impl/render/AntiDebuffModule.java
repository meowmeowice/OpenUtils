package org.afterlike.openutils.module.impl.render;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.BooleanSetting;

public class AntiDebuffModule extends Module {
	private final BooleanSetting nausea;
	private final BooleanSetting blindness;
	public AntiDebuffModule() {
		super("Anti Debuff", ModuleCategory.RENDER);
		nausea = this.registerSetting(new BooleanSetting("Remove nausea", true));
		blindness = this.registerSetting(new BooleanSetting("Remove blindness", true));
	}
}
