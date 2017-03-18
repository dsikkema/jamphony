package org.dsikkema.jamphony.jamphony.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the input required by the command, not actual input data itself
 */
public class CommandInputDefinition {
    private final List< ArgumentDefinition> arguments = new ArrayList<>();
	private final Map<String, OptionDefinition> options = new HashMap<>();
    private final List<String> flags = new ArrayList<>();

    private int argumentCount = 0;
    
	public int getArgumentCount() {
		return argumentCount;
	}
	
	public ArgumentDefinition getArgumentDefinitionByIndex(int index) throws InputException {
		if (index >= this.argumentCount || index < 0) {
			throw new InputException("Argument at index " + index + " does not exist");
		}
		return this.arguments.get(index);
	}
	
	public OptionDefinition getOptionDefinitionByName(String optionName) throws InputException {
		if (!this.options.containsKey(optionName)) {
			throw new InputException("Option '" + optionName + "' is not defined");
		}
		
		return this.options.get(optionName);
	}
	
	public boolean isFlagDefined(String name) {
		return this.flags.contains(name);
	}
	
	/**
     * Called by commands to define their input
     */
    public void addArgument(String argumentName, Type type) {
        this.arguments.add(new ArgumentDefinition(argumentName, this.argumentCount, type));
        this.argumentCount++;
    }
    
    public void addOption(String optionName, Type type) {
        this.options.put(optionName, new OptionDefinition(optionName, type));
    }
    
    public void addFlag(String flagName) {
        this.flags.add(flagName);
    }
}





