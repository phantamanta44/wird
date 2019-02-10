package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeLeafNode;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class SyntheticPart extends ClassificationPart {

    @Nullable
    @Override
    public Supplier<? extends Consumed<? extends ParseTreeNode>> consume(Parser parser, String data, int from, int to, int level) {
        throw new UnsupportedOperationException("Synthetic parts cannot parse!");
    }

    @Override
    public String stringify(ParseTreeNode node) {
        return ((ParseTreeLeafNode)node).getContent().replaceAll("\r?\n", "\\\\n");
    }

}
