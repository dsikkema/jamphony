package org.dsikkema.jamphony.jamphony;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.dsikkema.jamphony.jamphony.io.CommandInputDefinition;
import org.dsikkema.jamphony.jamphony.io.CommandInputDefinitionFactory;
import org.dsikkema.jamphony.jamphony.io.InputData;
import org.dsikkema.jamphony.jamphony.io.Stdout;
import org.dsikkema.jamphony.jamphony.io.InputData.Factory;
import org.dsikkema.jamphony.jamphony.io.InputException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class CommandRunnerTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Mock CommandRegistry commandRegistryMock;
	@Mock Factory inputDataFactoryMock;
	@Mock CommandInputDefinitionFactory inputDefinitionFactoryMock;
	@Mock Stdout stdoutMock;
	@Mock CommandInterface commandMock;
	
	@InjectMocks CommandRunner commandRunner;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * Test that command will get executed and that  as long as it does
	 * not throw exception, its return code will also be returned by 
	 * the command runner.
	 */
	@Test
	@Parameters({"-1", "0", "1", "123"})
	public void testCommandExecution(int exitCode) throws InputException {
		String commandString = "test-command stringArg 123 --option1";
		
		when(this.commandRegistryMock.getCommandInstance("test-command")).thenReturn(this.commandMock);
		when(this.inputDefinitionFactoryMock.create()).thenReturn(Mockito.mock(CommandInputDefinition.class));
		when(this.commandMock.execute(any())).thenReturn(exitCode);
		
		assertEquals(exitCode, this.commandRunner.run(commandString));
	}
	
	/**
	 * verify command does not get called, and exit code is 1 if there
	 * is invalid input
	 */
	@Test
	public void testCatchInputException() throws InputException {
		String commandString = "test-command something is wrong with this input";
		
		when(this.commandRegistryMock.getCommandInstance("test-command")).thenReturn(this.commandMock);
		when(this.inputDefinitionFactoryMock.create()).thenReturn(Mockito.mock(CommandInputDefinition.class));
		when(this.inputDataFactoryMock.create(any(), any())).thenThrow(new InputException("Bad input"));

		assertEquals(1, this.commandRunner.run(commandString));
		
		verify(this.commandMock, never()).execute(any());
		verify(this.stdoutMock, times(1)).writeLnErr("Bad input");
	}
	
	@Test
	public void testRethrowOtherExceptions() throws InputException {
		String commandString = "test-command-throws-exception --option1";
		String expectedExceptionMessage = "This is a test exception message!";
		String expectedErrorMessage = "Command 'test-command-throws-exception' returned a non-zero exit code";
		
		this.expectedException.expect(RuntimeException.class);
		this.expectedException.expectMessage(expectedExceptionMessage);
		
		when(this.commandRegistryMock.getCommandInstance("test-command-throws-exception")).thenReturn(this.commandMock);
		when(this.inputDefinitionFactoryMock.create()).thenReturn(Mockito.mock(CommandInputDefinition.class));
		when(this.commandMock.execute(any())).thenThrow(new RuntimeException(expectedExceptionMessage));

		this.commandRunner.run(commandString);
		verify(this.stdoutMock, times(1)).writeLnErr(expectedErrorMessage);
	}
	
	@Test
	public void testNoCommandGiven() {
		String commandString = "";
		String expectedErrorMessage = "No command given";
		
		assertEquals(1, this.commandRunner.run(commandString));
		verify(this.stdoutMock, times(1)).writeLnErr(expectedErrorMessage);
	}
	
	@Test
	@Parameters(method="splitSpaceDelimitedStringTestData")
	public void testSpaceSplitting(String commandString, String[] expectedSplitArray) throws Exception {
		Method splitCommandStringMethod = CommandRunner.class.getDeclaredMethod("splitCommandString", String.class);
		splitCommandStringMethod.setAccessible(true);
		
		String[] result = (String[])splitCommandStringMethod.invoke(this.commandRunner, commandString);
		assertArrayEquals(expectedSplitArray, result);
	}
	
	public Object[] splitSpaceDelimitedStringTestData() {
		List<Object[]> testCases = new ArrayList<>();
		
		String noArguments = "test-command";
		String[] noArgumentsArray = new String[] {"test-command"};
		testCases.add(new Object[] {noArguments, noArgumentsArray});
		
		String severalArguments = "test-command arg1 arg2 arg3";
		String[] severalArgumentsArray = new String[] {"test-command", "arg1", "arg2", "arg3"};
		testCases.add(new Object[] {severalArguments, severalArgumentsArray});
		
		String severalInputs = "test-command arg1 arg2 --option1=val --flag1";
		String[] severalInputsArray = new String[] {"test-command", "arg1", "arg2", "--option1=val", "--flag1"};
		testCases.add(new Object[] {severalInputs, severalInputsArray});
		
		String escapedSpaces = "test-command arg\\ with\\ \\ several\\ \\ spaces arg2 --option1=\\ \\ val\\ including\\ spaces\\ \\  --flag1";
		String[] escapedSpacesArray = new String[] {"test-command", "arg with  several  spaces", "arg2", "--option1=  val including spaces  ", "--flag1"};
		testCases.add(new Object[] {escapedSpaces, escapedSpacesArray});
		
		return testCases.toArray(new Object[testCases.size()]);
	}
}
