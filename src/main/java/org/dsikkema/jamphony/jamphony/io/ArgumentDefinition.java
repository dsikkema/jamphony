package org.dsikkema.jamphony.jamphony.io;

public class ArgumentDefinition {
    private String name;
    private int index;
    private Type type;

    public ArgumentDefinition(String name, int index, Type type) {
        this.name = name;
        this.index = index;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public Type getType() {
        return type;
    }
}
