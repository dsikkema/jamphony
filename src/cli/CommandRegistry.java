package cli;

import java.util.HashMap;

import com.google.inject.Injector;

public class CommandRegistry {
    private final HashMap<String, Class<? extends CommandInterface>> commandMap;
    private final Injector injector;
    
    /**
     * separate logic for retrieving command instances by name from  
     */
    public CommandRegistry(
            CommandModuleInterface commandModule,
            Injector injector
    ) {
        this.injector = injector;
        this.commandMap = commandModule.getCommandMap();
    }
    
    /**
     * TODO instead of new operator, use Guice to inject?
     */
    public CommandInterface getCommandInstance(String commandName) {
        try {
//            return this.commandMap.get(commandName).newInstance();
            return injector.getInstance(this.commandMap.get(commandName));
        } catch (Exception e) {
            String errorMessage = "Something went wrong while trying to instantiate the command class '"
                    + this.commandMap.get(commandName).getName()
                    + "' for command '" + commandName + "'";
            throw new RuntimeException(errorMessage, e);
        }
    }
}
