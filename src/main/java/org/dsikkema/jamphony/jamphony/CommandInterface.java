package org.dsikkema.jamphony.jamphony;

import org.dsikkema.jamphony.jamphony.io.CommandInputDefinition;
import org.dsikkema.jamphony.jamphony.io.InputData;

public interface CommandInterface {
    public int execute(InputData inputArgs);
    
    /**
     * Optionally implement this to populate an instance of CommandInputDefinition
     * with the arguments, options, and flags you want your command to receive
     */
    public void populateInputDefinition(CommandInputDefinition inputDefinition);
}
