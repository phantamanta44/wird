package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeLeafNode;
import xyz.phanta.wird.parsetree.ParseTreeNode;
import xyz.phanta.wird.util.SingleSupplier;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionPart extends ClassificationPart {

    private final String rawPattern;
    private final Pattern pattern;

    public RegularExpressionPart(String pattern) {
        this.rawPattern = pattern;
        this.pattern = Pattern.compile("^" + pattern);
    }

    @Nullable
    @Override
    public Supplier<? extends Consumed<? extends ParseTreeNode>> consume(Parser parser, String data, int from, int to, int level) {
        Matcher m = pattern.matcher(data.substring(from, to));
        if (!m.find()) return null;
        String match = m.group(0);
        return new SingleSupplier<>(new Consumed<>(match.length(), () -> new ParseTreeLeafNode(this, match)));
    }

    @Override
    public String stringify(ParseTreeNode node) {
        return ((ParseTreeLeafNode)node).getContent().replaceAll("\r?\n", "\\\\n");
    }

    @Override
    public String toString() {
        return "/" + rawPattern + "/";
    }

}
