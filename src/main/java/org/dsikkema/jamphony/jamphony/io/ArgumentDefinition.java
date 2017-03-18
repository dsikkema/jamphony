package org.dsikkema.jamphony.jamphony.io;

public class ArgumentDefinition  extends EntryDefinition {
    private String name;
    private int index;
    private Type type;

    public ArgumentDefinition(String name, int index, Type type) {
        super(name, type);
    	this.index = index;
    }
    
    public int getIndex() {
    	return this.index;
    }
}
