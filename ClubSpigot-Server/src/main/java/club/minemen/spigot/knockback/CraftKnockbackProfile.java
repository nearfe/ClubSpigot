package club.minemen.spigot.knockback;

import dev.cobblesword.nachospigot.knockback.KnockbackProfile;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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
}
