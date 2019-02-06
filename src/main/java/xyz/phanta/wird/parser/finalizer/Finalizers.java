package xyz.phanta.wird.parser.finalizer;

import xyz.phanta.wird.grammar.part.SyntheticPart;
import xyz.phanta.wird.parsetree.ParseTreeLeafNode;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Finalizers {

    public static ClassificationFinalizer flatten(int index) {
        return n -> {
            ParseTreeParentNode child = (ParseTreeParentNode)n.getChildren().remove(index);
            n.getChildren().addAll(index, child.getChildren());
        };
    }

    public static ClassificationFinalizer omit(int... omissions) {
        Arrays.sort(omissions);
        return n -> {
            for (int i = omissions.length - 1; i >= 0; i--) n.getChildren().remove(omissions[i]);
        };
    }

    public static ClassificationFinalizer join(String separator) {
        return n -> {
            String joined = n.getChildren().stream()
                    .map(c -> ((ParseTreeLeafNode)c).getContent())
                    .collect(Collectors.joining(separator));
            n.getChildren().clear();
            n.getChildren().add(new ParseTreeLeafNode(new SyntheticPart(), joined));
        };
    }

    public static ClassificationFinalizer join() {
        return n -> {
            String joined = n.getChildren().stream()
                    .map(c -> ((ParseTreeLeafNode)c).getContent())
                    .collect(Collectors.joining());
            n.getChildren().clear();
            n.getChildren().add(new ParseTreeLeafNode(new SyntheticPart(), joined));
        };
    }

}
