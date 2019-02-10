package xyz.phanta.wird.grammar.body;

import xyz.phanta.wird.grammar.Classification;
import xyz.phanta.wird.grammar.part.ClassificationPart;
import xyz.phanta.wird.grammar.part.DirectSubclassificationPart;
import xyz.phanta.wird.parser.ParserConfig;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.util.List;

public class LeftRecursiveClassificationBody extends ClassificationBody {

    public LeftRecursiveClassificationBody(int bodyIndex, Classification recursiveClassification, List<ClassificationPart> parts) {
        super(bodyIndex, parts);
        parts.add(new DirectSubclassificationPart(recursiveClassification));
    }

    @Override
    public void finalize(ParseTreeParentNode node, ParserConfig config) {
        // NO-OP
    }

}
