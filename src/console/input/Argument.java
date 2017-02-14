package console.input;

public class Argument {
    private String name;
    private int index;
    private InputDataType type;

    public Argument(String name, int index, InputDataType type) {
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

    public InputDataType getType() {
        return type;
    }
}
