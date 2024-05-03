package dev.cobblesword.nachospigot.knockback;

public interface KnockbackProfile {

	void save();

	void save(boolean projectiles);

	String getName();

	void setName(String name);
}
