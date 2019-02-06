package xyz.phanta.wird.grammar;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Classification {

    private final String identifier;
    private final List<ClassificationBody> bodies;

    public Classification(String identifier, List<ClassificationBody> bodies) {
        this.identifier = identifier;
        this.bodies = bodies;
    }

    public Classification(String identifier, ClassificationBody... bodies) {
        this(identifier, Arrays.asList(bodies));
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<ClassificationBody> getBodies() {
        return bodies;
    }

    @Override
    public String toString() {
        return identifier + " = " + bodies.stream().map(ClassificationBody::toString).collect(Collectors.joining(" | "));
    }

}
