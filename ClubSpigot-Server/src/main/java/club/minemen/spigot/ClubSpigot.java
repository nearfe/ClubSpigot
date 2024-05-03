package club.minemen.spigot;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import club.minemen.spigot.async.pathsearch.SearchHandler;
import club.minemen.spigot.async.thread.CombatThread;
import club.minemen.spigot.commands.MobAICommand;
import club.minemen.spigot.commands.PingCommand;
import club.minemen.spigot.commands.SetMaxSlotCommand;
import club.minemen.spigot.commands.SpawnMobCommand;
import club.minemen.spigot.config.ClubSpigotConfig;
import club.minemen.spigot.protocol.MovementListener;
import club.minemen.spigot.protocol.PacketListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import club.minemen.spigot.async.AsyncUtil;
import club.minemen.spigot.hitdetection.LagCompensator;
import club.minemen.spigot.statistics.StatisticsClient;
import net.minecraft.server.MinecraftServer;
import xyz.sculas.nacho.anticrash.AntiCrash;
import xyz.sculas.nacho.async.AsyncExplosions;

public class ClubSpigot {

	private StatisticsClient client;

	public static final Logger LOGGER = LogManager.getLogger();
	private static final Logger DEBUG_LOGGER = LogManager.getLogger();
	private static ClubSpigot INSTANCE;

	private CombatThread knockbackThread;

	private final Executor statisticsExecutor = Executors
			.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("ClubSpigot Statistics Thread")
					.build());

	private volatile boolean statisticsEnabled = false;

	private LagCompensator lagCompensator;

	private final Set<PacketListener> packetListeners = Sets.newConcurrentHashSet();
	private final Set<MovementListener> movementListeners = Sets.newConcurrentHashSet();

	public ClubSpigot() {
		INSTANCE = this;
		this.init();
	}

	public void reload() {
		this.init();
	}

	private void initCmds() {

		SimpleCommandMap commandMap = MinecraftServer.getServer().server.getCommandMap();

		if (ClubSpigotConfig.mobAiCmd) {
			MobAICommand mobAiCommand = new MobAICommand("mobai");
			commandMap.register(mobAiCommand.getName(), "", mobAiCommand);
		}

		if (ClubSpigotConfig.pingCmd) {
			PingCommand pingCommand = new PingCommand("ping");
			commandMap.register(pingCommand.getName(), "", pingCommand);
		}



		// NachoSpigot commands
		// TODO: add configuration for all of these
		SetMaxSlotCommand setMaxSlotCommand = new SetMaxSlotCommand("sms"); // [Nacho-0021] Add setMaxPlayers within Bukkit.getServer() and SetMaxSlot Command
		commandMap.register(setMaxSlotCommand.getName(), "ns", setMaxSlotCommand);

		SpawnMobCommand spawnMobCommand = new SpawnMobCommand("spawnmob");
		commandMap.register(spawnMobCommand.getName(), "ns", spawnMobCommand);
	}

	private void initStatistics() {
		if (ClubSpigotConfig.statistics && !statisticsEnabled) {
			Runnable statisticsRunnable = (() -> {
				client = new StatisticsClient();
				try {
					statisticsEnabled = true;

					if (!client.isConnected) {
						// Connect to the statistics server and notify that there is a new server
						client.start("150.230.35.78", 500);
						client.sendMessage("new server");

						while (true) {
							// Keep alive, this tells the statistics server that this server
							// is still online
							client.sendMessage("keep alive packet");

							// Online players, this tells the statistics server how many players
							// are on
							client.sendMessage("player count packet " + Bukkit.getOnlinePlayers().size());

							// Statistics are sent every 40 secs.
							TimeUnit.SECONDS.sleep(40);
						}

					}
				} catch (Exception ignored) {}
			});
			AsyncUtil.run(statisticsRunnable, statisticsExecutor);
		}
	}

	private void init() {
		initCmds();
		initStatistics();

		// We do not want to initialize this again after a reload
		if (ClubSpigotConfig.asyncPathSearches && SearchHandler.getInstance() == null) {
			new SearchHandler();
		}

		if (ClubSpigotConfig.asyncKnockback) {
			knockbackThread = new CombatThread("Knockback Thread");
		}
		lagCompensator = new LagCompensator();
		if (ClubSpigotConfig.asyncTnt) {
			AsyncExplosions.initExecutor(ClubSpigotConfig.fixedPoolSize);
		}
		if (ClubSpigotConfig.enableAntiCrash) {
			registerPacketListener(new AntiCrash());
		}
	}

	public StatisticsClient getClient() {
		return this.client;
	}

	public CombatThread getKnockbackThread() {
		return knockbackThread;
	}

	public LagCompensator getLagCompensator() {
		return lagCompensator;
	}

	public static void debug(String msg) {
		if (ClubSpigotConfig.debugMode)
			DEBUG_LOGGER.info(msg);
	}

	public void registerPacketListener(PacketListener packetListener) {
		this.packetListeners.add(packetListener);
	}

	public void unregisterPacketListener(PacketListener packetListener) {
		this.packetListeners.remove(packetListener);
	}

	public Set<PacketListener> getPacketListeners() {
		return this.packetListeners;
	}

	public void registerMovementListener(MovementListener movementListener) {
		this.movementListeners.add(movementListener);
	}

	public void unregisterMovementListener(MovementListener movementListener) {
		this.movementListeners.remove(movementListener);
	}

	public Set<MovementListener> getMovementListeners() {
		return this.movementListeners;
	}

	public static ClubSpigot getInstance() {
		return INSTANCE;
	}
}