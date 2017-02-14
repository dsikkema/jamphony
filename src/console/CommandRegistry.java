package console;

import java.util.HashMap;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Uses the instance of CommandModuleInterface implemented and provided by 
 */
public class CommandRegistry {
    private final HashMap<String, Class<? extends CommandInterface>> commandMap;
    private final Injector injector;
    
    /**
     * separate logic for retrieving command instances by name from  
     */
    @Inject
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
        
        // TODO: check contains
        try {
//            return this.commandMap.get(commandName).newInstance();
            Class<? extends CommandInterface> classClass = this.commandMap.get(commandName);
            return injector.getInstance(classClass);
        } catch (Exception e) {
            String errorMessage = "Could not found or could not build command class for command '" + commandName + "'";
            throw new RuntimeException(errorMessage, e);
        }
    }
}
