import com.google.inject.AbstractModule;

import console.CommandModuleInterface;

public class AppGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CommandModuleInterface.class).to(AppCommandModule.class);
    }
}