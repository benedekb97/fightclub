package benedekb.game.fightclub;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface GameInterface {
    void end(Player winner);

    void initialize(Player huntedPlayer, int time, int[] scheduledTasks, GameType type);

    GameType getType();

    boolean isCompassEnabled();

    Location getCompassTarget();

    int getTime();
}
