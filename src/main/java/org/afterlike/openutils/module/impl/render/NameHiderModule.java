package org.afterlike.openutils.module.impl.render;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.DescriptionSetting;
import org.jetbrains.annotations.NotNull;

public class NameHiderModule extends Module {
	private final @NotNull DescriptionSetting todo;
	public NameHiderModule() {
		super("Name Hider", ModuleCategory.RENDER);
		todo = this.registerSetting(new DescriptionSetting("Not implemented yet"));
	}
}
