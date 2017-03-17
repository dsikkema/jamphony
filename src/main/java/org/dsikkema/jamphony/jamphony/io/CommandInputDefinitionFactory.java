package org.dsikkema.jamphony.jamphony.io;

import com.google.inject.Singleton;

@Singleton
public class CommandInputDefinitionFactory {
	public CommandInputDefinition create() {
		return new CommandInputDefinition();
	}
}





