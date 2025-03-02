import java.util.ArrayList;
import java.util.List;

public class AST {
    private final String type;
    private final List<AST> children;
    private String value;
//    private final int line;
//    private final int column;

    private final Main main;

    public AST(String type, int line, int column, Main main) {
        this.type = type;
        this.children = new ArrayList<>();
//        this.line = line;
//        this.column = column;
        this.main = main;
    }

    public AST(String type, String value, int line, int column, Main main) {
        this.type = type;
        this.value = value;
        this.children = new ArrayList<>();
//        this.line = line;
//        this.column = column;
        this.main = main;
    }

    public void addChild(AST child) {
        children.add(child);
    }

    public void printTree(int indent) {
        String indentStr = "  ".repeat(indent);

        StringBuilder sb = new StringBuilder();
        sb.append(indentStr);
        sb.append(type);

        if (value != null) {
            sb.append(" (").append(value).append(")");
        }

        String output = sb.toString();
        System.out.println(output);

        if (main != null) {
            main.addToDisplayTree(STR."\{output}\n");
        }

        for (AST child : children) {
            child.printTree(indent + 1);
        }
    }
}