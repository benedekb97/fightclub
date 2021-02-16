package benedekb.command.fightclub;

import benedekb.game.fightclub.GameInterface;
import benedekb.main.fightclub.Main;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class CreateGame {
    protected final Server server;
    protected final Main plugin;

    protected final Biome[] OCEAN_BIOMES = {
            Biome.OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.WARM_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.DEEP_WARM_OCEAN,
            Biome.LUKEWARM_OCEAN
    };

    protected final double MAX_SIZE = 128;

    protected int[] scheduledTasks = {};
    protected GameInterface game;

    public CreateGame(Server server, Main plugin)
    {
        this.server = server;
        this.plugin = plugin;
    }

    protected Location getGameLocation(World world)
    {
        Location location;
        double mapCenterX, mapCenterZ;

        try {
            do {
                mapCenterX = this.numberBetween(
                        server.getMaxWorldSize(),
                        (-1) * server.getMaxWorldSize()
                );
                mapCenterZ = this.numberBetween(
                        server.getMaxWorldSize(),
                        (-1) * server.getMaxWorldSize()
                );

                location = new Location(
                        world,
                        mapCenterX,
                        world.getHighestBlockYAt((int)mapCenterX, (int)mapCenterZ),
                        mapCenterZ
                );
            } while(this.checkBiome(world, location));
        } catch (NullPointerException exception) {
            this.server.getLogger().log(Level.SEVERE, exception.getMessage());

            return null;
        }

        return location;
    }

    protected double numberBetween(int min, int max)
    {
        return Math.random() * (max - min) + min;
    }

    protected void scheduleTasksWithInterval(int maxTime)
    {
        if (game == null) {
            return;
        }

        if (maxTime > 10) {
            for (int j = 1; j <= 10; j++) {
                this.scheduleTimerMessage(j, maxTime);
            }
        }

        if (maxTime >= 30) {
            this.scheduleTimerMessage(30, maxTime);
        }

        if (maxTime >= 60) {
            this.scheduleTimerMessage(60, maxTime);
        }

        if (maxTime >= 120) {
            this.scheduleTimerMessage(120, maxTime);
        }
    }

    protected void movePlayersToLocation(Location location)
    {
        // teleport players to new arena
        for (Player player : server.getOnlinePlayers()) {
            double playerLocationX = this.numberBetween((
                            int)(location.getBlockX() - MAX_SIZE / 2),
                    (int)(location.getBlockX() + MAX_SIZE / 2)
            );

            double playerLocationZ = this.numberBetween(
                    (int)(location.getBlockZ() - MAX_SIZE / 2),
                    (int)(location.getBlockZ() + MAX_SIZE / 2)
            );

            server.getConsoleSender().sendMessage("Player X: " + playerLocationX + " Z: " + playerLocationZ);
            server.getConsoleSender().sendMessage("World center X: " + location.getBlockX() + " Z: " + location.getBlockZ());

            Location playerLocation = new Location(
                    player.getWorld(),
                    playerLocationX,
                    player.getWorld().getHighestBlockYAt((int)playerLocationX, (int)playerLocationZ) + 2,
                    playerLocationZ
            );

            player.teleport(playerLocation);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getInventory().clear();
        }

        server.getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> {
                    // set world border
                    for (World currentWorld : server.getWorlds()) {
                        currentWorld.getWorldBorder().setCenter(location.getBlockX(), location.getBlockZ());
                        currentWorld.getWorldBorder().setSize(MAX_SIZE);
                    }
                },
                40
        );
    }

    private boolean checkBiome(World world, Location location)
    {
        List<Biome> oceanBiomes = Arrays.asList(OCEAN_BIOMES);

        return oceanBiomes.contains(
                world.getBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ())
        );
    }

    private void scheduleTimerMessage(int secondsLeft, int maxTime)
    {
        int taskId = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> {
                    if (this.plugin.isGameInProgress()) {
                        this.plugin.getServer().broadcastMessage(secondsLeft + " seconds left!");
                    }
                },
                (maxTime - secondsLeft) * 20L
        );

        int[] newScheduledTasks = new int[this.scheduledTasks.length + 1];

        int i = 0;

        for (int task : this.scheduledTasks) {
            newScheduledTasks[i] = task;
            i++;
        }

        newScheduledTasks[i] = taskId;

        this.scheduledTasks = newScheduledTasks;
    }
}
