package console.input;

import java.util.*;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Represents the input required by the command and serves as a gateway
 * to accessing validated input.
 */
@Singleton
public class CommandInput {
    private UserConsoleInput userInput;
    
    private final Map<String, Argument> arguments;
    private final Map<String, Option> options;
    private final List<String> flags;

    private int argumentCount = 0;
    
    @Inject
    public CommandInput(UserConsoleInput userInput) {
        this.userInput = userInput;
        this.arguments = new HashMap<>();
        this.options = new HashMap<>();
        this.flags = new ArrayList<>();
    }
    
    /**
     * return whether all arguments are present, and no uncalled-for arguments/options are present
     */
    public boolean validate() {
        
        // validate arguments present
        if (this.argumentCount != this.userInput.getArguments().size()) {
            return false;
        }
        
        // validate argument types
        for (Argument argument : this.arguments.values()) {
            String givenArgumentValue = this.userInput.getArgument(argument.getIndex());
            if (!this.validateInputByType(givenArgumentValue, argument.getType())) {
                return false;
            }
        }
        
        // validate no extra options
        for (String optionName : this.userInput.getOptions().keySet()) {
            if (!this.options.containsKey(optionName)) {
                return false;
            }
        }
        
        // validate option types
        for (Option option : this.options.values()) {
            String optionName = option.getName();
            String givenOptionValue = this.userInput.getOption(optionName);
            if (!this.validateInputByType(givenOptionValue, option.getType())) {
                return false;
            }
        }
        
        // validate no extra flags
        for (String flag : this.userInput.getFlags()) {
            if (!this.flags.contains(flag)) {
                return false;
            }
        }
        
        // flags are just present or not present, no type validation required
        
        return true;
    }
    
    public void addArgument(String argumentName, InputDataType type) {
        this.arguments.put(argumentName, new Argument(argumentName, this.argumentCount, type));
        this.argumentCount++;
    }
    
    public void addOption(String optionName, InputDataType type) {
        this.options.put(optionName, new Option(optionName, type));
    }
    
    public void addFlag(String flagName) {
        this.flags.add(flagName);
    }
    
    /**
     * for each convenience method below:
     *  - check that the command defines that argument/option/flag
     *  - check that command requires type asked for
     *  - get it from user input
     *  - convert to desired type
     */
    
    public int getIntArgument(String argumentName) {
        Argument argument;
        
        if (!this.arguments.containsKey(argumentName)) {
            throw new RuntimeException("Cannot get undefined argument '" + argumentName + "'");
        }
        
        argument = this.arguments.get(argumentName);
        
        if (argument.getType() != InputDataType.INT) {
            throw new RuntimeException("Cannot get argument '" + argumentName + "' as an integer");
        }
        
        return Integer.parseInt(this.getRawStringArgument(argument));
    }
    
    public String getStringArgument(String argumentName) {
        Argument argument;
        
        if (!this.arguments.containsKey(argumentName)) {
            throw new RuntimeException("Cannot get undefined argument '" + argumentName + "'");
        }
        
        argument = this.arguments.get(argumentName);
        
        return this.getRawStringArgument(argument);
    }
    
    public int getIntOption(String optionName) {
        Option option;
        
        if (!this.options.containsKey(optionName)) {
            throw new RuntimeException("Cannot get undefined argument '" + optionName + "'");
        }
        
        option = this.options.get(optionName);
        
        if (option.getType() != InputDataType.INT) {
            throw new RuntimeException("Cannot get option '" + optionName + "' as an integer");
        }
        
        return Integer.parseInt(this.getRawStringOption(option));
    }
    
    public String getStringOption(String optionName) {
        Option option;
        
        if (!this.options.containsKey(optionName)) {
            throw new RuntimeException("Cannot get undefined argument '" + optionName + "'");
        }
        
        option = this.options.get(optionName);
        
        return this.getRawStringOption(option);
    }
    
    public boolean isOptionGiven(String optionName) {
        return this.userInput.isOptionProvided(optionName);
    }
    
    public boolean isFlagSet(String flagName) {
        return this.userInput.isFlagSet(flagName);
    }
    
    /**
     * TODO: stop abusing exception handling. Use a regex
     */
    private boolean isInt(String in) {
        try {
            Integer.parseInt(in);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    private boolean validateInputByType(String input, InputDataType type) {
        return type != InputDataType.INT || this.isInt(input);
    }
    
    /**
     * get argument without validation
     */
    private String getRawStringArgument(Argument argument) {
        return this.userInput.getArgument(argument.getIndex());
    }
    
    /**
     * get option without validation
     */
    private String getRawStringOption(Option option) {
        return this.userInput.getOption(option.getName());
    }
}
