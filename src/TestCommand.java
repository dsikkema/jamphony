

import java.util.HashMap;

import com.google.inject.Inject;

import console.CommandInterface;
import console.input.CommandInput;
import console.input.InputDataType;
import service.SomeTestDependency;

public class TestCommand implements CommandInterface {
    
    private final CommandInput commandInput;
    private final SomeTestDependency dependency;

    @Inject
    public TestCommand(
        CommandInput commandInput,
        SomeTestDependency dependency
    ) {
        this.commandInput = commandInput;
        this.dependency = dependency;
        
        // add arguments
        this.commandInput.addArgument("number", InputDataType.INT);
        this.commandInput.addArgument("number2", InputDataType.INT);
    }
    
    @Override
    public int execute() {
        System.out.println(this.dependency.getMessage());
        System.out.println("Arg1 + 1: " + (this.commandInput.getIntArgument("number") + 1));
        System.out.println("Arg2 + 1: " + (this.commandInput.getIntArgument("number2") + 1));
        
        return 0;
    }

    @Override
    public boolean validate() {
        return this.commandInput.validate();
    }
}
