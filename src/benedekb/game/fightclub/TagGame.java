package benedekb.game.fightclub;

import benedekb.main.fightclub.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TagGame extends Game implements GameInterface{
    private Player it;
    private Player previousIt;

    private int time;

    private boolean cannotPassIt = true;

    public TagGame(Main plugin)
    {
        super(plugin);
    }

    @Override
    public void end(Player winner) {
        this.finish();

        String message = this.it.getDisplayName() + " lost the game!";

        this.plugin.getServer().broadcastMessage(message);
    }

    @Override
    public void initialize(Player it, int time, int[] scheduledTasks, GameType type) {
        this.it = it;
        this.time = time;
        this.scheduledTasks = scheduledTasks;
    }

    @Override
    public GameType getType() {
        return GameType.TAG;
    }

    @Override
    public boolean isCompassEnabled() {
        return false;
    }

    @Override
    public Location getCompassTarget() {
        return null;
    }

    public void setIt(Player it)
    {
        this.it = it;
    }

    public Player getIt()
    {
        return this.it;
    }

    public void setPreviousIt(Player previous)
    {
        this.previousIt = previous;
    }

    public Player getPreviousIt()
    {
        return this.previousIt;
    }

    public boolean canItBePassed()
    {
        return !this.cannotPassIt;
    }

    public void setCannotPassIt(boolean cannotPassIt)
    {
        this.cannotPassIt = cannotPassIt;
    }

    public int getTime()
    {
        return this.time;
    }
}
