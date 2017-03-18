package org.dsikkema.jamphony.jamphony.io;

public class EntryDefinition {
    private String name;
    private Type type;

    public EntryDefinition(String name, Type type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
