package console;

import java.util.HashMap;

/**
 * Client application should implement this method to return whatever commands it offers
 */
public interface CommandModuleInterface {
    public HashMap<String, Class<? extends CommandInterface>> getCommandMap();
}
