import com.google.inject.Inject;
import console.CommandInterface;
import console.io.CommandInput;
import console.io.InputDataType;
import console.io.Output;
import service.SomeTestDependency;

public class TestCommand implements CommandInterface {
    
    private final CommandInput commandInput;
    private final SomeTestDependency dependency;
    private final Output consoleOutput;

    @Inject
    public TestCommand(
        CommandInput commandInput,
        Output consoleOutput,
        SomeTestDependency dependency
    ) {
        this.commandInput = commandInput;
        this.consoleOutput = consoleOutput;
        this.dependency = dependency;
        
        // add arguments
        this.commandInput.addArgument("number", InputDataType.INT);
        this.commandInput.addArgument("number2", InputDataType.INT);
    }
    
    @Override
    public int execute() {
        consoleOutput.writeLnOut(this.dependency.getMessage());
        consoleOutput.writeLnOut("Arg1 + 1: " + (this.commandInput.getIntArgument("number") + 1));
        consoleOutput.writeLnOut("Arg2 + 1: " + (this.commandInput.getIntArgument("number2") + 1));
        
        return 0;
    }

    @Override
    public boolean validate() {
        return this.commandInput.validate();
    }
}
