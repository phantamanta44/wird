package xyz.phanta.wird.parsetree;

import xyz.phanta.wird.grammar.part.ClassificationPart;

public class ParseTreeLeafNode extends ParseTreeNode {

    private final ClassificationPart part;
    private String content;

    public ParseTreeLeafNode(ClassificationPart part, String content) {
        this.part = part;
        this.content = content;
    }

    public ClassificationPart getPart() {
        return part;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String stringify(boolean space) {
        if (!space) return part.stringify(this);
        return part.shouldRetainSpace() ? part.stringify(this) : (" " + part.stringify(this));
    }

    @Override
    public String toString() {
        return "\"" + part.stringify(this) + "\"";
    }

}
