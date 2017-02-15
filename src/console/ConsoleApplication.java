package console;

import com.google.inject.Inject;

import console.io.CommandInput;
import console.io.Output;
import console.io.UserConsoleInput;

public class ConsoleApplication {
    
    private final CommandRegistry commandRegistry;
    private final UserConsoleInput userInput;
    private final CommandInput commandInput;
    private final Output consoleOutput;

    /**
     * constructor deals with dependency initialization, including module map
     */
    @Inject
    public ConsoleApplication(
            CommandRegistry commandRegistry,
            UserConsoleInput input,
            CommandInput commandInput,
            Output consoleOutput
    ) {
        this.commandRegistry = commandRegistry;
        this.userInput = input;
        this.commandInput = commandInput;
        this.consoleOutput = consoleOutput;
    }
    
    /**
     * run will prepare input/output (stateful, must be singletons), execute the command,
     * and return an exit code
     */
    public int run(String[] args) {
        // run test-command
        int exitCode;
        CommandInterface command;
        
        /**
         * TODO: more temporal coupling here... must call initialize before creating command
         */
        this.userInput.initialize(args);
        String commandName = userInput.getCommandName();
        
        // get command
        command = this.commandRegistry.getCommandInstance(commandName);

        /**
         * validate command input
         * 
         * CommandInput is singleton. When command is constructed it should inform
         * CommonInput in constructor of all its arguments or else "forever hold its 
         * peace". After command instantiation, assume CommandInput is populated with
         * info necessary to validate.
         * 
         *  TODO: resolve temporal coupling: this must be called after command is instantiated.
         *  Is that actually a problem, and how to resolve?
         */
        if (!this.commandInput.validate()) {
            throw new RuntimeException("Invalid input");
        }
        
        // execute command
        try {
            exitCode = command.execute();
        } catch (Exception e) {
            exitCode = 1;
        }
        
        if (exitCode != 0) {
            consoleOutput.writeLnErr("Command '" + commandName + "' failed");
        }
        
        return exitCode;
    }
}
