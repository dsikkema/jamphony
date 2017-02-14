package console.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashSet;

/**
 * Represents what the user provided as console input
 */
@Singleton
public class UserConsoleInput {
    
    /**
     * raw whitespace-separated strings given to CLI
     */
    private final List<String> rawArgs;
    
    private final List<String> arguments;
    private final Map<String, String> options;
    private final Set<String> flags;
    
    private String commandName;
    
    @Inject
    public UserConsoleInput() {
        this.rawArgs = new ArrayList<>();
        this.arguments = new ArrayList<>();
        this.options = new HashMap<>();
        this.flags = new HashSet<>();
    }

    /**
     * This method is taken in by constructors, and guice is not yet configured
     * to inject args from the main method in the constructor
     */
    public void initialize(String[] args) {
        // inject raw args
        for (String argument : args ) {
            this.rawArgs.add(argument);
        }
        
        // process command name, options, args, and flags
        boolean isArgumentAllowed = true;
        int equalSignIndex;
        String entry;
        
        // get command name
        if (this.rawArgs.size() >= 1) {
            this.commandName = this.rawArgs.get(0);
        } else {
            throw new RuntimeException("Command name not given"); 
        }
        
        for (int i = 1; i < this.rawArgs.size(); i++) {
            entry = this.rawArgs.get(i);
            if (this.isArgument(entry)) {
                // Add argument
                
                // check arguments allowed
                if (isArgumentAllowed) {
                    this.arguments.add(entry);
                } else {
                    /**
                     * enforce arguments coming before options and flags to enforce better UX, and 
                     * to catch what is most likely a typo where something that should be an option
                     * is interpreted as argument, e.g. one dash prefix instead of two 
                     */
                    throw new RuntimeException("Argument '" + entry + "' provided after options or flags");
                }
            } else if (this.isEntryOption(entry)) {
                // Add option
                
                isArgumentAllowed = false;
                equalSignIndex = entry.indexOf("=");
                this.options.put(
                    entry.substring(0, equalSignIndex), // option name
                    entry.substring(equalSignIndex + 1) // option value
                );
            } else if (this.isFlag(entry)) {
                // Add flag
                
                this.flags.add(entry);
            }
        }
    }
    
    private boolean isEntryOption(String entry) {
        return entry.substring(0, 2).equals("--")
                && entry.indexOf("=") > 2; // equal sign must be present and option name must not be empty
    }
    
    private boolean isFlag(String entry) {
        return entry.substring(0, 2).equals("--")
                && !entry.contains("=")
                && entry.length() > 2; // don't allow "--" flag
    }
    
    private boolean isArgument(String entry) {
        return !entry.substring(0, 2).equals("--");
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getRawArgs() {
        return rawArgs;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public Set<String> getFlags() {
        return flags;
    }
    
    public String getArgument(int index) {
        return this.arguments.get(index);
    }
    
    public String getOption(String optionName) {
        return this.options.get(optionName);
    }
    
    public boolean isOptionProvided(String optionName) {
        return this.options.containsKey(optionName);
    }
    
    public boolean isFlagSet(String flagName) {
        return this.flags.contains(flagName);
    }
}





















