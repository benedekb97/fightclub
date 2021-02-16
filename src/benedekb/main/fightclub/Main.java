package benedekb.main.fightclub;

import benedekb.command.fightclub.CreateHuntGame;
import benedekb.command.fightclub.CreateTagGame;
import benedekb.game.fightclub.GameInterface;
import benedekb.listener.fightclub.GenericListener;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
    private boolean gameInProgress = false;

    private int defaultTime = 60;

    private GameInterface game;

    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new GenericListener(this), this);

        for (World world : this.getServer().getWorlds()) {
            world.getWorldBorder().setDamageAmount(0.0);
        }
    }

    @Override
    public void onDisable()
    {

    }

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        if (
            command.getName().equalsIgnoreCase("fightclub") ||
            command.getName().equalsIgnoreCase("fc")
        ) {
            if (args.length == 0) {
                return this.showVersion(sender);
            }

            if (args[0].equalsIgnoreCase("game")) {
                if (args.length < 2) {
                    return false;
                }

                if (args[1].equalsIgnoreCase("tag")) {
                    return this.createTagGame(sender);
                }

                if (args[1].equalsIgnoreCase("hunt")) {
                    return this.createHuntGame(sender);
                }

                if (args[1].equalsIgnoreCase("end")) {
                    if (this.game != null) {
                        this.endGame(null);

                        return true;
                    }

                    return false;
                }

                return false;
            }

            return this.setSetting(sender, args);
        }

        return this.showVersion(sender);
    }

    private boolean createHuntGame(CommandSender sender)
    {
        if (this.gameInProgress) {
            sender.sendMessage("Cannot start new game while game is in progress! (/fc end)");
            return false;
        }

        CreateHuntGame createHuntGame = new CreateHuntGame(sender.getServer(), this);

        this.gameInProgress = true;

        this.game = createHuntGame.run();

        return this.game != null;
    }

    private boolean createTagGame(CommandSender sender)
    {
        if (this.gameInProgress) {
            sender.sendMessage("Cannot start new game while game is in progress! (/fc end)");
            return false;
        }

        CreateTagGame createTagGame = new CreateTagGame(sender.getServer(), this);

        this.gameInProgress = true;
        this.game = createTagGame.run();

        return this.game != null;
    }

    private boolean setSetting(CommandSender sender, String[] args)
    {
        if (args[0].equalsIgnoreCase("time")) {
            if (this.game != null) {
                sender.sendMessage("Cannot change timeout while ingame!");

                return false;
            }

            int timeout = Integer.parseInt(args[1]);

            if (timeout > 0) {
                this.defaultTime = timeout;
                sender.sendMessage("Delay set to " + args[1] + " seconds!");
            } else {
                sender.sendMessage("Timeout cannot be less than 0, te kuki!");
            }
        }

        return true;
    }

    private boolean showVersion(CommandSender sender)
    {
        sender.sendMessage("FightClub version 1.1");

        return true;
    }

    public boolean isGameInProgress()
    {
        return this.gameInProgress;
    }

    public void endGame(Player winner)
    {
        this.game.end(winner);
        this.game = null;
        this.gameInProgress = false;
    }

    public int getDefaultTime()
    {
        return this.defaultTime;
    }

    public GameInterface getGame()
    {
        return this.game;
    }
}
