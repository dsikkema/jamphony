import java.util.HashMap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

import cli.*;
import cli.commands.*;

public class Run {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        CommandRegistry commandList = new CommandRegistry(new LibraryCommandModule(), injector);
        
        // run test-command
        int exitCode;
        CommandInterface command;
        String commandName;
        
        
        commandName = "test-command";
        command = commandList.getCommandInstance(commandName);
        
        try {
            exitCode = command.execute();
        } catch (Exception e) {
            exitCode = 1;
        }
        
        if (exitCode == 0) {
            return;
        } else {
            System.err.println("Command '" + commandName + "' failed");
            System.exit(exitCode);
        }
    }
}
