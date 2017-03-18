package testhelper;

import static org.junit.Assert.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.dsikkema.jamphony.jamphony.io.CommandInputDefinition;
import org.dsikkema.jamphony.jamphony.io.InputData;
import org.dsikkema.jamphony.jamphony.io.InputException;
import org.dsikkema.jamphony.jamphony.io.Type;

public class CommandInputTestData {
	public List<Map.Entry<String, Object>> arguments = new ArrayList<>();
	public HashMap<String, String> stringOptions = new HashMap<>();
	public HashMap<String, Integer> intOptions = new HashMap<>();
	public HashMap<String, Type> optionsNotGiven = new HashMap<>();
	public HashSet<String> flags = new HashSet<>();
	public HashSet<String> flagsNotGiven = new HashSet<>();
	
	public CommandInputTestData addStringArgument(String name, String val) {
		this.arguments.add(new AbstractMap.SimpleEntry(name, val));
		return this;
	}
	
	public CommandInputTestData addIntArgument(String name, int val) {
		this.arguments.add(new AbstractMap.SimpleEntry(name, val));
		return this;
	}
	
	public CommandInputTestData addStringOption(String name, String val) {
		this.stringOptions.put(name, val);
		return this;
	}
	
	public CommandInputTestData addIntOption(String name, int val) {
		this.intOptions.put(name, Integer.valueOf(val));
		return this;
	}
	
	public CommandInputTestData addEmptyIntOption(String name) {
		this.optionsNotGiven.put(name, Type.INT);
		return this;
	}
	
	public CommandInputTestData addEmptyStringOption(String name) {
		this.optionsNotGiven.put(name, Type.STRING);
		return this;
	}
	
	public CommandInputTestData addFlag(String flag) {
		this.flags.add(flag);
		return this;
	}
	
	public CommandInputTestData addEmptyFlag(String flag) {
		this.flagsNotGiven.add(flag);
		return this;
	}
	
	public CommandInputDefinition createInputDefinition() {
		CommandInputDefinition inputDefinition = new CommandInputDefinition();
		
		// add args
		for (Map.Entry<String, Object> entry : this.arguments ) {
			inputDefinition.addArgument(entry.getKey(), entry.getValue().getClass() == Integer.class ? Type.INT : Type.STRING);
		}
		
		// add options
		for (Map.Entry<String, String> entry : this.stringOptions.entrySet() ) {
			inputDefinition.addOption(entry.getKey(), Type.STRING);
		}
		
		for (Map.Entry<String, Integer> entry : this.intOptions.entrySet() ) {
			inputDefinition.addOption(entry.getKey(), Type.INT);
		}
		
		for (Map.Entry<String, Type> entry : this.optionsNotGiven.entrySet()) {
			inputDefinition.addOption(entry.getKey(), entry.getValue());
		}
		
		// add flags
		for (String flag : this.flags) {
			inputDefinition.addFlag(flag);
		}
		
		for (String flag : this.flagsNotGiven) {
			inputDefinition.addFlag(flag);
		}
		
		return inputDefinition;
	}
	
	public void verifyProcessedInputData(InputData inputData) {
		// verify args
		for (Map.Entry<String, Object> entry : this.arguments) {
			if (entry.getValue().getClass() == Integer.class) {
				assertEquals(
					String.format("Integer argument '%s' has the wrong value", entry.getKey()),
					entry.getValue(),
					inputData.getArgument(entry.getKey()).getIntValue()
				);
			} else {
				assertEquals(
					String.format("String argument '%s' has the wrong value", entry.getKey()),
					entry.getValue(),
					inputData.getArgument(entry.getKey()).getStringValue()
				);
			}
		}
		
		assertEquals(
			"The wrong number of arguments was processed",
			this.arguments.size(),
			inputData.getArguments().size()
		);
		
		// verify options
		for (Map.Entry<String, String> entry : this.stringOptions.entrySet()) {
			assertEquals(entry.getValue(), inputData.getOption(entry.getKey()).getStringValue());
		}
		
		for (Map.Entry<String, Integer> entry : this.intOptions.entrySet()) {
			assertEquals(entry.getValue(), (Integer)inputData.getOption(entry.getKey()).getIntValue());
		}
		
		assertEquals(
			"The wrong number of options was processed",
			this.intOptions.size() + this.stringOptions.size(),
			inputData.getOptions().size()
		);
		
		for (String option : this.optionsNotGiven.keySet()) {
			assertFalse(inputData.isOptionProvided(option));
		}
		
		// verify flags
		for (String flag : this.flags) {
			assertTrue(inputData.isFlagSet(flag));
		}
		
		assertEquals(
			"The wrong number of flags was processed",
			this.flags.size(),
			inputData.getFlags().size()
		);

		for (String flag : this.flagsNotGiven) {
			assertFalse(inputData.isFlagSet(flag));
		}
	}
}
