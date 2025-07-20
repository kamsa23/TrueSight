package me.kamsa23.trueSight.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TrueSightCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            HelpCommand.execute(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> ReloadCommand.execute(sender);
            case "toggle" -> ToggleCheckCommand.execute(sender, args);
            case "logs" -> LogViewCommand.execute(sender);
            case "punish" -> PunishCommand.execute(sender, args);
            case "setback" -> SetbackCommand.execute(sender, args);
            case "test" -> TestCheckCommand.execute(sender, args);
            case "info" -> InfoCheckCommand.execute(sender, args);
            case "debug" -> DebugToggleCommand.execute(sender);
            case "help" -> HelpCommand.execute(sender);
            default -> HelpCommand.execute(sender);
        }

        return true;
    }
}