package org.dsikkema.jamphony.jamphony.io;

/**
 * "Entry" means an argument, option, or flag. This serves as a generic way to hold entries and their values, and
 * encapsulate retrieving them by type
 */
public class EntryData {
	
	private final EntryDefinition definition;
	private final Object val;

	public EntryData(EntryDefinition definition, String val) throws InputException {
		switch (definition.getType()) { 
			case INT:
				if (!this.isInt(val)) {
					throw new InputException(String.format(
		    			"Entry '%s' with value '%s' does not match expected type '%s'",
		    			definition.getName(),
		    			val,
		    			definition.getType().getName()
					));
				}
				this.val = Integer.valueOf(val);
				break;
			case STRING:
				this.val = val;
				break;
				
			default:
				throw new RuntimeException("Unhandled argument type"); // just to make this compile. Should never be hit
		}
		
		this.definition = definition;
	}
	
	public String getStringValue() {
		if (this.definition.getType() != Type.STRING) {
			throw new RuntimeException("Argument '" + definition.getName() + "' is not of type " + Type.STRING.getName());
		}

		return (String)this.val;
	}
	
	public int getIntValue() {
		if (this.definition.getType() != Type.INT) {
			throw new RuntimeException("Argument '" + definition.getName() + "' is not of type " + Type.INT.getName());
		}

		return (int)this.val;
	}
	
    private boolean isInt(String in) {
        try {
            Integer.parseInt(in);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
