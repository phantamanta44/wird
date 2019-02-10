package xyz.phanta.wird.grammar.body;

import xyz.phanta.wird.grammar.part.EmptyPart;
import xyz.phanta.wird.parser.ParserConfig;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.util.Collections;

public class NoopClassificationBody extends ClassificationBody {

    public NoopClassificationBody() {
        super(-1, Collections.singletonList(new EmptyPart()));
    }

    @Override
    public void finalize(ParseTreeParentNode node, ParserConfig config) {
        // NO-OP
    }

}
