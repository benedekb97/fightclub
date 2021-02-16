package benedekb.command.fightclub;

import benedekb.game.fightclub.GameType;
import benedekb.game.fightclub.TagGame;
import benedekb.main.fightclub.Main;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class CreateTagGame extends CreateGame implements CommandInterface{
    public CreateTagGame(Server server, Main plugin)
    {
        super(server, plugin);
    }

    public TagGame run() {
        World world = null;
        Location location = null;
        TagGame game = new TagGame(this.plugin);
        Player firstPlayer = null;
        Player it = null;

        for (Player player : server.getOnlinePlayers()) {
            firstPlayer = player;
            break;
        }

        world = firstPlayer.getWorld();

        location = this.getGameLocation(world);

        this.movePlayersToLocation(location);

        int taggedPlayerId = (int)this.numberBetween(1, server.getOnlinePlayers().size());
        int i = 0;

        for (Player player : server.getOnlinePlayers()) {
            i++;

            if (taggedPlayerId == i) {
                it = player;
            }
        }

        if (it == null) {
            return null;
        }

        super.game = game;
        this.scheduleTasksWithInterval(this.plugin.getDefaultTime());

        game.initialize(it, this.plugin.getDefaultTime(), this.scheduledTasks, GameType.TAG);

        int endGameTask = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
            this.plugin,
            () -> {
                this.plugin.endGame(null);
            },
            this.plugin.getDefaultTime() * 20L
        );

        this.plugin.getServer().broadcastMessage(it.getDisplayName() + " a fogó!");

        game.addScheduledTask(endGameTask);

        return game;
    }
}
