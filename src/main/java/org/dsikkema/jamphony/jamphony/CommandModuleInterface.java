package org.dsikkema.jamphony.jamphony;

import java.util.HashMap;

/**
 * Client application should implement this method to define whatever commands it offers
 */
public interface CommandModuleInterface {
    public HashMap<String, Class<? extends CommandInterface>> getCommandMap();
}
