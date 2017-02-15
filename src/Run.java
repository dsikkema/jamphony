import com.google.inject.Guice;
import com.google.inject.Injector;

import console.ConsoleApplication;

public class Run {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppGuiceModule());
        ConsoleApplication app = injector.getInstance(ConsoleApplication.class);
        app.run(args);
    }
}
