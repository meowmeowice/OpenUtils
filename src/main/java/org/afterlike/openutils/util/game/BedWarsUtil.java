package org.afterlike.openutils.util.game;

import net.minecraft.entity.player.EntityPlayer;
import org.jetbrains.annotations.Nullable;

public final class BedWarsUtil {
	public enum TeamColor {
		RED("Red", "§c"), BLUE("Blue", "§9"), GREEN("Green", "§a"), YELLOW("Yellow", "§e"), AQUA(
				"Aqua", "§b"), WHITE("White", "§f"), PINK("Pink", "§d"), GRAY("Gray", "§8");
		private final String displayName;
		private final String colorCode;
		TeamColor(final String displayName, final String colorCode) {
			this.displayName = displayName;
			this.colorCode = colorCode;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getColorCode() {
			return colorCode;
		}

		public static TeamColor fromFormattedName(@Nullable final String formattedName) {
			if (formattedName == null) {
				return null;
			}
			for (TeamColor color : values()) {
				if (formattedName.contains(color.colorCode)) {
					return color;
				}
			}
			return null;
		}
	}
	public static @Nullable String getTeamName(@Nullable final EntityPlayer player) {
		TeamColor color = getTeamColor(player);
		return color != null ? color.getDisplayName() : null;
	}

	public static @Nullable TeamColor getTeamColor(@Nullable final EntityPlayer player) {
		if (player == null) {
			return null;
		}
		return TeamColor.fromFormattedName(player.getDisplayName().getFormattedText());
	}
}
