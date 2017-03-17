package org.dsikkema.jamphony.jamphony;

import org.dsikkema.jamphony.jamphony.io.CommandInputDefinition;
import org.dsikkema.jamphony.jamphony.io.CommandInputDefinitionFactory;
import org.dsikkema.jamphony.jamphony.io.InputData;
import org.dsikkema.jamphony.jamphony.io.InputException;
import org.dsikkema.jamphony.jamphony.io.Stdout;
import org.dsikkema.jamphony.jamphony.io.InputData.Factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CommandRunner {
    
    private final CommandRegistry commandRegistry;
	private Factory inputDataFactory;
	private CommandInputDefinitionFactory inputDefinitionFactory;
	private Stdout stdout;

    /**
     * constructor deals with dependency initialization, including module map
     */
    @Inject
    public CommandRunner(
            CommandRegistry commandRegistry,
            InputData.Factory inputDataBuilder,
            CommandInputDefinitionFactory inputDefinitionFactory,
            Stdout output
    ) {
        this.commandRegistry = commandRegistry;
		this.inputDataFactory = inputDataBuilder;
		this.inputDefinitionFactory = inputDefinitionFactory;
		this.stdout = output;
    }
    
    /**
     * Command will prepare the inputdata object it will pass down to the command
     * because input data needs to be validated specifically according to the
     * input definition of the argument which we create in this method.
     * 
     * Output, however, has no special dependency on the command and indeed we
     * may want to share it with whatever program is calling this runner.
     */ 
    public int run(String[] args) {
        int exitCode = 1;
        CommandInterface command;
        String commandName = "";
        CommandInputDefinition inputDefinition;
        InputData inputData;
        
        try {
            commandName = this.getCommandName(args);
            command = this.commandRegistry.getCommandInstance(commandName);
            inputDefinition = this.inputDefinitionFactory.create();
            command.populateInputDefinition(inputDefinition);
            
            /**
             * Note: input validation occurs inside the input data factory
             */
			inputData = this.inputDataFactory.create(inputDefinition, args);
            exitCode = command.execute(inputData);
		} catch (InputException e) {
			/**
			 * InputExceptions are thrown by the console framework itself and
			 * should always have informative messages
			 */
			this.stdout.writeLnErr(e.getMessage());
		}
        catch (Exception e) {
			String message;
			if (!commandName.isEmpty()) {
				message = "Command '" + commandName + "' returned a non-zero exit code";
			} else {
				message = "Command returned a non-zero exit code";
			}
			this.stdout.writeLnErr(message);
			throw e;
		}
        
        return exitCode;
    }
    
    /**
     * Convenience method for passing in a whole string that is not yet
     * split apart by spaces
     */
    public int run(String commandString) {
    	return this.run(this.splitCommandString(commandString));
    }
    
    private String[] splitCommandString(String commandString) {
    	/**
    	 * With deep gratitude to the StackOverflow author who provided 
    	 * this answer, which helped get me to the regexes I needed here
    	 * 
    	 * http://stackoverflow.com/a/23445626/1464099
    	 */
    	String[] argsSplitByWhitespace = commandString.split("(?<!\\\\)\\s+");
    	String[] argsWithEscapersRemoved = new String[argsSplitByWhitespace.length];
    	
    	for (int i = 0; i < argsSplitByWhitespace.length; i++) {
			argsWithEscapersRemoved[i] = argsSplitByWhitespace[i].replaceAll("\\\\(\\s)", "$1");
		}
    	
    	return argsWithEscapersRemoved;
    }
    
    private String getCommandName(String[] args) throws InputException {
    	// check length of args
    	if (args.length < 1 || args[0].isEmpty()) {
    		throw new InputException("No command given");
    	}
    	
    	return args[0];
    }
}
