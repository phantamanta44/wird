package xyz.phanta.wird.parser.finalizer;

import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.util.function.Predicate;

public interface ClassificationFinalizer {

    void finalize(ParseTreeParentNode node);

    default ClassificationFinalizer onlyIf(Predicate<ParseTreeParentNode> condition) {
        return n -> {
            if (condition.test(n)) finalize(n);
        };
    }

}
