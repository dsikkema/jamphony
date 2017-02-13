import java.util.HashMap;

import cli.CommandClass;
import cli.CommandInterface;
import cli.CommandModuleInterface;

public class LibraryCommandModule implements CommandModuleInterface {

    /**
     * Here, return a map from command names to command class instances
     */
    @Override
    public HashMap<String, Class<? extends CommandInterface>> getCommandMap() {
        HashMap<String, Class<? extends CommandInterface>> commandMap = new HashMap<>();
        
        /**
         * populate commands
         */
        commandMap.put("test-command", CommandClass.class);
        
        return commandMap;
    }
}
