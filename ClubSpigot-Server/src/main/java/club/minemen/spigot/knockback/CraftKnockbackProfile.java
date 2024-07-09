package club.minemen.spigot.knockback;

import club.minemen.spigot.knockback.KnockbackConfig;
import dev.cobblesword.nachospigot.knockback.KnockbackProfile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CraftKnockbackProfile implements KnockbackProfile {

	private String name;
	private final String saveProfilePath;

	private double horizontal = 0.9055D;
	private double vertical = 0.25635D;
	private double sprintMultiplier = 1;
	private double rangeFactor = 0.025D;
	private double maxRangeReduction = 1.2D;
	private double startRangeReduction = 3.0D;
	private double minRange = 0.12D;
	private boolean verticalLimit = true;
	private double verticalLimitValue = 4.0D;

	public CraftKnockbackProfile(String name) {
		this.name = name;
		this.saveProfilePath = "knockback.profiles." + this.name;
	}

	public void save() {
		save(false);
	}

	private void set(String savePath, Object value) {
		KnockbackConfig.set(saveProfilePath + savePath, value);
	}

	public void save(boolean projectiles) {

		set(".horizontal", this.horizontal);
		set(".vertical", this.vertical);
		set(".sprint-multiplier", this.sprintMultiplier);
		set(".range-factor", this.rangeFactor);
		set(".max-range-reduction", this.maxRangeReduction);
		set(".start-range-reduction", this.startRangeReduction);
		set(".min-range", this.minRange);
		set(".vertical-limit", this.verticalLimit);
		set(".vertical-limit-value", this.verticalLimitValue);

		KnockbackConfig.save();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String[] getKnockbackValues() {
		return new String[]{
				"Horizontal§7: " + this.horizontal, "Vertical§7: " + this.vertical,
				"Sprint-Multiplier§7: " + this.sprintMultiplier, "Range-Factor§7: " + this.rangeFactor,
				"Max-Range-Reduction§7: " + this.maxRangeReduction, "Start-Range-Reduction§7: " + this.startRangeReduction,
				"Min-Range§7: " + this.minRange, "Vertical-Limit§7: " + this.verticalLimit,
				"Vertical-Limit-Value§7: " + this.verticalLimitValue
		};
	}
}