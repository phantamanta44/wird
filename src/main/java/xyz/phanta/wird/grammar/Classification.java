package xyz.phanta.wird.grammar;

import xyz.phanta.wird.grammar.body.ClassificationBody;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Classification {

    private final String identifier;
    private final String displayName;
    private final List<? extends ClassificationBody> bodies;

    public Classification(String identifier, String displayName, List<? extends ClassificationBody> bodies) {
        this.identifier = identifier;
        this.displayName = displayName;
        this.bodies = bodies;
    }

    public Classification(String identifier, List<? extends ClassificationBody> bodies) {
        this(identifier, identifier, bodies);
    }

    public Classification(String identifier, ClassificationBody... bodies) {
        this(identifier, Arrays.asList(bodies));
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<? extends ClassificationBody> getBodies() {
        return bodies;
    }

    @Override
    public String toString() {
        return displayName + " = " + bodies.stream().map(ClassificationBody::toString).collect(Collectors.joining(" | "));
    }

}
