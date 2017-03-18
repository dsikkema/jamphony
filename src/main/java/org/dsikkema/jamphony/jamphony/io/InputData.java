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
    
    private final Map<String, EntryData> argumentValues = new HashMap<>();
    private final Map<String, EntryData> optionValues = new HashMap<>();
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
     * An "entry" is either an argument, option, or flag. For option, it means the whole key-value pair
     */
    private void initialize(String[] entries) throws InputException {
    	if (entries.length == 0) {
    		throw new InputException("Command name not given");
    	}
        
        // process command name, options, args, and flags
        int equalSignIndex;
        String entry;
        int index = 1;
        this.commandName = entries[0];
        
        for ( ; index < this.inputDefinition.getArgumentCount() + 1 && index < entries.length; index++) {
        	entry = entries[index];
            if (!this.isArgument(entry)) {
            	throw new InputException("Too few arguments given");
            }
            this.addArgument(index - 1, entry);
        }
        
        if (index != this.inputDefinition.getArgumentCount() + 1) {
        	// means that there are fewer entries than there are arguments required
        	throw new InputException("Too few arguments given");
        } else if (index < entries.length && this.isArgument(entries[index])) {
        	throw new InputException("Too many arguments given");
        }
        
        
        for ( ; index < entries.length; index++) {
        	entry = entries[index];
        	if (this.isOption(entry)) {
                // Add option
                equalSignIndex = entry.indexOf("=");
                this.addOption(
                    entry.substring(2, equalSignIndex), // option name
                    entry.substring(equalSignIndex + 1) // option value
                );
            } else if (this.isFlag(entry)) {
                // Add flag
                this.addFlag(entry.substring(2));
            } else {
            	// must be an argument
            	throw new InputException("Argument '" + entry + "' is not given at the beginning of the input");
            }
        }
    }
    
    public void addArgument(int index, String value) throws InputException {
    	ArgumentDefinition definition = this.inputDefinition.getArgumentDefinitionByIndex(index);
    	this.argumentValues.put(definition.getName(), new EntryData(definition, value));
    }
    
    public void addOption(String name, String value) throws InputException {
    	OptionDefinition definition = this.inputDefinition.getOptionDefinitionByName(name);
    	this.optionValues.put(name, new EntryData(definition, value));
    }
    
    public void addFlag(String name) throws InputException {
    	if (this.inputDefinition.isFlagDefined(name)) {
    		this.flagsProvided.add(name);
    	} else {
    		throw new InputException("Flag '" + name + "' is not defined");
    	}
    }
    
    private boolean isOption(String entry) {
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

    /**
     * TODO: remove these three methods because the unnecessarily expose internals.
     * They're needed to do state-based unit testing, but we should use reflection
     * in the test to expose the variables
     */
    public Map<String, EntryData> getArguments() {
        return argumentValues;
    }

    public Map<String, EntryData> getOptions() {
        return optionValues;
    }
    
    public Set<String> getFlags() {
        return flagsProvided;
    }
    
    /**
     * Data getters
     */
    public EntryData getOption(String optionName) {
        return this.optionValues.get(optionName);
    }
    
    public boolean isOptionProvided(String optionName) {
        return this.optionValues.containsKey(optionName);
    }
    
    public boolean isFlagSet(String flagName) {
        return this.flagsProvided.contains(flagName);
    }
    
    public EntryData getArgument(String name) {
    	return this.argumentValues.get(name);
    }
    
    public static class Factory {
    	
    	public InputData create(CommandInputDefinition inputDefinition, String[] args) throws InputException
    	{
    		InputData inputData = new InputData(inputDefinition, args);    		
    		return inputData;
    	}
    }
}





















