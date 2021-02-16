package benedekb.listener.fightclub;

import benedekb.game.fightclub.GameType;
import benedekb.game.fightclub.HuntGame;
import benedekb.game.fightclub.TagGame;
import benedekb.main.fightclub.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GenericListener implements Listener {
    private final Main plugin;

    public GenericListener(
        Main plugin
    ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onUseCompassEvent(PlayerInteractEvent event)
    {
        ItemStack item = event.getItem();

        if (this.plugin.getGame() == null) {
            return;
        }

        if (!this.plugin.getGame().getType().equals(GameType.HUNT)) {
            return;
        }

        if (!this.plugin.getGame().isCompassEnabled()) {
            return;
        }

        if (item == null) {
            return;
        }

        if (item.getType() != Material.COMPASS) {
            return;
        }

        if (null == this.plugin.getGame().getCompassTarget()) {
            return;
        }

        event.getPlayer().setCompassTarget(this.plugin.getGame().getCompassTarget());
    }

    /** used only in HuntGame */
    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
    {
        if (!this.plugin.isGameInProgress()) {
            return;
        }

        if (this.plugin.getGame() == null) {
            return;
        }

        if (this.plugin.getGame().getType().equals(GameType.HUNT)) {
            HuntGame game = (HuntGame)this.plugin.getGame();

            this.handleHuntPunch(game, event);
        }

        if (this.plugin.getGame().getType().equals(GameType.TAG)) {
            TagGame game = (TagGame)this.plugin.getGame();

            this.handleTagPunch(game, event);
        }
    }

    private void handleTagPunch(TagGame game, EntityDamageByEntityEvent event)
    {
        if (game == null || game.getIt() == null) {
            return;
        }

        if (
            event.getDamager() instanceof Player &&
            event.getEntity() instanceof Player &&
            event.getDamager().getName().equalsIgnoreCase(game.getIt().getName()) &&
            event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
        ) {
            Player damager = (Player) event.getDamager();
            Player wasDamaged = (Player) event.getEntity();

            if (game.getIt() == null) {
                return;
            }

            if (
                (
                    game.getPreviousIt() != null &&
                    game.getPreviousIt().equals(wasDamaged) &&
                    game.canItBePassed() &&
                    game.getIt().equals(damager)
                ) || (
                    game.getPreviousIt() != null &&
                    !game.getPreviousIt().equals(wasDamaged) &&
                    game.getIt().equals(damager)
                ) || game.getPreviousIt() == null && game.getIt().equals(damager)
            ) {
                game.setPreviousIt(game.getIt());
                game.setIt(wasDamaged);
                game.setCannotPassIt(true);

                this.plugin.getServer().broadcastMessage(wasDamaged.getDisplayName() + " a fogó!");

                int allowAgain = this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                    this.plugin,
                    () -> {
                        game.setCannotPassIt(false);
                        plugin.getServer().broadcastMessage(damager.getDisplayName() + "-t is el lehet már kapni!");
                    },
                    400L
                );

                game.addScheduledTask(allowAgain);
            }
        }
    }

    private void handleHuntPunch(HuntGame game, EntityDamageByEntityEvent event)
    {
        if (game == null || game.getHuntedPlayer() == null) {
            return;
        }

        if (
            event.getDamager() instanceof Player &&
            event.getEntity() instanceof Player &&
            event.getEntity().getName().equalsIgnoreCase(game.getHuntedPlayer().getName()) &&
            event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
        ) {
            Player damager = (Player)event.getDamager();
            Player entity = (Player)event.getEntity();

            entity.setHealth(0.0);
            entity.sendMessage("You dieded!");

            damager.sendMessage("You won!");

            this.plugin.endGame(null);
        }
    }
}
