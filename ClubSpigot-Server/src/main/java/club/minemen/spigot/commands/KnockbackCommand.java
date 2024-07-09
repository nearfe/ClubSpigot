package club.minemen.spigot.commands;

import club.minemen.spigot.commons.ClickableBuilder;
import club.minemen.spigot.knockback.CraftKnockbackProfile;
import club.minemen.spigot.knockback.KnockbackConfig;
import dev.cobblesword.nachospigot.knockback.KnockbackProfile;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KnockbackCommand extends Command {

    private final String separator = "§8§m-=-------------------------=-";

    public KnockbackCommand(String name) {
        super(name);
        this.description = "Assists in knockback configuration.";
        this.setPermission("clubspigot.command.knockback");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender) || !(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        switch (args.length) {
            case 2: {
                switch (args[0].toLowerCase()) {
                    case "create": {
                        if (!isProfileName(args[1])) {
                            CraftKnockbackProfile profile = new CraftKnockbackProfile(args[1]);
                            KnockbackConfig.getKbProfiles().add(profile);
                            profile.save();
                            knockbackCommandMain(player);
                            player.sendMessage("§aThe profile §e" + args[1] + " §ahas been created.");
                            return true;
                        } else {
                            player.sendMessage("§cA knockback profile with that name already exists.");
                        }
                        break;
                    }
                    case "delete": {
                        if (KnockbackConfig.getCurrentKb().getName().equalsIgnoreCase(args[1])) {
                            knockbackCommandMain(player);
                            player.sendMessage("§cYou cannot delete the profile that is being used.");
                            return false;
                        }
                        if (KnockbackConfig.getKbProfiles().removeIf(profile -> profile.getName().equalsIgnoreCase(args[1]))) {
                            KnockbackConfig.set("knockback.profiles." + args[1], null);
                            knockbackCommandMain(player);
                            player.sendMessage("§aThe profile §e" + args[1] + " §ahas been removed.");
                            return true;
                        } else {
                            player.sendMessage("§cThis profile doesn't exist.");
                        }
                        break;
                    }
                    case "load": {
                        KnockbackProfile profile = KnockbackConfig.getKbProfileByName(args[1]);
                        if (profile != null) {
                            if (KnockbackConfig.getCurrentKb().getName().equalsIgnoreCase(args[1])) {
                                player.sendMessage("§cThis profile is loaded.");
                                return false;
                            }
                            KnockbackConfig.setCurrentKb(profile);
                            KnockbackConfig.set("knockback.current", profile.getName());
                            KnockbackConfig.save();
                            knockbackCommandMain(player);
                            player.sendMessage("§aThe profile §e" + args[1] + " §ahas been loaded.");
                            return true;
                        } else {
                            player.sendMessage("§cThis profile doesn't exist.");
                        }
                        break;
                    }
                    case "view": {
                        KnockbackProfile profile = KnockbackConfig.getKbProfileByName(args[1]);
                        if (profile != null) {
                            knockbackCommandView(player, profile);
                            return true;
                        }
                        player.sendMessage("§cThis profile doesn't exist.");
                        break;
                    }
                    default: {
                        knockbackCommandMain(player);
                    }
                }
                break;
            }
            case 3: {
                switch (args[0].toLowerCase()) {
                    case "set": {
                        KnockbackProfile profile = KnockbackConfig.getKbProfileByName(args[1]);
                        if (profile == null) {
                            sender.sendMessage("§cA profile with that name could not be found.");
                            return false;
                        }
                        Player target = Bukkit.getPlayer(args[2]);
                        if (target == null) {
                            sender.sendMessage("§cThat player is not online.");
                            return false;
                        }
                        target.setKnockbackProfile(profile);
                        break;
                    }
                }
                break;
            }
            case 4: {
                if ("edit".equalsIgnoreCase(args[0])) {
                    CraftKnockbackProfile profile = (CraftKnockbackProfile) KnockbackConfig.getKbProfileByName(args[1].toLowerCase());
                    if (profile == null) {
                        player.sendMessage("§cThis profile doesn't exist.");
                        return false;
                    }
                    switch (args[2].toLowerCase()) {
                        case "sprint-multiplier": {
                            if (!NumberUtils.isNumber(args[3])) {
                                player.sendMessage("§4" + args[3] + " §c is not a number.");
                                return false;
                            }
                            double value = Double.parseDouble(args[3]);
                            profile.setSprintMultiplier(value);
                            profile.save();
                            knockbackCommandView(player, profile);
                            player.sendMessage("§aValue edited and saved.");
                            break;
                        }
                        case "range-factor": {
                            if (!NumberUtils.isNumber(args[3])) {
                                player.sendMessage("§4" + args[3] + " §c is not a number.");
                                return false;
                            }
                            double value = Double.parseDouble(args[3]);
                            profile.setRangeFactor(value);
                            profile.save();
                            knockbackCommandView(player, profile);
                            player.sendMessage("§aValue edited and saved.");
                            break;
                        }
                        case "horizontal": {
                            if (!NumberUtils.isNumber(args[3])) {
                                player.sendMessage("§4" + args[3] + " §c is not a number.");
                                return false;
                            }
                            double value = Double.parseDouble(args[3]);
                            profile.setHorizontal(value);
                            profile.save();
                            knockbackCommandView(player, profile);
                            player.sendMessage("§aValue edited and saved.");
                            break;
                        }
                        case "vertical": {
                            if (!NumberUtils.isNumber(args[3])) {
                                player.sendMessage("§4" + args[3] + " §c is not a number.");
                                return false;
                            }
                            double value = Double.parseDouble(args[3]);
                            profile.setVertical(value);
                            profile.save();
                            knockbackCommandView(player, profile);
                            player.sendMessage("§aValue edited and saved.");
                            break;
                        }
                        case "vertical-limit": {
                            if (!NumberUtils.isNumber(args[3])) {
                                if (isBoolean(args[3])) {
                                    profile.setVerticalLimit(Boolean.parseBoolean(args[3]));
                                    profile.save();
                                    knockbackCommandView(player, profile);
                                    player.sendMessage("§aValue edited and saved.");
                                } else {
                                    sender.sendMessage("§4" + args[3] + " §c is not a number or a boolean.");
                                    return false;
                                }
                            }
                            double value = Double.parseDouble(args[3]);
                            profile.setVerticalLimitValue(value);
                            profile.save();
                            knockbackCommandView(player, profile);
                            player.sendMessage("§aValue edited and saved.");
                            break;
                        }
                        case "range-reduction": {
                            if (!NumberUtils.isNumber(args[3])) {
                                player.sendMessage("§4" + args[3] + " §c is not a number.");
                                return false;
                            }
                            double value = Double.parseDouble(args[3]);
                            profile.setMaxRangeReduction(value);
                            profile.save();
                            knockbackCommandView(player, profile);
                            player.sendMessage("§aValue edited and saved.");
                            break;
                        }
                        case "start-range-reduction": {
                            if (!NumberUtils.isNumber(args[3])) {
                                player.sendMessage("§4" + args[3] + " §c is not a number.");
                                return false;
                            }
                            double value = Double.parseDouble(args[3]);
                            profile.setStartRangeReduction(value);
                            profile.save();
                            knockbackCommandView(player, profile);
                            player.sendMessage("§aValue edited and saved.");
                            break;
                        }

                        case "min-range": {
                            if (!NumberUtils.isNumber(args[3])) {
                                player.sendMessage("§4" + args[3] + " §c is not a number.");
                                return false;
                            }
                            double value = Double.parseDouble(args[3]);
                            profile.setMinRange(value);
                            profile.save();
                            knockbackCommandView(player, profile);
                            player.sendMessage("§aValue edited and saved.");
                            break;
                        }
                    }
                }
                break;
            }
            default: {
                knockbackCommandMain(player);
            }
        }
        return false;
    }

    private boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
    }

    private void knockbackCommandMain(Player player) {
        player.sendMessage(separator + "\n" + "§a§lKnockback profile list:\n");

        for (KnockbackProfile profile : KnockbackConfig.getKbProfiles()) {
            boolean current = KnockbackConfig.getCurrentKb().getName().equals(profile.getName());

            TextComponent line = new ClickableBuilder("§8§l(§e§l➜§8§l) ")
                    .setHover("§c[Click here to apply this profile to a player] ")
                    .setClick("/knockback set " + profile.getName() + " ", ClickEvent.Action.SUGGEST_COMMAND).build();
            TextComponent load = new ClickableBuilder("§8§l(" + (current ? "§a" : "§7") + "✔§8§l) ")
                    .setHover(current ? "§c[This profile is loaded] " : "§a[Click here to load this profile]")
                    .setClick("/knockback load " + profile.getName(), ClickEvent.Action.RUN_COMMAND).build();
            TextComponent delete = new ClickableBuilder("§8§l(§c§l✖§8§l) ")
                    .setHover("§c[Click here to delete this profile] ")
                    .setClick("/knockback delete " + profile.getName(), ClickEvent.Action.RUN_COMMAND).build();
            TextComponent edit = new ClickableBuilder("§7 " + profile.getName() + " §8§l[§6§l✎§8§l]")
                    .setHover("§e[Click here to edit this profile]")
                    .setClick("/knockback view " + profile.getName(), ClickEvent.Action.RUN_COMMAND).build();
            player.spigot().sendMessage(line, load, delete, edit);
        }

        player.spigot()
                .sendMessage(new ClickableBuilder("\n§8§l[§a§lCreate new profile§8]")
                        .setHover("§c[Click here to create a new profile]")
                        .setClick("/knockback create ", ClickEvent.Action.SUGGEST_COMMAND).build());
        player.sendMessage(separator);
    }

    private void knockbackCommandView(Player player, KnockbackProfile profile) {
        player.sendMessage(separator + "\n" + "§a§lKnockback values:\n");
        for (String values : profile.getKnockbackValues()) {
            TextComponent value = new TextComponent("§6» §e" + values);
            TextComponent edit = new ClickableBuilder(" §8§l[§e§l✎§8§l]")
                    .setHover("§e[Click to edit " + values + " value]")
                    .setClick("/knockback edit " + profile.getName() + " " + values.replace("§7: ", " "),
                            ClickEvent.Action.SUGGEST_COMMAND)
                    .build();
            player.spigot().sendMessage(value, edit);
        }
        TextComponent page = new ClickableBuilder("\n§8§l[§c§l⬑§8§l] ").setHover("§e[Click to back]")
                .setClick("/knockback", ClickEvent.Action.RUN_COMMAND).build();
        TextComponent projectiles = new ClickableBuilder(" §8§l[§a§lEdit projectiles§8§l]")
                .setClick("/knockback projectile " + profile.getName(), ClickEvent.Action.RUN_COMMAND)
                .setHover("§e[Click to edit projectiles]").build();
        player.spigot().sendMessage(page, projectiles);
        player.sendMessage(separator);
    }

    private boolean isProfileName(String name) {
        for (KnockbackProfile profile : KnockbackConfig.getKbProfiles()) {
            if (profile.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}