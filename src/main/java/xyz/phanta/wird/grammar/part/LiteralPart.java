package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeLeafNode;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;

public class LiteralPart extends ClassificationPart {

    private final String value;
    private final String displayValue;

    public LiteralPart(String value) {
        this.value = value;
        this.displayValue = value.replaceAll("\r?\n", "\\\\n");
    }

    @Nullable
    @Override
    public Consumed consume(Parser parser, String data, int from, int to) {
        if (to - from < value.length()) return null;
        String extracted = data.substring(from, from + value.length());
        return extracted.equals(value) ? new Consumed(value.length(), () -> new ParseTreeLeafNode(this, value)) : null;
    }

    @Override
    public String stringify(ParseTreeNode node) {
        return displayValue;
    }

    @Override
    public String toString() {
        return "\"" + displayValue + "\"";
    }

}
