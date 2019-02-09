package xyz.phanta.wird.grammar.body;

import xyz.phanta.wird.grammar.part.ClassificationPart;
import xyz.phanta.wird.parsetree.ParseTreeNode;
import xyz.phanta.wird.parsetree.ParseTreeParentNode;

import java.util.Arrays;
import java.util.List;

public class FlatClassificationBody extends ClassificationBody {

    private boolean recursive = false;

    public FlatClassificationBody(int bodyIndex, List<ClassificationPart> parts) {
        super(bodyIndex, parts);
    }

    public FlatClassificationBody(int bodyIndex, ClassificationPart... parts) {
        this(bodyIndex, Arrays.asList(parts));
    }

    public void markRecursive() {
        recursive = true;
    }

    @Override
    public void finalize(ParseTreeParentNode node) {
        if (recursive) {
            List<ParseTreeNode> children = node.getChildren();
            int bodyIndex = node.getBodyIndex();
            boolean retainSpace = node.doesRetainSpace();
            while (true) {
                int tailIndex = children.size() - 1;
                ParseTreeParentNode tail = (ParseTreeParentNode)children.get(tailIndex);
                if (tail.getBodyIndex() == -1) break;

                ParseTreeParentNode terminal = new ParseTreeParentNode(node.getClassification(), bodyIndex, retainSpace);
                List<ParseTreeNode> terminalNodes = children.subList(0, tailIndex);
                terminal.getChildren().addAll(terminalNodes);
                terminalNodes.clear();
                children.add(0, terminal);

                bodyIndex = tail.getBodyIndex();
                retainSpace = tail.doesRetainSpace();
                children.remove(children.size() - 1);
                children.addAll(tail.getChildren());
            }
            node.setBodyIndex(bodyIndex);
            children.remove(children.size() - 1);
        }
    }

}
