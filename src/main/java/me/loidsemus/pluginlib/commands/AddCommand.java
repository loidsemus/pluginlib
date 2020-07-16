package me.loidsemus.pluginlib.commands;

import me.loidsemus.pluginlib.commands.annotations.Executor;
import me.loidsemus.pluginlib.commands.annotations.SubCommandInfo;
import org.bukkit.entity.Player;

/**
 * Just a test file
 */
@SubCommandInfo(command = "add", description = "Adds stuff")
public class AddCommand extends SubCommand {

    @Executor
    public void execute(Player player, int num1) {
        player.sendMessage(num1 + "");
    }

}
