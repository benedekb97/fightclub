package benedekb.game.fightclub;

import benedekb.main.fightclub.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Game {
    protected int[] scheduledTasks = {};

    protected final Main plugin;

    public Game(Main plugin)
    {
        this.plugin = plugin;
    }

    protected int[] getScheduledTasks()
    {
        return this.scheduledTasks;
    }

    protected void setScheduledTasks(int[] scheduledTasks)
    {
        this.scheduledTasks = scheduledTasks;
    }

    public void addScheduledTask(int taskId)
    {
        int[] newScheduledTasks = new int[this.scheduledTasks.length + 1];

        int i = 0;
        for (int task : this.scheduledTasks) {
            newScheduledTasks[i] = task;
            i++;
        }

        newScheduledTasks[i] = taskId;

        this.scheduledTasks = newScheduledTasks;
    }

    protected void finish()
    {
        for (int taskId : this.scheduledTasks) {
            this.plugin.getServer().getScheduler().cancelTask(taskId);
        }

        for (World world : this.plugin.getServer().getWorlds()) {
            world.getWorldBorder().setCenter(new Location(world, 0.0, 0.0, 0.0));
            world.getWorldBorder().setSize((int)(this.plugin.getServer().getMaxWorldSize() / 2));
        }

        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            player.teleport(player.getWorld().getSpawnLocation());
        }

        this.scheduledTasks = null;
    }
}
