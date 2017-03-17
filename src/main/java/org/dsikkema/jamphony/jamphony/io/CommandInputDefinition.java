package org.dsikkema.jamphony.jamphony.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the input required by the command, not actual input data itself
 */
public class CommandInputDefinition {
    private final Map<String, ArgumentDefinition> arguments = new HashMap<>();
	private final Map<String, OptionDefinition> options = new HashMap<>();
    private final List<String> flags = new ArrayList<>();

    private int argumentCount = 0;
    
    public void addArgument(String argumentName, Type type) {
        this.arguments.put(argumentName, new ArgumentDefinition(argumentName, this.argumentCount, type));
        this.argumentCount++;
    }
    
    public void addOption(String optionName, Type type) {
        this.options.put(optionName, new OptionDefinition(optionName, type));
    }
    
    public void addFlag(String flagName) {
        this.flags.add(flagName);
    }
    
    public Map<String, ArgumentDefinition> getArguments() {
		return arguments;
	}

	public Map<String, OptionDefinition> getOptions() {
		return options;
	}

	public List<String> getFlags() {
		return flags;
	}

	public int getArgumentCount() {
		return argumentCount;
	}
	
	public ArgumentDefinition getArgumentDefinition(String argumentName) {
		if (!this.arguments.containsKey(argumentName)) { 
			throw new RuntimeException("Argument '" + argumentName + "' not defined");
		}
		return this.arguments.get(argumentName);
	}
	
	public OptionDefinition getOptionDefinition(String optionName) {
		if (!this.options.containsKey(optionName)) {
			throw new RuntimeException("Option '" + optionName + "' not defined");
		}
		
		return this.options.get(optionName);
	}
	
	public void validate(InputData inputData) throws InputException {
		// validate that no arguments are missing or extra
		if (inputData.getArguments().size() > this.argumentCount) {
        	throw new InputException("Too many arguments given");
        } else if (inputData.getArguments().size() < this.argumentCount) {
        	throw new InputException("Too few arguments given");
        }
        
        // validate argument types
        /**
         * For each defined argument, get its index and then get the given value
         * at that index. Validate that value against the required type.
         */
        for (ArgumentDefinition argumentDefinition : this.arguments.values()) {
            String givenArgumentValue = inputData.getRawArgumentByIndex(argumentDefinition.getIndex());
            if (!this.validateInputByType(givenArgumentValue, argumentDefinition.getType())) {
            	throw new InputException(String.format(
        			"Argument '%s' with value '%s' does not match expected type '%s'",
        			argumentDefinition.getName(),
        			givenArgumentValue,
        			argumentDefinition.getType().getName()
    			));
            }
        }
        
        // validate no extra options
        /**
         * Options are inherently _optional_, therefore only validate that there
         * are no extra ones
         */
        for (String optionName : inputData.getOptions().keySet()) {
            if (!this.options.containsKey(optionName)) {
            	throw new InputException(String.format("Option '%s' is not defined", optionName));
            }
        }
        
        // validate option types
        /**
         * Validate that all given options have the correct type
         */
        for (OptionDefinition optionDefinition : this.options.values()) {
            String optionName = optionDefinition.getName();
            String givenOptionValue = inputData.getOption(optionName);
            
            // only validate type if option is actually provided
            if (
        		inputData.isOptionProvided(optionName)
        		&& !this.validateInputByType(givenOptionValue, optionDefinition.getType())
    		) {
            	throw new InputException(String.format(
        			"Option '%s' with value '%s' does not match expected type '%s'",
        			optionDefinition.getName(),
        			givenOptionValue,
        			optionDefinition.getType().getName()
    			));
            }
        }
        
        // validate no extra flags
        for (String flag : inputData.getFlags()) {
            if (!this.flags.contains(flag)) {
            	throw new InputException(String.format("Flag '%s' is not defined", flag));
            }
        }
        
        // flags are just present or not present, no type validation required
	}
	
    private boolean validateInputByType(String input, Type type) {
        return type != Type.INT || this.isInt(input);
    }
    
    /**
     * TODO: stop abusing exception handling. Use a regex
     */
    private boolean isInt(String in) {
        try {
            Integer.parseInt(in);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}





