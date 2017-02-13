package cli;

import java.util.HashMap;

import com.google.inject.Inject;

import service.SomeTestDependency;

public class CommandClass implements CommandInterface {
    
    private SomeTestDependency dependency;

    @Inject
    public CommandClass(SomeTestDependency dependency) {
        this.dependency = dependency;
    }
    
    
    public int execute() {
        System.out.println(this.dependency.getMessage());
        return 0;
    }
}
