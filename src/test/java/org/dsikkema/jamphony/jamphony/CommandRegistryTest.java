package org.dsikkema.jamphony.jamphony;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.inject.Injector;

import org.dsikkema.jamphony.jamphony.io.InputException;

@RunWith(MockitoJUnitRunner.class)
public class CommandRegistryTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Mock CommandModuleInterface commandModule;
	@Mock Injector injector;
	@Mock CommandInterface commandMock;
	@InjectMocks CommandRegistry commandRegistry;
	
	String commandName = "test-command";

	private HashMap<String, Class<? extends CommandInterface>> testCommandMap;
	
	@Before
	public void setUp() {
		this.testCommandMap = new HashMap<>();
		this.testCommandMap.put(this.commandName, this.commandMock.getClass());
		when(this.commandModule.getCommandMap()).thenReturn(this.testCommandMap);
	}
	
	@Test
	public void testNonExistingCommand() throws InputException {
		String commandName = "non-existing-command";
		
		// expectations
		expectedException.expect(InputException.class);
		expectedException.expectMessage(String.format("Command '%s' not found", commandName));
		verify(this.injector, never()).getInstance(any(Class.class));
		
		this.commandRegistry.getCommandInstance(commandName);
	}
	
	@Test
	public void testErrorCreatingCommand() throws InputException {
		expectedException.expect(RuntimeException.class);
		expectedException.expectMessage(String.format("Could not load command '%s'", this.commandName));
		when(this.injector.getInstance(this.commandMock.getClass())).thenThrow(Exception.class);
		
		this.commandRegistry.getCommandInstance(this.commandName);
	}

	@Test
	public void testSuccessfullyCreateCommand() throws InputException {
		when(this.injector.getInstance(any(Class.class))).thenReturn(this.commandMock);
		
		assertEquals(
			this.commandMock,
			this.commandRegistry.getCommandInstance(this.commandName)
		);
		
		verify(this.injector, times(1)).getInstance(this.commandMock.getClass());
	}
}
