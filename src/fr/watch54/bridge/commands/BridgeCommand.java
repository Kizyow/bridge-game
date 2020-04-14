package fr.watch54.bridge.commands;

import fr.watch54.bridge.Bridge;
import fr.watch54.bridge.state.GameState;
import fr.watch54.bridge.tasks.StartRunnable;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BridgeCommand implements CommandExecutor {

    private Bridge bridge;

    public BridgeCommand(Bridge bridge){
        this.bridge = bridge;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        if(sender instanceof Player){

            Player player = (Player) sender;

            if (args.length < 1){
                sender.sendMessage("§6/bridge forcestart §eForcer le démarrage d'une partie");
                return false;

            }

            if (args[0].equalsIgnoreCase("forcestart") && GameState.isState(GameState.WAITING)){

                StartRunnable task = new StartRunnable(bridge, true);
                task.runTaskTimer(bridge, 0, 20);
                GameState.setState(GameState.STARTING);

            } else {

                player.sendMessage("§cVous n'avez pas la permission nécessaire");

            }

        }

        return false;

    }

}
