package fr.watch54.bridge.listeners;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class WorldEventGame implements Listener {

    @EventHandler
    public void onFood(FoodLevelChangeEvent event){
        event.setFoodLevel(20);
        event.setCancelled(true);

    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event){
        event.setCancelled(true);

    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        event.setCancelled(true);

    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event){
        event.setCancelled(true);

    }

    @EventHandler
    public void onBurn(BlockBurnEvent event){
        event.setCancelled(true);

    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent event){
        event.setCancelled(true);

    }

    @EventHandler
    public void onFrameBreak(HangingBreakEvent event){
        event.setCancelled(true);

    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof ItemFrame && event.getDamager() instanceof Player) event.setCancelled(true);

    }

    @EventHandler
    public void onInteractEntity(PlayerInteractAtEntityEvent event){
        event.setCancelled(true);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent e){
        e.setCancelled(true);

    }

}
