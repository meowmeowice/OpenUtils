package org.afterlike.openutils.module.impl.render;

import org.afterlike.openutils.module.api.Module;
import org.afterlike.openutils.module.api.ModuleCategory;
import org.afterlike.openutils.module.api.setting.impl.TextFieldSetting;
import org.afterlike.openutils.util.client.ClientUtil;
import org.afterlike.openutils.util.client.TextUtil;

public class NameHiderModule extends Module {
	private final TextFieldSetting customName;
	private String cachedUsername;
	public NameHiderModule() {
		super("Name Hider", ModuleCategory.RENDER);
		customName = this
				.registerSetting(new TextFieldSetting("Custom Name", "&bYou", "Enter name..."));
	}

	public String replaceName(String text) {
		if (!ClientUtil.notNull()) {
			return text;
		}
		String username = mc.thePlayer.getName();
		if (!username.equals(cachedUsername)) {
			cachedUsername = username;
		}
		if (!text.contains(cachedUsername)) {
			return text;
		}
		String replacement = TextUtil.replaceColorCodes(customName.getValue());
		return text.replace(cachedUsername, replacement);
	}
}
