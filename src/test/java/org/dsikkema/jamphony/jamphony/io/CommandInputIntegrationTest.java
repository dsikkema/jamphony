package org.dsikkema.jamphony.jamphony.io;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.dsikkema.jamphony.jamphony.io.InputData.Factory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import testhelper.CommandInputTestData;

/**
 * Test real instances of InputData and CommandInputDefinition as they
 * are integrated together.  
 */
@RunWith(JUnitParamsRunner.class)
public class CommandInputIntegrationTest {
	
	/**
	 * Used for testing invalid input
	 */
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private Factory inputDataFactory;
	
	@Before
	public void setUp() {
		this.inputDataFactory = new InputData.Factory();
	}
	
	/**
	 * Test with the new data object
	 */
	@Test
	@Parameters(method = "validCommandInput")
	public void testWithDataObject(String argString, CommandInputTestData testData) throws InputException {
		String[] args = this.explodeByWhiteSpace(argString);
		CommandInputDefinition inputDefinition = testData.createInputDefinition();
		
		// build input data
		InputData inputData = this.inputDataFactory.create(inputDefinition, args);
		
		// perform test assertions based on the mocked data
		testData.verifyProcessedInputData(inputData);
	}
	
	@Test
	@Parameters(method = "invalidCommandInput")
	public void testInvalidInput(String commandString, CommandInputTestData testData, String exceptionMessage) throws InputException {
		// Define expected failure
		this.expectedException.expect(InputException.class);
		this.expectedException.expectMessage(exceptionMessage);
		
		// prepare input
		String[] args = this.explodeByWhiteSpace(commandString);
		CommandInputDefinition inputDefinition = testData.createInputDefinition();
		
		this.inputDataFactory.create(inputDefinition, args);
	}
	
	private Object[] invalidCommandInput() {
		List<Object[]> testCaseList = new ArrayList<>();
		
		/**
		 * One argument defined, none given
		 */
		CommandInputTestData argumentNotGiven = new CommandInputTestData();
		argumentNotGiven.addStringArgument("stringArg", "");
		String argumentNotGivenCommand = "test-command";
		testCaseList.add(new Object[] {argumentNotGivenCommand, argumentNotGiven, "Too few arguments given"});
		
		/**
		 * Two arguments defined, one given
		 */
		CommandInputTestData oneNotTwoArguments = new CommandInputTestData();
		oneNotTwoArguments.addStringArgument("arg1", "given")
			.addStringArgument("arg2", "");
		
		String oneNotTwoArgumentsCommand = "test-command given";
		testCaseList.add(new Object[] {oneNotTwoArgumentsCommand, oneNotTwoArguments, "Too few arguments given"});

		/**
		 * No arguments are defined but one is given
		 */
		CommandInputTestData oneNotZero = new CommandInputTestData();
		
		String oneNotZeroCommand = "test-command undefined-arg";
		testCaseList.add(new Object[] {oneNotZeroCommand, oneNotZero, "Too many arguments given"});
		
		/**
		 * One argument is defined but two are given
		 */
		CommandInputTestData twoNotOne = new CommandInputTestData();
		twoNotOne.addStringArgument("arg1", "given");
		
		String twoNotOneCommand = "test-command given extra-undefined-arg";
		testCaseList.add(new Object[] {twoNotOneCommand, twoNotOne, "Too many arguments given"});
		
		/**
		 * Undefined option given, along with other input
		 */
		CommandInputTestData undefinedOption = new CommandInputTestData();
		undefinedOption.addStringArgument("stringArg", "given")
			.addIntArgument("intArg", 1)
			.addStringOption("stringOption", "given")
			.addIntOption("intOption", 123)
			.addEmptyIntOption("emptyOption")
			.addFlag("givenFlag")
			.addEmptyFlag("emptyFlag");
		
		String undefinedOptionCommand = "test-command given 1 --stringOption=given --intOption=123 --givenFlag --undefinedOption=1";
		testCaseList.add(new Object[] {undefinedOptionCommand, undefinedOption, "Option 'undefinedOption' is not defined"});
		
		/**
		 * Undefined option given, with no other input
		 */
		CommandInputTestData undefinedOptionAlone = new CommandInputTestData();
		
		String undefinedOptionAloneCommand = "test-command  --undefinedOption=1";
		testCaseList.add(new Object[] {undefinedOptionAloneCommand, undefinedOptionAlone, "Option 'undefinedOption' is not defined"});
		
		/**
		 * undefined flag given, along with other input
		 */
		CommandInputTestData undefinedFlag = new CommandInputTestData();
		undefinedFlag.addStringArgument("stringArg", "given")
			.addIntArgument("intArg", 1)
			.addStringOption("stringOption", "given")
			.addIntOption("intOption", 123)
			.addEmptyIntOption("emptyOption")
			.addFlag("givenFlag")
			.addEmptyFlag("emptyFlag");
		
		String undefinedFlagCommand = "test-command given 1 --stringOption=given --intOption=123 --givenFlag --undefinedFlag";
		testCaseList.add(new Object[] {undefinedFlagCommand, undefinedFlag, "Flag 'undefinedFlag' is not defined"});
		
		/**
		 * undefined flag given, with no other input
		 */
		CommandInputTestData undefinedFlagAlone = new CommandInputTestData();
		String undefinedFlagAloneCommand = "test-command --undefinedFlag";
		testCaseList.add(new Object[] {undefinedFlagAloneCommand, undefinedFlagAlone, "Flag 'undefinedFlag' is not defined"});
		
		/**
		 * argument illegally placed after flag
		 */
		CommandInputTestData argumentAfterFlag = new CommandInputTestData();
		argumentAfterFlag.addStringArgument("stringArg", "given")
			.addIntArgument("intArg", 1)
			.addStringOption("stringOption", "given")
			.addIntOption("intOption", 123)
			.addEmptyIntOption("emptyOption")
			.addFlag("givenFlag")
			.addEmptyFlag("emptyFlag");
		
		String argumentAfterFlagCommand = "test-command given 1 --givenFlag illegallyPlacedArg --stringOption=given --intOption=123 ";
		testCaseList.add(new Object[] {argumentAfterFlagCommand, argumentAfterFlag, "Argument 'illegallyPlacedArg' is not given at the beginning of the input"});
		
		/**
		 * argument illegally placed after option
		 */
		CommandInputTestData argumentAfterOption = new CommandInputTestData();
		argumentAfterOption.addStringArgument("stringArg", "given")
			.addIntArgument("intArg", 1)
			.addStringOption("stringOption", "given")
			.addIntOption("intOption", 123)
			.addEmptyIntOption("emptyOption")
			.addFlag("givenFlag")
			.addEmptyFlag("emptyFlag");
		
		String argumentAfterOptionCommand = "test-command given 1 --stringOption=given illegallyPlacedArg --intOption=123 --givenFlag";
		testCaseList.add(new Object[] {argumentAfterOptionCommand, argumentAfterOption, "Argument 'illegallyPlacedArg' is not given at the beginning of the input"});
		
		/**
		 * argument illegally placed between flags and options
		 */
		CommandInputTestData argumentAfterFlagAndOpt = new CommandInputTestData();
		argumentAfterFlagAndOpt.addStringArgument("stringArg", "given")
			.addIntArgument("intArg", 1)
			.addStringOption("stringOption", "given")
			.addIntOption("intOption", 123)
			.addEmptyIntOption("emptyOption")
			.addFlag("givenFlag")
			.addEmptyFlag("emptyFlag");
		
		String argumentAfterFlagAndOptCommand = "test-command given 1 --stringOption=given --givenFlag illegallyPlacedArg --intOption=123 --givenFlag2";
		testCaseList.add(new Object[] {argumentAfterFlagAndOptCommand, argumentAfterFlagAndOpt, "Argument 'illegallyPlacedArg' is not given at the beginning of the input"});
		
		/**
		 * multiple illegally placed arguments
		 */
		CommandInputTestData multipleIllegallyPlacedArgs = new CommandInputTestData();
		multipleIllegallyPlacedArgs.addStringArgument("stringArg", "given")
			.addIntArgument("intArg", 1)
			.addStringOption("stringOption", "given")
			.addIntOption("intOption", 123)
			.addEmptyIntOption("emptyOption")
			.addFlag("givenFlag")
			.addEmptyFlag("emptyFlag");
		
		String multipleIllegallyPlacedArgsCommand = "test-command given 1 --stringOption=given illegallyPlacedArg " 
				+ "--givenFlag illegallyPlacedArg --intOption=123 --givenFlag2 illegallyPlacedArg";
		testCaseList.add(new Object[] {multipleIllegallyPlacedArgsCommand, multipleIllegallyPlacedArgs, "Argument 'illegallyPlacedArg' is not given at the beginning of the input"});
		
		/**
		 * argument has wrong type
		 */
		CommandInputTestData argumentWrongType = new CommandInputTestData();
		argumentWrongType.addIntArgument("intArg", 1);
		
		String argumentWrongTypeCommand = "test-command stringValue";
		testCaseList.add(new Object[] {argumentWrongTypeCommand, argumentWrongType, "Entry 'intArg' with value 'stringValue' does not match expected type 'Int'"});
		
		/**
		 * option has wrong type
		 */
		CommandInputTestData optionWrongType = new CommandInputTestData();
		optionWrongType.addIntOption("intOption", 1);
		
		String optionWrongTypeCommand = "test-command --intOption=stringValue";
		testCaseList.add(new Object[] {optionWrongTypeCommand, optionWrongType, "Entry 'intOption' with value 'stringValue' does not match expected type 'Int'"});
		
		return testCaseList.toArray(new Object[testCaseList.size()][]);
	}
	
	private Object[] validCommandInput() {
		List<Object[]> testCaseList = new ArrayList<>();
		
		/**
		 * Two each of arguments, options, and flags
		 */
		CommandInputTestData twoOfEach = new CommandInputTestData();
		twoOfEach.addIntArgument("intArg", 123)
			.addStringArgument("stringArg", "i_am_a_string")
			.addIntOption("intOption", 456)
			.addStringOption("stringOption", "i_am_another_string")
			.addEmptyStringOption("notPresentOption")
			.addFlag("flag1")
			.addFlag("flag2")
			.addEmptyFlag("flagNotPresent");
		
		String twoOfEachCommand = "test-command 123 i_am_a_string --intOption=456 --stringOption=i_am_another_string --flag1 --flag2";
		testCaseList.add(new Object[] {twoOfEachCommand, twoOfEach});
		
		/**
		 * One each of arguments, options, and flags
		 */
		CommandInputTestData oneOfEach = new CommandInputTestData();
		oneOfEach.addIntArgument("intArg", 123)
			.addIntOption("intOption", 456)
			.addEmptyStringOption("notPresentOption")
			.addFlag("flag1")
			.addEmptyFlag("flagNotPresent");
		
		String oneOfEachCommand = "test-command 123 --intOption=456 --flag1";
		testCaseList.add(new Object[] {oneOfEachCommand, oneOfEach});
		
		/**
		 * had a bug at one point that occurred with argument values of length 1,
		 * so test length=1 edge cases for argument/option/flag names and value
		 */
		CommandInputTestData lengthOneData = new CommandInputTestData();
		lengthOneData.addIntArgument("intArg", 1)
			.addStringArgument("stringArg", "s")
			.addIntOption("i", 1)
			.addStringOption("s", "s")
			.addEmptyStringOption("n")
			.addFlag("f")
			.addFlag("g")
			.addEmptyFlag("h");
		
		String lengthOneCommandString = "test-command 1 s --i=1 --s=s --f --g";
		testCaseList.add(new Object[] {lengthOneCommandString, lengthOneData});
		
		/**
		 * Only options and flags are defined
		 */
		CommandInputTestData onlyOptionsAndFlagsData = new CommandInputTestData();
		onlyOptionsAndFlagsData.addIntOption("intOption", 456)
			.addStringOption("stringOption", "i_am_another_string")
			.addEmptyStringOption("notPresentOption")
			.addFlag("flag1")
			.addFlag("flag2")
			.addEmptyFlag("flagNotPresent");
		
		String onlyOptionsAndFlagsCommand = "test-command --intOption=456 --stringOption=i_am_another_string --flag1 --flag2";
		testCaseList.add(new Object[] {onlyOptionsAndFlagsCommand, onlyOptionsAndFlagsData});
		
		
		/**
		 * Only arguments are defined, only arguments provided
		 */
		CommandInputTestData onlyArgumentsDefined = new CommandInputTestData();
		onlyArgumentsDefined.addIntArgument("intArg", 123)
			.addStringArgument("stringArg", "i_am_a_string");
		
		String onlyArgumentsDefinedCommand = "test-command 123 i_am_a_string";
		testCaseList.add(new Object[] {onlyArgumentsDefinedCommand, onlyArgumentsDefined});
		
		/**
		 * Only arguments provided. but other things are defined
		 */
		CommandInputTestData onlyArgumentsProvided = new CommandInputTestData();
		onlyArgumentsProvided.addIntArgument("intArg", 123)
			.addStringArgument("stringArg", "i_am_a_string")
			.addEmptyFlag("flagNotPresent")
			.addEmptyIntOption("intOption")
			.addEmptyStringOption("stringOption");
		
		String onlyArgumentsProvidedCommand = "test-command 123 i_am_a_string";
		testCaseList.add(new Object[] {onlyArgumentsProvidedCommand, onlyArgumentsProvided});
		
		/**
		 * No input defined
		 */
		CommandInputTestData noInput = new CommandInputTestData();
		String noInputCommand = "test-command";
		testCaseList.add(new Object[] {noInputCommand, noInput});
		
		/**
		 * Optional input is defined but not given
		 */
		CommandInputTestData noInputGiven = new CommandInputTestData();
		noInputGiven.addEmptyFlag("flag1")
			.addEmptyFlag("flag2")
			.addEmptyIntOption("intOpt")
			.addEmptyStringOption("stringOpt");
		
		String noInputGivenCommand = "test-command";
		testCaseList.add(new Object[] {noInputGivenCommand, noInputGiven});
		
		/**
		 *  Integers <= 0 as arguments and options
		 */
		CommandInputTestData negativeInts = new CommandInputTestData();
		negativeInts.addIntArgument("zeroInt", 0)
			.addIntArgument("negativeInt", -1)
			.addIntArgument("negativeInt2", -2)
			.addIntArgument("negativeInt1000", -1000)
			.addIntOption("zeroOpt", 0)
			.addIntOption("negativeOpt", -1)
			.addIntOption("negativeOpt2", -2)
			.addIntOption("negativeOpt1000", -1000);
		
		String negativeIntsCommand = "test-command 0 -1 -2 -1000 --zeroOpt=0 --negativeOpt=-1 --negativeOpt2=-2 --negativeOpt1000=-1000";
		testCaseList.add(new Object[] {negativeIntsCommand, negativeInts});
		
		/**
		 *  Max/min integers as arguments and options
		 */
		CommandInputTestData boundaryInts = new CommandInputTestData();
		boundaryInts.addIntArgument("maxIntArg", Integer.MAX_VALUE)
			.addIntArgument("minIntArg", Integer.MIN_VALUE)
			.addIntOption("maxIntOpt", Integer.MAX_VALUE)
			.addIntOption("minIntOpt", Integer.MIN_VALUE);
		
		
		String boundaryIntsCommand = String.format(
			"test-command %d %d --maxIntOpt=%d --minIntOpt=%d", Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE
		);
		testCaseList.add(new Object[] {boundaryIntsCommand, boundaryInts});
		
		return testCaseList.toArray(new Object[testCaseList.size()][]);		
	}
	
	@Test
	public void testValuesWithSpaces() throws InputException {
		String arg1 = "    argument with    spaces ";
		String arg2 = "argument_no_spaces";
		String arg3 = " another   argument with spaces ";
		String arg4 = "more     spaces";
		String opt1 = " an option    with spaces   ";
		String opt2 = "option_no_spaces";
		String opt3 = "another option   with spaces";
		
		String[] args = new String[] {"test-command", arg1, arg2, arg3, arg4, "--opt1=" + opt1, "--opt2=" + opt2, "--opt3=" + opt3};
		
		CommandInputTestData testData = new CommandInputTestData();
		testData.addStringArgument("arg1", arg1)
			.addStringArgument("arg2", arg2)
			.addStringArgument("arg3", arg3)
			.addStringArgument("arg4", arg4)
			.addStringOption("opt1", opt1)
			.addStringOption("opt2", opt2)
			.addStringOption("opt3", opt3);
		
		CommandInputDefinition inputDefinition = testData.createInputDefinition();
		
		// build input data
		InputData inputData = (new InputData.Factory()).create(inputDefinition, args);
		
		// perform test assertions based on the mocked data
		testData.verifyProcessedInputData(inputData);
	}
	
    private String[] explodeByWhiteSpace(String s) {
        return s.split("\\s+");
    }
}
