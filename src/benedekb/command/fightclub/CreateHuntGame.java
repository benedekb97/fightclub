package benedekb.command.fightclub;

import benedekb.game.fightclub.GameInterface;
import benedekb.game.fightclub.HuntGame;
import benedekb.game.fightclub.GameType;
import benedekb.main.fightclub.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CreateHuntGame extends CreateGame implements CommandInterface{
    private HuntGame game;

    public CreateHuntGame(
        Server server,
        Main plugin
    )
    {
        super(server, plugin);
    }

    public GameInterface run()
    {
        World world;
        Location location;
        Player firstPlayer = null;
        Player huntedPlayer = null;
        double mapCenterX = 0.0, mapCenterZ = 0.0;
        this.game = new HuntGame(this.plugin);

        for (Player player : server.getOnlinePlayers()) {
            firstPlayer = player;
            break;
        }

        if (firstPlayer == null) {
            return null;
        }

        world = firstPlayer.getWorld();

        location = this.getGameLocation(world);

        int i = 0;

        int huntedPlayerId = (int)this.numberBetween(1, server.getOnlinePlayers().size());

        this.movePlayersToLocation(location);

        for (Player player : server.getOnlinePlayers()) {
            i++;

            if (i == huntedPlayerId) {
                huntedPlayer = player;
            } else {
                player.getInventory().addItem(new ItemStack(Material.COMPASS));
                player.updateInventory();
            }
        }

        super.game = game;

        server.broadcastMessage("You should try and punch: " + huntedPlayer.getDisplayName());

        this.scheduleTasksWithInterval(game.getTime());

        int endGameTask = server.getScheduler().scheduleSyncDelayedTask(
                this.plugin,
                () -> {
                    if (this.plugin.isGameInProgress()) {
                        this.plugin.endGame(game.getHuntedPlayer());
                    }
                },
                20L * this.plugin.getDefaultTime()
        );

        this.scheduledTasks = new int[1];
        this.scheduledTasks[0] = endGameTask;

        game.initialize(huntedPlayer, this.plugin.getDefaultTime(), this.scheduledTasks, GameType.HUNT);

        return game;
    }
}
