package org.dsikkema.jamphony.jamphony.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents the values the user provided as console arguments, serves
 * as source of data for commands.
 * 
 * Object is immutable, and can only be created with builder.
 */
public class InputData {
    
    /**
     * raw whitespace-separated strings given to CLI
     */
    private final List<String> rawArgs = new ArrayList<>();
    
    private final List<String> argumentValues = new ArrayList<>();
    private final Map<String, String> optionValues = new HashMap<>();
    private final Set<String> flagsProvided = new HashSet<>();
    
    private String commandName = "";

	private CommandInputDefinition inputDefinition;
    
    private InputData(
		CommandInputDefinition inputDefinition,
		String[] args
	) throws InputException {
    	this.inputDefinition = inputDefinition;
    	this.initialize(args);
    }

    /**
     * This method is taken in by constructors, and guice is not yet configured
     * to inject args from the main method in the constructor
     * @throws InputException 
     */
    private void initialize(String[] args) throws InputException {
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
        }
        
        for (int i = 1; i < this.rawArgs.size(); i++) {
            entry = this.rawArgs.get(i);
            if (this.isArgument(entry)) {
                // Add argument
                
                // check arguments allowed
                if (isArgumentAllowed) {
                    this.argumentValues.add(entry);
                } else {
                    /**
                     * enforce arguments coming before options and flags to enforce better UX, and 
                     * to catch what is most likely a typo where something that should be an option
                     * is interpreted as argument, e.g. one dash prefix instead of two 
                     */
                    throw new InputException("Argument '" + entry + "' provided after options or flags");
                }
            } else if (this.isEntryOption(entry)) {
                // Add option
                
                isArgumentAllowed = false;
                equalSignIndex = entry.indexOf("=");
                this.optionValues.put(
                    entry.substring(2, equalSignIndex), // option name
                    entry.substring(equalSignIndex + 1) // option value
                );
            } else if (this.isFlag(entry)) {
                // Add flag
                isArgumentAllowed = false;
                this.flagsProvided.add(entry.substring(2));
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
    	boolean isArgument = true;
    	if (entry.length() >= 2) {
    		isArgument = !entry.substring(0, 2).equals("--");
    	} else {
    		isArgument = !entry.isEmpty() && !entry.equals("-");
    	}
        return isArgument;
    }

    public String getCommandName() {
        return commandName;
    }

    public List<String> getRawArgs() {
        return rawArgs;
    }

    public List<String> getArguments() {
        return argumentValues;
    }

    public Map<String, String> getOptions() {
        return optionValues;
    }
    
    public Set<String> getFlags() {
        return flagsProvided;
    }
    
    public String getOption(String optionName) {
        return this.optionValues.get(optionName);
    }
    
    public boolean isOptionProvided(String optionName) {
        return this.optionValues.containsKey(optionName);
    }
    
    public boolean isFlagSet(String flagName) {
        return this.flagsProvided.contains(flagName);
    }
    
    private String getArgumentByIndex(int index) {
    	return this.argumentValues.get(index);
    }
    
    /**
     * The following are convenience getters that enforce data types.
     * 
     * Probably the better option would be to use objects that represent
     * the supplied values, then for the command itself to get a value it
     * would first get the argument object from this class, and call 
     * argumentObject.getStringValue(), etc, and have the object do type
     * validation and throw needed exceptions. This would be similar to
     * how you read json documents: jsonObject.getStringValue(), .getIntValue(),
     * etc. Will leave this for later.
     */
    public int getIntArgument(String argumentName) {
		ArgumentDefinition argument;
		argument = this.inputDefinition.getArgumentDefinition(argumentName);
		if (argument.getType() != Type.INT) {
			throw new RuntimeException("Argument '" + argumentName + "' is not of type INT");
		}
		
		return Integer.parseInt(this.getArgumentByIndex(argument.getIndex()));
    }
    
    public String getStringArgument(String argumentName) {
		ArgumentDefinition argument;
		argument = this.inputDefinition.getArgumentDefinition(argumentName);
		if (argument.getType() != Type.STRING) {
			throw new RuntimeException("Argument '" + argumentName + "' is not of type STRING");
		}
		
		return this.getArgumentByIndex(argument.getIndex());
    }
    
    public int getIntOption(String optionName) {
		OptionDefinition option;
		option = this.inputDefinition.getOptionDefinition(optionName);
		if (option.getType() != Type.INT) {
			throw new RuntimeException("Option '" + optionName + "' is not of type INT");
		}
		
		return Integer.parseInt(this.getOption(optionName));
    }
    
    public String getStringOption(String optionName) {
		OptionDefinition option;
		option = this.inputDefinition.getOptionDefinition(optionName);
		if (option.getType() != Type.STRING) {
			throw new RuntimeException("Option '" + optionName + "' is not of type STRING");
		}
		
		return this.getOption(optionName);
    }
    
    public String getRawArgumentByIndex(int index) {
    	if (this.argumentValues.size() <= index || index < 0) {
    		throw new RuntimeException("There is no argument at index '" + index + "'");
    	}
    	return this.argumentValues.get(index);
    }
    
    private boolean validate() {
    	// TODO: implement
    	return true;
    }
    
    public static class Factory {
    	
    	public InputData create(CommandInputDefinition inputDefinition, String[] args) throws InputException
    	{
    		InputData inputData = new InputData(inputDefinition, args);
			inputDefinition.validate(inputData);
    		
    		return inputData;
    	}
    }
}





















