import java.util.HashMap;

import console.CommandInterface;
import console.CommandModuleInterface;

public class AppCommandModule implements CommandModuleInterface {

    /**
     * Here, return a map from command names to command class instances
     */
    @Override
    public HashMap<String, Class<? extends CommandInterface>> getCommandMap() {
        HashMap<String, Class<? extends CommandInterface>> commandMap = new HashMap<>();
        
        /**
         * populate commands
         */
        commandMap.put("test-command", TestCommand.class);
        
        return commandMap;
    }
}
