package xyz.phanta.wird.grammar.part;

import xyz.phanta.wird.parser.Consumed;
import xyz.phanta.wird.parser.ParseFrame;
import xyz.phanta.wird.parser.Parser;
import xyz.phanta.wird.parsetree.ParseTreeLeafNode;
import xyz.phanta.wird.parsetree.ParseTreeNode;

import javax.annotation.Nullable;

public class SyntheticPart extends ClassificationPart {

    @Nullable
    @Override
    public Consumed consume(Parser parser, ParseFrame frame, int index, String data) {
        throw new UnsupportedOperationException("Synthetic parts cannot parse!");
    }

    @Override
    public String stringify(ParseTreeNode node) {
        return ((ParseTreeLeafNode)node).getContent().replaceAll("\r?\n", "\\\\n");
    }

}
