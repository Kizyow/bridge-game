package fr.watch54.bridge.listeners;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.utils.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TntFlyGame implements Listener {

    private Bridge bridge;

    public TntFlyGame(Bridge bridge){
        this.bridge = bridge;

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Block block = event.getClickedBlock();

        if(itemStack != null && itemStack.getType() == Material.FLINT_AND_STEEL){

            if(event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && block.getType() == Material.TNT){

                event.setCancelled(true);
                this.spawnTnt(block);

            }

        }

        if(event.getAction() == Action.RIGHT_CLICK_AIR && itemStack != null && itemStack.getType() == Material.SNOW_BALL) this.snowFly(player);

    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event){

        for(Block block : event.blockList()) if(block.getType() == Material.TNT) this.spawnTnt(block);
        event.setCancelled(true);

    }

    private void spawnTnt(Block block){

        block.setType(Material.AIR);

        Location location = block.getLocation().add(0.5, 0.25, 0.5);
        TNTPrimed tntPrimed = (TNTPrimed) block.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        tntPrimed.setCustomName("§cTNTFly §8(☠)");
        tntPrimed.setCustomNameVisible(true);
        tntPrimed.setVelocity(new Vector(0, 0.25, 0));
        tntPrimed.teleport(location);

    }

    private void snowFly(Player player){

        Location location = player.getLocation();
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setHelmet(new ItemStack(Material.SNOW_BLOCK));
        armorStand.setPassenger(player);
        armorStand.setVelocity(location.getDirection().multiply(new Vector(3.0, 3.0, 3.0)));

        new BukkitRunnable(){

            @Override
            public void run(){

                Bukkit.getOnlinePlayers().forEach(players -> ParticleEffect.CLOUD.display(0.5F, 0.5F, 0.5F, 0.0F, 10, armorStand.getLocation(), players));

                if(armorStand.getPassenger() == null || armorStand.isOnGround()){

                    Bukkit.getOnlinePlayers().forEach(players -> ParticleEffect.EXPLOSION_HUGE.display(0.0F, 0.0F, 0.0F, 0.0F, 1, armorStand.getLocation(), players));
                    armorStand.remove();
                    cancel();

                }

            }

        }.runTaskTimer(bridge, 0, 1);

    }

}
