package me.loidsemus.pluginlib.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Represents the root command, for example /f in Factions.
 */
public class RootCommand implements CommandExecutor {

    private final String alias;

    /**
     * @param alias Only used for displaying. Does not handle the actual command checking, so all aliases defined in plugin.yml will work.
     */
    public RootCommand(String alias) {
        this.alias = alias;
    }

    public void addSubcommand(SubCommand subCommand) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }
}
