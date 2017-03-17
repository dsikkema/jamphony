package org.dsikkema.jamphony.jamphony.io;

public enum Type {
    INT("Int"),
    STRING("String");
    
    private final String name;
	
	private Type(String n) {
		this.name = n;
	}
	
	public String getName() {
		return this.name;
	}
}
