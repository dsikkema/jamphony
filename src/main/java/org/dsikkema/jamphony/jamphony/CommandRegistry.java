package org.dsikkema.jamphony.jamphony;

import java.util.HashMap;

import org.dsikkema.jamphony.jamphony.io.InputException;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class CommandRegistry {
    private final CommandModuleInterface commandModule;
    private final Injector injector;
    
    @Inject
    public CommandRegistry(
            CommandModuleInterface commandModule,
            Injector injector
    ) {
        this.injector = injector;
        this.commandModule = commandModule;
    }
    
    /**
     * Uses the instance of CommandModuleInterface implemented and provided by
     * the application to get an actual command class from the command name,
     * and then instantiate it with Guice
     */
    public CommandInterface getCommandInstance(String commandName) throws InputException {
    	HashMap<String, Class<? extends CommandInterface>> commandMap = this.commandModule.getCommandMap();
        if (!commandMap.containsKey(commandName)) {
            throw new InputException("Command '" + commandName + "' not found");
        }
        try {
            Class<? extends CommandInterface> classClass = commandMap.get(commandName);
            return injector.getInstance(classClass);
        } catch (Exception e) {
            String errorMessage = "Could not load command '" + commandName + "'";
            throw new RuntimeException(errorMessage, e);
        }
    }
}
