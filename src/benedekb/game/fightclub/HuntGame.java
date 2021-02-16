package benedekb.game.fightclub;

import benedekb.main.fightclub.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HuntGame extends Game implements GameInterface {
    private Player huntedPlayer;

    private int time;

    private GameType type;

    private Location gameLocation;

    public HuntGame(Main plugin)
    {
        super(plugin);
    }

    public boolean isCompassEnabled()
    {
        return true;
    }

    public Location getCompassTarget()
    {
        return this.huntedPlayer.getLocation();
    }

    public void initialize(Player huntedPlayer, int time, int[] scheduledTasks, GameType type)
    {
        this.huntedPlayer = huntedPlayer;
        this.time = time;
        this.scheduledTasks = scheduledTasks;
        this.type = type;
    }

    public void end(Player winner)
    {
        this.finish();

        String message;

        if (winner == null) {
            message = "The hunter(s) won the game!";
        } else {
            message = (new StringBuilder())
                    .append(winner.getDisplayName())
                    .append(" won the game!")
                    .toString();
        }

        this.plugin.getServer().broadcastMessage(message);

        this.huntedPlayer = null;
    }

    public void setTime(int time)
    {
        this.time = time;
    }

    public int getTime()
    {
        return this.time;
    }

    public Player getHuntedPlayer()
    {
        return this.huntedPlayer;
    }

    public GameType getType()
    {
        return this.type;
    }

    public void setLocation(Location gameLocation)
    {
        this.gameLocation = gameLocation;
    }

    public Location getGameLocation()
    {
        return this.gameLocation;
    }
}
