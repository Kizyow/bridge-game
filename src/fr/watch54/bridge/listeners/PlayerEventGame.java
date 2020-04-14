package fr.watch54.bridge.listeners;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.scoreboard.BridgeScoreboard;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.tasks.EndRunnable;
import fr.watch54.bridge.tasks.StartRunnable;
import fr.watch54.bridge.teams.Team;
import fr.watch54.bridge.utils.ColorUtils;
import fr.watch54.bridge.utils.ParseLocation;
import fr.watch54.bridge.utils.TitleBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PlayerEventGame implements Listener {

    private Bridge bridge;

    public PlayerEventGame(Bridge bridge){
        this.bridge = bridge;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();
        int playerSize = Bukkit.getOnlinePlayers().size();
        int playersMax = bridge.getConfiguration().amountPlayers();

        if(!GameState.isState(GameState.WAITING)){

            event.setJoinMessage(null);

            BridgeScoreboard scoreboard = new BridgeScoreboard(bridge);
            scoreboard.updateWaitingScoreboard(player, 0, 20);

            player.setGameMode(GameMode.SPECTATOR);
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);

            Location location = new ParseLocation(bridge).parseLocation(bridge.getConfiguration().waitingSpawnName(), bridge.getConfiguration().waitingSpawnLocation());
            player.teleport(location);

            return;

        }

        ChatColor chatColor = ChatColor.RED;
        if(playerSize >= playersMax) chatColor = ChatColor.GREEN;
        event.setJoinMessage(player.getName() + ChatColor.GRAY + " a rejoint la partie " + chatColor + "(" + playerSize + "/" + playersMax + ")");

        new TitleBuilder(player).setColor(ChatColor.GOLD)
                .setTitle(ChatColor.BOLD + "BRIDGE")
                .setSubtitle(ChatColor.YELLOW + "En attente de joueurs...")
                .fadeIn(10)
                .stay(60)
                .fadeOut(10)
                .build();

        BridgeScoreboard scoreboard = new BridgeScoreboard(bridge);
        scoreboard.updateWaitingScoreboard(player, 0, 20);

        player.setGameMode(GameMode.ADVENTURE);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        Location location = new ParseLocation(bridge).parseLocation(bridge.getConfiguration().waitingSpawnName(), bridge.getConfiguration().waitingSpawnLocation());
        player.teleport(location);

        this.giveItems(player);

        if(playerSize >= playersMax && GameState.isState(GameState.WAITING)){

            StartRunnable task = new StartRunnable(bridge, false);
            task.runTaskTimer(bridge, 0, 20);
            GameState.setState(GameState.STARTING);

        }

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Block block = event.getClickedBlock();

        if(itemStack != null && itemStack.getType() == Material.INK_SACK){

            ItemMeta itemMeta = itemStack.getItemMeta();

            for(Team team : bridge.getTeamManager().getTeamList()){

                if (itemMeta.getDisplayName().contains(team.getName())){
                    bridge.getTeamManager().addPlayer(player, team);
                    player.closeInventory();
                    for(Player pls : Bukkit.getOnlinePlayers()) this.giveItems(pls);

                    break;

                }

            }

        }

        if(itemStack != null && itemStack.getType() == Material.NAME_TAG){
            player.closeInventory();
            player.sendMessage("§cCette fonctionnalité sera disponible prochainement...");

        }

        if(itemStack != null && itemStack.getType() == Material.BED){
            player.closeInventory();

        }

        if(block != null && block.getType() == Material.BED_BLOCK){
            player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 40, 0, true));

        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){

        Player player = event.getPlayer();
        event.setQuitMessage(null);

        BridgeScoreboard scoreboard = new BridgeScoreboard(bridge);
        scoreboard.remove(player);

        if(!GameState.isState(GameState.PLAYING))
            for(Team team : bridge.getTeamManager().getTeamList())
                if(team.getPlayers().contains(player)) team.removePlayer(player);

        if(bridge.getTeamManager().getTeamMap().containsKey(player) && GameState.isState(GameState.PLAYING)){

            Team playerTeam = bridge.getTeamManager().getTeam(player);

            Bukkit.broadcastMessage(playerTeam.getColor() + player.getName() + ChatColor.GRAY + " est mort en se déconnectant.");

            bridge.getTeamManager().removePlayer(player);

            if(playerTeam.getPlayers().size() < 1){

                bridge.getLoopRunnable().cancel();

                Team winnerTeam;
                if(playerTeam.getName().equalsIgnoreCase("Rouge")) winnerTeam = bridge.getTeamManager().getBlueTeam();
                else winnerTeam = bridge.getTeamManager().getRedTeam();

                GameState.setState(GameState.ENDING);
                Bukkit.getScheduler().cancelTasks(bridge);

                EndRunnable endRunnable = new EndRunnable(bridge, winnerTeam);
                endRunnable.runTaskTimer(bridge, 0, 20);

                for (Player players : Bukkit.getOnlinePlayers()){

                    players.setGameMode(GameMode.SURVIVAL);
                    players.getInventory().clear();
                    players.getInventory().setArmorContents(null);
                    players.setAllowFlight(true);

                    bridge.getBlockManager().removeAllBlock();

                    BridgeScoreboard bridgeScoreboard = new BridgeScoreboard(bridge);
                    bridgeScoreboard.remove(players);
                    bridgeScoreboard.createEndingScoreboard(players, winnerTeam);

                }

            }

        }

    }

    private void giveItems(Player player){

        player.getInventory().clear();

        for(Team team : bridge.getTeamManager().getTeamList()){

            List<String> lores = new ArrayList<>();
            team.getPlayers().forEach(players -> lores.add("§8 - §6" + players.getName()));

            ItemStack bannerTeam = new ItemStack(Material.INK_SACK, 1, (byte) ColorUtils.getbyChatColor(team.getColor()).getDyeData());
            ItemMeta bannerMeta = bannerTeam.getItemMeta();
            bannerMeta.setDisplayName("§7Équipe " + team.getColor() + team.getName());
            bannerMeta.setLore(lores);
            bannerTeam.setItemMeta(bannerMeta);

            player.getInventory().addItem(bannerTeam);

        }

        ItemStack kitTeam = new ItemStack(Material.NAME_TAG);
        ItemMeta kitMeta = kitTeam.getItemMeta();
        kitMeta.setDisplayName("§6Séléction du kit");
        kitTeam.setItemMeta(kitMeta);

        ItemStack hub = new ItemStack(Material.BED);
        ItemMeta hubMeta = hub.getItemMeta();
        hubMeta.setDisplayName("§cQuitter la partie");
        hub.setItemMeta(hubMeta);

        player.getInventory().setItem(7, kitTeam);
        player.getInventory().setItem(8, hub);

    }

}
