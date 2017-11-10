package pl.kamcio96.disableplayerdata;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class SavePlayerEvent extends PlayerEvent implements Cancellable {

    private static HandlerList handlerList = new HandlerList();

    private boolean cancelled;

    public SavePlayerEvent(Player who) {
        super(who);
        this.cancelled = true;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
