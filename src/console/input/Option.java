package console.input;

public class Option {
    private String name;
    private InputDataType type;

    public Option(String name, InputDataType type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return name;
    }

    public InputDataType getType() {
        return type;
    }
}
