package cli;

import java.util.HashMap;

public interface CommandModuleInterface {
    
    /**
     * Client application should implement this method to return whatever list of commands it has
     * to offer
     */
    public HashMap<String, Class<? extends CommandInterface>> getCommandMap();
}
